package com.suypower.pms.view;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.suypower.pms.R;
import com.suypower.pms.app.Config;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.SuyService;
import com.suypower.pms.app.configxml.GlobalConfig;
import com.suypower.pms.server.ControlCenter;
import com.suypower.pms.server.NotificationClass;
import com.suypower.pms.server.StereoService;
import com.suypower.pms.server.StereoServiceMonitor;
import com.suypower.pms.view.contacts.ContactsSelectActivity;
import com.suypower.pms.view.plugin.CustomPopWindowPlugin;
import com.suypower.pms.view.plugin.chat.Emoji;
import com.suypower.pms.view.plugin.fragmeMager.FragmentMangerX;
import com.suypower.pms.view.plugin.fragmeMager.FragmentName;
import com.suypower.pms.view.plugin.scan.ScanActivity;
import com.suypower.pms.view.plugin.scan.ScanPlugin;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by Stereo on 16/2/23.
 */
public class MainTabView extends BaseActivityPlugin implements CordovaInterface {


    public static final String RECEIVERSTARTUPSERVER = "RESETSERVERBROADCAST";

    private ImageView imgtab1, imgtab2, imgtab3, imgtab4;

    private CDVWebviewfragment cdvWebviewfragment;
    private PhoneBookfragment phoneBookfragment;
    private MessageCenterfragment messageCenterfragment;
    private Fragment fragmentnow;
    private UserInfofragment userInfofragment;

    public static FragmentMangerX fragmentMangerX;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintabview);




        imgtab1 = (ImageView) findViewById(R.id.menu1_main_tabbar);
//        imgtab2 = (ImageView) findViewById(R.id.menu2_main_tabbar);
        imgtab3 = (ImageView) findViewById(R.id.menu3_main_tabbar);
        imgtab4 = (ImageView) findViewById(R.id.menu4_main_tabbar);

        imgtab1.setOnClickListener(onClickListenertab);
//        imgtab2.setOnClickListener(onClickListenertab);
        imgtab3.setOnClickListener(onClickListenertab);
        imgtab4.setOnClickListener(onClickListenertab);




        fragmentMangerX = new FragmentMangerX(getFragmentManager());
        intFragment();
//        ((FragmentName) cdvWebviewfragment).SetFragmentName("cdvWebviewfragment");
//        fragmentMangerX.AddFragment(cdvWebviewfragment, "cdvWebviewfragment");
//        fragmentMangerX.FragmentHide("cdvWebviewfragment");

        ((FragmentName) phoneBookfragment).SetFragmentName("PhoneBook");
        fragmentMangerX.AddFragment(phoneBookfragment, "PhoneBook");
        fragmentMangerX.FragmentHide("PhoneBook");

        ((FragmentName) userInfofragment).SetFragmentName("userInfofragment");
        fragmentMangerX.AddFragment(userInfofragment, "userInfofragment");
        fragmentMangerX.FragmentHide("userInfofragment");

        ((FragmentName) messageCenterfragment).SetFragmentName("messageCenterfragment");
        fragmentMangerX.AddFragment(messageCenterfragment, "messageCenterfragment");
        fragmentMangerX.ShowFragment("messageCenterfragment");


        fragmentnow = messageCenterfragment;


        imgtab1.setBackground(getResources().getDrawable(R.drawable.tab_message_down));
        Emoji.initemoji(this);


