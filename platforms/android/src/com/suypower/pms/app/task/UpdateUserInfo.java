package com.suypower.pms.app.task;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.suypower.pms.app.ReturnData;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.SuyHttpClient;
import com.suypower.pms.app.configxml.GlobalConfig;
import com.suypower.pms.view.contacts.ContactsDB;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Stereo on 16/4/15.
 */
public class UpdateUserInfo extends BaseTask {

    public static final int UPDATENICKIMG=0;
    private InterfaceTask interfaceTask;
    private int tasktype;
    public String oldvalues,newvalue;



    public UpdateUserInfo(InterfaceTask interfaceTask,int TaskType) {
        super();
        this.interfaceTask = interfaceTask;
        tasktype = TaskType;
    }


    @Override
    public void startTask() {

        m_ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    switch (tasktype)
                    {
                        case 0:
                            updateNickimg();
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            interfaceTask.TaskResultForMessage(msg);

        }
    };

    @Override
    public void stopTask() {
        m_httpClient.closeRequest();
        m_httpClient = null;
        handler = null;
    }

    void

    updateNickimg() {

        String url;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            url = String.format("%1$suser/savePicture",
                    GlobalConfig.globalConfig.getAppUrl());
            Log.i("url", url);
            m_httpClient.openRequest(url, SuyHttpClient.REQ_METHOD_POST);
            m_httpClient.setPostValuesForKey("oldMediaId",oldvalues);
            m_httpClient.setPostValuesForKey("mediaId",newvalue);
            m_httpClient.setEntity(m_httpClient.getPostData());

            Message message = handler.obtainMessage();
            if (!m_httpClient.sendRequest()) {
                message.what = UPDATEUSERINFO;
                message.arg1= FAILED;
                message.arg2=UPDATENICKIMG;
                message.obj = "网络错误";
                handler.sendMessage(message);
                return;
            }
            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                message.what = UPDATEUSERINFO;
                message.arg1 = FAILED;
                message.arg2=UPDATENICKIMG;
                message.obj = "网络错误";
                handler.sendMessage(message);
                return;
            }
            String result = new String(buffer, "utf-8");
            Log.i("登陆信息返回:", result);
            JSONObject jsonObject = null;
            ReturnData returnData;
            try {
                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, false);
                if (returnData.getReturnCode() != 0) {

                    message.what = UPDATEUSERINFO;
                    message.arg1 = FAILED;
                    message.arg2=UPDATENICKIMG;
                    message.obj = "更新头像失败";
                    handler.sendMessage(message);

                    return;
                }
                message.what = UPDATEUSERINFO;
                message.arg1 = SUCCESS;
                message.arg2=UPDATENICKIMG;
                message.obj = newvalue;
                handler.sendMessage(message);

            } catch (Exception e) {
                e.printStackTrace();
                message.what = UPDATEUSERINFO;
                message.arg1 = FAILED;
                message.arg2=UPDATENICKIMG;
                message.obj = e.getLocalizedMessage();
                handler.sendMessage(message);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();


        }
    }







}
