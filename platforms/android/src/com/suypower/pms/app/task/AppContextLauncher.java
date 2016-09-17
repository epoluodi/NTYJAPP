package com.suypower.pms.app.task;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.suypower.pms.app.Config;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.SuyUserInfo;
import com.suypower.pms.view.LoginView;
import com.suypower.pms.view.MainTabView;
import com.suypower.pms.view.plugin.CustomPopWindowPlugin;
import com.suypower.pms.view.plugin.UpdatePlugin;

import org.json.JSONObject;

/**
 * 初始化
 * Created by Stereo on 16/5/27.
 */
public class AppContextLauncher {

    private Context context;
    private Login login;
    private String username, userpwd;
    private UpdatePlugin updatePlugin;
    private Intent intent;

    private AppLaucherCallback appLaucherCallback;


    public AppContextLauncher(Activity activity, final AppLaucherCallback appLaucherCallback) {
        this.appLaucherCallback = appLaucherCallback;
        context = activity;

        username = Config.getKeyShareVarForString(context, "username");
        userpwd = Config.getKeyShareVarForString(context, "userpwd");
        //判断是否有用户名和密码，才可以登录
        if (username.equals("null") || userpwd.equals("null")) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    appLaucherCallback.onInitOver(getLoginView());
                }
            }, 1000);
            return;
        }

        SuyUserInfo suyUserInfo = SuyApplication.getApplication().getSuyClient().getSuyUserInfo();
        suyUserInfo.userName = username;
        suyUserInfo.password = userpwd;

        /**
         * 更新检查
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                updatePlugin = new UpdatePlugin(context, new UpdatePlugin.IUpdateResult() {
                    @Override
                    public void CheckResult(int state, String msg) {

                    }

                    @Override
                    public void DownloadResult(int state, String msg) {
                        //更新
                        if (state == 1) {
                            updatePlugin.downloadAPK(UpdatePlugin.UPDATEURL);
                            Toast.makeText(SuyApplication.getApplication(),"正在下载请稍后",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //取消
                        if (state == 0 || state ==-1) {
                            login = new Login(interfaceTask);
                            login.startTask();

                        }
                    }
                });
                boolean r = updatePlugin.CheckVerison();
                if (r) {
                    try {
                        JSONObject jsonObject = (JSONObject) updatePlugin.getData();
                        Log.i("url", jsonObject.getString("versionNum"));
                        handler.sendEmptyMessage(0);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                login = new Login(interfaceTask);
                login.startTask();
            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                String _verstr = ((JSONObject) updatePlugin.getData()).getString("versionStr");
                if (((JSONObject) updatePlugin.getData()).getString("isForce").equals("1"))
                    updatePlugin.showAlertDialog(context, _verstr, true);
                else
                    updatePlugin.showAlertDialog(context, _verstr, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    InterfaceTask interfaceTask = new InterfaceTask() {
        @Override
        public void TaskResultForMessage(Message message) {
            login.stopTask();

            if (message.arg1 != BaseTask.SUCCESS) {
                Toast.makeText(context, message.obj.toString(), Toast.LENGTH_SHORT).show();
                appLaucherCallback.onInitOver(getLoginView());
                return;
            }
            downloadContacts();

        }
    };

    public void downloadContacts() {
        Common common = new Common(interfaceTask2, Common.GETDEPTS);
        common.startTask();
    }


    InterfaceTask interfaceTask2 = new InterfaceTask() {
        @Override
        public void TaskResultForMessage(Message message) {

            if (message.what == Common.CommonTask) {
                if (message.arg2 == Common.GETDEPTS) {
                    if (message.arg1 != BaseTask.SUCCESS) {

                        Toast.makeText(context, "获取部门信息失败，请重新登录", Toast.LENGTH_SHORT).show();
                        appLaucherCallback.onInitOver(getLoginView());
                        return;
                    }
                    try {
                        Contacts contacts = new Contacts(interfaceTask2);
                        contacts.startTask();
                    } catch (Exception e) {

                        Toast.makeText(context, "获取公司员工信息失败，请重新登录", Toast.LENGTH_SHORT).show();
                        appLaucherCallback.onInitOver(getLoginView());
                        e.printStackTrace();
                    }
                }
                return;
            }
            if (message.what == Common.ContactsTask) {
                if (message.arg2 == Contacts.GETCONTACTS) {
                    if (message.arg1 != BaseTask.SUCCESS) {

                        Toast.makeText(context, "获取公司员工信息失败，请重新登录", Toast.LENGTH_SHORT).show();
                        appLaucherCallback.onInitOver(getLoginView());
                        return;
                    }

                    try {

                        appLaucherCallback.onInitOver(getMainTabView());
                    } catch (Exception e) {

                        Toast.makeText(context, "获取公司员工信息失败，请重新登录", Toast.LENGTH_SHORT).show();
                        appLaucherCallback.onInitOver(getLoginView());
                        e.printStackTrace();
                    }
                }
                return;
            }
        }
    };

    /**
     * 返回登录view
     *
     * @return
     */
    private Intent getLoginView() {
        Intent intent = new Intent(context, LoginView.class);
        return intent;
    }

    /**
     * 返回Mainview
     *
     * @return
     */
    private Intent getMainTabView() {
        Intent intent = new Intent(context, MainTabView.class);
        return intent;
    }


    /**
     * 接口
     */
    public interface AppLaucherCallback {
        void onInitOver(Intent intent);
    }
}