//        ControlCenter.controlCenter = new ControlCenter(this);
        if (GlobalConfig.globalConfig.getMessage() == 1) {
            Intent intent = new Intent(this, StereoService.class);
            intent.putExtra("mode", "1");
            startService(intent);
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        }
        NotificationClass.Clear_Notify();
        IntentFilter intentFilter=new IntentFilter(RECEIVERSTARTUPSERVER);
        registerReceiver(broadcastReceiver,intentFilter);
    }


    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("重新启动服务","------");
            Intent intent1 = new Intent(MainTabView.this, StereoService.class);
            intent1.putExtra("mode", "1");
            startService(intent1);
            serviceConnection=null;
            bindService(intent1, serviceConnection, BIND_AUTO_CREATE);
        }
    };
    /**
     * 服务
     */

    ServiceConnection serviceConnection=  new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ControlCenter.controlCenter = (ControlCenter)iBinder;
            ControlCenter.controlCenter.disturb  = Config.getKeyShareVarForBoolean(MainTabView.this,"disturb");
            ControlCenter.controlCenter.msgdisturb  = Config.getKeyShareVarForBoolean(MainTabView.this,"msgdisturb");
            ControlCenter.controlCenter.init(SuyApplication.getApplication()
                    .getSuyClient().getSuyUserInfo().m_loginResult.m_strSKey);
            ControlCenter.controlCenter.setIsRunAPP(true);
            ControlCenter.controlCenter.LoopMsgStart();
            ControlCenter.controlCenter.setIsNotification(true);
            messageCenterfragment.startIMessageControl();
        }


        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("APP unbind","退出");
            ControlCenter.controlCenter.setIsRunAPP(false);
            ControlCenter.controlCenter.LoopMsgStop();
            ControlCenter.controlCenter=null;
        }
    };


    void intFragment() {
        messageCenterfragment = new MessageCenterfragment();
//        cdvWebviewfragment = new CDVWebviewfragment(1);
        phoneBookfragment = new PhoneBookfragment();
        userInfofragment = new UserInfofragment();//加载用户信息
    }
    /**
     * 消息控制回调
     */
    ControlCenter.IMessageControl iMessageControl = new ControlCenter.IMessageControl() {
        @Override
        public void OnGetGroupList(int state) {

        }

        @Override
        public void OnNewMessage(String Message) {
            messageCenterfragment.refreshList();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("keycode",String.valueOf(keyCode));
        return super.onKeyDown(keyCode, event);
    }



    View.OnClickListener onClickListenertab = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            resetTabBackInmg();
            switch (view.getId()) {
                case R.id.menu1_main_tabbar:
                    imgtab1.setBackground(getResources().getDrawable(R.drawable.tab_message_down));
                    fragmentMangerX.FragmentHide(fragmentnow);
                    fragmentMangerX.ShowFragment(messageCenterfragment);
                    fragmentnow = messageCenterfragment;
                    messageCenterfragment.refreshList();
                    messageCenterfragment.startIMessageControl();
                    break;
//                case R.id.menu2_main_tabbar:
//                    imgtab2.setBackground(getResources().getDrawable(R.drawable.tab_work_down));
//                    fragmentMangerX.FragmentHide(fragmentnow);
//                    fragmentMangerX.ShowFragment(cdvWebviewfragment);
//                    fragmentnow = cdvWebviewfragment;
//
//                    messageCenterfragment.stopIMessageControl();
//                    break;
                case R.id.menu3_main_tabbar:
                    imgtab3.setBackground(getResources().getDrawable(R.drawable.tab_phonebook_down));
                    fragmentMangerX.FragmentHide(fragmentnow);
                    fragmentMangerX.ShowFragment(phoneBookfragment);
                    fragmentnow = phoneBookfragment;

                    messageCenterfragment.stopIMessageControl();
                    break;
                case R.id.menu4_main_tabbar:
                    imgtab4.setBackground(getResources().getDrawable(R.drawable.tab_me_down));
                    fragmentMangerX.FragmentHide(fragmentnow);
                    fragmentMangerX.ShowFragment(userInfofragment);
                    fragmentnow = userInfofragment;

                    messageCenterfragment.stopIMessageControl();
                    break;
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        if (ControlCenter.controlCenter != null) {
            if (fragmentnow == messageCenterfragment) {
                messageCenterfragment.startIMessageControl();
            }
            else
                messageCenterfragment.stopIMessageControl();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (messageCenterfragment !=null)
            messageCenterfragment.stopIMessageControl();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.i("---", "keyup");
        if (keyCode == 4) {
            ((FragmentName) fragmentnow).returnWeb();
        }
        return true;
    }

    void resetTabBackInmg() {
        imgtab1.setBackground(getResources().getDrawable(R.drawable.tab_message_normal));
//        imgtab2.setBackground(getResources().getDrawable(R.drawable.tab_work_normal));
        imgtab3.setBackground(getResources().getDrawable(R.drawable.tab_phonebook_normal));
        imgtab4.setBackground(getResources().getDrawable(R.drawable.tab_me_normal));
    }


    /**
     * 退出APP
     */
    public void FinishAPP()
    {
        finish();

    }
    @Override
    protected void onDestroy() {
//        cdvWebviewfragment.close();

        unregisterReceiver(broadcastReceiver);
        iMessageControl=null;
        ControlCenter.controlCenter.setiMessageControl(null);
        ControlCenter.controlCenter=null;
        unbindService(serviceConnection);
        serviceConnection=null;
        super.onDestroy();
    }

    /**
     * 注销用户
     */
    public void logoutUser()
    {
        Config.setKeyShareVar(getApplicationContext(),"userpwd","");
        Intent intent= new Intent(MainTabView.this,LoginView.class);
        ControlCenter.controlCenter.disconnecetMQTT();
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (((FragmentName)fragmentnow).GetFragmentName().equals("userInfofragment"))
        {
            ((UserInfofragment)fragmentnow).onActivityResult(requestCode,resultCode,data);
            return;
        }

        if (ScanActivity.SCANRESULTREQUEST == requestCode)//扫描
        {
            switch (resultCode) {
                case 1://结果
                 Toast.makeText(MainTabView.this,data.getExtras().getString("code"),Toast.LENGTH_SHORT).show();
                    break;
                case 0://没有结果
                    break;
            }
        }




    }

    @Override
    public void startActivityForResult(CordovaPlugin command, Intent intent, int requestCode) {

    }

    @Override
    public void setActivityResultCallback(CordovaPlugin plugin) {

    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public Object onMessage(String id, Object data) {


        if (id.equals("onProgressChanged")) {

            Map<String, Object> map = new Hashtable<String, Object>();
            map.put("id", id);
            map.put("data", data);
            Message message = new Message();
            message.obj = map;
            message.what = 0;

            ((FragmentName) fragmentnow).onMessage(message);

            return null;
        }

        if (data.getClass().getSimpleName().equals("Message")) {
            //异步不带返回调用

            Map<String, Object> map = new Hashtable<String, Object>();
            map.put("id", id);
            map.put("data", data);
            Message message = new Message();
            message.obj = map;
            message.what = 0;

            ((FragmentName) fragmentnow).onMessage(message);
        }

        if (data.getClass().getSuperclass().getSimpleName().equals("CordovaPlugin")) {
            //异步带返回调用

            Map<String, Object> map = new Hashtable<String, Object>();
            map.put("id", id);
            map.put("data", data);
            Message message = new Message();
            message.obj = map;
            message.what = 1;

            ((FragmentName) fragmentnow).onMessage(message);
        }


        return null;
    }

    @Override
    public ExecutorService getThreadPool() {
        return  threadPool;
    }
}