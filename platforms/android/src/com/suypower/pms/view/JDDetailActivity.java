package com.suypower.pms.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suypower.pms.R;
import com.suypower.pms.app.task.BaseTask;
import com.suypower.pms.app.task.FileDownload;
import com.suypower.pms.app.task.IM;
import com.suypower.pms.app.task.InterfaceTask;
import com.suypower.pms.view.dlg.IMenu;
import com.suypower.pms.view.dlg.Menu_Custom;
import com.suypower.pms.view.plugin.CommonPlugin;
import com.suypower.pms.view.plugin.CustomPopWindowPlugin;
import com.suypower.pms.view.plugin.chat.MediaSupport;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by cjw on 16/6/30.
 */
public class JDDetailActivity extends Activity {


    private ImageView btnmore, btncancel;
    private Menu_Custom menu_custom;
    private int mode;
    private String json,strsender;
    private TextView jdtitle, sender, senddt, content;
    private String DISPATCH_ID, send_account_id;
    private ImageView btnplay;
    private String audioid;
    private Boolean isplaying = false;
    private String[] pics = null;
    private LinearLayout linearLayout;
    private String sendtime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jddetail_activity);
        btncancel = (ImageView) findViewById(R.id.btnreturn);
        btncancel.setOnClickListener(onClickListenerreturn);

        btnmore = (ImageView) findViewById(R.id.btnmore);
        btnmore.setOnClickListener(onClickListenermore);

        jdtitle = (TextView) findViewById(R.id.jdtitle);
        sender = (TextView) findViewById(R.id.sender);
        senddt = (TextView) findViewById(R.id.senddt);
        content = (TextView) findViewById(R.id.content);
        btnplay = (ImageView) findViewById(R.id.play);
        linearLayout = (LinearLayout)findViewById(R.id.linearLayout);
        json = getIntent().getStringExtra("json");
        mode = getIntent().getIntExtra("mode", 0);

        String pic_ids;
        btnplay.setVisibility(View.GONE);
        if (mode == 1) {
            menu_custom = new Menu_Custom(this, iMenu);
            menu_custom.addMenuItem(R.drawable.sp_ok, "审核通过", 0);
            menu_custom.addMenuItem(R.drawable.sp_no, "拒绝", 1);
            try {
                JSONObject jsonObject = new JSONObject(json);
                jdtitle.setText(jsonObject.getString("dispatch_title"));
                senddt.setText("发布时间:" + jsonObject.getString("create_time"));
                sendtime = jsonObject.getString("create_time");
                sender.setText("发布人:" + jsonObject.getString("send_user_name"));
                strsender = jsonObject.getString("send_user_name");
                content.setText(jsonObject.getString("dispatch_content"));
                DISPATCH_ID = jsonObject.getString("dispatch_id");
                send_account_id = jsonObject.getString("send_account_id");
                audioid = jsonObject.getString("audio_id");
                audiodownload();
                pic_ids = jsonObject.getString("pic_ids");
                if (!pic_ids.equals("")) {
                    pics = pic_ids.split(",");
                    picsdownload();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (mode == 2) {
            btnmore.setVisibility(View.GONE);
            try {
                JSONObject jsonObject = new JSONObject(json);
                jdtitle.setText(jsonObject.getString("dispatch_title"));
                senddt.setText("发布时间:" + jsonObject.getString("send_time"));
                sendtime = jsonObject.getString("send_time");
                sender.setText("发布人:" + jsonObject.getString("send_user_name"));
                content.setText(jsonObject.getString("dispatch_content"));
                DISPATCH_ID = jsonObject.getString("dispatch_id");
                send_account_id = jsonObject.getString("send_account_id");
                audioid = jsonObject.getString("audio_id");
                audiodownload();
                pic_ids = jsonObject.getString("pic_ids");
                if (!pic_ids.equals("")) {
                    pics = pic_ids.split(",");
                    picsdownload();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mode == 3) {
            btnmore.setVisibility(View.VISIBLE);

            try {
                JSONObject jsonObject = new JSONObject(json);
                jdtitle.setText(jsonObject.getString("DISPATCH_TITLE"));
                senddt.setText("发布时间:" + jsonObject.getString("SEND_TIME"));
                sendtime = jsonObject.getString("SEND_TIME");
                sender.setText("发布人:" + jsonObject.getString("USER_NAME"));
                content.setText(jsonObject.getString("DISPATCH_CONTENT"));
                DISPATCH_ID = jsonObject.getString("DISPATCH_ID");
//                send_account_id = jsonObject.getString("send_account_id");
                audioid = jsonObject.getString("AUDIO_ID");
                audiodownload();
                pic_ids = jsonObject.getString("PIC_IDS");
                if (!pic_ids.equals("")) {
                    pics = pic_ids.split(",");
                    picsdownload();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    private void audiodownload() {
        FileDownload fileDownload;
        if (!audioid.equals("")) {
            if (!CommonPlugin.checkFileIsExits(audioid, ".aac")) {
                fileDownload = new FileDownload(interfaceTask, FileDownload.StreamFile);
                fileDownload.mediaid = audioid;
                fileDownload.mediatype = ".aac";
                fileDownload.suffix = "";
                fileDownload.flag = "aac";
                fileDownload.startTask();
            } else {
                btnplay.setVisibility(View.VISIBLE);
                btnplay.setOnClickListener(onClickListenerplay);
            }
        }
    }


    private void picsdownload() {
        FileDownload fileDownload;
        if (pics == null)
            return;

        for (int i = 0; i < pics.length; i++) {
            if (!CommonPlugin.checkFileIsExits(pics[i], "aumb.jpg")) {
                fileDownload = new FileDownload(interfaceTask, FileDownload.StreamFile);
                fileDownload.mediaid = pics[i];
                fileDownload.mediatype = ".jpg";
                fileDownload.suffix = "aumb";
                fileDownload.flag = "jpg";
                fileDownload.startTask();
            } else {
                addimgview(pics[i]);
            }
        }

    }


    private void addimgview(String picmediaid) {
        ImageView imageView=new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                540);

        Bitmap bitmap = BitmapFactory.decodeFile(getCacheDir() + File.separator
        + picmediaid + "aumb.jpg");
        layoutParams.setMargins(10,20,10,0);
        imageView.setImageBitmap(bitmap);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        linearLayout.addView(imageView);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 0) {
                btnplay.setVisibility(View.VISIBLE);
                btnplay.setOnClickListener(onClickListenerplay);
                return;
            }

            if (msg.what == 1) {
                String mediaid = msg.obj.toString();
                addimgview(mediaid);
                return;
            }

        }
    };


    View.OnClickListener onClickListenerplay = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (!isplaying) {
                isplaying = true;
                btnplay.setBackground(getResources().getDrawable(R.drawable.btn_pause_selector));

                MediaSupport.playAudio(getCacheDir() + File.separator + audioid + ".aac", new MediaSupport.IPlayCallBack() {
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaSupport.stopAudio();

    }

    InterfaceTask interfaceTask = new InterfaceTask() {
        @Override
        public void TaskResultForMessage(Message message) {
            if (message.what == BaseTask.IMTask) {
                if (message.arg2 == IM.APPOVEMSG) {
                    if (message.arg1 == BaseTask.SUCCESS) {
                        Toast.makeText(JDDetailActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(R.anim.alpha, R.anim.slide_out_to_bottom);
                    } else {
                        Toast.makeText(JDDetailActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            if (message.what == BaseTask.DownloadFILETask) {
                if (message.arg2 == FileDownload.StreamFile) {
                    if (message.arg1 == BaseTask.SUCCESS) {
                        if (message.obj.toString().equals("aac")) {
                            handler.sendEmptyMessage(0);
                            return;
                        }
                        if (message.obj.toString().equals("jpg")) {

                            Message message1=handler.obtainMessage();
                            message1.what=1;
                            message1.obj=message.getData().getString("mediaid");
                            handler.sendMessage(message1);
                            return;
                        }
                    } else {
                        Toast.makeText(JDDetailActivity.this, "下载附件错误,请重新尝试", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        }
    };
    IMenu iMenu = new IMenu() {
        @Override
        public void ClickMenu(int itemid) {
            IM im = new IM(interfaceTask, IM.APPOVEMSG);
            switch (itemid) {
                case 0:

                    im.setPostValuesForKey("dispatch_id", DISPATCH_ID);
                    im.setPostValuesForKey("approve_result", "01");
                    im.setPostValuesForKey("approve_desc", "");
                    im.setPostValuesForKey("send_account_id", send_account_id);
                    im.startTask();
                    break;
                case 1:
                    AlertDialog.Builder builder = new AlertDialog.Builder(JDDetailActivity.this);
                    builder.setTitle("拒绝原因");
                    EditText editText = new EditText(JDDetailActivity.this);
                    builder.setView(editText);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            im.setPostValuesForKey("dispatch_id", DISPATCH_ID);
                            im.setPostValuesForKey("approve_result", "02");
                            im.setPostValuesForKey("approve_desc", editText.getText().toString());
                            im.setPostValuesForKey("send_account_id", send_account_id);
                            im.setPostValuesForKey("send_user_name", strsender);

                            im.startTask();
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();


                    break;
            }

        }
    };
    View.OnClickListener onClickListenermore = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (mode ==1)
            {
                menu_custom.ShowMenu(btnmore);
                return;
            }
            if (mode == 3) {
                Intent intent = new Intent(JDDetailActivity.this, JDMemberStateActivity.class);
                intent.putExtra("groupid", DISPATCH_ID);
                intent.putExtra("sendtime", sendtime);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            finish();
            overridePendingTransition(R.anim.alpha, R.anim.slide_out_to_bottom);
            return false;
        }
        return super.onKeyUp(keyCode, event);
    }
}