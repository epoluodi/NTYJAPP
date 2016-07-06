package com.suypower.pms.app.task;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TabHost;

import com.suypower.pms.app.Config;
import com.suypower.pms.app.ReturnData;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.SuyHttpClient;
import com.suypower.pms.app.configxml.GlobalConfig;
import com.suypower.pms.app.protocol.data.LoginResult;
import com.suypower.pms.view.plugin.AjaxHttpPlugin;
import com.suypower.pms.view.plugin.chat.ChatDB;
import com.suypower.pms.view.plugin.chat.ChatMessage;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Stereo on 16/4/15.
 */
public class IM extends BaseTask {

    public static final int CREATEGROUP = 1;//创建群组
    public static final int SENDMSG = 2;//发送聊天内容
    public static final int QUERYGROUPINFO = 3;//群信息查询
    public static final int REMOVEGROUP = 4;//从群组里面退出
    public static final int ADDEXGROUP = 5;//增加成员
    public static final int QUERYAPPOVE = 6;//增加成员
    public static final int APPOVEMSG = 7;//审核信息
    public static final int READMSG = 8;//阅读反馈
    public static final int GETUSERSTATE = 9;//获得用户反馈数据


    int type;
    InterfaceTask interfaceTask;
    String params;
    ChatMessage chatMessage;
    List<NameValuePair> pairList;


    public void setPostValuesForKey(String Key, String value) {
        BasicNameValuePair basicNameValuePair = new BasicNameValuePair(Key, value);
        pairList.add(basicNameValuePair);


    }


    UrlEncodedFormEntity getPostData() {
        try {
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(pairList, HTTP.UTF_8);
            return urlEncodedFormEntity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 设置聊天信息
     *
     * @param chatMessage
     */
    public void setChatMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }


    public IM(InterfaceTask interfaceTask, int type) {
        super();
        this.interfaceTask = interfaceTask;
        this.type = type;
        pairList = new ArrayList<>();
    }


    public void setParams(String params) {
        this.params = params;
    }

    @Override
    public void startTask() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    switch (type) {
                        case CREATEGROUP:
                            createGorup();
                            break;
                        case SENDMSG:
                            sendMsg();
                            break;
                        case QUERYGROUPINFO:
                            queryGorup();
                            break;
                        case REMOVEGROUP:
                            removeGorup();
                            break;
                        case ADDEXGROUP:
                            addGorup();
                            break;
                        case QUERYAPPOVE:
                            queryappove();
                            break;
                        case APPOVEMSG:
                            appovemsg();
                            break;
                        case READMSG:
                            readmsg();
                            break;
                        case GETUSERSTATE:
                            getUserState();
                            break;

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (interfaceTask != null)
                interfaceTask.TaskResultForMessage(msg);

        }
    };


    @Override
    public void stopTask() {
        m_httpClient.closeRequest();
        m_httpClient = null;
        handler = null;
    }


    /**
     * 退出群组
     */
    void removeGorup() {
        Message message = handler.obtainMessage();
        String url;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            url = String.format("%1$sgroup/removeGroup",
                    GlobalConfig.globalConfig.getImUrl());
            m_httpClient.openRequest(url, SuyHttpClient.REQ_METHOD_POST);
            m_httpClient.setEntity(getPostData());
            Log.i("url", url);
            if (!m_httpClient.sendRequest()) {
                message.what = IMTask;
                message.arg1 = FAILED;
                message.arg2 = REMOVEGROUP;
                message.obj = "网络错误";
                handler.sendMessage(message);
                return;
            }

            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                message.what = IMTask;
                message.arg1 = FAILED;
                message.arg2 = REMOVEGROUP;
                message.obj = "网络错误";
                handler.sendMessage(message);
                return;
            }

