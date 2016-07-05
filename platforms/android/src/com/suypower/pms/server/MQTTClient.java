package com.suypower.pms.server;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.suypower.pms.app.ReturnData;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.configxml.GlobalConfig;

import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Stereo on 16/5/9.
 */
public class MQTTClient {

//    static final String userName = "admin";
//    static final String passWord = "password";
    static final String myTopic = "test/topic";
    Topic[] topics;
    Topic topic;
    CallbackConnection callbackConnection;
    MQTT mqtt;
    MQTTCallBack mqttCallBack;


    Boolean IsConnected=false;


    public Boolean getConnected() {
        return IsConnected;
    }

    public MQTTClient(MQTTCallBack mqttCallBack)
    {
        mqtt = new MQTT();
        this.mqttCallBack=mqttCallBack;
        try {
            mqtt.setHost(GlobalConfig.globalConfig.getMqttServer());
        }
        catch (Exception e)
        {e.printStackTrace();}
        Log.i("UUID",SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_strUserId);
        mqtt.setClientId(SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_strUserId); //用于设置客户端会话的ID。在setCleanSession(false);被调用时，MQTT服务器利用该ID获得相应的会话。此ID应少于23个字符，默认根据本机地址、端口和时间自动生成
        mqtt.setCleanSession(true); //若设为false，MQTT服务器将持久化客户端会话的主体订阅和ACK位置，默认为true
        mqtt.setKeepAlive((short) 30);//定义客户端传来消息的最大时间间隔秒数，服务器可以据此判断与客户端的连接是否已经断开，从而避免TCP/IP超时的长时间等待
        mqtt.setUserName(GlobalConfig.globalConfig.getMqttUserName());//服务器认证用户名
        mqtt.setPassword(GlobalConfig.globalConfig.getMqttPwd());//服务器认证密码

        mqtt.setWillTopic(myTopic);//设置“遗嘱”消息的话题，若客户端与服务器之间的连接意外中断，服务器将发布客户端的“遗嘱”消息
        mqtt.setWillMessage("{\"clientID\":"+SuyApplication.getApplication().getUuid()+",\"status\":\"offline\"}");//设置“遗嘱”消息的内容，默认是长度为零的消息
        mqtt.setWillQos(QoS.AT_LEAST_ONCE);//设置“遗嘱”消息的QoS，默认为QoS.ATMOSTONCE
        mqtt.setWillRetain(false);//若想要在发布“遗嘱”消息时拥有retain选项，则为true
        mqtt.setVersion("3.1.1");
        //失败重连接设置说明
        mqtt.setConnectAttemptsMax(-1);//客户端首次连接到服务器时，连接的最大重试次数，超出该次数客户端将返回错误。-1意为无重试上限，默认为-1
        mqtt.setReconnectAttemptsMax(-1);//客户端已经连接到服务器，但因某种原因连接断开时的最大重试次数，超出该次数客户端将返回错误。-1意为无重试上限，默认为-1
        mqtt.setReconnectDelay(100L);//首次重连接间隔毫秒数，默认为10ms
        mqtt.setReconnectDelayMax(10000L);//重连接间隔毫秒数，默认为30000ms
        mqtt.setReconnectBackOffMultiplier(2);//设置重连接指数回归。设置为1则停用指数回归，默认为2
        //Socket设置说明
        mqtt.setReceiveBufferSize(65536);//设置socket接收缓冲区大小，默认为65536（64k）
        mqtt.setSendBufferSize(65536);//设置socket发送缓冲区大小，默认为65536（64k）
        mqtt.setTrafficClass(8);//设置发送数据包头的流量类型或服务类型字段，默认为8，意为吞吐量最大化传输
        //带宽限制设置说明
        mqtt.setMaxReadRate(0);//设置连接的最大接收速率，单位为bytes/s。默认为0，即无限制
        mqtt.setMaxWriteRate(0);//设置连接的最大发送速率，单位为bytes/s。默认为0，即无限制


        callbackConnection = mqtt.callbackConnection();

        callbackConnection.listener(listener);
        topics=new Topic[1];
        topic=  new Topic(SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_strUserId, QoS.EXACTLY_ONCE);
        topics[0]=topic;
    }


