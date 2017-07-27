package com.suypower.pms.view.plugin.message;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.suypower.pms.R;
import com.suypower.pms.app.SuyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Stereo on 16/4/5.
 */
public class MessageList implements Serializable {


    public enum MessageEnum {
        PUBLICNOTICE,//公司公告 1
        FLOWTODO,//待办事项 2
        CHAT,//聊天 3
        CHATS,//群聊 4
    }


    MessageEnum messageEnum;//消息类型
    String title="";//消息标题
    String content="";//消息子内容
    int msgmark;//消息标记
    String msgdate="";//消息时间
    String msgid="";//消息ID
    String mediaid="";
    int msgType;


    String sender;

    public String getMediaid() {
        return mediaid;
    }

    public void setMediaid(String mediaid) {
        this.mediaid = mediaid;
        //下载
    }


    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    /**
     * 获得消息图标
     * @return
     */
    public Drawable getMsgdrawable() {
        Drawable msgdrawable;
        try
        {
            File file =new File(SuyApplication.getApplication().getCacheDir()+File.separator + mediaid);
            if (file.exists())
            {
                InputStream inputStream=new FileInputStream(file);
                BitmapDrawable bitmapDrawable=new BitmapDrawable(inputStream);
                msgdrawable = bitmapDrawable;

            }
            else
                msgdrawable =SuyApplication.getApplication().getResources().getDrawable(R.drawable.logo);
        }
        catch (Exception e)
        {e.printStackTrace();
            msgdrawable =SuyApplication.getApplication().getResources().getDrawable(R.drawable.logo);
        }

        return msgdrawable;
    }



    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public MessageEnum getMessageEnum() {
        return messageEnum;
    }

    public void setMessageEnum(MessageEnum messageEnum) {
        this.messageEnum = messageEnum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMsgmark() {
        return msgmark;
    }

    public void setMsgmark(int msgmark) {
        this.msgmark = msgmark;
    }

    public String getMsgdate() {
        return msgdate;
    }

    public void setMsgdate(String msgdate) {
        this.msgdate = msgdate;
    }


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