            String result = new String(buffer, "utf-8");
            Log.i("信息返回:", result);
            JSONObject jsonObject = null;
            ReturnData returnData;
            try {
                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, true);
                if (returnData.getReturnCode() != 0) {

                    message.what = IMTask;
                    message.arg1 = REMOVEGROUP_FAIL;
                    message.arg2 = REMOVEGROUP;
                    message.obj = returnData.getReturnMsg();
                    handler.sendMessage(message);

                    return;
                }


            } catch (Exception e) {
                e.printStackTrace();

                message.what = IMTask;
                message.arg1 = FAILED;
                message.arg2 = REMOVEGROUP;
                message.obj = e.getLocalizedMessage();
                handler.sendMessage(message);
                return;
            }
            message.what = IMTask;
            message.arg1 = SUCCESS;
            message.arg2 = REMOVEGROUP;
            message.obj = returnData;
            handler.sendMessage(message);

        } catch (Exception e) {
            e.printStackTrace();
            message.what = IMTask;
            message.arg1 = FAILED;
            message.arg2 = CREATEGROUP;
            message.obj = e.getLocalizedMessage();
            handler.sendMessage(message);

        }
    }


    /**
     * 创建群组
     */
    void createGorup() {
        Message message = handler.obtainMessage();
        String url;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            url = String.format("%1$sgroup/add",
                    GlobalConfig.globalConfig.getImUrl());
            m_httpClient.openRequest(url, SuyHttpClient.REQ_METHOD_POST);
            m_httpClient.setPostValuesForKey("members", params);
            m_httpClient.setEntity(m_httpClient.getPostData());
            Log.i("url", url);
            if (!m_httpClient.sendRequest()) {
                message.what = IMTask;
                message.arg1 = FAILED;
                message.arg2 = CREATEGROUP;
                message.obj = "网络错误";
                handler.sendMessage(message);
                return;
            }

            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                message.what = IMTask;
                message.arg1 = FAILED;
                message.arg2 = CREATEGROUP;
                message.obj = "网络错误";
                handler.sendMessage(message);
                return;
            }

            String result = new String(buffer, "utf-8");
            Log.i("信息返回:", result);
            JSONObject jsonObject = null;
            ReturnData returnData;
            try {
                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, true);
                if (returnData.getReturnCode() != 0) {

                    message.what = IMTask;
                    message.arg1 = CREATEGROUP_FAIL;
                    message.arg2 = CREATEGROUP;
                    message.obj = returnData.getReturnMsg();
                    handler.sendMessage(message);

                    return;
                }


            } catch (Exception e) {
                e.printStackTrace();

                message.what = IMTask;
                message.arg1 = FAILED;
                message.arg2 = CREATEGROUP;
                message.obj = e.getLocalizedMessage();
                handler.sendMessage(message);
                return;
            }
            message.what = IMTask;
            message.arg1 = SUCCESS;
            message.arg2 = CREATEGROUP;
            message.obj = returnData;
            handler.sendMessage(message);

        } catch (Exception e) {
            e.printStackTrace();
            message.what = IMTask;
            message.arg1 = FAILED;
            message.arg2 = CREATEGROUP;
            message.obj = e.getLocalizedMessage();
            handler.sendMessage(message);

        }
    }

    void appovemsg() {
        Message message = handler.obtainMessage();
        String url;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            url = String.format("%1$smsg/approveDispatchMsg",
                    GlobalConfig.globalConfig.getImUrl());
            m_httpClient.openRequest(url, SuyHttpClient.REQ_METHOD_POST);
            m_httpClient.setEntity(getPostData());
            Log.i("url", url);
            if (!m_httpClient.sendRequest()) {
                message.what = IMTask;
                message.arg1 = FAILED;
                message.arg2 = APPOVEMSG;
                message.obj = "网络错误";
                handler.sendMessage(message);
                return;
            }

            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                message.what = IMTask;
                message.arg1 = FAILED;
                message.arg2 = APPOVEMSG;
                message.obj = "网络错误";
                handler.sendMessage(message);
                return;
            }

            String result = new String(buffer, "utf-8");
            Log.i("信息返回:", result);
            JSONObject jsonObject = null;
            ReturnData returnData;
            try {
                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, false);
                if (returnData.getReturnCode() != 0) {

                    message.what = IMTask;
                    message.arg1 = CREATEGROUP_FAIL;
                    message.arg2 = APPOVEMSG;
                    message.obj = returnData.getReturnMsg();
                    handler.sendMessage(message);

                    return;
                }


            } catch (Exception e) {
                e.printStackTrace();

                message.what = IMTask;
                message.arg1 = FAILED;
                message.arg2 = APPOVEMSG;
                message.obj = e.getLocalizedMessage();
                handler.sendMessage(message);
                return;
            }
            message.what = IMTask;
            message.arg1 = SUCCESS;
            message.arg2 = APPOVEMSG;
            message.obj = result;
            handler.sendMessage(message);

        } catch (Exception e) {
            e.printStackTrace();
            message.what = IMTask;
            message.arg1 = FAILED;
            message.arg2 = APPOVEMSG;
            message.obj = e.getLocalizedMessage();
            handler.sendMessage(message);

        }
    }

    void getUserState() {
        Message message = handler.obtainMessage();
        String url;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            url = String.format("%1$smsg/queryUsersByDispatchId/%2$S",
                    GlobalConfig.globalConfig.getImUrl(),params);
            m_httpClient.openRequest(url, SuyHttpClient.REQ_METHOD_POST);
            m_httpClient.setEntity(getPostData());
            Log.i("url", url);
            if (!m_httpClient.sendRequest()) {
                message.what = IMTask;
                message.arg1 = FAILED;
                message.arg2 = GETUSERSTATE;
                message.obj = "网络错误";
                handler.sendMessage(message);
                return;
            }

            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                message.what = IMTask;
                message.arg1 = FAILED;
                message.arg2 = GETUSERSTATE;
                message.obj = "网络错误";
                handler.sendMessage(message);
                return;
            }

            String result = new String(buffer, "utf-8");
            Log.i("信息返回:", result);
            JSONObject jsonObject = null;
            ReturnData returnData;
            try {
                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, false);
                if (returnData.getReturnCode() != 0) {

                    message.what = IMTask;
                    message.arg1 = CREATEGROUP_FAIL;
                    message.arg2 = GETUSERSTATE;
                    message.obj = returnData.getReturnMsg();
                    handler.sendMessage(message);

                    return;
                }


            } catch (Exception e) {
                e.printStackTrace();

                message.what = IMTask;
                message.arg1 = FAILED;
                message.arg2 = GETUSERSTATE;
                message.obj = e.getLocalizedMessage();
                handler.sendMessage(message);
                return;
            }
            message.what = IMTask;
            message.arg1 = SUCCESS;
            message.arg2 = GETUSERSTATE;
            message.obj = returnData;
            handler.sendMessage(message);

        } catch (Exception e) {
            e.printStackTrace();
            message.what = IMTask;
            message.arg1 = FAILED;
            message.arg2 = GETUSERSTATE;
            message.obj = e.getLocalizedMessage();
            handler.sendMessage(message);

        }
    }



    void readmsg() {
        Message message = handler.obtainMessage();
        String url;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            url = String.format("%1$smsg/readDispatchMsg",
                    GlobalConfig.globalConfig.getImUrl());
            m_httpClient.openRequest(url, SuyHttpClient.REQ_METHOD_POST);
            m_httpClient.setEntity(getPostData());
            Log.i("url", url);
            if (!m_httpClient.sendRequest()) {
                message.what = IMTask;
                message.arg1 = FAILED;
                message.arg2 = READMSG;
                message.obj = "网络错误";
                handler.sendMessage(message);
                return;
            }

            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                message.what = IMTask;
                message.arg1 = FAILED;
                message.arg2 = READMSG;
                message.obj = "网络错误";
                handler.sendMessage(message);
                return;
            }

            String result = new String(buffer, "utf-8");
            Log.i("信息返回:", result);
            JSONObject jsonObject = null;
            ReturnData returnData;
            try {
                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, true);
                if (returnData.getReturnCode() != 0) {

                    message.what = IMTask;
                    message.arg1 = CREATEGROUP_FAIL;
                    message.arg2 = READMSG;
                    message.obj = returnData.getReturnMsg();
                    handler.sendMessage(message);

                    return;
                }


            } catch (Exception e) {
                e.printStackTrace();

                message.what = IMTask;
                message.arg1 = FAILED;
                message.arg2 = READMSG;
                message.obj = e.getLocalizedMessage();
                handler.sendMessage(message);
                return;
            }
            message.what = IMTask;
            message.arg1 = SUCCESS;
            message.arg2 = READMSG;
            message.obj = result;
            handler.sendMessage(message);

        } catch (Exception e) {
            e.printStackTrace();
            message.what = IMTask;
            message.arg1 = FAILED;
            message.arg2 = READMSG;
            message.obj = e.getLocalizedMessage();
            handler.sendMessage(message);

        }
    }


    void queryappove() {
        Message message = handler.obtainMessage();
        String url;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            url = String.format("%1$smsg/queryDispatchMsgs",
                    GlobalConfig.globalConfig.getImUrl());
            m_httpClient.openRequest(url, SuyHttpClient.REQ_METHOD_GET);
            Log.i("url", url);
            if (!m_httpClient.sendRequest()) {
                message.what = IMTask;
                message.arg1 = FAILED;
                message.arg2 = QUERYAPPOVE;
                message.obj = "网络错误";
                handler.sendMessage(message);
                return;
            }

            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                message.what = IMTask;
                message.arg1 = FAILED;
                message.arg2 = QUERYAPPOVE;
                message.obj = "网络错误";
                handler.sendMessage(message);
                return;
            }

            String result = new String(buffer, "utf-8");
            Log.i("信息返回:", result);
            JSONObject jsonObject = null;
            ReturnData returnData;
            try {
                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, false);
                if (returnData.getReturnCode() != 0) {

                    message.what = IMTask;
                    message.arg1 = CREATEGROUP_FAIL;
                    message.arg2 = QUERYAPPOVE;
                    message.obj = returnData.getReturnMsg();
                    handler.sendMessage(message);

                    return;
                }


            } catch (Exception e) {
                e.printStackTrace();

                message.what = IMTask;
                message.arg1 = FAILED;
                message.arg2 = QUERYAPPOVE;
                message.obj = e.getLocalizedMessage();
                handler.sendMessage(message);
                return;
            }
            message.what = IMTask;
            message.arg1 = SUCCESS;
            message.arg2 = QUERYAPPOVE;
            message.obj = result;
            handler.sendMessage(message);

        } catch (Exception e) {
            e.printStackTrace();
            message.what = IMTask;
            message.arg1 = FAILED;
            message.arg2 = QUERYAPPOVE;
            message.obj = e.getLocalizedMessage();
            handler.sendMessage(message);

        }
    }


    /**
     * 增加成员到群组
     */
    void addGorup() {
        Message message = handler.obtainMessage();
        String url;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            url = String.format("%1$sgroup/addex",
                    GlobalConfig.globalConfig.getImUrl());
            JSONObject jsonObject1 = new JSONObject(params);
            m_httpClient.openRequest(url, SuyHttpClient.REQ_METHOD_POST);
            m_httpClient.setPostValuesForKey("members", jsonObject1.getString("members"));
            m_httpClient.setPostValuesForKey("groupid", jsonObject1.getString("groupid"));
            m_httpClient.setEntity(m_httpClient.getPostData());
            Log.i("url", url);
            if (!m_httpClient.sendRequest()) {
                message.what = IMTask;
                message.arg1 = FAILED;
                message.arg2 = ADDEXGROUP;
                message.obj = "网络错误";
                handler.sendMessage(message);
                return;
            }

            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                message.what = IMTask;
                message.arg1 = FAILED;
                message.arg2 = ADDEXGROUP;
                message.obj = "网络错误";
                handler.sendMessage(message);
                return;
            }

            String result = new String(buffer, "utf-8");
            Log.i("信息返回:", result);
            JSONObject jsonObject = null;
            ReturnData returnData;
            try {
                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, true);
                if (returnData.getReturnCode() != 0) {

                    message.what = IMTask;
                    message.arg1 = CREATEGROUP_FAIL;
                    message.arg2 = ADDEXGROUP;
                    message.obj = returnData.getReturnMsg();
                    handler.sendMessage(message);

                    return;
                }


            } catch (Exception e) {
                e.printStackTrace();

                message.what = IMTask;
                message.arg1 = FAILED;
                message.arg2 = ADDEXGROUP;
                message.obj = e.getLocalizedMessage();
                handler.sendMessage(message);
                return;
            }
            message.what = IMTask;
            message.arg1 = SUCCESS;
            message.arg2 = ADDEXGROUP;
            message.obj = returnData;
            handler.sendMessage(message);

        } catch (Exception e) {
            e.printStackTrace();
            message.what = IMTask;
            message.arg1 = FAILED;
            message.arg2 = ADDEXGROUP;
            message.obj = e.getLocalizedMessage();
            handler.sendMessage(message);

        }
    }

    /**
     * 群信息查询
     */
    void queryGorup() {
        Message message = handler.obtainMessage();
        String url;
        try {
            Thread.sleep(100);
            Looper.prepare();
            url = String.format("%1$smsg/queryDispatchMsg/%2$s",
                    GlobalConfig.globalConfig.getImUrl(), params);
            m_httpClient.openRequest(url, SuyHttpClient.REQ_METHOD_GET);
//            m_httpClient.setPostValuesForKey("groupId",params);
//            m_httpClient.setEntity(m_httpClient.getPostData());
            Log.i("url", url);
            if (!m_httpClient.sendRequest()) {
                message.what = IMTask;
                message.arg1 = FAILED;
                message.arg2 = QUERYGROUPINFO;
                message.obj = "网络错误";
                handler.sendMessage(message);
                return;
            }

            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                message.what = IMTask;
                message.arg1 = FAILED;
                message.arg2 = QUERYGROUPINFO;
                message.obj = "网络错误";
                handler.sendMessage(message);
                return;
            }

            String result = new String(buffer, "utf-8");
            Log.i("信息返回:", result);
            JSONObject jsonObject = null;
            ReturnData returnData;
            try {
                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, true);
                if (returnData.getReturnCode() != 0) {
                    message.what = IMTask;
                    message.arg1 = CREATEGROUP_FAIL;
                    message.arg2 = QUERYGROUPINFO;
                    message.obj = returnData.getReturnMsg();
                    handler.sendMessage(message);
                    return;
                }


            } catch (Exception e) {
                e.printStackTrace();
                message.what = IMTask;
                message.arg1 = FAILED;
                message.arg2 = CREATEGROUP;
                message.obj = e.getLocalizedMessage();
                handler.sendMessage(message);
                return;
            }
            message.what = IMTask;
            message.arg1 = SUCCESS;
            message.arg2 = QUERYGROUPINFO;
            message.obj = returnData;
            handler.sendMessage(message);

        } catch (Exception e) {
            e.printStackTrace();
            message.what = IMTask;
            message.arg1 = FAILED;
            message.arg2 = QUERYGROUPINFO;
            message.obj = e.getLocalizedMessage();
            handler.sendMessage(message);

        }
    }


    /**
     * 群信息查询
     */
    public static ReturnData getGroupidlist() {
        Message message = new Message();
        String url;
        AjaxHttpPlugin ajaxHttpPlugin = new AjaxHttpPlugin();
        SuyHttpClient m_httpClient = ajaxHttpPlugin.initHttp();
        try {
//            Looper.prepare();
            url = String.format("%1$sgroup/queryGroupsAndDispatchs",
                    GlobalConfig.globalConfig.getImUrl());
            m_httpClient.openRequest(url, SuyHttpClient.REQ_METHOD_GET);
            Log.i("url", url);
            if (!m_httpClient.sendRequest()) {
                return null;
            }
            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                return null;
            }
            String result = new String(buffer, "utf-8");
            Log.i("信息返回:", result);
            JSONObject jsonObject = null;
            ReturnData returnData;
            try {
                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, true);
                if (returnData.getReturnCode() != 0) {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return returnData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            m_httpClient.closeRequest();
            m_httpClient = null;
        }
    }


    /**
     * 发送消息
     */
    void sendMsg() {
        Message message = handler.obtainMessage();
        String url;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            url = String.format("%1$smsg/saveSessionMsg",
                    GlobalConfig.globalConfig.getImUrl());
            m_httpClient.openRequest(url, SuyHttpClient.REQ_METHOD_POST);

            switch (chatMessage.getMessageTypeEnum()) {
                case TEXT:
                    m_httpClient.setPostValuesForKey("msg_type", "01");
                    m_httpClient.setPostValuesForKey("msg_content", chatMessage.getMsg().toString());
                    break;
                case PICTURE:
                    m_httpClient.setPostValuesForKey("msg_type", "02");
                    m_httpClient.setPostValuesForKey("msg_content", chatMessage.getMediaid());
                    break;
                case AUDIO:
                    m_httpClient.setPostValuesForKey("msg_type", "03");
                    m_httpClient.setPostValuesForKey("msg_content", chatMessage.getMediaid());
                    break;
            }

            if (chatMessage.getMsgMode() == 1)
                m_httpClient.setPostValuesForKey("reciveUserId", chatMessage.getMessageid());
            if (chatMessage.getMsgMode() == 2)
                m_httpClient.setPostValuesForKey("dispatch_id", chatMessage.getMessageid());

            m_httpClient.setEntity(m_httpClient.getPostData());
            Log.i("url", url);
            if (!m_httpClient.sendRequest()) {
                chatMessage.setMsgSendState(0);
                ChatDB chatDB = new ChatDB(SuyApplication.getApplication().getSuyDB().getDb());
                chatDB.insertChatlog(chatMessage);
                message.what = IMTask;
                message.arg1 = FAILED;
                message.arg2 = SENDMSG;
                message.obj = "网络错误";
                handler.sendMessage(message);

                return;
            }

            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                chatMessage.setMsgSendState(0);
                ChatDB chatDB = new ChatDB(SuyApplication.getApplication().getSuyDB().getDb());
                chatDB.insertChatlog(chatMessage);
                message.what = IMTask;
                message.arg1 = FAILED;
                message.arg2 = SENDMSG;
                message.obj = "网络错误";
                handler.sendMessage(message);
                return;
            }

            String result = new String(buffer, "utf-8");
            Log.i("信息返回:", result);
            JSONObject jsonObject = null;
            ReturnData returnData;
            try {
                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, true);
                if (returnData.getReturnCode() != 0) {
                    chatMessage.setMsgSendState(0);
                    ChatDB chatDB = new ChatDB(SuyApplication.getApplication().getSuyDB().getDb());
                    chatDB.insertChatlog(chatMessage);
                    message.what = IMTask;
                    message.arg1 = SENDMSG_FAIL;
                    message.arg2 = SENDMSG;
                    message.obj = returnData.getReturnMsg();
                    handler.sendMessage(message);

                    return;
                }

                chatMessage.setMsgSendState(2);
                ChatDB chatDB = new ChatDB(SuyApplication.getApplication().getSuyDB().getDb());
                String msgid = returnData.getReturnData().getString("msgId");
                chatMessage.setMsgid(msgid);
                chatDB.insertChatlog(chatMessage);

            } catch (Exception e) {
                e.printStackTrace();
                chatMessage.setMsgSendState(0);
                ChatDB chatDB = new ChatDB(SuyApplication.getApplication().getSuyDB().getDb());
                chatDB.insertChatlog(chatMessage);
                message.what = IMTask;
                message.arg1 = FAILED;
                message.arg2 = SENDMSG;
                message.obj = e.getLocalizedMessage();
                handler.sendMessage(message);
                return;
            }

            message.what = IMTask;
            message.arg1 = SUCCESS;
            message.arg2 = SENDMSG;
            message.obj = returnData;
            handler.sendMessage(message);

        } catch (Exception e) {
            e.printStackTrace();
            message.what = IMTask;
            message.arg1 = FAILED;
            message.arg2 = SENDMSG;
            message.obj = e.getLocalizedMessage();
            handler.sendMessage(message);

        }
    }


}
