package com.suypower.pms.view.seeting;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.suypower.pms.R;
import com.suypower.pms.app.Config;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.server.ControlCenter;

/**
 * Created by Stereo on 16/4/16.
 */
public class NotificationConfigActivity extends Activity {

    Boolean disturb,msgdisturb;

    Switch switch1,switch2,switch3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otificationonfig_activity);
        disturb = Config.getKeyShareVarForBoolean(SuyApplication.getApplication(),"disturb");
        msgdisturb = Config.getKeyShareVarForBoolean(SuyApplication.getApplication(),"msgdisturb");

        switch1 = (Switch)findViewById(R.id.notificationswitch);
        switch1.setChecked(disturb);
        switch2 = (Switch)findViewById(R.id.zntxswitch);
        switch3 = (Switch)findViewById(R.id.msgtxswitch);
        switch3.setChecked(msgdisturb);


        switch1.setOnCheckedChangeListener(onCheckedChangeListener);
        switch2.setOnCheckedChangeListener(onCheckedChangeListener);
        switch3.setOnCheckedChangeListener(onCheckedChangeListener);
    }


    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            switch (compoundButton.getId())
            {
                case R.id.notificationswitch:
                    Config.setKeyShareVar(getApplicationContext(),"disturb",b);
                    ControlCenter.controlCenter.disturb = b;
                    break;
                case R.id.zntxswitch:
                    Toast.makeText(NotificationConfigActivity.this,"此功能暂不提供",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.msgtxswitch:
                    Config.setKeyShareVar(getApplicationContext(),"msgdisturb",b);
                    ControlCenter.controlCenter.msgdisturb = b;
                    break;
            }
        }
    };
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode==4) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }

        return super.onKeyUp(keyCode, event);
    }
}