package com.suypower.pms.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.suypower.pms.R;
import com.suypower.pms.app.Config;
import com.suypower.pms.app.ReturnData;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.SuyClient;
import com.suypower.pms.app.SuyHttpClient;
import com.suypower.pms.app.SuyUserInfo;
import com.suypower.pms.app.task.BaseTask;
import com.suypower.pms.app.task.Common;
import com.suypower.pms.app.task.Contacts;
import com.suypower.pms.app.task.InterfaceTask;
import com.suypower.pms.app.task.Login;
import com.suypower.pms.server.NotificationClass;
import com.suypower.pms.view.contacts.PhoneBookAdapter;
import com.suypower.pms.view.plugin.CustomPopWindowPlugin;


/**
 * Created by Stereo on 16/2/25.
 */
public class LoginView extends Activity {

    Button btnlogin;

    EditText username, password;
    Login login;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginview);

        btnlogin = (Button) findViewById(R.id.btnlogin);
        btnlogin.setOnClickListener(onClickListenerbtnlogin);

        username = (EditText) findViewById(R.id.txtuser);
        password = (EditText) findViewById(R.id.txtpwd);

        if (!Config.getKeyShareVarForString(LoginView.this, "username").equals("null"))
            username.setText(Config.getKeyShareVarForString(LoginView.this, "username"));
        if (!Config.getKeyShareVarForString(LoginView.this, "userpwd").equals("null"))
            password.setText(Config.getKeyShareVarForString(LoginView.this, "userpwd"));


        if (!username.equals("") && !password.equals("")) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onClickListenerbtnlogin.onClick(btnlogin);
                }
            }, 200);
        }
    }


    InterfaceTask interfaceTask = new InterfaceTask() {
        @Override
        public void TaskResultForMessage(Message message) {
            login.stopTask();

            if (message.arg1 != BaseTask.SUCCESS) {
                CustomPopWindowPlugin.CLosePopwindow();
                Toast.makeText(LoginView.this, message.obj.toString(), Toast.LENGTH_SHORT).show();
                return;
            }

            downloadContacts();

        }


    };

    View.OnClickListener onClickListenerbtnlogin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (username.getText().toString().equals("") || password.getText().toString().equals("")) {
                Toast.makeText(LoginView.this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                return;
            }
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(password.getWindowToken(), 0);
            CustomPopWindowPlugin.ShowPopWindow(btnlogin, getLayoutInflater(), "登录...");
            SuyUserInfo suyUserInfo = SuyApplication.getApplication().getSuyClient().getSuyUserInfo();
            suyUserInfo.userName = username.getText().toString();
            suyUserInfo.password = password.getText().toString();
            login = new Login(interfaceTask);
            login.startTask();
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
                        CustomPopWindowPlugin.CLosePopwindow();
                        Toast.makeText(LoginView.this, "获取部门信息失败，请重新登录", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {

                        Contacts contacts = new Contacts(interfaceTask2);
                        contacts.startTask();
                    } catch (Exception e) {
                        CustomPopWindowPlugin.CLosePopwindow();
                        Toast.makeText(LoginView.this, "获取公司员工信息失败，请重新登录", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }


                }
                return;
            }
            if (message.what == Common.ContactsTask) {
                if (message.arg2 == Contacts.GETCONTACTS) {
                    if (message.arg1 != BaseTask.SUCCESS) {
                        CustomPopWindowPlugin.CLosePopwindow();
                        Toast.makeText(LoginView.this, "获取公司员工信息失败，请重新登录", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        CustomPopWindowPlugin.CLosePopwindow();
                        Intent intent = new Intent(LoginView.this, MainTabView.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_to_bottom);
                        finish();
                    } catch (Exception e) {
                        CustomPopWindowPlugin.CLosePopwindow();
                        Toast.makeText(LoginView.this, "获取公司员工信息失败，请重新登录", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                return;
            }
        }
    };
}