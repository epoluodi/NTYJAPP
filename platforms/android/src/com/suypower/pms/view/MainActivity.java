package com.suypower.pms.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.suypower.pms.R;

import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.configxml.AppConfig;
import com.suypower.pms.app.configxml.GlobalConfig;
import com.suypower.pms.app.configxml.TabBar;
import com.suypower.pms.view.plugin.BaseViewPlugin;
import com.suypower.pms.view.plugin.CustomPopWindowPlugin;
import com.suypower.pms.view.plugin.CordovaWebViewPlugin;
import com.suypower.pms.view.plugin.InterFace.iOptionmenu_Item_Select;
import com.suypower.pms.view.plugin.InterFace.ITabbar_Item_Click;
import com.suypower.pms.view.plugin.MainTabBarPlugin;
import com.suypower.pms.view.plugin.camera.CameraHelper;
import com.suypower.pms.view.plugin.camera.CameraPlugin;
import com.suypower.pms.view.plugin.camera.PreviewPhotoViewPlugin;
import com.suypower.pms.view.plugin.scan.ScanActivity;



import org.apache.cordova.Config;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 应用主框架,
 *
 * @author YXG 2015-4-9.
 */
public class MainActivity extends Activity implements CordovaInterface,
        ITabbar_Item_Click,iOptionmenu_Item_Select {

    public static final String TAG = "MainActivity";


    /**
     * viewpager 环境变量
     *
     */
    PagerAdapter pagerAdapter;
    ViewPager viewPager;
    List<View> viewList;
    TextView titletext;

    Boolean rightmenuSingle=false;
    String rightmenuurl="";
    MainTabBarPlugin mainTabBarPlugin;//菜单定义控制类
    ImageView leftbutton;//左上角 控制按钮
    ImageView rightmenu;//右上角 菜单按钮
    CameraPlugin cameraPlugin=null;//拍照插件
    BaseViewPlugin baseViewPlugin=null;

    ProgressBar progressBar;

    public CordovaPlugin cordovaPlugin =null;//当前 cordova插件对象
    public View backview=null;



    /**
     * 处理窗口UI
     */
    public Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what)
            {
                case 1:// option菜单
                    setRightButton(msg.arg1);
                    break;
                case 11://单独
                    rightmenuSingle=true;
                    Map<String, Object> map = (Map<String, Object>)msg.obj;
                    if (!GlobalConfig.globalConfig.getIsCachefile())
                        rightmenuurl ="file:///" +
                                Environment.getExternalStorageDirectory() +
                                "/suyuan/" + SuyApplication.getApplication().getRelAppPath
                                + map.get("uri");
                    else
                        rightmenuurl=SuyApplication.getApplication().getAbsluteAppPath + map.get("uri");

                    rightmenu.setImageDrawable((BitmapDrawable) map.get("menu_image"));
//                    rightmenu.setBackground((BitmapDrawable)map.get("menu_image"));
                    setRightButton(msg.arg1);
                    break;

            }
        }


    };


    public void setHideLeftButton(Boolean ishide)
    {
        if (ishide)
            leftbutton.setVisibility(View.VISIBLE);
        else
            leftbutton.setVisibility(View.INVISIBLE);

    }
    /**
     * plugin viewpager list
     */
    List<Map<String,BaseViewPlugin>> viewpluginlist = new ArrayList<Map<String, BaseViewPlugin>>();

    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        titletext=(TextView)findViewById(R.id.title_text);

        // 右上角点击菜单
        rightmenu = (ImageView)findViewById(R.id.title_right_menu_image);
        rightmenu.setOnClickListener(onClickListener_rightmenu);

//        rightmenu.setVisibility(View.VISIBLE);//隐藏right button
        leftbutton=(ImageView)findViewById(R.id.title_left_menu_image);

