package com.suypower.pms.view.plugin.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suypower.pms.R;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.configxml.GlobalConfig;
import com.suypower.pms.app.task.BaseTask;
import com.suypower.pms.app.task.FileDownload;
import com.suypower.pms.app.task.FileUpLoad;
import com.suypower.pms.app.task.InterfaceTask;
import com.suypower.pms.view.contacts.Contacts;
import com.suypower.pms.view.contacts.ContactsDB;
import com.suypower.pms.view.plugin.CommonPlugin;
import com.suypower.pms.view.plugin.camera.CameraPlugin;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stereo on 16/4/12.
 */
public class ChatAdpter extends BaseAdapter {

    private TextView msg, msgdate,txt;
    private ImageView nickimg;
    private TextView nickName;
    private ImageView picture,state;
    private RelativeLayout btncontent;
    private ProgressBar progressBar;
    private IChat iChat;
    private String predt="";
    private ContactsDB contactsDB;

    public List<ChatMessage> chatMessageList;

    Context context;
    LayoutInflater layoutInflater;

    public ChatAdpter(Context context, IChat iChat) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        chatMessageList = new ArrayList<>();
        contactsDB = new ContactsDB(SuyApplication.getApplication().getSuyDB().getDb());
        this.iChat = iChat;
    }

    @Override
    public int getCount() {
        return chatMessageList.size();
    }

    @Override
    public Object getItem(int i) {
        return chatMessageList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ChatMessage chatMessage = chatMessageList.get(i);
        View v = null;
        switch (chatMessage.getMessageTypeEnum()) {
            case TEXT:
                if (chatMessage.getSelf()){
                    v = layoutInflater.inflate(R.layout.chat_text_self_list, null);
                    nickimg = (ImageView) v.findViewById(R.id.sendimg);
                }
                else{
                    v = layoutInflater.inflate(R.layout.chat_text_other_list, null);
                    nickimg = (ImageView) v.findViewById(R.id.sendimg);
                    nickName = (TextView) v.findViewById(R.id.senderName);
                    nickName.setText(chatMessage.getSender());

                    nickimg.setTag(chatMessage.getSenderid());
                    nickimg.setOnClickListener(onClickListenersend);
                }
                btncontent = (RelativeLayout) v.findViewById(R.id.btncontentimg);
                msg = (TextView) v.findViewById(R.id.msg);
                msgdate = (TextView) v.findViewById(R.id.dt);
                predt = chatMessageList.get((i==0)?0:i-1).getMsgdateInit();
                if (i==0)
                {
                    msgdate.setVisibility(View.VISIBLE);
                    msgdate.setText(chatMessage.getMsgdate());
                }
                else {
                    if (!CommonPlugin.isInOneMin(predt, chatMessage.getMsgdateInit()))
                        msgdate.setVisibility(View.GONE);
                    else {
                        msgdate.setVisibility(View.VISIBLE);
                        msgdate.setText(chatMessage.getMsgdate());
                    }
                }
                msg.setText(chatMessage.getMsg());
                btncontent.setTag(i);
                btncontent.setOnClickListener(onClickListener);
                btncontent.setOnLongClickListener(onLongClickListener);
                break;
            case PICTURE:
                BitmapDrawable bitmapDrawable;

                if (chatMessage.getSelf()) {
                    v = layoutInflater.inflate(R.layout.chat_picture_self_list, null);
                    nickimg = (ImageView)v.findViewById(R.id.sendimg);
                    bitmapDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.img_self);
                } else {
                    v = layoutInflater.inflate(R.layout.chat_picture_other_list, null);
                    nickimg = (ImageView)v.findViewById(R.id.sendimg);
                    bitmapDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.img_other);
                    nickName = (TextView) v.findViewById(R.id.senderName);
                    nickName.setText(chatMessage.getSender());
                    nickimg.setTag(chatMessage.getSenderid());
                    nickimg.setOnClickListener(onClickListenersend);
                }
                progressBar = (ProgressBar) v.findViewById(R.id.pbar);
                picture = (ImageView) v.findViewById(R.id.img);
                picture.setTag(i);
                picture.setOnClickListener(onClickListener);
                picture.setOnLongClickListener(onLongClickListener);
                msgdate = (TextView) v.findViewById(R.id.dt);
                predt = chatMessageList.get((i==0)?0:i-1).getMsgdateInit();
                if (i==0)
                {
                    msgdate.setVisibility(View.VISIBLE);
                    msgdate.setText(chatMessage.getMsgdate());
                }
                else {
                    if (!CommonPlugin.isInOneMin(predt, chatMessage.getMsgdateInit()))
                        msgdate.setVisibility(View.GONE);
                    else {
                        msgdate.setVisibility(View.VISIBLE);
                        msgdate.setText(chatMessage.getMsgdate());
                    }
                }
                if (!CommonPlugin.checkFileIsExits(chatMessage.getMediaid() + "aumb",".jpg")) {
                    Log.i("不图片id", chatMessage.getMediaid() + "aumb");
                    FileDownload fileDownload = new FileDownload(interfaceTask, FileDownload.StreamFile);
                    fileDownload.mediaid = chatMessage.getMediaid();
                    fileDownload.mediatype=".jpg";
                    fileDownload.suffix="aumb";

                    fileDownload.flag = picture;
                    fileDownload.startTask();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Log.i("存图片id", chatMessage.getMediaid() + "aumb");
                    Bitmap bitmap = BitmapFactory.decodeFile(context.getCacheDir() + "/" + chatMessage.getMediaid() + "aumb.jpg"); //将图片的长和宽缩小味原来的1/2
                    Bitmap result = CommonPlugin.MaskImage(bitmap, bitmapDrawable.getBitmap());
                    if (result!=null) {
                        picture.setImageBitmap(result);
                        picture.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    }
                    else

                    bitmap.recycle();
                }



                break;
            case AUDIO:

                if (chatMessage.getSelf()) {
                    v = layoutInflater.inflate(R.layout.chat_audio_self_list, null);
                    nickimg = (ImageView)v.findViewById(R.id.sendimg);
                } else {
                    v = layoutInflater.inflate(R.layout.chat_audio_other_list, null);
                    nickimg = (ImageView)v.findViewById(R.id.sendimg);
                    nickName = (TextView) v.findViewById(R.id.senderName);
                    nickName.setText(chatMessage.getSender());

                    nickimg.setTag(chatMessage.getSenderid());
                    nickimg.setOnClickListener(onClickListenersend);
                }
                progressBar = (ProgressBar) v.findViewById(R.id.pbar);
                state = (ImageView)v.findViewById(R.id.state);
                if (chatMessage.getMsgSendState()==1)
                    state.setVisibility(View.VISIBLE);
                else
                    state.setVisibility(View.GONE);
                msg = (TextView) v.findViewById(R.id.msg);
                msg.setText(chatMessage.getMsg());
                msgdate = (TextView) v.findViewById(R.id.dt);
                predt = chatMessageList.get((i==0)?0:i-1).getMsgdateInit();
                if (i==0)
                {
                    msgdate.setVisibility(View.VISIBLE);
                    msgdate.setText(chatMessage.getMsgdate());
                }
                else {
                    if (!CommonPlugin.isInOneMin(predt, chatMessage.getMsgdateInit()))
                        msgdate.setVisibility(View.GONE);
                    else {
                        msgdate.setVisibility(View.VISIBLE);
                        msgdate.setText(chatMessage.getMsgdate());
                    }
                }
                if (!CommonPlugin.checkFileIsExits(chatMessage.getMediaid() ,".aac")) {
                    Log.i("语音", chatMessage.getMediaid() );
                    FileDownload fileDownload = new FileDownload(interfaceTask, FileDownload.StreamFile);
                    fileDownload.mediaid = chatMessage.getMediaid();
                    fileDownload.mediatype=".aac";
                    fileDownload.suffix="";
                    fileDownload.startTask();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Log.i("存语音id", chatMessage.getMediaid() );
                    btncontent = (RelativeLayout) v.findViewById(R.id.btncontentimg);
                    btncontent.setTag(i);
                    btncontent.setOnClickListener(onClickListener);
                    btncontent.setOnLongClickListener(onLongClickListener);
                    txt = (TextView)v.findViewById(R.id.txt);

                    if (chatMessage.getEx1()==null)
                    {
                        int _t = MediaSupport.getMediaDuration(GlobalConfig.AUDIO_CACHE_PATH+
                                File.separator+chatMessage.getMediaid()+".aac" );
                        chatMessage.setEx1(String.valueOf(_t) + "\"");
                    }
                    txt.setText(chatMessage.getEx1());

                }
                break;
        }

        if (chatMessage.getSelf()) {
            Bitmap bitmap = BitmapFactory.decodeFile(SuyApplication.getApplication().getCacheDir() +
                    File.separator + SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult
                    .m_strPhoto + "40.jpg");
            if (bitmap != null) {
                nickimg.setImageBitmap(bitmap);
            }
        }
        else
        {
            Contacts contacts = contactsDB.LoadChaterInfo(chatMessage.getSenderid());
            if (CommonPlugin.checkFileIsExits(contacts.getNickimgurl(), "40.jpg")) {
                Bitmap bitmap = BitmapFactory.decodeFile(SuyApplication.getApplication().getCacheDir()
                        + File.separator + contacts.getNickimgurl() + "40.jpg");
                if (bitmap != null) {
                    nickimg.setImageBitmap(bitmap);
                    nickimg.setScaleType(ImageView.ScaleType.FIT_CENTER);
                } else {
                    File file = new File(SuyApplication.getApplication().getCacheDir()
                            + File.separator + contacts.getNickimgurl() + "40.jpg");
                    file.delete();
                }
            } else {
                FileDownload fileDownload = new FileDownload(interfaceTask, FileDownload.StreamFile);
                fileDownload.mediaid = contacts.getNickimgurl();
                fileDownload.mediatype = ".jpg";
                fileDownload.suffix="40";
                fileDownload.imgamub = "";
                fileDownload.flag = contacts.getNickimgurl();
                fileDownload.startTask();
            }
        }



        return v;
    }

    InterfaceTask interfaceTask = new InterfaceTask() {
        @Override
        public void TaskResultForMessage(Message message) {
//            try {
//                Thread.sleep(400);
//            }
//            catch (Exception e)
//            {e.printStackTrace();}


            handler.sendMessage(message);

        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == BaseTask.DownloadFILETask) {
                if (msg.arg2 == FileDownload.StreamFile) {

                    iChat.OnClickChatItem(null);
//                    Bundle bundle=msg.getData();
//                    Bitmap bitmap = BitmapFactory.decodeFile(context.getCacheDir() + "/" + bundle.getString("mediaid") + "_aumb.jpg"); //将图片的长和宽缩小味原来的1/2
//                    ((ImageView)msg.obj).setImageBitmap(bitmap);
//                    ((ImageView)msg.obj).setScaleType(ImageView.ScaleType.CENTER_CROP);


                }
            }
        }
    };

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            ChatMessage chatMessage = chatMessageList.get(((Integer) view.getTag()));
            iChat.OnLongClickItem(chatMessage, view);
            return false;
        }
    };


    View.OnClickListener onClickListenersend =new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            iChat.OnClickSender(view.getTag().toString());
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i("点击聊天", String.valueOf(view.getTag()));

            ChatMessage chatMessage = chatMessageList.get(((Integer) view.getTag()));

            iChat.OnClickChatItem(chatMessage);

        }
    };


}
