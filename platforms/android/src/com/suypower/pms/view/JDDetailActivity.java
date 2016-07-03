package com.suypower.pms.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    private String json;
    private TextView jdtitle,sender,senddt,content;
    private String DISPATCH_ID,send_account_id;
    private ImageView btnplay;
    private String audioid;
    private Boolean isplaying = false;
    private String[] pics = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jddetail_activity);
        btncancel = (ImageView) findViewById(R.id.btnreturn);
        btncancel.setOnClickListener(onClickListenerreturn);

        btnmore = (ImageView) findViewById(R.id.btnmore);
        btnmore.setOnClickListener(onClickListenermore);

        jdtitle = (TextView)findViewById(R.id.jdtitle);
        sender = (TextView)findViewById(R.id.sender);
        senddt = (TextView)findViewById(R.id.senddt);
        content = (TextView)findViewById(R.id.content);
        btnplay = (ImageView)findViewById(R.id.play);

        json = getIntent().getStringExtra("json");
        mode = getIntent().getIntExtra("mode", 0);

        String pic_ids;

        if (mode == 1) {
            menu_custom = new Menu_Custom(this, iMenu);
            menu_custom.addMenuItem(R.drawable.sp_ok, "审核通过", 0);
            menu_custom.addMenuItem(R.drawable.sp_no, "拒绝", 1);
            try {
                JSONObject jsonObject=new JSONObject(json);
                jdtitle.setText(jsonObject.getString("DISPATCH_TITLE"));
                senddt.setText("发布时间:"+jsonObject.getString("CREATE_TIME"));
                sender.setText("发布人:"+jsonObject.getString("SEND_USER_NAME"));
                content.setText(jsonObject.getString("DISPATCH_CONTENT"));
                DISPATCH_ID = jsonObject.getString("DISPATCH_ID");
                send_account_id = jsonObject.getString("SEND_ACCOUNT_ID");
                audioid = jsonObject.getString("AUDIO_ID");
                audiodownload();
                pic_ids = jsonObject.getString("PIC_IDS");
                if (!pic_ids.equals(""))
                    pics = pic_ids.split(",");


            }
            catch (Exception e)
            {e.printStackTrace();}
        }
        btnplay.setVisibility(View.GONE);
        if (mode==2)
        {
            btnmore.setVisibility(View.GONE);
            try {
                JSONObject jsonObject=new JSONObject(json);
                jdtitle.setText(jsonObject.getString("dispatch_title"));
                senddt.setText("发布时间:"+jsonObject.getString("send_time"));
                sender.setText("发布人:"+jsonObject.getString("send_user_name"));
                content.setText(jsonObject.getString("dispatch_content"));
                DISPATCH_ID = jsonObject.getString("dispatch_id");
                send_account_id = jsonObject.getString("send_account_id");
                audioid = jsonObject.getString("audio_id");
                audiodownload();
                pic_ids = jsonObject.getString("pic_ids");
                if (!pic_ids.equals(""))
                    pics = pic_ids.split(",");


            }
            catch (Exception e)
            {e.printStackTrace();}
        }
    }


    private void audiodownload()
    {
        FileDownload fileDownload;
        if (!audioid.equals(""))
        {
            if (!CommonPlugin.checkFileIsExits(audioid,".aac")) {
                fileDownload = new FileDownload(interfaceTask, FileDownload.StreamFile);
                fileDownload.mediaid = audioid;
                fileDownload.mediatype = ".aac";
                fileDownload.suffix = "";
                fileDownload.flag = "aac";
                fileDownload.startTask();
            }
            else
            {
                btnplay.setVisibility(View.VISIBLE);
                btnplay.setOnClickListener(onClickListenerplay);
            }
        }
    }



    private void picsdownload()
    {
        FileDownload fileDownload;
        if (pics==null)
            return;

        for ( int i=0;i<pics.length;i++  )
        {
            if (!CommonPlugin.checkFileIsExits(pics[i],"aumb.jpg")) {
                fileDownload = new FileDownload(interfaceTask, FileDownload.StreamFile);
                fileDownload.mediaid = pics[i];
                fileDownload.mediatype = ".jpg";
                fileDownload.suffix = "aumb";
                fileDownload.flag = "jpg";
                fileDownload.startTask();
            }
            else
            {

            }
        }

    }


    private void addimgview(String picmediaid)
    {

    }

    Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what==0)
            {
                btnplay.setVisibility(View.VISIBLE);
                btnplay.setOnClickListener(onClickListenerplay);
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

    InterfaceTask interfaceTask=new InterfaceTask() {
        @Override
        public void TaskResultForMessage(Message message) {
            if (message.what== BaseTask.IMTask)
            {
                if (message.arg2 == IM.APPOVEMSG)
                {
                    if (message.arg1==BaseTask.SUCCESS)
                    {
                        Toast.makeText(JDDetailActivity.this,"提交成功",Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(R.anim.alpha, R.anim.slide_out_to_bottom);
                    }
                    else
                    {
                        Toast.makeText(JDDetailActivity.this,"提交失败",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            if (message.what==BaseTask.DownloadFILETask)
            {
                if (message.arg2 == FileDownload.StreamFile)
                {
                    if (message.arg1 == BaseTask.SUCCESS)
                    {
                        if (message.obj.toString().equals("aac"))
                        {
                            handler.sendEmptyMessage(0);
                            return;
                        }
                    }
                    else
                    {
                        Toast.makeText(JDDetailActivity.this,"下载附件错误,请重新尝试",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        }
    };
    IMenu iMenu=new IMenu() {
        @Override
        public void ClickMenu(int itemid) {
            IM im=new IM(interfaceTask,IM.APPOVEMSG);
            switch (itemid)
            {
                case 0:

                    im.setPostValuesForKey("dispatch_id",DISPATCH_ID);
                    im.setPostValuesForKey("approve_result","01");
                    im.setPostValuesForKey("approve_desc","");
                    im.setPostValuesForKey("send_account_id",send_account_id);
                    im.startTask();
                    break;
                case 1:
                    AlertDialog.Builder builder=new AlertDialog.Builder(JDDetailActivity.this);
                    builder.setTitle("拒绝原因");
                    EditText editText=new EditText(JDDetailActivity.this);
                    builder.setView(editText);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            im.setPostValuesForKey("dispatch_id",DISPATCH_ID);
                            im.setPostValuesForKey("approve_result","02");
                            im.setPostValuesForKey("approve_desc",editText.getText().toString());
                            im.setPostValuesForKey("send_account_id",send_account_id);
                            im.startTask();
                        }
                    });
                    builder.setNegativeButton("取消",null);
                    AlertDialog alertDialog=builder.create();
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
            if (mode ==0 )
            {

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