package com.suypower.pms.server;

import android.graphics.Color;

import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.view.plugin.CommonPlugin;
import com.suypower.pms.view.plugin.message.MessageDB;

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
    private String approve_account_id;
    private int MsgScope;
    private JSONArray aList;
    private JSONObject jsonObject;

    public int getMsgScope() {
        return MsgScope;
    }

    public void setMsgScope(int msgScope) {
        MsgScope = msgScope;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getApprove_account_id() {
        return approve_account_id;
    }

    public void setApprove_account_id(String approve_account_id) {
        this.approve_account_id = approve_account_id;
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
            JSONObject optdata;


            if (jsonObject.getString("scope").equals("system")) {

                JSONObject msgBody = jsonObject.getJSONObject("msgBody");
                msgBodyChat.setMsgtype(Integer.valueOf(msgBody.getString("optCode")));

                //创建
                if (msgBodyChat.getMsgtype()==1) {

                    MessageDB messageDB = new MessageDB(SuyApplication.getApplication().getSuyDB().getDb());


                    msgBodyChat.setMsgScope(1);
                    optdata = msgBody.getJSONObject("optData");
                    //系统组，公告，待办事项
                    msgBodyChat.setMsgid(optdata.getString("dispatch_id"));
                    if (messageDB.isExitsjd(msgBodyChat.getMsgid()))
                        return null;
                    msgBodyChat.setMsgtitle(optdata.getString("dispatch_title"));
                    msgBodyChat.setMsgmode(3);
                    msgBodyChat.setContent(optdata.getString("dispatch_content"));
//                    msgBodyChat.setApprove_account_id(optdata.getString("approve_account_id"));
                    msgBodyChat.setSendtime(CommonPlugin.GetSysTime());

                    messageDB.insertjdinfo(optdata.getString("dispatch_id"),
                            optdata.getString("dispatch_title"),
                            optdata.getString("is_top"),
                            jsonObject.getString("sendTime"),
                            ""//todo 增加人
                            );
                    return msgBodyChat;
                }

                //提醒重发
                if (msgBodyChat.getMsgtype()==6)
                {
                    msgBodyChat.setMsgScope(6);
                    msgBodyChat.setMsgmode(3);
                    optdata = msgBody.getJSONObject("optData");
                    msgBodyChat.setMsgtitle(optdata.getString("dispatch_title"));
                    msgBodyChat.setContent(optdata.getString("dispatch_content"));
                    msgBodyChat.setSendtime(CommonPlugin.GetSysTime());
                    return msgBodyChat;

                }
                //审批
                if (msgBodyChat.getMsgtype()==2)
                {
                    msgBodyChat.setMsgScope(2);
                    msgBodyChat.setMsgmode(3);
                    optdata = msgBody.getJSONObject("optData");
                    msgBodyChat.setMsgtitle(optdata.getString("dispatch_title"));
                    msgBodyChat.setContent(optdata.getString("dispatch_content"));
                    msgBodyChat.setSendtime(CommonPlugin.GetSysTime());
                    return msgBodyChat;

                }
                //审批拒绝
                if (msgBodyChat.getMsgtype()==5)
                {
                    msgBodyChat.setMsgScope(5);
                    msgBodyChat.setMsgmode(3);
                    optdata = msgBody.getJSONObject("optData");
                    msgBodyChat.setMsgtitle(optdata.getString("approve_user_name"));
                    msgBodyChat.setContent(optdata.getString("approve_desc"));
                    return msgBodyChat;

                }
                //关闭调度
                if (msgBodyChat.getMsgtype()==3)
                {
                    msgBodyChat.setMsgScope(3);
                    msgBodyChat.setMsgmode(3);
                    optdata = msgBody.getJSONObject("optData");
                    msgBodyChat.setMsgtitle(optdata.getString("dispatch_title"));
                    msgBodyChat.setSender(optdata.getString("send_user_name"));

                    return msgBodyChat;

                }


            } else {
                //单聊
//                if (jsonObject.isNull("receiverGroupType")) {
//                    msgBodyChat.setMsgid(jsonObject.getString("senderUserId"));
//                    msgBodyChat.setMsgtitle(jsonObject.getString("senderUserName"));
//                    msgBodyChat.setMsgmode(1);
//                    msgBodyChat.setSender(jsonObject.getString("senderUserName"));
//                    msgBodyChat.setSenderid(jsonObject.getString("senderUserId"));
//                }
                //群聊
//                if (jsonObject.getString("receiverGroupType").equals("02")) {
//
//                }
                msgBodyChat.setMsgScope(10);
                msgBodyChat.setMsgid(jsonObject.getString("receiverDispatchId"));
                msgBodyChat.setMsgtitle("");
                msgBodyChat.setMsgmode(2);
                msgBodyChat.setSender(jsonObject.getString("sendUserName"));
                msgBodyChat.setSenderid(jsonObject.getString("sendAccountId"));
                msgBodyChat.setMsgtitle(jsonObject.getString("receiverDispatchTitle"));

                JSONObject msgBody = jsonObject.getJSONObject("msgBody");
                String msgContent = msgBody.getString("content");
                msgBodyChat.setMsgtype(Integer.valueOf(msgBody.getString("msgType")));
                msgBodyChat.setContent(msgContent);
                msgBodyChat.setId("");
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
