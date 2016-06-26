package com.suypower.pms.view.seeting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.suypower.pms.R;
import com.suypower.pms.app.ReturnData;
import com.suypower.pms.app.task.BaseTask;
import com.suypower.pms.app.task.Common;
import com.suypower.pms.app.task.InterfaceTask;
import com.suypower.pms.view.plugin.CustomPopWindowPlugin;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by cjw on 16/6/26.
 */
public class Passwordset extends Activity {

    private ImageView btnreturn, btnok;
    private TextView oldpwd,newpwd1,newpwd2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passwordset_activity);
        btnreturn = (ImageView) findViewById(R.id.btnreturn);
        btnreturn.setOnClickListener(onClickListenerreturn);
        btnok = (ImageView) findViewById(R.id.btnok);
        btnok.setOnClickListener(onClickListenerbtnok);
        oldpwd = (TextView)findViewById(R.id.oldpwd);
        newpwd1 = (TextView)findViewById(R.id.newpwd1);
        newpwd2 = (TextView)findViewById(R.id.newpwd2);


    }


    View.OnClickListener onClickListenerbtnok = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (oldpwd.getText().toString().equals("")||
                    newpwd1.getText().toString().equals("")||
                    newpwd2.getText().toString().equals("")
                    )
            {
                Toast.makeText(Passwordset.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newpwd1.getText().toString().equals(newpwd2.getText().toString()))
            {
                Toast.makeText(Passwordset.this,"新密码不一致",Toast.LENGTH_SHORT).show();
                return;
            }
            CustomPopWindowPlugin.ShowPopWindow(newpwd1,getLayoutInflater(),"更新密码");
            Common common=new Common(interfaceTask,Common.MODIPWD);
            common.oldpwd=oldpwd.getText().toString();
            common.newpwd=newpwd1.getText().toString();
            common.startTask();

        }
    };


    InterfaceTask interfaceTask=new InterfaceTask() {
        @Override
        public void TaskResultForMessage(Message message) {
            if (message.what== BaseTask.CommonTask)
            {
                CustomPopWindowPlugin.CLosePopwindow();
                if (message.arg2==Common.MODIPWD)
                {
                    if (message.arg1 == BaseTask.SUCCESS)
                    {
                        ReturnData returnData=(ReturnData) message.obj;
                        JSONObject jsonObject=returnData.getReturnData();
                        try {
                            if (jsonObject.getBoolean("flag")) {
                                Toast.makeText(Passwordset.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                                finish();
                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                return;
                            }
                            else
                            {
                                Toast.makeText(Passwordset.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e)
                        {e.printStackTrace();
                            Toast.makeText(Passwordset.this,message.obj.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(Passwordset.this,message.obj.toString(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    /**
     * 返回
     */
    View.OnClickListener onClickListenerreturn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        return super.onKeyUp(keyCode, event);
    }



}