package com.suypower.pms.view.plugin.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;

import com.suypower.pms.view.plugin.message.MessageInfo;
import com.suypower.pms.view.plugin.message.MessageList;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Stereo on 16/4/12.
 */
public class ChatMessage {

    public enum MessageTypeEnum {
        TEXT ,//文字聊天
        PICTURE,//图片
        AUDIO,//语音
        VIDEO,//视频
        RICHTXT1,//多媒体文本单图文
        RICHTXT2,//多媒体文本多图文
        MEETTING,//会议信息
        GPSLOACTION,//位置信息
        VODE,//投票信息

    }

    private  String msgid;
    private String messageid;
    private MessageTypeEnum messageTypeEnum;
    private SpannableString msg;
    private String mediaid;
    private String RICHTXT;
    private String msgdate;
    private String sender;
    private String senderid;
    private Boolean IsSelf;
    private String ex1;
    private int msgMode ;
    private int msgSendState;

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getEx1() {
        return ex1;
    }

    public void setEx1(String ex1) {
        this.ex1 = ex1;
    }

    public int getMsgMode() {
        return msgMode;
    }

    public void setMsgMode(int msgMode) {
        this.msgMode = msgMode;
    }

    public int getMsgType() {
        switch (getMessageTypeEnum())
        {
            case TEXT:
                return  0;
            case PICTURE:
                return  1;
            case AUDIO:
                return  2;
        }
        return -1;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public int getMsgSendState() {
        return msgSendState;
    }

    public void setMsgSendState(int msgSendState) {
        this.msgSendState = msgSendState;
    }

    public Boolean getSelf() {
        return IsSelf;
    }

    public void setSelf(Boolean self) {
        IsSelf = self;
    }

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    public MessageTypeEnum getMessageTypeEnum() {
        return messageTypeEnum;
    }

    public void setMessageTypeEnum(MessageTypeEnum messageTypeEnum) {
        this.messageTypeEnum = messageTypeEnum;
    }

    public SpannableString getMsg() {
        return msg;
    }

    public void setMsg(String msg) {


//        SpannableString spannableString=new SpannableString(msg);
//
//        Pattern p = Pattern.compile("\\[\\!\\w*\\!\\]");
//        Matcher m = p.matcher(msg);
//        int startindex=0;
//        while (m.find())
//        {
//            Log.i("表情",m.group());
//            int i= msg.indexOf(m.group(),startindex);
//            String str = msg.substring(i,i+m.group().length());
//            try {
//                String emojifile = Emoji.stringMapemoji.get(str);
//                String emojipath = String.format("emoji/%1$s.png", emojifile);
//                InputStream inputStream = context.getAssets().open(emojipath);
//                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                ImageSpan imageSpan = new ImageSpan(context, bitmap);
//                spannableString.setSpan(imageSpan, i, i+m.group().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//            startindex = i + m.group().length();
//            Log.i("表情str",str);
//
//
//
//        }
        SpannableString spannableString = Emoji.getEmojistring(msg);
        this.msg = spannableString;
    }

    public String getMediaid() {
        return mediaid;
    }

    public void setMediaid(String mediaid) {
        this.mediaid = mediaid;
    }

    public String getRICHTXT() {
        return RICHTXT;
    }

    public void setRICHTXT(String RICHTXT) {
        this.RICHTXT = RICHTXT;
    }

    public String getMsgdateInit() {
        return msgdate;
    }
    public String getMsgdate() {
        return MessageInfo.GetSysTime(msgdate);
    }

    public void setMsgdate(String msgdate) {

        this.msgdate = msgdate;
    }
}
