package com.suypower.pms.view.plugin;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.suypower.pms.R;
import com.suypower.pms.view.MainActivity;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义 提示窗口
 * @author YXG
 */
public class CustomPopWindowPlugin {



    //popwindows方法
    static PopupWindow popupWindow = null;
    static View popview;
    static Thread thread;
    static MainActivity mainActivity;





    public static void ShowPopWindow(View v, LayoutInflater inflater, String title,
                                     String info, final long timeout) {
        System.gc();

        popview = inflater.inflate(R.layout.popinfowindows, null);
        ((TextView) popview.findViewById(R.id.title)).setText(title);
        ((TextView) popview.findViewById(R.id.poptext)).setText(info);
        popupWindow = new PopupWindow();
        popupWindow.setContentView(popview);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);

        popupWindow.setAnimationStyle(R.style.Animationpopwindows);
        popupWindow.showAtLocation(v, Gravity.CENTER_VERTICAL, 0, 0);


        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    Thread.sleep(timeout);
                    handler.sendEmptyMessage(0);
                }
                catch (Exception e)
                {e.printStackTrace();}
            }
        });
        thread.start();
    }



    static Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (popupWindow !=null) {
                popupWindow.dismiss();
                popupWindow = null;
            }

        }
    };


    public static void ShowPopWindowForTimeout(View v, LayoutInflater inflater, String text,final long timeout)
    {
        ShowPopWindow(v, inflater, text);
        thread =new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    Thread.sleep(timeout);
                    handler.sendEmptyMessage(0);
                }
                catch (Exception e)
                {e.printStackTrace();}
            }
        });
        thread.start();

    }

    public static void setMainView(MainActivity mainView)
    {
        mainActivity=mainView;
    }


    //显示popwindows
    public static void ShowPopWindow(View v, LayoutInflater inflater, String text) {

        System.gc();
        popview = inflater.inflate(R.layout.popwindows, null);

        ((TextView) popview.findViewById(R.id.poptext)).setText(text);
        popupWindow = new PopupWindow();
        popupWindow.setContentView(popview);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);

        popupWindow.setAnimationStyle(R.style.Animationpopwindows);
        popupWindow.showAtLocation(v, Gravity.CENTER_VERTICAL, 0, 0);


    }

    //设置popwindows中的文本
    public static void Setpoptext(String text) {
        if (popupWindow !=null)
            ((TextView) popview.findViewById(R.id.poptext)).setText(text);
    }

    //关闭POPwindows
    public static void CLosePopwindow() {
        if (thread !=null)
        {
            thread.interrupt();
            thread = null;
        }
        if (popupWindow !=null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }







}
