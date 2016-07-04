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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.suypower.pms.view.dlg.AlertDlg;
import com.suypower.pms.view.dlg.AlertSheet;
import com.suypower.pms.view.plugin.CommonPlugin;
import com.suypower.pms.view.plugin.CustomPopWindowPlugin;
import com.suypower.pms.view.plugin.camera.CameraHelper;
import com.suypower.pms.view.plugin.camera.CameraPlugin;
import com.suypower.pms.view.plugin.camera.PreviewMediaView;
import com.suypower.pms.view.plugin.camera.PreviewPhotoViewPlugin;
import com.suypower.pms.view.plugin.chat.ChatMessage;
import com.suypower.pms.view.plugin.chat.ChatsMangerActivity;
import com.suypower.pms.view.plugin.chat.MediaSupport;
import com.suypower.pms.view.plugin.message.MessageDB;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
    private EditText editText, titleedit;
    private CheckBox chkzd;
    private int images = 0;
    private AlertSheet alertSheet;
    private List<String> listmediaid;
    private ImageView imageViewadd, btnplay;
    private int sendimg = 0;
    private TextView chattxtcount, peoples, sendtager;
    private MediaSupport mediaSupport;
    private RelativeLayout btnrecord, btnsendtager, btnsp;
    private String recordfile = "";
    private Boolean isplaying = false;
    private Boolean iscontainrecord = false;
    private String sp = "";
    private String sendlists = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publishnotics_activity);
        btncancel = (Button) findViewById(R.id.btncancel);
        btncancel.setOnClickListener(onClickListenerreturn);
        btnsend = (Button) findViewById(R.id.btnsend);
        btnsend.setOnClickListener(onClickListenersend);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        chattxtcount = (TextView) findViewById(R.id.charcount);
        titleedit = (EditText) findViewById(R.id.title);
        sendtager = (TextView) findViewById(R.id.sendtager);
        btnrecord = (RelativeLayout) findViewById(R.id.menu_record);
        btnrecord.setOnClickListener(onClickListenerrecord);
        btnsendtager = (RelativeLayout) findViewById(R.id.menu_sendtager);
        btnsendtager.setOnClickListener(onClickListenersender);
        peoples = (TextView) findViewById(R.id.peoples);
        peoples.setText("");
        sendtager.setText("");
        btnsp = (RelativeLayout) findViewById(R.id.menu_sp);
        btnsp.setOnClickListener(onClickListenersp);

        chkzd = (CheckBox) findViewById(R.id.checkzd);
        if (SuyApplication.getApplication().getSuyClient().getSuyUserInfo()
                .m_loginResult.auths.contains("DISPATCH_APPROVE")
                )
            btnsp.setVisibility(View.GONE);
        if (!SuyApplication.getApplication().getSuyClient().getSuyUserInfo()
                .m_loginResult.auths.contains("DISPATCH_TOP"))
            chkzd.setVisibility(View.GONE);
        btnplay = (ImageView) findViewById(R.id.play);
        btnplay.setOnClickListener(onClickListenerplay);
        btnplay.setVisibility(View.GONE);


        listmediaid = new ArrayList<>();
        editText = (EditText) findViewById(R.id.txt);
        editText.addTextChangedListener(textWatcher);
        titleedit.requestFocus();
        addBtnImg();
        alertSheet = new AlertSheet(this);
        alertSheet.addSheetButton("拍照", onClickListenertakepicture);
        alertSheet.addSheetButton("从相机中选择", onClickListenerchoosepicture);
    }


    View.OnClickListener onClickListenerplay = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (!isplaying) {
                isplaying = true;
                btnplay.setBackground(getResources().getDrawable(R.drawable.btn_pause_selector));

                MediaSupport.playAudio(getCacheDir() + File.separator + recordfile + ".aac", new MediaSupport.IPlayCallBack() {
                    @Override
                    public void OnPlayFinish() {
                        isplaying = false;
                        btnplay.setBackground(getResources().getDrawable(R.drawable.btn_play_selector));
                    }
                });
            } else {
                isplaying = false;
                MediaSupport.stopAudio();
                btnplay.setBackground(getResources().getDrawable(R.drawable.btn_play_selector));

            }
        }
    };


    View.OnClickListener onClickListenersp = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(PublishNoticsActivity.this, SelectActivity.class);
            intent.putExtra("mode", 2);
            startActivityForResult(intent, SelectActivity.ApproveUsers);
            overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.alpha_exit);

        }
    };


    View.OnClickListener onClickListenersender = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(PublishNoticsActivity.this, SelectActivity.class);
            intent.putExtra("mode", 1);
            startActivityForResult(intent, SelectActivity.GroupInfo);
            overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.alpha_exit);
        }
    };


    View.OnClickListener onClickListenerrecord = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mediaSupport = new MediaSupport(PublishNoticsActivity.this, iRecordCallBack);
            try {
                mediaSupport.startAudioRecord(btnrecord);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    MediaSupport.IRecordCallBack iRecordCallBack = new MediaSupport.IRecordCallBack() {


        @Override
        public void OnRecordFinish(String recordname, int t) {


            Log.i("录音 完成", recordname);
            recordfile = recordname;
            btnplay.setVisibility(View.VISIBLE);

//            StringBuilder stringBuilder = new StringBuilder();
//            for (int i = 0; i < t; i++) {
//                stringBuilder.append("  ");
//            }


        }

        @Override
        public void OnRecordCancel() {

            Toast.makeText(PublishNoticsActivity.this, "取消录音", Toast.LENGTH_SHORT).show();


        }
    };


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


        if (requestCode == SelectActivity.ApproveUsers) {
            if (resultCode == 2) {
                sp = data.getStringExtra("sp");
                peoples.setText(data.getStringExtra("name"));
                Log.i("sp", sp);
                Log.i("peoples", peoples.getText().toString());
            }
            return;
        }
        if (requestCode == SelectActivity.GroupInfo) {
            if (resultCode == 2) {
                sendlists = data.getStringExtra("groupid");
                sendtager.setText(data.getStringExtra("name"));

            }
            return;
        }


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
                            + "/" + mediaid + "aumb.jpg");
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
                            + "/" + files[i] + "aumb.jpg");
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
            chattxtcount.setText(String.format("%1$s/800字数", String.valueOf(editText.getText().toString().length())));
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
            overridePendingTransition(R.anim.alpha, R.anim.slide_out_to_bottom);
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
            if (sendlists.equals("")) {
                Toast.makeText(PublishNoticsActivity.this, "请选择发送目标", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!SuyApplication.getApplication().getSuyClient().getSuyUserInfo()
                    .m_loginResult.auths.contains("DISPATCH_APPROVE")) {
                if (sp.equals("")) {
                    Toast.makeText(PublishNoticsActivity.this, "请选择审批人", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (editText.getText().toString().equals("")) {
                Toast.makeText(PublishNoticsActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!recordfile.equals("")) {
                AlertDlg alertDlg = new AlertDlg(PublishNoticsActivity.this, AlertDlg.AlertEnum.TIPS);
                alertDlg.setContentText("是否包含录音信息");
                alertDlg.setOkClickLiseter(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDlg.dismiss();
                        iscontainrecord = true;
                        submitinfo();
                    }
                });
                alertDlg.show();
            } else
                submitinfo();


        }
    };


    private void submitinfo() {
        CustomPopWindowPlugin.ShowPopWindow(btnsend, getLayoutInflater(), "正在发布");
        sendimg = 0;


        if (iscontainrecord) {
            if (!recordfile.equals("")) {
                FileUpLoad fileUpLoad = new FileUpLoad(interfaceTask, FileUpLoad.UPLOADFILE);
                fileUpLoad.mediaid = recordfile;
                fileUpLoad.mediaidtype = ".aac";
                fileUpLoad.mediatype="03";
                fileUpLoad.flag = recordfile;
                fileUpLoad.IsNotics = true;
                fileUpLoad.startTask();
            }
            if (listmediaid.size() > 0) {

                for (int i = 0; i < listmediaid.size(); i++) {
                    FileUpLoad fileUpLoad = new FileUpLoad(interfaceTask, FileUpLoad.UPLOADFILE);
                    fileUpLoad.mediaid = listmediaid.get(i);
                    fileUpLoad.flag = listmediaid.get(i);
                    fileUpLoad.mediatype="02";
                    fileUpLoad.IsNotics = true;
                    fileUpLoad.startTask();
                }


            }
            return;
        }
        if (listmediaid.size() > 0) {

            for (int i = 0; i < listmediaid.size(); i++) {
                FileUpLoad fileUpLoad = new FileUpLoad(interfaceTask, FileUpLoad.UPLOADFILE);
                fileUpLoad.mediaid = listmediaid.get(i);
                fileUpLoad.flag = listmediaid.get(i);
                fileUpLoad.mediatype="02";
                fileUpLoad.IsNotics = true;
                fileUpLoad.startTask();
            }

            return;
        }

        publishNoticsInfo();
    }

    /**
     * 发布公告信息
     */
    private void publishNoticsInfo() {
        PublishNotics publishNotics = new PublishNotics(interfaceTask, PublishNotics.SENDNOTICS);
        publishNotics.setPostValuesForKey("dispatch_title", titleedit.getText().toString());
        publishNotics.setPostValuesForKey("dispatch_content", editText.getText().toString());
        if (listmediaid.size() == 0)
            publishNotics.setPostValuesForKey("pic_ids", "");
        else {
            String strmedias = "";
            for (String s : listmediaid) {
                strmedias += s + ",";
            }
            publishNotics.setPostValuesForKey("pic_ids", strmedias.substring(0, strmedias.length() - 1));
        }
        if (recordfile.equals(""))
            publishNotics.setPostValuesForKey("audio_id", "");
        else
            publishNotics.setPostValuesForKey("audio_id", recordfile);
        if (SuyApplication.getApplication().getSuyClient().getSuyUserInfo()
                .m_loginResult.auths.contains("DISPATCH_APPROVE")
                ) {
            publishNotics.setPostValuesForKey("status_code", "01");
            publishNotics.setPostValuesForKey("approve_account_id", "");
        }
        else {
            publishNotics.setPostValuesForKey("status_code", "02");
            publishNotics.setPostValuesForKey("approve_account_id",sp);
        }
        publishNotics.setPostValuesForKey("group_ids",sendlists);
        publishNotics.setPostValuesForKey("is_top",
                chkzd.isChecked()?"01":"02");
        publishNotics.startTask();
    }


    InterfaceTask interfaceTask = new InterfaceTask() {
        @Override
        public void TaskResultForMessage(Message message) {

            if (message.what == BaseTask.UploadFileTask) {
                if (message.arg2 == FileUpLoad.UPLOADFILE) {
                    if (message.arg1 == BaseTask.SUCCESS) {

                        sendimg++;
                        if (iscontainrecord) {
                            if (sendimg == listmediaid.size() + 1) {
                                 publishNoticsInfo();
                            }
                        } else {
                            if (sendimg == listmediaid.size()) {
                                //成功上传
                                publishNoticsInfo();
                            }
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