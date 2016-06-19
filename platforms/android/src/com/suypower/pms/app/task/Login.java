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

import org.apache.cordova.CordovaWebView;
import org.json.JSONObject;

/**
 * Created by Stereo on 16/4/15.
 */
public class Login extends BaseTask {


    InterfaceTask interfaceTask;

    public Login(InterfaceTask interfaceTask) {
        super();
        this.interfaceTask = interfaceTask;
    }


    @Override
    public void startTask() {
        m_ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    doTask();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    Handler handler = new Handler(SuyApplication.getApplication().getMainLooper()) {
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

    public String onlyLogin()
    {
        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
//            SuyApplication.getApplication().getMainLooper().prepare();


            loginurl = String.format("%1$sgetToken?username=%2$s&password=%3$s",
                    GlobalConfig.globalConfig.getAuthUrl(),
                    (suyUserInfo.userName),
                    (suyUserInfo.password));
            Log.i("loginurl", loginurl);

            m_httpClient.openRequest(loginurl, SuyHttpClient.REQ_METHOD_GET);


            if (!m_httpClient.sendRequest()) {
                return "";
            }

            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {

                return "";
            }

            String result = new String(buffer, "utf-8");
            Log.i("登陆信息返回:", result);
            JSONObject jsonObject = null;
            ReturnData returnData;
            try {
                //解析json
                jsonObject = new JSONObject(result);
                returnData = new ReturnData(jsonObject, true);
                if (returnData.getReturnCode() != 0) {
                    return "";
                }

                LoginResult loginResult = new LoginResult();
                JSONObject ajax_data = returnData.getReturnData();
                loginResult.m_strSKey = ajax_data.getString("token");
//                CordovaWebView.token = loginResult.m_strSKey;
//                CordovaWebView.deviceid = SuyApplication.getApplication()
//                        .getUuid();
                JSONObject user = ajax_data.getJSONObject("user");
                JSONObject mqtt = ajax_data.getJSONObject("MQTT");
                loginResult.m_strDeparmentNo = user.getString("deptNo");
                loginResult.m_strDeparment = user.getString("deptName");
                loginResult.m_strEmail = user.isNull("email")?"":user.getString("email");
                loginResult.m_strMobile = user.getString("mobile");
                loginResult.m_strUserName = user.getString("name");
                loginResult.m_strUserId = user.getString("userID");
                loginResult.m_userType =Integer.valueOf(user.getString("usertype"));
                suyUserInfo.m_loginResult = loginResult;
                //获取mqtt 信息
                GlobalConfig.globalConfig.setMqttUserName(mqtt.getString("username"));
                GlobalConfig.globalConfig.setMqttPwd(mqtt.getString("password"));
                GlobalConfig.globalConfig.setMqttServer(mqtt.getString("mqttserver"));


                return loginResult.m_strSKey;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    void doTask() {

        String loginurl;
        try {
            //登录成功后，需要启动消息轮询机制
            Looper.prepare();







            loginurl = String.format("%1$sgetToken?username=%2$s&password=%3$s",
                    GlobalConfig.globalConfig.getAuthUrl(),
                    (suyUserInfo.userName),
                    (suyUserInfo.password));

//            DesEncrypter.encrypt(suyUserInfo.userName),
//                    DesEncrypter.encrypt(suyUserInfo.password)

            Log.i("loginurl", loginurl);

            m_httpClient.openRequest(loginurl, SuyHttpClient.REQ_METHOD_GET);

            Message message = handler.obtainMessage();
            if (!m_httpClient.sendRequest()) {
                message.what = LoginTask;
                message.arg1= FAILED;
                message.obj = "网络错误";
                handler.sendMessage(message);
                return;
            }

            byte[] buffer = m_httpClient.getRespBodyData();
            if (buffer == null) {
                message.what = LoginTask;
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
                returnData = new ReturnData(jsonObject, true);
                if (returnData.getReturnCode() != 0) {

                    message.what = LoginTask;
                    message.arg1 = PASSWORD_ERROR;
                    message.obj = returnData.getReturnMsg();
                    handler.sendMessage(message);

                    return;
                }

                Config.setKeyShareVar(SuyApplication.getApplication(), "username", suyUserInfo.userName);
                Config.setKeyShareVar(SuyApplication.getApplication(), "userpwd", suyUserInfo.password);

                LoginResult loginResult = new LoginResult();
                JSONObject ajax_data = returnData.getReturnData();
                loginResult.m_strSKey = ajax_data.getString("token");
//                CordovaWebView.token = loginResult.m_strSKey;
//                CordovaWebView.deviceid = SuyApplication.getApplication()
//                        .getUuid();
                JSONObject user = ajax_data.getJSONObject("user");
                JSONObject mqtt = ajax_data.getJSONObject("MQTT");
//                loginResult.m_strDeparmentNo = user.getString("deptNo");
//                loginResult.m_strDeparment = user.getString("deptName");
//                loginResult.m_strEmail = user.isNull("email")?"":user.getString("email");
                loginResult.m_strSex = user.getString("sex");
                loginResult.m_position=user.getString("positionId");
                loginResult.m_positionName=user.getString("positionName");
                loginResult.m_strMobile = user.getString("tel");
                loginResult.m_strloginname = user.getString("sysUserName");
                loginResult.m_strUserName = user.getString("userName");
                loginResult.m_strUserId = user.getString("accountId");
                loginResult.m_strPhoto = user.isNull("picture")?"":user.getString("picture");
//                loginResult.m_userType =Integer.valueOf(user.getString("usertype"));
                //获取mqtt 信息
                GlobalConfig.globalConfig.setMqttUserName(mqtt.getString("username"));
                GlobalConfig.globalConfig.setMqttPwd(mqtt.getString("password"));
                GlobalConfig.globalConfig.setMqttServer(mqtt.getString("mqttserver"));


                suyUserInfo.m_loginResult = loginResult;



            } catch (Exception e) {
                e.printStackTrace();

                message.what = LoginTask;
                message.arg1 = FAILED;
                message.obj = e.getLocalizedMessage();
                handler.sendMessage(message);
                return;
            }
            message.what = LoginTask;
            message.arg1 = SUCCESS;
            message.obj = "登录成功";
            handler.sendMessage(message);

        } catch (Exception e) {
            e.printStackTrace();


        }
    }


}
