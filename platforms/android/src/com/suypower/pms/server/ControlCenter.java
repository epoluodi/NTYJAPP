package com.suypower.pms.server;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;

import com.suypower.pms.app.Config;
import com.suypower.pms.app.ReturnData;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.task.BaseTask;
import com.suypower.pms.app.task.IM;
import com.suypower.pms.app.task.InterfaceTask;
import com.suypower.pms.app.task.Login;
import com.suypower.pms.view.PublishNoticsActivity;
import com.suypower.pms.view.SplashActivity;
import com.suypower.pms.view.plugin.CommonPlugin;
import com.suypower.pms.view.plugin.chat.ChatActivity;
import com.suypower.pms.view.plugin.chat.ChatDB;
import com.suypower.pms.view.plugin.chat.ChatMessage;
import com.suypower.pms.view.plugin.message.MessageDB;
import com.suypower.pms.view.plugin.message.MessageDetailView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务中心控制类
 */
public class ControlCenter extends Binder {

    //服务全局
    private Context context;
    public static ControlCenter controlCenter = null;
    public static final long msgidoffet = 2803227000l;
    private String user = "";
    private String pwd = "";
    public Boolean disturb = false;//通知开关开关
    public Boolean msgdisturb = false;//消息免打扰开关
    private Boolean islogin = false;
    private Boolean isRunAPP = false;
    private String _token;
    public int mode = 0;
    private IMessageControl iMessageControl;//信息回调
    private NotificationClass notificationClass; //全局通知类
    private MQTTClient mqttClient;//mqtt 服务对象
    private Boolean isNotification = false;//是否通知

    public void publishNotics(String strtopic) {
        mqttClient.setPublictopic(strtopic);
    }

    public Boolean getIsNotification() {
        return isNotification;
    }

    public void setIsNotification(Boolean isNotification) {
        this.isNotification = isNotification;
    }

    public Boolean getIsRunAPP() {
        return isRunAPP;
    }

    public void setIsRunAPP(Boolean isRunAPP) {
        this.isRunAPP = isRunAPP;
    }

    public void setiMessageControl(IMessageControl iMessageControl) {
        this.iMessageControl = iMessageControl;

    }

    public ControlCenter(Context context) {
        this.context = context;
        notificationClass = new NotificationClass();
        notificationClass.Clear_Notify();
        disturb = Config.getKeyShareVarForBoolean(context, "disturb");
        msgdisturb = Config.getKeyShareVarForBoolean(context, "msgdisturb");
        setIsNotification(true);
    }


    public boolean init(String token) {


        user = Config.getKeyShareVarForString(SuyApplication.getApplication(), "username");
        pwd = Config.getKeyShareVarForString(SuyApplication.getApplication(), "userpwd");
        disturb = Config.getKeyShareVarForBoolean(SuyApplication.getApplication(), "disturb");


        Log.i("user", user);
        Log.i("pwd", pwd);

        SuyApplication.getApplication().getSuyClient().getSuyUserInfo().userName = user;
        SuyApplication.getApplication().getSuyClient().getSuyUserInfo().password = pwd;


        return true;
    }


    //设置Token
    public void setToken(String token) {
        _token = token;
    }

    public void LoopMsgStart() {
        if (!islogin) {

            if (mqttClient == null)
                mqttClient = new MQTTClient(mqttCallBack);
            if (!mqttClient.getConnected())
                mqttClient.connecetServer();
        }
    }

    public void LoopMsgStop() {

//        mqttClient.disConnectServer();

    }


    /**
     * 登陆上一次用户信息
     */
    public void login() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    if (pwd.equals(""))
                        return;
                    while (true) {
                        islogin = true;
                        //初始插件
                        Login login = new Login(interfaceTask);
                        String token = login.onlyLogin();
                        if (token.equals("")) {
                            islogin = false;
                            if (isRunAPP)
                                return;
                            Thread.sleep(10000);
                            continue;
                        }
                        Message message = handler.obtainMessage();
                        message.obj = token;
                        handler.sendMessage(message);
                        return;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    InterfaceTask interfaceTask = new InterfaceTask() {
        @Override
        public void TaskResultForMessage(Message message) {
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==2)
            {
                if (iMessageControl !=null)
                    iMessageControl.OnGetGroupList(0);
                return;
            }
            setToken((String) msg.obj);
            islogin = false;
            //执行登陆后启动轮询服务
            LoopMsgStart();


        }
    };


