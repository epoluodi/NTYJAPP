package com.suypower.pms.view.splitMainView;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.suypower.pms.R;
import com.suypower.pms.view.plugin.BaseViewPlugin;
import com.suypower.pms.view.plugin.CordovaWebViewPlugin;
import com.suypower.pms.view.plugin.camera.CameraPlugin;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 分屏显示主窗口
 * Created by cjw on 15/7/17.
 */
public class MainSplit extends Activity implements CordovaInterface {


    public static final String TAG = "MainSplit";
    LinearLayout mainview;
    LinearLayout linearLayout1;
    LinearLayout linearLayout2;
    LinearLayout linearLayoutcenter;
    LinearLayout.LayoutParams layoutParams1;

    CordovaWebViewPlugin cordovaWebViewPlugin;




    public WindowManager mWm;
    int oldY;
    int oldheight;
    int windowsheight;
    Boolean IsSplit=true;

    CameraPlugin cameraPlugin=null;//拍照插件
    BaseViewPlugin baseViewPlugin=null;
    public CordovaPlugin cordovaPlugin =null;//当前 cordova插件对象


    public String CordovaURL="";//传入显示URL信息


    private final ExecutorService threadPool = Executors.newCachedThreadPool();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainsplit);
        mWm = (WindowManager) getSystemService(WINDOW_SERVICE);
        linearLayout1 = (LinearLayout)findViewById(R.id.l1);
        linearLayoutcenter = (LinearLayout)findViewById(R.id.l_center);
        linearLayout2 = (LinearLayout)findViewById(R.id.l2);
        mainview = (LinearLayout)findViewById(R.id.mainview);
        linearLayoutcenter.setOnClickListener(onClickListenercenter);
    
        layoutParams1 = (LinearLayout.LayoutParams)linearLayout1.getLayoutParams();


        Display mDisplay = getWindowManager().getDefaultDisplay();
        int W = mDisplay.getWidth();
        int H = mDisplay.getHeight();
        Log.i("Main", "Width = " + W);
        Log.i("Main", "Height = " + H);

        windowsheight = (H-20-20);


        initView();

    }

    void initView()
    {
        CordovaWebView cordovaWebView = new CordovaWebView(this);
        linearLayout1.addView(cordovaWebView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT));
        cordovaWebView.loadUrl("http://m.163.com");
        cordovaWebViewPlugin = new CordovaWebViewPlugin(linearLayout1,this);
        cordovaWebViewPlugin.setCordova(cordovaWebView, "http://www.baidu.com");

        ImageView imageView = new ImageView(this);
        imageView.setBackground(getResources().getDrawable(R.drawable.login_info));
        linearLayout2.addView(imageView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT));


    }

    public  boolean isScreenOriatationPortrait() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    View.OnClickListener onClickListenercenter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Animation  animation;

            if (IsSplit) {
                if (isScreenOriatationPortrait()) {
                    animation = AnimationUtils.loadAnimation(MainSplit.this,
                            R.anim.anim_bottommenu_exit);
                }
                else
                    animation = AnimationUtils.loadAnimation(MainSplit.this,
                            R.anim.anim_bottommenu_exit_land);
                linearLayout2.startAnimation(animation);
                linearLayout2.setVisibility(View.GONE);


                IsSplit =false;

            }
            else {
                IsSplit=true;
                if (isScreenOriatationPortrait())
                    animation= AnimationUtils.loadAnimation(MainSplit.this,
                        R.anim.anim_bottommenu_enter);
                else
                    animation= AnimationUtils.loadAnimation(MainSplit.this,
                            R.anim.anim_bottommenu_enter_land);
                linearLayout2.startAnimation(animation);
                linearLayoutcenter.startAnimation(animation);

                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        linearLayout2.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }


        }
    };
    View.OnTouchListener onTouchListenercenter  = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {



            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
//                    oldheight = layoutParams1.height;

                    oldY = (int)event.getY();
                    Log.i("X",String.valueOf(event.getX()));
                    Log.i("Y",String.valueOf(event.getY()));

                    break;
                case MotionEvent.ACTION_MOVE:

                    Log.i("X",String.valueOf(event.getX()));
                    Log.i("Y",String.valueOf(event.getY()));


//                    if (event.getY()>oldY)
//                    {
//                        windowsheight +=20 ;
//                        layoutParams1.height =windowsheight;
//
//                    }
//                    else
//                    {
//                        windowsheight -=20 ;
//                        layoutParams1.height =windowsheight;
//                    }
//
//                    mainview.updateViewLayout(linearLayout1,layoutParams1);

                    break;
            }


            return true;
        }
    };





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
        Log.i(id, data.getClass().getSuperclass().getSimpleName());




        if (data.getClass().getSimpleName().equals("Message")) {
            //异步不带返回调用

            Map<String, Object> map = new Hashtable<String, Object>();
            map.put("id", id);
            map.put("data", data);
            Message message = new Message();
            message.obj = map;
            message.what=0;
            handlerCordovaMessage.sendMessage(message);
        }

        if (data.getClass().getSuperclass().getSimpleName().equals("CordovaPlugin")) {
            //异步带返回调用

            Map<String, Object> map = new Hashtable<String, Object>();
            map.put("id", id);
            map.put("data", data);
            Message message = new Message();
            message.obj = map;
            message.what=1;
            handlerCordovaMessage.sendMessage(message);
        }




        return null;
    }

    /**
     * cordova 来的数据 传给UI线程处理
     */
    Handler handlerCordovaMessage = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            System.gc();

            String id="";
            Message message=null;
            CordovaPlugin cordovaPlugindata=null;
            Map<String, Object> mapmsg =null;
            switch (msg.what) {
                case 0:
                    mapmsg = (Map<String, Object>) msg.obj;
                    id = mapmsg.get("id").toString();
                    message = (Message) mapmsg.get("data");
                    break;
                case 1:
                    mapmsg = (Map<String, Object>) msg.obj;
                    id = mapmsg.get("id").toString();
                    cordovaPlugindata = (CordovaPlugin)mapmsg.get("data");
                    break;
            }



            Map<String, BaseViewPlugin> map;