    //退出订阅
    public void exitPublictopic(String strtopic)
    {
        Topic topic1 = new Topic(strtopic, QoS.EXACTLY_ONCE);
        UTF8Buffer[] utf8Buffers=new UTF8Buffer[1];
        utf8Buffers[0] = topic1.name();
        callbackConnection.unsubscribe(utf8Buffers,null);
    }
    /**
     * 订阅公告频道
     */
    public void setPublictopic(String strtopic)
    {
        if (IsConnected) {
            Topic[] topic = new Topic[1];
            Topic topic1 = new Topic(strtopic, QoS.EXACTLY_ONCE);
            topic[0] = topic1;
            callbackConnection.subscribe(topic, new Callback<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    System.out.println("========群组订阅成功=======");
                    if (mqttCallBack != null)
                        mqttCallBack.OnStartReceiveGroup();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    System.out.println("========订阅失败=======");
                    if (mqttCallBack != null)
                        mqttCallBack.OnConnectServerFail();
                }
            });
        }
    }
    /**
     *
     * @param strlist
     */
    public void setGrouptopic(List<String> strlist)
    {
        if (IsConnected) {
            Topic[] topic = new Topic[strlist.size()];
            for (int i = 0; i < strlist.size(); i++) {
                Topic topic1 = new Topic(strlist.get(i), QoS.EXACTLY_ONCE);
                topic[i] = topic1;

            }
            callbackConnection.subscribe(topic, new Callback<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    System.out.println("========群组订阅成功=======");
                    if (mqttCallBack != null)
                        mqttCallBack.OnStartReceiveGroup();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    System.out.println("========订阅失败=======");
                    if (mqttCallBack != null)
                        mqttCallBack.OnConnectServerFail();
                }
            });
        }
    }

    /**
     * 发送消息
     * @param receiver
     * @param msgjson
     */
    public void sendMessage(String receiver,String msgjson,final IMQTTSendCallBack imqttSendCallBack)
    {
        callbackConnection.publish(receiver, msgjson.getBytes(), QoS.EXACTLY_ONCE, false, new Callback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                imqttSendCallBack.SendSuccess();
            }

            @Override
            public void onFailure(Throwable throwable) {
                imqttSendCallBack.Sendfail();
            }
        });
    }

    /**
     * 取消与服务器连接
     */
    public void disConnectServer()
    {

//        UTF8Buffer[] utf8Buffers=new UTF8Buffer[]{topic.name()};
//        callbackConnection.unsubscribe(utf8Buffers,null);
        callbackConnection.disconnect(null);
        mqttCallBack=null;
    }







    /**
     * 连接服务
     */
    public void connecetServer()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                callbackConnection.connect(new Callback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbackConnection.subscribe(topics, new Callback<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                System.out.println("========订阅成功=======");
                                Log.i("订阅1000", "1111111");
                                setPublictopic("10000");
                                if (mqttCallBack != null)
                                    mqttCallBack.OnStartReceive();
                            }
                            @Override
                            public void onFailure(Throwable throwable) {
                                System.out.println("========订阅失败=======");
                                if (mqttCallBack != null)
                                    mqttCallBack.OnConnectServerFail();
                            }
                        });
                    }
                    @Override
                    public void onFailure(Throwable throwable) {
                        if (mqttCallBack != null)
                            mqttCallBack.OnConnectServerFail();
                    }
                });
            }
        }).start();
    }


    /**
     * MQTT连接监控
     * 连接成功，失败，接收消息
     */
    Listener listener=new Listener() {
        @Override
        public void onConnected() {
            IsConnected=true;
            if (mqttCallBack != null)
                mqttCallBack.OnConnected();

        }

        @Override
        public void onDisconnected() {
            IsConnected=false;
            if (mqttCallBack != null)
                mqttCallBack.OnDisConnected();

        }

        @Override
        public void onPublish(org.fusesource.hawtbuf.UTF8Buffer utf8Buffer, org.fusesource.hawtbuf.Buffer buffer, Runnable runnable) {
            runnable.run();
            Log.i("UTF8Buffer ",new String(utf8Buffer.getData()));
            Log.i("收到的byte ",new String(buffer.getData()));
            String json = new String(buffer.toByteArray());
            try {
                JSONObject jsonObject = new JSONObject(json);
                final ReturnData returnData = new ReturnData(jsonObject, true);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (mqttCallBack != null)
                            mqttCallBack.OnMsgCallBack(returnData);
                    }
                }).start();

            }
            catch (Exception e)
            {e.printStackTrace();
                if (mqttCallBack != null)
                    mqttCallBack.OnMsgCallBack(buffer.toByteArray());
            }


        }

        @Override
        public void onFailure(Throwable throwable) {

        }
    };






    /**
     * mqtt回调
     */
    public interface MQTTCallBack
    {
        void OnMsgCallBack(byte[] buffer);//消息回调
        void OnMsgCallBack(ReturnData returnData);//消息回调
        void OnConnected();//连接成功
        void OnDisConnected();//连接断开
        void OnStartReceive();
        void OnStartReceiveGroup();
        void OnConnectServerFail();

    }


    public interface IMQTTSendCallBack
    {
        void SendSuccess();
        void Sendfail();
    }
}
