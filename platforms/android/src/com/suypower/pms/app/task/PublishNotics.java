package com.suypower.pms.app.task;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.suypower.pms.app.ReturnData;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.SuyHttpClient;
import com.suypower.pms.app.configxml.GlobalConfig;
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

/**
 * Created by Stereo on 16/4/15.
 */
public class PublishNotics extends BaseTask {

    public static final int SENDNOTICS =1;//发布公告
    public static final int QUERYNOTICS =2;//查询



    int type;
    InterfaceTask interfaceTask;
    String params;
    List<NameValuePair> pairList;




    public void setPostValuesForKey(String Key,String value)
    {
        BasicNameValuePair basicNameValuePair = new BasicNameValuePair(Key,value);
        pairList.add(basicNameValuePair);


    }


    UrlEncodedFormEntity getPostData()
    {
        try {
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(pairList, HTTP.UTF_8);
            return urlEncodedFormEntity;
        }
        catch (Exception e)
        {e.printStackTrace();}
        return null;
    }


    public PublishNotics(InterfaceTask interfaceTask, int type) {
        super();
        this.interfaceTask = interfaceTask;
        this.type=type;
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
                    switch (type)
                    {
                        case SENDNOTICS:
                            SendNotics();
                            break;
                        case QUERYNOTICS:
                            QueryNotics();
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
            if (interfaceTask !=null)
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
    private void SendNotics() {
        Message message = handler.obtainMessage();
        String url;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            url = String.format("%1$smsg/saveDispatchMsg",
                    GlobalConfig.globalConfig.getImUrl());
            m_httpClient.openRequest(url, SuyHttpClient.REQ_METHOD_POST);
            m_httpClient.setEntity(getPostData());
            Log.i("url", url);
            if (!m_httpClient.sendRequest()) {
                message.what = PublishNotics;
                message.arg1= FAILED;
                message.arg2=SENDNOTICS;
                message.obj = "网络错误";
                handler.sendMessage(message);
                return;
            }

            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                message.what = PublishNotics;
                message.arg1 = FAILED;
                message.arg2=SENDNOTICS;
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

                    message.what = PublishNotics;
                    message.arg1 = REMOVEGROUP_FAIL;
                    message.arg2=SENDNOTICS;
                    message.obj = returnData.getReturnMsg();
                    handler.sendMessage(message);

                    return;
                }



            } catch (Exception e) {
                e.printStackTrace();

                message.what = PublishNotics;
                message.arg1 = FAILED;
                message.arg2=SENDNOTICS;
                message.obj = e.getLocalizedMessage();
                handler.sendMessage(message);
                return;
            }
            message.what = PublishNotics;
            message.arg1 = SUCCESS;
            message.arg2=SENDNOTICS;
            message.obj = returnData;
            handler.sendMessage(message);

        } catch (Exception e) {
            e.printStackTrace();
            message.what = PublishNotics;
            message.arg1 = FAILED;
            message.arg2=SENDNOTICS;
            message.obj = e.getLocalizedMessage();
            handler.sendMessage(message);

        }
    }


    /**
     * 退出群组
     */
    private void QueryNotics() {
        Message message = handler.obtainMessage();
        String url;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            url = String.format("%1$smsg/queryDispatchMsg/%2$s",
                    GlobalConfig.globalConfig.getImUrl(),params);
            m_httpClient.openRequest(url, SuyHttpClient.REQ_METHOD_GET);
            Log.i("url", url);
            if (!m_httpClient.sendRequest()) {
                message.what = PublishNotics;
                message.arg1= FAILED;
                message.arg2=QUERYNOTICS;
                message.obj = "网络错误";
                handler.sendMessage(message);
                return;
            }

            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                message.what = PublishNotics;
                message.arg1 = FAILED;
                message.arg2=QUERYNOTICS;
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

                    message.what = PublishNotics;
                    message.arg1 = REMOVEGROUP_FAIL;
                    message.arg2=QUERYNOTICS;
                    message.obj = returnData.getReturnMsg();
                    handler.sendMessage(message);

                    return;
                }



            } catch (Exception e) {
                e.printStackTrace();

                message.what = PublishNotics;
                message.arg1 = FAILED;
                message.arg2=QUERYNOTICS;
                message.obj = e.getLocalizedMessage();
                handler.sendMessage(message);
                return;
            }
            message.what = PublishNotics;
            message.arg1 = SUCCESS;
            message.arg2=QUERYNOTICS;
            message.obj = returnData;
            handler.sendMessage(message);

        } catch (Exception e) {
            e.printStackTrace();
            message.what = PublishNotics;
            message.arg1 = FAILED;
            message.arg2=QUERYNOTICS;
            message.obj = e.getLocalizedMessage();
            handler.sendMessage(message);

        }
    }




}