//            if (id.equals(BaseViewPlugin.TITLE_ACTION))
//            {
//                try {
//                    JSONObject jsonObject = (JSONObject) message.obj;
//                    setWebTitle(jsonObject.getString("title"));
//                }
//                catch (Exception e)
//                {e.printStackTrace();}
//            }

//            if (id.equals(BaseViewPlugin.GOBACK_ACTION) ||
//                    id.equals(BaseViewPlugin.DIALOG_ACTION)  ||
//                    id.equals(BaseViewPlugin.INFO_ACTION)   ) {
//                //获得基类信息
//                map = viewpluginlist.get(message.arg1);
//                Log.i("tabid ===", String.valueOf(message.arg1));
//                //传给插件基类 js 交互信息
//                map.get(String.valueOf(message.arg1)).onCordovaMessage(id, message.obj);
//            }


//            if (id.equals(BaseViewPlugin.MENU_ACTION)) {
//                //获得基类信息
//                map = viewpluginlist.get(message.arg1);
//                Log.i("tabid ===", String.valueOf(message.arg1));
//                //传给插件基类 js 交互信息
//                map.get(String.valueOf(message.arg1)).onCordovaMessage(id, message.obj);
//            }
//            if (id.equals(BaseViewPlugin.INITWEB_ACTION)) {
//
//
//                //获得基类信息
//                map = viewpluginlist.get(message.arg1);
//                Log.i("tabid ===", String.valueOf(message.arg1));
//                //传给插件基类 js 交互信息
//                map.get(String.valueOf(message.arg1)).onCordovaMessage(id, message.obj);
//            }


