package com.suypower.pms.server;

import android.graphics.Color;

import com.suypower.pms.app.SuyApplication;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Stereo on 16/5/31.
 */
public class MsgBodyChat {

    private String id;
    private String msgid;
    private int msgmode;//01 单聊 02 群聊
    private String content;
    private String sendtime;
    private String msgtitle;
    private int msgtype;//01 文本
    private String sender;
    private String senderid;

    private int color;
    private JSONArray aList;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public JSONArray getaList() {
        return aList;
    }

    public void setaList(JSONArray aList) {
        this.aList = aList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public int getMsgmode() {
        return msgmode;
    }

    public void setMsgmode(int msgmode) {
        this.msgmode = msgmode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendtime() {
        return sendtime;
    }

    public void setSendtime(String sendtime) {
        this.sendtime = sendtime;
    }

    public String getMsgtitle() {
        return msgtitle;
    }

    public void setMsgtitle(String msgtitle) {
        this.msgtitle = msgtitle;
    }

    public int getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(int msgtype) {
        this.msgtype = msgtype;
    }


    public static MsgBodyChat decodeJson(JSONObject jsonObject) {
        MsgBodyChat msgBodyChat = new MsgBodyChat();
        try {

            if (jsonObject.getString("receiverGroupType").equals("01")) {
                //系统组，公告，待办事项
                msgBodyChat.setMsgid(jsonObject.getString("receiverGroupId"));
                msgBodyChat.setMsgtitle("公司公告");
                msgBodyChat.setMsgmode(3);
                msgBodyChat.setSender(jsonObject.getString("senderUserName"));
                msgBodyChat.setSenderid(jsonObject.getString("senderUserId"));


                JSONObject msgBody = jsonObject.getJSONObject("msgBody");
                msgBodyChat.setaList(msgBody.getJSONArray("aList"));
                msgBodyChat.setColor(Color.parseColor(msgBody.getString("color")));
                msgBodyChat.setMsgtype(Integer.valueOf(msgBody.getString("msgType")));
                msgBodyChat.setContent(msgBodyChat.getaList().getJSONObject(0).getString("title"));
                msgBodyChat.setId(jsonObject.getString("msgId"));
                msgBodyChat.setSendtime(jsonObject.getString("sendTime"));


            } else {
                //单聊
                if (jsonObject.isNull("receiverGroupType")) {
                    msgBodyChat.setMsgid(jsonObject.getString("senderUserId"));
                    msgBodyChat.setMsgtitle(jsonObject.getString("senderUserName"));
                    msgBodyChat.setMsgmode(1);
                    msgBodyChat.setSender(jsonObject.getString("senderUserName"));
                    msgBodyChat.setSenderid(jsonObject.getString("senderUserId"));
                }
                //群聊
                if (jsonObject.getString("receiverGroupType").equals("02")) {
                    msgBodyChat.setMsgid(jsonObject.getString("receiverGroupId"));
                    msgBodyChat.setMsgtitle(jsonObject.getString("receiverGroupName"));
                    msgBodyChat.setMsgmode(2);
                    msgBodyChat.setSender(jsonObject.getString("senderUserName"));
                    msgBodyChat.setSenderid(jsonObject.getString("senderUserId"));
                }
                JSONObject msgBody = jsonObject.getJSONObject("msgBody");
                String msgContent = msgBody.getString("content");
                msgBodyChat.setMsgtype(Integer.valueOf(msgBody.getString("msgType")));
                msgBodyChat.setContent(msgContent);
                msgBodyChat.setId(jsonObject.getString("msgId"));
                msgBodyChat.setSendtime(jsonObject.getString("sendTime"));
                if (msgBodyChat.getSenderid().equals(SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_strUserId))
                    return null;
            }

            return msgBodyChat;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
