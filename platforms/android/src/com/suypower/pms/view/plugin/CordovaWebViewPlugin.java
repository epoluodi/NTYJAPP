package com.suypower.pms.view.plugin;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;



import com.suypower.pms.R;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.configxml.AppConfig;
import com.suypower.pms.app.configxml.GlobalConfig;
import com.suypower.pms.view.MainActivity;
import com.suypower.pms.view.plugin.InterFace.iOptionmenu_Item_Select;
import com.suypower.pms.view.plugin.camera.CameraPlugin;
import com.suypower.pms.view.plugin.fileEx.FilePlugin;
import com.suypower.pms.view.plugin.scan.ScanPlugin;
import com.suypower.pms.view.splitMainView.MainSplit;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * cordovaplguin 插件 viewpager
 * @author  YXG on 15/4/14.
 */

public class CordovaWebViewPlugin extends BaseViewPlugin implements
        iOptionmenu_Item_Select {

    public static final String TAG = "CordovaWebViewPlugin";




    public CordovaWebView cordovaWebView;
    public String isGoBack="";//是否调用原生 goback

    View mainview;//viewpager setting view
    String mainurl;//主页
    String callbackJs;//回调的函数名称

    OptionMenuPlugin optionMenuPlugin;
    MainActivity mainActivity;
    MainSplit mainSplit;
    Activity activity;

    List<Map<String, Object>> leftmapList;//左边菜单
    List<Map<String, Object>> rightmapList;//右边菜单
    List<Map<String, Object>> bottommapList;//右边菜单


    View view;//灰度view
    ScanPlugin scanPlugin; //扫描插件

    FilePlugin filePlugin;




    public CordovaWebViewPlugin(View v, MainActivity mainActivity)
    {
        mainview = v;
        this.mainActivity =mainActivity;

        view = new View(mainActivity);
        Window window = mainActivity.getWindow();
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        view.setBackground(mainActivity.getResources().getDrawable(R.color.blackTransparent8));
        window.addContentView(view, layoutParams);
        view.setVisibility(View.GONE);


    }


    public CordovaWebViewPlugin(View v, Activity _activity)
    {
        mainview = v;
        activity =_activity;
        view = new View(activity);
        Window window = activity.getWindow();
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        view.setBackground(activity.getResources().getDrawable(R.color.blackTransparent8));
        window.addContentView(view, layoutParams);
        view.setVisibility(View.GONE);
    }

    public CordovaWebViewPlugin(View v, MainSplit mainSplit)
    {
        mainview = v;
        this.mainSplit =mainSplit;

        view = new View(mainSplit);
        Window window = mainSplit.getWindow();
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        view.setBackground(mainSplit.getResources().getDrawable(R.color.blackTransparent8));
        window.addContentView(view, layoutParams);
        view.setVisibility(View.GONE);
    }



    @Override
    public void goback() {



        if (isGoBack.equals("")) {
            if (cordovaWebView.startOfHistory())
            {
                mainActivity.finishView();
                return;
            }
            cordovaWebView.goBack();

        }
        else
            CallBackCordovaJS(isGoBack,null);
    }

    /**
     * 显示菜单
     * @param v 被加载的view
     * @param menutype 1 pop显示，2 底部显示
     */
    @Override
    public void showOptionMenu(View v,int menutype) {


        if (optionMenuPlugin !=null) {
            switch (menutype)
            {
                case 1:
                    optionMenuPlugin.showPopWindow(v, menutype);
                    break;
                case 2:
                    mainActivity.setShowBackView();
                    optionMenuPlugin.showBotomWindow(view);
                    break;
            }

        }
    }

    @Override
    public void OnOptionMenuItemSelect(int i,String uri) {
        if(uri.equals(AppConfig.userprofileplugin)) //用户设置
        {
            //
        }
        else if(uri.equals(AppConfig.cameraplugin)) //用户设置
        {
            CameraPlugin cameraPlugin=new CameraPlugin(mainActivity,this);

            cameraPlugin.takePicture();

        }
        else
        {
            if (TAG.equals("CordovaWebViewPlugin"))
                cordovaWebView.loadUrl(SuyApplication.getApplication().getAbsluteAppPath + uri);
        }
       }

    /**
     * js交互信息处理
     * @param id
     * @param data
     */
    @Override
    public void onCordovaMessage(String id, Object data) {
        Log.i("js信息 id:",id);
//        Log.i("js信息 data:", data.toString());

        if (id.equals(MENU_ACTION)) //如果是菜单进行加载
        {
            getMenuList((JSONArray) data);

            Log.i("js信息 data:", data.toString());
            super.setIsshowmenu(true);
            Message message =new Message();
            message.what=1;
            message.arg1=View.VISIBLE;

            if (bottommapList.size() ==1)
            {
                message.what=11;
                message.obj = bottommapList.get(0);
                mainActivity.handler.sendMessage(message);
            }
            else {
                mainActivity.handler.sendMessage(message);
                optionMenuPlugin =new OptionMenuPlugin(mainActivity.getLayoutInflater(),2,mainActivity);
                optionMenuPlugin.setMenuBar(bottommapList, this);
            }




//            switch (menutype)
//            {
//                case 1://左边
//                    if (leftmapList.size()==0)
//                        return;
//                    optionMenuPlugin.setMenuList(leftmapList,this,1);
//                    break;
//                case 2://右边
//                    if (rightmapList.size()==0)
//                        return;
//                    optionMenuPlugin.setMenuList(rightmapList,this,2);
//                    break;
//            }
        }
        if (id.equals("onPageStarted")) //
        {
            isGoBack="";

        }
        if (id.equals(GOBACK_ACTION)) //
        {
            cordovaWebView.goBack();

        }
        if (id.equals(DIALOG_ACTION)) //
        {

            try {
                JSONObject jsonObject = (JSONObject)data;
                if (jsonObject.getString("model").equals("show"))
                {
                    int timeout=15000;
                    try {
                        timeout = jsonObject.getInt("timeout");
                    }
                    catch (Exception e)
                    {e.printStackTrace();}
                    CustomPopWindowPlugin.ShowPopWindowForTimeout(cordovaWebView,
                            mainActivity.getLayoutInflater(),jsonObject.getString("info"),timeout);

                }
                if (jsonObject.getString("model").equals("change"))
                {
                    CustomPopWindowPlugin.Setpoptext(jsonObject.getString("info"));

                }
                if (jsonObject.getString("model").equals("close"))
                {
                    CustomPopWindowPlugin.CLosePopwindow();

                }
//                callbackJs = jsonObject.getString("callback");


                return;

            }
            catch (Exception e)
            {e.printStackTrace();}

        }


        if (id.equals(INFO_ACTION)) //
        {

            try {
                JSONObject jsonObject = (JSONObject)data;
                String title = jsonObject.getString("title");
                if (title.equals(""))
                    title = "信息";
                String info = jsonObject.getString("info");
                int timeout= jsonObject.getInt("timeout");

                CustomPopWindowPlugin.ShowPopWindow(cordovaWebView,mainActivity.getLayoutInflater(),
                        title,info,timeout);

                return;

            }
            catch (Exception e)
            {e.printStackTrace();}

        }



        if (id.equals(DIALOG_ACTION)) //
        {

            CustomPopWindowPlugin.ShowPopWindow(cordovaWebView,
                    mainActivity.getLayoutInflater(),"");

        }

        if (id.equals(INITWEB_ACTION)) //初始化web
        {
            try {
                setInitWeb((JSONObject)data);
            }
            catch (Exception e)
            {e.printStackTrace();}

        }

        if (id.equals(SCAN_ACTION)) //如果是菜单进行加载
        {
            mainActivity.cordovaPlugin = (CordovaPlugin)data;

            scanPlugin =new ScanPlugin(mainActivity,this);
            scanPlugin.showScanActivity();
        }


        if (id.equals(UPLOADFILE_ACTION)) //文件上传
        {
            filePlugin = new FilePlugin(mainActivity,this,(CordovaPlugin)data);
            filePlugin.uploadFile();
        }
        if (id.equals(FILEDOWNLOAD_ACTION)) //文件下载
        {
            filePlugin = new FilePlugin(mainActivity,this,(CordovaPlugin)data);
            filePlugin.fileDownload();
        }
        if (id.equals(FILECANCELDOWNLOAD_ACTION)) //文件下载
        {
            filePlugin = new FilePlugin(mainActivity,this,(CordovaPlugin)data);
            filePlugin.cancelDownloadTask();
        }

    }







    /**
     * 设置 cordova插件信息
     * @param cordova cordovawebview
     * @param url 首页
     */
    public void setCordova(CordovaWebView cordova,String url)
    {

        cordovaWebView= cordova;
        mainurl =url;

    }


    /**
     * 回调 JS
     */
    @Override
    public void CallBackCordovaJS(String method,Object jsonObject) {


        //js 命令组合
        String js;
        if (jsonObject ==null)
            js= String.format("javascript:%1$s()", method);
        else
            js= String.format("javascript:%1$s(%2$s)", method, jsonObject);


        if (cordovaWebView != null)
            cordovaWebView.loadUrl(js);

    }



    void setInitWeb(JSONObject jsonObject)
    {
        Log.i("json",jsonObject.toString());
        try {
            isGoBack = jsonObject.getString("onGoHistory");
//            if (jsonObject.getString("screenOrientation").equals("portrait"))
//                mainActivity.setRequestedOrientation(
//                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            if (jsonObject.getString("screenOrientation").equals("landscape"))
//                mainActivity.setRequestedOrientation(
//                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            if (jsonObject.getString("screenOrientation").equals("all"))
//                mainActivity.setRequestedOrientation(
//                        ActivityInfo.SCREEN_ORIENTATION_USER);
            if (jsonObject.getString("showLeftButton").equals("true"))
                mainActivity.setHideLeftButton(true);
            else
                mainActivity.setHideLeftButton(false);
            GlobalConfig.globalConfig.setDebug(
                    (jsonObject.getString("debug") =="true" )?true:false);

        }
        catch (Exception e)
        {e.printStackTrace();}

    }

    @Override
    public void loadWebUrl(String Url) {
        cordovaWebView.loadUrl(Url);
    }

    /**
     * 实现积累加载menu方法
     * @param data  menu json 信息
     * @return menulist
     */
    @Override
    public int getMenuList(JSONArray data) {

//        int result=1;
        try
        {
//            JSONObject jsonappcode = data.getJSONObject(0);
            JSONObject jsonmenu = data.getJSONObject(0);//接口
//            JSONObject jsonappservercode = data.getJSONObject(2);


//            result = jsonmenu.getInt("placement");
            List<Map<String, Object>> mapList =new ArrayList<Map<String, Object>>();

            JSONArray jsonArray = jsonmenu.getJSONArray("items");

            Map<String, Object> map;
            InputStream inputStream;
            Bitmap bitmap;
            for (int i =0; i < jsonArray.length();i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                map =new Hashtable<String, Object>();
                map.put("menuKey",jsonObject.getString("menuKey"));
                map.put("menu_name",jsonObject.getString("name"));
                if (GlobalConfig.globalConfig.getIsCachefile())
                    inputStream = new FileInputStream(
                            SuyApplication.getApplication().getRelAppPath + jsonObject.getString("icon"));
                else
                    inputStream = new FileInputStream(Environment.getExternalStorageDirectory() + "/suyuan/"+
                            SuyApplication.getApplication().getRelAppPath + jsonObject.getString("icon"));
//
//                inputStream = mainActivity.getAssets().open(jsonObject.getString("icon"));
                bitmap = BitmapFactory.decodeStream(inputStream);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
                inputStream.close();
                map.put("menu_image",bitmapDrawable);
                map.put("uri",jsonObject.getString("uri"));
                mapList.add(map);
            }

//            if (result ==1) {
//                leftmapList = mapList;
//            }
//            else if (result ==2) {
//                rightmapList = mapList;
//            }

            bottommapList = mapList;

        }
        catch (Exception e)
        {
            e.printStackTrace();

        }
        return 2;
    }


}