//            if (id.equals(BaseViewPlugin.TAKEPICTURE_ACTION))//打开拍照
//            {
//                map = viewpluginlist.get(cordovaPlugindata.webView.cordovaWebViewId);
//                cameraPlugin = new CameraPlugin(MainActivity.this,
//                        map.get(String.valueOf(cordovaPlugindata.webView.cordovaWebViewId)
//                        ));
//                cordovaPlugin =cordovaPlugindata;
//                cameraPlugin.takePicture();
//
//
//
//            }
//            if (id.equals(BaseViewPlugin.PREVIEWPICTURE_ACTION))// 打开预览
//            {
//                map = viewpluginlist.get(cordovaPlugindata.webView.cordovaWebViewId);
//                cameraPlugin = new CameraPlugin(MainActivity.this,
//                        map.get(String.valueOf(cordovaPlugindata.webView.cordovaWebViewId)));
//
//                try {
//                    cordovaPlugin = cordovaPlugindata;
//                    int imageCount = ((JSONObject)cordovaPlugindata.jsondata).getInt("imageCount");
//                    cameraPlugin.openPreviewPhoto(imageCount);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//
//
//            }
//            if (id.equals(BaseViewPlugin.CHOOSEIMAGE_ACTION))// 打开预览
//            {
//                map = viewpluginlist.get(cordovaPlugindata.webView.cordovaWebViewId);
//                cameraPlugin = new CameraPlugin(MainActivity.this,
//                        map.get(String.valueOf(cordovaPlugindata.webView.cordovaWebViewId)));
//                try {
//                    cordovaPlugin = cordovaPlugindata;
//                    int imageCount = ((JSONObject) cordovaPlugindata.jsondata).getInt("imageCount");
//                    cameraPlugin.openChooseImage(imageCount);
//                }
//                catch (Exception e)
//                {
//                    e.printStackTrace();
//                }
//
//
//
//            }
//            if (id.equals(BaseViewPlugin.UPLOAD_ACTION))// 上传
//            {
//
//                map = viewpluginlist.get(cordovaPlugindata.webView.cordovaWebViewId);
//                cameraPlugin = new CameraPlugin(MainActivity.this,
//                        map.get(String.valueOf(cordovaPlugindata.webView.cordovaWebViewId)));
//
//                cameraPlugin.uploadPhoto(((JSONObject)cordovaPlugindata.jsondata).toString());
//
//
//            }
//            if (id.equals(BaseViewPlugin.PREVIEWURL_ACTION))// url 图片预览
//            {
//
//                map = viewpluginlist.get(cordovaPlugindata.webView.cordovaWebViewId);
//                cameraPlugin = new CameraPlugin(MainActivity.this,
//                        map.get(String.valueOf(cordovaPlugindata.webView.cordovaWebViewId)));
//
//                cameraPlugin.openPreviewUrlPhoto(cordovaPlugindata.jsondata);
//
//
//            }


//            //处理word操作
//            if (id.equals(BaseViewPlugin.SCAN_ACTION)
//                    ||id.equals(BaseViewPlugin.WORD_ACTION)
//                    || id.equals(BaseViewPlugin.SIGNATURE_ACTION)
//                    || id.equals(BaseViewPlugin.WORDPREVIEW_ACTION)
//                    || id.equals(BaseViewPlugin.UPLOADFILE_ACTION)
//                    ||  id.equals(BaseViewPlugin.FILEDOWNLOAD_ACTION)
//                    ||  id.equals(BaseViewPlugin.FILECANCELDOWNLOAD_ACTION))
//
//            {
//
////                AlertDlg alertDlg = new AlertDlg(MainActivity.this, AlertDlg.AlertEnum.ALTERTYPE);
////                alertDlg.setContentText("样式测试,效果如何?");
////                alertDlg.show();
//
//                //获得基类信息
//                map = viewpluginlist.get(cordovaPlugindata.webView.cordovaWebViewId);
//                Log.i("tabid ===", String.valueOf(cordovaPlugindata.webView.cordovaWebViewId));
//                //传给插件基类 js 交互信息
//                baseViewPlugin = map.get(String.valueOf(cordovaPlugindata.webView.cordovaWebViewId));
//                baseViewPlugin.onCordovaMessage(id, cordovaPlugindata);
//            }


        }
    };

    @Override
    public ExecutorService getThreadPool() {
        return threadPool;
    }
}