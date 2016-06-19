package com.suypower.pms.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suypower.pms.R;
import com.suypower.pms.app.ReturnData;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.task.BaseTask;
import com.suypower.pms.app.task.FileUpLoad;
import com.suypower.pms.app.task.IM;
import com.suypower.pms.app.task.InterfaceTask;
import com.suypower.pms.app.task.PublishNotics;
import com.suypower.pms.view.contacts.Contacts;
import com.suypower.pms.view.contacts.ContactsDB;
import com.suypower.pms.view.contacts.ContactsSelectActivity;
import com.suypower.pms.view.dlg.AlertSheet;
import com.suypower.pms.view.plugin.CommonPlugin;
import com.suypower.pms.view.plugin.CustomPopWindowPlugin;
import com.suypower.pms.view.plugin.camera.CameraHelper;
import com.suypower.pms.view.plugin.camera.CameraPlugin;
import com.suypower.pms.view.plugin.camera.PreviewMediaView;
import com.suypower.pms.view.plugin.camera.PreviewPhotoViewPlugin;
import com.suypower.pms.view.plugin.chat.ChatMessage;
import com.suypower.pms.view.plugin.chat.ChatsMangerActivity;
import com.suypower.pms.view.plugin.message.MessageDB;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;

/**
 * Created by Stereo on 16/6/12.
 */
public class PublishNoticsActivity extends BaseActivityPlugin {

