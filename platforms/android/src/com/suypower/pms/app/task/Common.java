package com.suypower.pms.app.task;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.suypower.pms.app.Config;
import com.suypower.pms.app.ReturnData;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.SuyHttpClient;
import com.suypower.pms.app.configxml.GlobalConfig;
import com.suypower.pms.app.protocol.data.LoginResult;
import com.suypower.pms.view.contacts.ContactsDB;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Stereo on 16/4/15.
 */
public class Common extends BaseTask {


    public static final int GETDEPTS=0;


    InterfaceTask interfaceTask;
    int tasktype;

    public Common(InterfaceTask interfaceTask,int TaskType) {
        super();
        this.interfaceTask = interfaceTask;
        this.tasktype=TaskType;
    }


    @Override
    public void startTask() {

        m_ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    switch (tasktype)
                    {
                        case GETDEPTS:
                            getDepts();
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

    void getDepts() {

        String url;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();
            url = String.format("%1$sgroup/queryGroups",
                    GlobalConfig.globalConfig.getImUrl());
            Log.i("url", url);

            m_httpClient.openRequest(url, SuyHttpClient.REQ_METHOD_GET);

            Message message = handler.obtainMessage();
            if (!m_httpClient.sendRequest()) {
                message.what = CommonTask;
                message.arg2= GETDEPTS;
                message.arg1= FAILED;
                message.obj = "网络错误";
                handler.sendMessage(message);
                return;
            }

            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                message.what = CommonTask;
                message.arg2= GETDEPTS;
                message.arg1 = FAILED;
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
                if (returnData.getReturnCode() == 0) {





                    JSONArray jsonArray = returnData.getJsonArray();
                    ContactsDB contactsDB = new ContactsDB(SuyApplication.getApplication().getSuyDB().getDb());
                    contactsDB.delDepartment();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsondata = (JSONObject) jsonArray.get(i);
                        contactsDB.insertDepartment(jsondata);
                    }

                    message.what = CommonTask;
                    message.arg2= GETDEPTS;
                    message.arg1 = SUCCESS;

                    handler.sendMessage(message);
                    return;
                }
                message.what = CommonTask;
                message.arg2= GETDEPTS;
                message.arg1 = FAILED;
                message.obj = "获取部门信息失败";
                handler.sendMessage(message);

            } catch (Exception e) {
                e.printStackTrace();
                message.what = CommonTask;
                message.arg2= GETDEPTS;
                message.arg1 = FAILED;
                message.obj = e.getLocalizedMessage();
                handler.sendMessage(message);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();


        }
    }


}