    MQTTClient.MQTTCallBack mqttCallBack = new MQTTClient.MQTTCallBack() {
        @Override
        public void OnMsgCallBack(byte[] buffer) {
            Log.i("接收到MQTT的数据", new String(buffer));

        }

        @Override
        public void OnMsgCallBack(ReturnData returnData) {
//消息回调
            MsgBodyChat msgBodyChat;
            ChatDB chatDB;
            MessageDB messageDB;
            ChatMessage chatMessage;

            switch (returnData.getReturnCode()) {
                case 0:


                    Log.i("msg 处理", CommonPlugin.GetSysTime());
                    JSONObject jsonObjectmsg;
                    jsonObjectmsg = returnData.getReturnData();
                    msgBodyChat = MsgBodyChat.decodeJson(jsonObjectmsg);


                    try {
                        messageDB = new MessageDB(SuyApplication.getApplication().getSuyDB().getDb());
                        chatDB = new ChatDB(SuyApplication.getApplication().getSuyDB().getDb());


                        if (messageDB.isExitsMsgid(msgBodyChat.getMsgid()) > 0) {
                            if (msgBodyChat.getMsgScope() != 1) {
                                int mark = messageDB.getMsgMark(msgBodyChat.getMsgid());
                                messageDB.updateMessageForServer(msgBodyChat, mark);
                            }
                        } else
                            messageDB.insertGroupForSigle(msgBodyChat);
                        if (msgBodyChat == null)
                            return;


                        //公告
                        if (msgBodyChat.getMsgScope() == 1) {



                            publishNotics(msgBodyChat.getMsgid());
                            notificationClass.add_Notification("调度信息", msgBodyChat.getMsgtitle(), msgBodyChat.getContent(),
                                    10000
                                    , getpendingIntentSys(msgBodyChat.getMsgid(), 3));
                            if (iMessageControl != null) {
                                Message message = msghandler.obtainMessage();
                                message.obj = jsonObjectmsg.toString();
                                msghandler.sendMessage(message);
                            }
                            return;
                        }

                        //聊天
                        if (msgBodyChat.getMsgmode() == 1 ||
                                msgBodyChat.getMsgmode() == 2) {
                            chatMessage = new ChatMessage();
                            chatMessage.setMsgSendState(1);
                            //文本
                            if (msgBodyChat.getMsgtype() == 1) {
                                chatMessage.setMsg(msgBodyChat.getContent());
                                chatMessage.setMessageTypeEnum(ChatMessage.MessageTypeEnum.TEXT);
                            }
                            //图片
                            if (msgBodyChat.getMsgtype() == 2) {
                                chatMessage.setMediaid(msgBodyChat.getContent());
                                chatMessage.setMessageTypeEnum(ChatMessage.MessageTypeEnum.PICTURE);
                            }
                            if (msgBodyChat.getMsgtype() == 3) {
                                chatMessage.setMediaid(msgBodyChat.getContent());
                                chatMessage.setMessageTypeEnum(ChatMessage.MessageTypeEnum.AUDIO);
                                chatMessage.setMsgSendState(1);
                            }
                            chatMessage.setMsgid(msgBodyChat.getId());
                            chatMessage.setSenderid(msgBodyChat.getSenderid());
                            chatMessage.setMessageid(msgBodyChat.getMsgid());
                            chatMessage.setSender(msgBodyChat.getSender());
                            chatMessage.setSelf(false);
                            chatMessage.setMsgdate(msgBodyChat.getSendtime());

                            if (msgBodyChat.getMsgmode() == 1)//单聊
                                chatMessage.setMsgMode(1);
                            if (msgBodyChat.getMsgmode() == 2)//群聊
                                chatMessage.setMsgMode(2);
                            chatDB.insertChatlog(chatMessage);
                        }

                        if (isNotification) {

                            if (disturb) //通知开关
                            {
                                if (msgBodyChat.getMsgmode() == 1 ||
                                        msgBodyChat.getMsgmode() == 2) {
                                    if (msgdisturb) //消息开关
                                    {
                                        if (messageDB.isExitsdisturblist(msgBodyChat.getMsgid(),
                                                SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_strUserId) == 0) {
                                            notificationClass = new NotificationClass();
                                            String t = "";
                                            switch (msgBodyChat.getMsgtype()) {
                                                case 1:
                                                    t = "文字信息";
                                                    break;
                                                case 2:
                                                    t = "图片信息";
                                                    break;
                                                case 3:
                                                    t = "语音信息";
                                                    break;
                                            }


                                            notificationClass.add_Notification(msgBodyChat.getMsgtitle() + "[" + t + "]", msgBodyChat.getMsgtitle(), t,
                                                    (int) (Long.valueOf(msgBodyChat.getMsgid()) - msgidoffet)
                                                    , getpendingIntent(msgBodyChat.getMsgid(), msgBodyChat.getMsgmode()));
                                        }
                                    }
                                }
                            }
                        }
                        if (iMessageControl != null) {
                            Message message = msghandler.obtainMessage();
                            message.obj = jsonObjectmsg.toString();
                            msghandler.sendMessage(message);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case 10002:
                    ControlCenter.controlCenter.LoopMsgStop();
                    try {
                        Thread.sleep(15000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    login();//重新登陆获取Token
                    break;
            }
        }


        @Override
        public void OnConnectServerFail() {
            //重新连接
            new Thread(checkMqttstate).start();
        }

        @Override
        public void OnStartReceive() {

        }

        @Override
        public void OnStartReceiveGroup() {

        }

        @Override
        public void OnConnected() {
            Log.i("接收到MQTT的数据", "连接成功============");
            new Thread(runnable).start();//获取群消息列表

        }

        @Override
        public void OnDisConnected() {
            Log.i("接收到MQTT的数据", "连接断开============");
            new Thread(checkMqttstate).start();
        }
    };


    /**
     * 循环获取 group信息
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                Boolean w = true;
                while (w) {
                    ReturnData returnData = IM.getGroupidlist();
                    if (returnData != null) {
                        JSONObject jsonObject = returnData.getReturnData();
                        JSONArray jsonArray = jsonObject.getJSONArray("groups");
                        if (jsonArray.length() == 0)
                            return;

                        List<String> strings = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            Log.i("群组编号", jsonObject1.getString("GROUP_ID"));
                            strings.add(jsonObject1.getString("GROUP_ID"));
                        }
                        mqttClient.setGrouptopic(strings);

                        jsonArray = jsonObject.getJSONArray("dispatchs");
                        if (jsonArray.length() == 0)
                            return;
                        strings.clear();
                        strings = new ArrayList<>();
                        MessageDB messageDB=new MessageDB(SuyApplication.getApplication().getSuyDB().getDb());
                        messageDB.deletejdinfo();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            Log.i("群组编号", jsonObject1.getString("DISPATCH_ID"));
                            strings.add(jsonObject1.getString("DISPATCH_ID"));
                            messageDB.insertjdinfo( jsonObject1.getString("DISPATCH_ID"),
                                    jsonObject1.getString("DISPATCH_TITLE"),
                                            jsonObject1.getString("IS_TOP"),
                                                    jsonObject1.getString("SEND_TIME"));

                        }
                        handler.sendEmptyMessage(2);
                        mqttClient.setGrouptopic(strings);
                        return;
                    }
                    Thread.sleep(3000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    public void disconnecetMQTT() {
        mqttClient.disConnectServer();
        mqttClient = null;
    }

    /***
     * 检查网络是否连接
     */
    Runnable checkMqttstate = new Runnable() {
        @Override
        public void run() {
            int i = 0;
            while (mqttClient.getConnected()) {
                i++;
                try {

                    if (i > 10) {
                        mqttClient.disConnectServer();
                        mqttClient = null;
                        mqttClient = new MQTTClient(mqttCallBack);
                        mqttClient.connecetServer();

                        Thread.sleep(5000);
                    } else
                        Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    Handler msghandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            iMessageControl.OnNewMessage(msg.obj.toString());
        }
    };


    public void sendNotification(int msgmode, String name, String content, String sender, int id) {
        notificationClass = new NotificationClass();

        notificationClass.add_Notification("运营平台消息通知", "来自:" + name, "一条聊天信息",
                id, getpendingIntent(sender, msgmode));
    }

    PendingIntent getpendingIntent(String userid, int msgMode) {

        //        Intent notificationIntent = new Intent(Common.Appcontext, barcode.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(Common.Appcontext, 0,
//                notificationIntent, 0);
        PendingIntent contentIntent = null;
        Intent notificationIntent;
        if (this.getIsRunAPP()) {
            notificationIntent = new Intent(SuyApplication.getApplication(), ChatActivity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            notificationIntent.putExtra("msgid", userid);
            if (msgMode == 1)
                notificationIntent.putExtra("chattype", "1");
            if (msgMode == 2)
                notificationIntent.putExtra("chattype", "2");
            contentIntent = PendingIntent.getActivity(SuyApplication.getApplication()
                    , msgMode, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            notificationIntent = new Intent(SuyApplication.getApplication(),
                    SplashActivity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            contentIntent = PendingIntent.getActivity(SuyApplication.getApplication()
                    , 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return contentIntent;
    }


    PendingIntent getpendingIntentSys(String userid, int msgMode) {

        //        Intent notificationIntent = new Intent(Common.Appcontext, barcode.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(Common.Appcontext, 0,
//                notificationIntent, 0);
        PendingIntent contentIntent = null;
        Intent notificationIntent;
        if (this.getIsRunAPP()) {
            notificationIntent = new Intent(SuyApplication.getApplication(), ChatActivity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            notificationIntent.putExtra("msgid", userid);
            notificationIntent.putExtra("chattype", msgMode);
            contentIntent = PendingIntent.getActivity(SuyApplication.getApplication()
                    , msgMode, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            notificationIntent = new Intent(SuyApplication.getApplication(),
                    SplashActivity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            contentIntent = PendingIntent.getActivity(SuyApplication.getApplication()
                    , 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return contentIntent;
    }


    public interface IMessageControl {
        void OnNewMessage(String Message);

        void OnGetGroupList(int state);
    }

}
