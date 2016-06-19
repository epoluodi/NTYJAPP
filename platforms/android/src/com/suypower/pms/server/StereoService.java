package com.suypower.pms.server;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.suypower.pms.view.plugin.CommonPlugin;


public class StereoService extends Service {

    /**
     * 消息控制处理类
     */
    IBinder controlCenter ;
    String mode = "0";

    private Boolean iswhile= true;
    public static final String ClassName = StereoService.class.getName();


    @Override
    public void onCreate() {
        super.onCreate();

        NotificationClass.Clear_Notify();
        controlCenter = new ControlCenter(getApplicationContext());
        new Thread(runnable).start();
        Log.i("消息服务", "启动");
    }

    Runnable runnable=new Runnable() {
        @Override
        public void run() {

            try
            {
                while (iswhile)
                {
                    Thread.sleep(5000);
                    Boolean r = CommonPlugin.isCoreServiceRunning(StereoServiceMonitor.ClassName);
                    if (!r)
                    {
                        Intent intent2 = new Intent(StereoService.this, StereoServiceMonitor.class);
                        startService(intent2);
                    }
                }
            }
            catch (Exception e)
            {e.printStackTrace();}
        }
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if (intent == null )
        {
            Log.i("消息服务", "独立启动");

            ControlCenter.controlCenter=(ControlCenter)controlCenter;
            ControlCenter.controlCenter.init("");
            ControlCenter.controlCenter.login();
            return START_STICKY;
        }

        mode = intent.getStringExtra("mode");

        if (mode.equals("0"))
        {
            Log.i("消息服务", "独立启动");

            ControlCenter.controlCenter=(ControlCenter)controlCenter;
            ControlCenter.controlCenter.init("");
            ControlCenter.controlCenter.login();
            return START_STICKY;
        }



        Log.i("消息服务", "app启动");

        return START_STICKY;
    }






    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("onDestroy", "停止");
        iswhile=false;
        if (controlCenter != null) {
            ((ControlCenter)controlCenter).disconnecetMQTT();
            ((ControlCenter) controlCenter).LoopMsgStop();
            ((ControlCenter)controlCenter).setiMessageControl(null);
            controlCenter = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (controlCenter == null)
            return  null;

        return controlCenter;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("unbind","退出");
        ((ControlCenter)controlCenter).mode=0;
        ((ControlCenter)controlCenter).LoopMsgStop();
        ((ControlCenter)controlCenter).setIsRunAPP(false);
        ((ControlCenter)controlCenter).setIsNotification(true);
        ((ControlCenter)controlCenter).setiMessageControl(null);

//        controlCenter=null;
        //进程退出
        return super.onUnbind(intent);

    }





}