    private Button btncancel;
    private Button btnsend;
    private LinearLayout linearLayout;
    private EditText editText,titleedit;
    private int images = 0;
    private AlertSheet alertSheet;
    private List<String> listmediaid;
    private ImageView imageViewadd;
    private int sendimg = 0;
    private TextView chattxtcount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publishnotics_activity);
        btncancel = (Button) findViewById(R.id.btncancel);
        btncancel.setOnClickListener(onClickListenerreturn);
        btnsend = (Button) findViewById(R.id.btnsend);
        btnsend.setOnClickListener(onClickListenersend);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        chattxtcount = (TextView)findViewById(R.id.charcount) ;
        titleedit = (EditText)findViewById(R.id.title);

        listmediaid = new ArrayList<>();
        editText = (EditText) findViewById(R.id.txt);
        editText.addTextChangedListener(textWatcher);
        titleedit.requestFocus();
        addBtnImg();
        alertSheet = new AlertSheet(this);
        alertSheet.addSheetButton("拍照", onClickListenertakepicture);
        alertSheet.addSheetButton("从相机中选择", onClickListenerchoosepicture);
    }


    /**
     * 拍照
     */
    View.OnClickListener onClickListenertakepicture = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            alertSheet.dismiss();
            CameraPlugin cameraPlugin = new CameraPlugin(PublishNoticsActivity.this);
            cameraPlugin.takePictureForNaative();
        }
    };

    /**
     * 从相机中选择
     */
    View.OnClickListener onClickListenerchoosepicture = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            alertSheet.dismiss();
            CameraPlugin cameraPlugin = new CameraPlugin(PublishNoticsActivity.this);
            cameraPlugin.openPreviewPhotoForNavtive(4 - listmediaid.size());
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle;


        if (requestCode == PreviewMediaView.REQUESTCODE) {
            //删除返回
            if (resultCode == 1) {
                int delindex = data.getExtras().getInt("delindex");
                delImage(delindex);
                listmediaid.remove(delindex);
                if (listmediaid.size() < 4) {
                    imageViewadd.setVisibility(View.VISIBLE);
                }

            }

            return;
        }
        if (CameraHelper.JSCallCamera == requestCode)//拍照回调
        {
            //拍照返回信息
            if (resultCode == -1) {
                String mediaid = CameraPlugin.copyCacheFile(CameraHelper.PhotoPath);
                Bitmap bitmap = CameraPlugin.bitbmpfrommediaLocal(mediaid, 0);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(getCacheDir()
                            + "/" + mediaid + "_aumb.jpg");
                    fileOutputStream.write(baos.toByteArray());
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                addImage(bitmap, mediaid);
                listmediaid.add(mediaid);
                if (listmediaid.size() == 4) {
                    imageViewadd.setVisibility(View.GONE);
                }
                return;
            }

        }

        if (requestCode == PreviewPhotoViewPlugin.JSCallPreviewPhtoto) {
            if (resultCode == 1) {
                bundle = data.getExtras();
                String[] files = bundle.getStringArray("files");
                for (int i = 0; i < files.length; i++) {
                    Log.i("选择照片", files[i]);
                    listmediaid.add(files[i]);
                    Bitmap bitmap = BitmapFactory.decodeFile(getCacheDir()
                            + "/" + files[i] + "_aumb.jpg");
                    addImage(bitmap, files[i]);
                }
                if (listmediaid.size() == 4) {
                    imageViewadd.setVisibility(View.GONE);
                }
                return;
            }
        }


    }


    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            chattxtcount.setText(String.format("%1$s/800字数",String.valueOf(editText.getText().toString().length())));
        }
    };

    /**
     * 添加图片图标
     */
    private void addBtnImg() {

        imageViewadd = new ImageView(this);
        imageViewadd.setOnClickListener(onClickListeneradd);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CommonPlugin.dip2px(80), CommonPlugin.dip2px(80));
        imageViewadd.setBackground(getResources().getDrawable(R.drawable.contacts_add_selector));
        params.setMargins(5, 5, 10, 5);
        imageViewadd.setLayoutParams(params);
        linearLayout.addView(imageViewadd);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
    }


    private void addImage(Bitmap bitmap, String mediaid) {
        ImageView imageView = new ImageView(this);
        imageView.setOnClickListener(onClickListenerimg);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CommonPlugin.dip2px(80), CommonPlugin.dip2px(80));
        imageView.setImageBitmap(bitmap);
        params.setMargins(5, 5, 10, 5);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(params);
        imageView.setId(images);
        imageView.setTag(mediaid);
        linearLayout.addView(imageView, images);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        images++;
    }


    private void delImage(int delindex) {
        View v = linearLayout.getChildAt(delindex);
        linearLayout.removeView(v);
        images--;
    }

    View.OnClickListener onClickListeneradd = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            alertSheet.show();
        }
    };

    /**
     * 点击返回
     */
    View.OnClickListener onClickListenerreturn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
            overridePendingTransition(R.anim.alpha,R.anim.slide_out_to_bottom);
        }
    };

    /**
     * 发送
     */
    View.OnClickListener onClickListenersend = new View.OnClickListener() {
        @Override
        public void onClick(View view) {


            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(editText.getWindowToken(), 0);

            if (titleedit.getText().toString().equals("")) {
                Toast.makeText(PublishNoticsActivity.this, "请输入标题", Toast.LENGTH_SHORT).show();
                return;
            }
            if (editText.getText().toString().equals("")) {
                Toast.makeText(PublishNoticsActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
                return;
            }


            CustomPopWindowPlugin.ShowPopWindow(btnsend, getLayoutInflater(), "正在发布");
            if (listmediaid.size() > 0) {
                sendimg = 0;
                for (int i = 0; i < listmediaid.size(); i++) {
                    FileUpLoad fileUpLoad = new FileUpLoad(interfaceTask, FileUpLoad.UPLOADFILE);
                    fileUpLoad.mediaid = listmediaid.get(i);
                    fileUpLoad.flag = listmediaid.get(i);
                    fileUpLoad.IsNotics = true;
                    fileUpLoad.startTask();
//                    try
//                    {
//                        Thread.sleep(500);
//                    }
//                    catch (Exception e)
//                    {e.printStackTrace();}
                }

                return;
                //发送图片
            }
            publishNoticsInfo();

        }
    };


    /**
     * 发布公告信息
     */
    private void publishNoticsInfo() {
        PublishNotics publishNotics = new PublishNotics(interfaceTask, PublishNotics.SENDNOTICS);
        publishNotics.setPostValuesForKey("msgType", "05");
        publishNotics.setPostValuesForKey("reciveUserId", "");
        publishNotics.setPostValuesForKey("groupId", "10000");
        publishNotics.setPostValuesForKey("circularType", "1");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title",titleedit.getText().toString());
            jsonObject.put("content",editText.getText().toString());
            jsonObject.put("type","1");
            jsonObject.put("status","1");
            if (listmediaid.size()==0)
                jsonObject.put("mediaIds","");
            else
            {
                String strmedias="";
                for (String s :listmediaid)
                {
                    strmedias +=s +"#";
                }
                strmedias = strmedias.substring(0,strmedias.length()-1);
                jsonObject.put("mediaIds",strmedias);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(PublishNoticsActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
            return;
        }

        publishNotics.setPostValuesForKey("circularJson", jsonObject.toString());
        publishNotics.startTask();

    }


    InterfaceTask interfaceTask = new InterfaceTask() {
        @Override
        public void TaskResultForMessage(Message message) {

            if (message.what == BaseTask.UploadFileTask) {
                if (message.arg2 == FileUpLoad.UPLOADFILE) {
                    if (message.arg1 == BaseTask.SUCCESS) {

                        sendimg++;
                        if (sendimg == listmediaid.size()) {
                            //成功上传
                            publishNoticsInfo();
                        }
                    } else {
                        CustomPopWindowPlugin.CLosePopwindow();
                        Toast.makeText(PublishNoticsActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            if (message.what == BaseTask.PublishNotics) {
                if (message.arg2 == PublishNotics.SENDNOTICS) {
                    CustomPopWindowPlugin.CLosePopwindow();
                    if (message.arg1 == BaseTask.SUCCESS) {
                        Toast.makeText(PublishNoticsActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    } else {

                        Toast.makeText(PublishNoticsActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }


        }
    };


    /**
     * 点击获得
     */
    View.OnClickListener onClickListenerimg = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CameraPlugin cameraPlugin = new CameraPlugin(PublishNoticsActivity.this);

            cameraPlugin.openPreviewUrlPhoto(PublishNoticsActivity.this,
                    listmediaid, view.getTag().toString(), true);
        }
    };

}