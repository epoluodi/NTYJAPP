package com.suypower.pms.server;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import com.suypower.pms.R;
import com.suypower.pms.app.SuyApplication;


/**
 * Created by Administrator on 14-9-18.
 */
public class NotificationClass extends BroadcastReceiver {


    NotificationManager nm = SuyApplication.getApplication().getNotificationManager();
    Notification baseNF ;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
    }


    public void add_Notification(String msg,String title,String text,int id,PendingIntent pendingIntent)
    {
        baseNF=new Notification();
        baseNF.icon = R.drawable.i_launch;
        baseNF.tickerText = msg;
        baseNF.defaults = Notification.DEFAULT_LIGHTS;
//        baseNF.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        baseNF.sound= Uri.parse("android.resource://" + SuyApplication.getApplication().getPackageName() + "/" + R.raw.tejat);
//        Intent notificationIntent = new Intent(Common.Appcontext, barcode.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(Common.Appcontext, 0,
//                notificationIntent, 0);
        baseNF.setLatestEventInfo(SuyApplication.getApplication(), title ,text,pendingIntent);

        nm.notify(id, baseNF);

    }

    public void add_Notification(String msg,String title,String text,int id,boolean issound,int ico)
    {
        baseNF=new Notification();
        baseNF.icon = ico;
        baseNF.tickerText = msg;
        baseNF.defaults = Notification.DEFAULT_LIGHTS;
//        if (issound)
//            baseNF.sound= Uri.parse("android.resource://" + Common.Appcontext.getPackageName() + "/" + R.raw.talitha);
//        baseNF.sound =Url.pa
//                    RingtoneManager.getDefaultUri(R.raw.talitha);
//        Intent notificationIntent = new Intent(Common.Appcontext, barcode.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(Common.Appcontext, 0,
//                notificationIntent, 0);
        baseNF.setLatestEventInfo(SuyApplication.getApplication(), title ,text,null);

        nm.notify(id, baseNF);

    }


    public static void Clear_Notify()
    {
        NotificationManager notificationManager = SuyApplication.getApplication().getNotificationManager();
        notificationManager.cancelAll();
    }

    public static void Clear_Notify(int id)
    {
        NotificationManager notificationManager = SuyApplication.getApplication().getNotificationManager();

        notificationManager.cancel(id);
    }

    public Notification add_Notification_ProgressBar(String msg,String title,int id)
    {
        baseNF=new Notification();
//        baseNF.icon = R.drawable.appico;
        baseNF.tickerText = msg;
        baseNF.defaults = Notification.DEFAULT_LIGHTS;
//        baseNF.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

//        baseNF.contentView = new RemoteViews("com.seuic.biostime",R.layout.notify_download);
//        baseNF.contentView.setProgressBar(R.id.pb, 50,0, false);
//        baseNF.contentView.setTextViewText(R.id.down_titile,title);
//        PendingIntent contentIntent = PendingIntent.getActivity(Common.Appcontext, 0,
//                notificationIntent, 0);
//        baseNF.setLatestEventInfo(Common.Appcontext, "" ,"",null);

        nm.notify(id, baseNF);
        return baseNF;

    }


}