//        leftbutton.setVisibility(View.INVISIBLE);//隐藏left button

        leftbutton.setOnClickListener(onClickListener_leftbutton);

        viewPager=(ViewPager)findViewById(R.id.main_viewpager);


        if (getIntent().getStringExtra("displaytype").equals("main")) {
            InitViewPager(getLayoutInflater());//加载viewpager
            mainTabBarPlugin =new MainTabBarPlugin(this);//加载3个标签栏
            mainTabBarPlugin.setOnTabBarClickItem(this);
            mainTabBarPlugin.setTabBarItemstate(0);
        }
        String url="";
        if (getIntent().getStringExtra("displaytype").equals("suggest")) {


            if (GlobalConfig.globalConfig.getIsCachefile()) {

                url ="file:///" + getFilesDir() +
                        File.separator + "www/about/feedback.html";
            }
            else {

                url ="file:///" + Environment.getExternalStorageDirectory() +
                        File.separator + "suyuan/www/about/feedback.html";
            }

            InitViewPager(getLayoutInflater(), url);//加载viewpager
        }
        if (getIntent().getStringExtra("displaytype").equals("outside")) {


            url= getIntent().getStringExtra("url");

            InitViewPager(getLayoutInflater(), url);//加载viewpager
        }

        if (getIntent().getStringExtra("displaytype").equals("inside")) {

            url= getIntent().getStringExtra("url");

            if (GlobalConfig.globalConfig.getIsCachefile()) {

                url ="file:///" +getFilesDir() + "/www/apps/" + url;
            }
            else {

                url ="file:///" + Environment.getExternalStorageDirectory() + "/suyuan/www/apps/" + url;
            }

            InitViewPager(getLayoutInflater(), url);//加载viewpager
        }
        if (getIntent().getStringExtra("displaytype").equals("about")) {


            if (GlobalConfig.globalConfig.getIsCachefile()) {

                url ="file:///" + getFilesDir() +
                        File.separator + "www/about/about.html";
            }
            else {

                url ="file:///" + Environment.getExternalStorageDirectory() +
                        File.separator + "suyuan/www/about/about.html";
            }

            InitViewPager(getLayoutInflater(), url);//加载viewpager
        }
        /**
         * viewpager 适配器
         */
        pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return viewList.size();
            }



            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {

                return arg0 == arg1;
            }


            @Override
            public int getItemPosition(Object object) {

                return super.getItemPosition(object);
            }

            @Override
            public CharSequence getPageTitle(int position) {

                return "";
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewList.get(position));
                return viewList.get(position);
            }
            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View)object);


            }

        };


        //设置viewpager 是配置
        viewPager.setAdapter(pagerAdapter);

        viewPager.setOnPageChangeListener(onPageChangeListener);//监听viewpager 切换




    }


    public void setWebTitle(String strTitle)
    {
        titletext.setText(strTitle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (backview==null)
        {
            backview =new View(this);

            //增加滤镜
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            backview.setBackground(getResources().getDrawable(R.color.blackTransparent8));
            addContentView(backview, layoutParams);
            backview.setVisibility(View.GONE);
            CustomPopWindowPlugin.setMainView(this);
        }

    }

    public void setShowBackView()
    {
        Animation animation2 = AnimationUtils.loadAnimation(backview.getContext(), android.R.anim.fade_in);

        backview.startAnimation(animation2);
        backview.setVisibility(View.VISIBLE);
    }

    public void setHideBackView()
    {
        Animation animation2 = AnimationUtils.loadAnimation(backview.getContext(), android.R.anim.fade_out);

        backview.startAnimation(animation2);
        backview.setVisibility(View.GONE);
    }

    //左边按钮点击事件
    View.OnClickListener onClickListener_leftbutton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Map<String,BaseViewPlugin> map;
            if (getIntent().getStringExtra("displaytype").equals("main")){
                map = viewpluginlist.get(mainTabBarPlugin.itemindex);

                //传给插件基类 js 交互信息
                map.get(String.valueOf(mainTabBarPlugin.itemindex)).goback();
            }
            else
            {
                map = viewpluginlist.get(0);

                //传给插件基类 js 交互信息
                map.get(String.valueOf(0)).goback();
            }


        }
    };



    //右上角菜单 点击控制
    View.OnClickListener onClickListener_rightmenu =new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //获得基类信息
            int index=0;
            Map<String,BaseViewPlugin> map;
            if (getIntent().getStringExtra("displaytype").equals("main"))
            {
                index = mainTabBarPlugin.itemindex;
            }
            else
                index=0;

            map = viewpluginlist.get(index);
            if (rightmenuSingle) {

                map.get(String.valueOf(index)).loadWebUrl(rightmenuurl);
            }
            else {
                //传给插件基类 js 交互信息
                map.get(String.valueOf(index)).showOptionMenu(view, 2);
            }

        }
    };

    /**
     * 关闭窗体
     */
    public void finishView()
    {



        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        System.gc();
    }



    @Override
    public void OnOptionMenuItemSelect(int i ,String uri) {

    }

    //viewpager 改变事件监听
    ViewPager.OnPageChangeListener onPageChangeListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            mainTabBarPlugin.setTabBarItemstate(i);
            Map<String,BaseViewPlugin> map = viewpluginlist.get(mainTabBarPlugin.itemindex);

            //如果有菜单就显示菜单
            BaseViewPlugin baseViewPlugin =  map.get(String.valueOf(i));
            if (baseViewPlugin.isShowmenu())
                setRightButton(View.VISIBLE);
            else
                setRightButton(View.INVISIBLE);


        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };



    void InitViewPager(LayoutInflater layoutInflater,String Url) {
        viewList = new ArrayList<View>();
        View view;

        //位cordovaweb plugin
        view = layoutInflater.inflate(R.layout.cordovaplugin_viewpager, null);
        viewList.add(view);
        CordovaWebView cordovaWebView = (CordovaWebView) view.findViewById(R.id.cordovawebview);
        cordovaWebView.cordovaWebViewId = 0;
        progressBar = (ProgressBar)view.findViewById(R.id.pb);



//        if (getIntent().getStringExtra("type").equals("3"))
//        {
//
//            if (GlobalConfig.globalConfig.getIsCachefile()) {
//                cordovaWebView.loadUrl("file:///" + getFilesDir() +
//                        File.separator + "www/about.html");
//                Url ="file:///" + getFilesDir() +
//                        File.separator + "www/about.html";
//            }
//            else {
//                cordovaWebView.loadUrl("file:///" + Environment.getExternalStorageDirectory() +
//                        File.separator + "www/about/addFeedback.html");
//                Url ="file:///" + Environment.getExternalStorageDirectory() +
//                        File.separator + "www/about/addFeedback.html";
//            }
//        }
//        if (getIntent().getStringExtra("type").equals("1"))
        cordovaWebView.loadUrl(Url);
        Map<String,BaseViewPlugin> map;

        CordovaWebViewPlugin cordovaWebViewPlugin = new CordovaWebViewPlugin(view, this);
        cordovaWebViewPlugin.setCordova(cordovaWebView, Url);
        titletext.setText(cordovaWebView.webTitle);
        map = new Hashtable<String, BaseViewPlugin>();

        map.put("0", cordovaWebViewPlugin);
        viewpluginlist.add(map);





    }



    /**
     *  初始化viewpager
     * @param layoutInflater
     */
    void InitViewPager(LayoutInflater layoutInflater)
    {
        viewList=new ArrayList<View>();
        View view;
        Map<String,BaseViewPlugin> map;
        TabBar[] tabBars = AppConfig.appConfig.getTabBar();//  获得tabbar数组
        //遍历tabbars 加载viewpager
        for (int i=0;i<tabBars.length;i++) {
            //判断是否位userprofileplguin
            if (tabBars[i].getUri_tab().equals(AppConfig.userprofileplugin)) {
//                view = layoutInflater.inflate(R.layout.userprofileplugin_viewpager, null);
//                viewList.add(view);
//                UserProfileViewPlugin userProfileViewPlugin = new UserProfileViewPlugin(view, this);
//                map = new Hashtable<String, BaseViewPlugin>();
//                map.put(String.valueOf(tabBars[i].getTabid()), userProfileViewPlugin);
//                viewpluginlist.add(map);


            } else if (tabBars[i].getUri_tab().equals(AppConfig.cordovawebviewplugin))
            {
                //位cordovaweb plugin
                view = layoutInflater.inflate(R.layout.cordovaplugin_viewpager, null);
                viewList.add(view);
                CordovaWebView cordovaWebView = (CordovaWebView)view.findViewById(R.id.cordovawebview);

                cordovaWebView.cordovaWebViewId=i;
                progressBar = (ProgressBar)view.findViewById(R.id.pb);


                if (!GlobalConfig.globalConfig.getIsCachefile())
                     cordovaWebView.loadUrl("file:///" + Environment.getExternalStorageDirectory() + "/suyuan/www/apps/dyyk/" + tabBars[i].getUrl());
                else
//                   cordovaWebView.loadUrl("file:///" + getFilesDir() + "/www/apps/apptest/" + tabBars[i].getUrl());
                    cordovaWebView.loadUrl(SuyApplication.getApplication().getAbsluteAppPath + tabBars[i].getUrl());
//
                Log.i("URL",SuyApplication.getApplication().getAbsluteAppPath + tabBars[i].getUrl());
//                cordovaWebView.loadUrl(Config.getStartUrl());
                CordovaWebViewPlugin cordovaWebViewPlugin = new CordovaWebViewPlugin(view, this);
                cordovaWebViewPlugin.setCordova(cordovaWebView,Config.getStartUrl());
                titletext.setText(cordovaWebView.webTitle);
                map = new Hashtable<String, BaseViewPlugin>();
                Log.i("tabid",String.valueOf(tabBars[i].getTabid()));
                map.put(String.valueOf(tabBars[i].getTabid()), cordovaWebViewPlugin);
                viewpluginlist.add(map);


            }
        }


    }




    /**
     * 响应 Acitvity 回传信息
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        JSONObject jsonObject=new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            if (CameraHelper.JSCallCamera == requestCode)//拍照回调
            {
                //拍照返回信息
                ImageView imageView;

                if (resultCode == 0) {
                    jsonObject.put("  ", 0);
                    jsonObject.put("photofile", null);
//                    cameraPlugin.CallBackCordovaJS("CameraResult", jsonObject);
                    if (cordovaPlugin != null) {
                        cordovaPlugin.callbackContext.error(jsonObject);
                        cordovaPlugin=null;
                    }
                    return;
                }
                if (resultCode == -1) {
                    jsonObject.put("photos", 1);


                    jsonArray.put("stereo://" + CameraPlugin.copyCacheFile(CameraHelper.PhotoPath));
                    jsonObject.put("photofile", jsonArray);

//                    cameraPlugin.CallBackCordovaJS("CameraResult", jsonObject);
                    if (cordovaPlugin != null) {
                        cordovaPlugin.callbackContext.success(jsonObject);
                        cordovaPlugin=null;
                    }
                    return;
                }

            }

            if (PreviewPhotoViewPlugin.JSCallPreviewPhtoto == requestCode)//预览回调
            {
                switch (resultCode) {
                    case 1://图片选自
                        String[] files = data.getExtras().getStringArray("files");
                        jsonObject.put("photos", files.length);

                        for (int i = 0; i < files.length; i++) {
                            jsonArray.put("stereo://" + files[i]);
                        }
                        jsonObject.put("photofile", jsonArray);

                        Log.i("数量", jsonObject.toString());
//                        cameraPlugin.CallBackCordovaJS("CameraResult", jsonObject);
                        if (cordovaPlugin != null) {
                            cordovaPlugin.callbackContext.success(jsonObject);
                            cordovaPlugin=null;
                        } break;

                }

            }

            if (ScanActivity.SCANRESULTREQUEST == requestCode)//扫描
            {
                switch (resultCode) {
                    case 1://结果

                        if (cordovaPlugin !=null) {
                            jsonObject.put("barcode", data.getExtras().getString("code"));
                            cordovaPlugin.callbackContext.success(jsonObject);
                            cordovaPlugin = null;
                        }
                        break;
                    case 0://没有结果
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //Cordova接口 实现

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

    /**
     * 处理cordova 数据
     * @param id            The message id
     * @param data          The message data
     * @return
     */
    @Override
    public Object onMessage(String id, Object data) {
        Log.i(id, data.getClass().getSuperclass().getSimpleName());


        //
        if (id.equals("onReceivedTitle"))
        {
            if (getIntent().getStringExtra("displaytype").equals("outside"))
                titletext.setText(data.toString());
            return null;
        }


        if (id.equals("onProgressChanged"))
        {

            Log.i("web 进度",String.valueOf(data));
            int p = (Integer)data;

            if (p != 100)
            {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(p);
            }
            else
            {
                progressBar.setProgress(0);
                progressBar.setVisibility(View.GONE);
            }



            return null;
        }
        if (id.equals("onPageStarted"))
        {

            Map<String, Object> map = new Hashtable<String, Object>();
            map.put("id", id);

            Message message = new Message();
            message.obj = map;
            message.what=1;
            handlerCordovaMessage.sendMessage(message);




            return null;
        }

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


            if (id.equals(BaseViewPlugin.TITLE_ACTION))
            {
                try {
                    JSONObject jsonObject = (JSONObject) message.obj;
                    setWebTitle(jsonObject.getString("title"));
                }
                catch (Exception e)
                {e.printStackTrace();}
            }

            if (id.equals("onPageStarted")) {
                //获得基类信息

                setRightButton(View.INVISIBLE);

                setWebTitle("");

                map = viewpluginlist.get(0);
                Log.i("tabid ===", String.valueOf(0));
                //传给插件基类 js 交互信息
                map.get(String.valueOf(0)).onCordovaMessage(id, null);
            }


            if (id.equals(BaseViewPlugin.GOBACK_ACTION) ||
                    id.equals(BaseViewPlugin.DIALOG_ACTION)  ||
                    id.equals(BaseViewPlugin.INFO_ACTION)   ) {
                //获得基类信息
                map = viewpluginlist.get(message.arg1);
                Log.i("tabid ===", String.valueOf(message.arg1));
                //传给插件基类 js 交互信息
                map.get(String.valueOf(message.arg1)).onCordovaMessage(id, message.obj);
            }


            if (id.equals(BaseViewPlugin.MENU_ACTION)) {
                //获得基类信息
                map = viewpluginlist.get(message.arg1);
                Log.i("tabid ===", String.valueOf(message.arg1));
                //传给插件基类 js 交互信息
                map.get(String.valueOf(message.arg1)).onCordovaMessage(id, message.obj);
            }
            if (id.equals(BaseViewPlugin.INITWEB_ACTION)) {


                //获得基类信息
                map = viewpluginlist.get(message.arg1);
                Log.i("tabid ===", String.valueOf(message.arg1));
                //传给插件基类 js 交互信息
                map.get(String.valueOf(message.arg1)).onCordovaMessage(id, message.obj);
            }


            if (id.equals(BaseViewPlugin.TAKEPICTURE_ACTION))//打开拍照
            {
                map = viewpluginlist.get(cordovaPlugindata.webView.cordovaWebViewId);
                cameraPlugin = new CameraPlugin(MainActivity.this,
                        map.get(String.valueOf(cordovaPlugindata.webView.cordovaWebViewId)
                ));
                cordovaPlugin =cordovaPlugindata;
                cameraPlugin.takePicture();



            }
            if (id.equals(BaseViewPlugin.PREVIEWPICTURE_ACTION))// 打开预览
            {
                map = viewpluginlist.get(cordovaPlugindata.webView.cordovaWebViewId);
                cameraPlugin = new CameraPlugin(MainActivity.this,
                        map.get(String.valueOf(cordovaPlugindata.webView.cordovaWebViewId)));

                try {
                    cordovaPlugin = cordovaPlugindata;
                    int imageCount = ((JSONObject)cordovaPlugindata.jsondata).getInt("imageCount");
                    cameraPlugin.openPreviewPhoto(imageCount);
                } catch (Exception e) {
                    e.printStackTrace();
                }



            }
            if (id.equals(BaseViewPlugin.CHOOSEIMAGE_ACTION))// 打开预览
            {
                map = viewpluginlist.get(cordovaPlugindata.webView.cordovaWebViewId);
                cameraPlugin = new CameraPlugin(MainActivity.this,
                        map.get(String.valueOf(cordovaPlugindata.webView.cordovaWebViewId)));
                try {
                    cordovaPlugin = cordovaPlugindata;
                    int imageCount = ((JSONObject) cordovaPlugindata.jsondata).getInt("imageCount");
                    cameraPlugin.openChooseImage(imageCount);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }



            }
            if (id.equals(BaseViewPlugin.UPLOAD_ACTION))// 上传
            {

                map = viewpluginlist.get(cordovaPlugindata.webView.cordovaWebViewId);
                cameraPlugin = new CameraPlugin(MainActivity.this,
                        map.get(String.valueOf(cordovaPlugindata.webView.cordovaWebViewId)));

                cameraPlugin.uploadPhoto(((JSONObject)cordovaPlugindata.jsondata).toString());


            }
            if (id.equals(BaseViewPlugin.PREVIEWURL_ACTION))// url 图片预览
            {

                map = viewpluginlist.get(cordovaPlugindata.webView.cordovaWebViewId);
                cameraPlugin = new CameraPlugin(MainActivity.this,
                        map.get(String.valueOf(cordovaPlugindata.webView.cordovaWebViewId)));

                cameraPlugin.openPreviewUrlPhoto(cordovaPlugindata.jsondata);


            }


            //处理word操作
            if (id.equals(BaseViewPlugin.SCAN_ACTION)
                    ||id.equals(BaseViewPlugin.WORD_ACTION)
                    || id.equals(BaseViewPlugin.SIGNATURE_ACTION)
                    || id.equals(BaseViewPlugin.WORDPREVIEW_ACTION)
                    || id.equals(BaseViewPlugin.UPLOADFILE_ACTION)
                    ||  id.equals(BaseViewPlugin.FILEDOWNLOAD_ACTION)
                    ||  id.equals(BaseViewPlugin.FILECANCELDOWNLOAD_ACTION))

            {

//                AlertDlg alertDlg = new AlertDlg(MainActivity.this, AlertDlg.AlertEnum.ALTERTYPE);
//                alertDlg.setContentText("样式测试,效果如何?");
//                alertDlg.show();

                //获得基类信息
                map = viewpluginlist.get(cordovaPlugindata.webView.cordovaWebViewId);
                Log.i("tabid ===", String.valueOf(cordovaPlugindata.webView.cordovaWebViewId));
                //传给插件基类 js 交互信息
                baseViewPlugin = map.get(String.valueOf(cordovaPlugindata.webView.cordovaWebViewId));
                baseViewPlugin.onCordovaMessage(id, cordovaPlugindata);
            }


        }
    };
    @Override
    public ExecutorService getThreadPool() {
        return threadPool;
    }

    //Cordova接口 实现 end


    /**
     * tabbar 点击事件，对应产生界面布局变化
     * @param v 标签view 当前点击的tabbar view
     * @param index 标签索引  tabbar index
     */
    @Override
    public void OnTabBarClickItem(View v, int index) {
        viewPager.setCurrentItem(index);
    }



    //设置右上角菜单 显示状态
    public void setRightButton(int visibility)
    {

//        if (rightmenu.getVisibility() ==visibility)
//            return;

        Animation animation1 = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation animation2 = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);


        if (visibility == View.INVISIBLE)
            rightmenu.startAnimation(animation2);
        if (visibility == View.VISIBLE)
            rightmenu.startAnimation(animation1);
        rightmenu.setVisibility(visibility);

    }




    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode==4) {

            CustomPopWindowPlugin.CLosePopwindow();
            Map<String,BaseViewPlugin> map;
            if (getIntent().getStringExtra("displaytype").equals("main")){
                map = viewpluginlist.get(mainTabBarPlugin.itemindex);

                //传给插件基类 js 交互信息
                map.get(String.valueOf(mainTabBarPlugin.itemindex)).goback();
            }
            else
            {
                map = viewpluginlist.get(0);

                //传给插件基类 js 交互信息
                map.get(String.valueOf(0)).goback();
            }



            return false;
        }


        return super.onKeyUp(keyCode, event);
    }


}