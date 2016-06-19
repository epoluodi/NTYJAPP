package com.suypower.pms.view.plugin.word;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.PopupWindow;

import com.aspose.words.Document;

import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.SuyClient;
import com.suypower.pms.app.configxml.GlobalConfig;
import com.suypower.pms.view.MainActivity;
import com.suypower.pms.view.plugin.BaseViewPlugin;
import com.suypower.pms.view.plugin.CordovaWebViewPlugin;
import com.suypower.pms.view.plugin.CustomPopWindowPlugin;

import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.UUID;

/**
 * word 控制类
 * @author YXG 2015-04-13
 */
public class WordPlugin extends BaseViewPlugin {



    public static final String TAG = "WordPlugin";

    private SuyClient suyClient;

    MainActivity mainActivity;
    BaseViewPlugin baseViewPlugin = null;
    PopupWindow popupWindow=null;
    RunnableMakeWord runnableMakeWord;
    CordovaPlugin cordovaPlugin;
    Thread thread;
    SingnaturepadView2Plugin singnaturepadView2Plugin;

    String docUUID;
    /**
     * 拍照狗展函数
     *
     * @param mainActivity
     * @param baseViewPlugin 传入调用的基础插件
     */
    public WordPlugin(MainActivity mainActivity, BaseViewPlugin baseViewPlugin) {
        this.mainActivity = mainActivity;
        this.baseViewPlugin = baseViewPlugin;
        runnableMakeWord = new RunnableMakeWord();

    }




    /**
     * 回调 JS
     */
    @Override
    public void CallBackCordovaJS(String method,Object jsonObject) {

//        if (method.equals("OK"))
//            cordovaPlugin.callbackContext.success(params);
//        if (method.equals("Cancel"))
//            cordovaPlugin.callbackContext.error(params);
        //js 命令组合
        String js = String.format("javascript:%1$s(%2$s)", method, jsonObject);


        if (baseViewPlugin != null)
            ((CordovaWebViewPlugin) baseViewPlugin).cordovaWebView.loadUrl(js);

    }



    //处理word 执行类
    protected class RunnableMakeWord  implements Runnable
    {
        JSONObject jsonObject;

        Message message;
        public void setRunInfo(JSONObject jsonObject)
        {
            this.jsonObject = jsonObject;

        }


        @Override
        public void run() {


            try
            {
                String wordpath = jsonObject.getString("templatePath");
                JSONObject jsonObjectparams = jsonObject.getJSONObject("placeholder");
                String dataDir = SuyApplication.getApplication().getRelAppPath + wordpath;
                InputStream inputStream;
                if (!GlobalConfig.globalConfig.getIsCachefile())
                     inputStream = mainActivity.getAssets().open(dataDir);
                else
                     inputStream =new FileInputStream(dataDir);

//                InputStream inputStream = new FileInputStream(Environment.getExternalStorageDirectory()
//                + "/suyuan/www/apps/dyyk/template/bargain.docx");
                Document doc = new Document(inputStream);
                Iterator<String> iterator =  jsonObjectparams.keys();
                while (iterator.hasNext())
                {
                    String key = iterator.next();
                    doc.getRange().replace(key, jsonObjectparams.getString(key),false,false);

                }
                UUID uuid = UUID.randomUUID();
                docUUID =uuid.toString();
                doc.save(getDocPath(docUUID));
                doc.save(getDocPathForImage(docUUID));
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("file", docUUID);
                    jsonObject.put("fileType","WORD");
                }
                catch (Exception e)
                {e.printStackTrace();}
//                handlerwork.sendEmptyMessage(0);
                cordovaPlugin.callbackContext.success(jsonObject);



            }
            catch (Exception e)
            {
                e.printStackTrace();

            }


        }


    }

    Handler handlerwork = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 0:
                    CustomPopWindowPlugin.CLosePopwindow();

                    break;
            }
        }
    };



    public static String getDocPath(String docuuid)
    {
        String path = String.format("%1$s.docx",docuuid);
//        path =Environment.getExternalStorageDirectory() +File.separator + path;

        path =  SuyApplication.getApplication().getCacheDir() + File.separator + path;
        return path;
    }

    public static String getDocPathForImage(String docuuid)
    {
        String path = String.format("%1$s_iamge.docx",docuuid);
//        path =Environment.getExternalStorageDirectory() +File.separator + path;

        path =  SuyApplication.getApplication().getCacheDir() + File.separator + path;
        return path;
    }

    public void makeWordForSign(String uuid,Handler handler)
    {
        Log.i("pngpath",uuid);
    }


    //生成word
    public void makeWord(CordovaPlugin data)
    {
        cordovaPlugin = data;
        runnableMakeWord.setRunInfo((JSONObject)cordovaPlugin.jsondata);
        thread = new Thread(runnableMakeWord);
        thread.start();
    }


    //显示 签字版
    public void showSignaturePad(CordovaPlugin data)
    {

        cordovaPlugin =data;




        Intent intent  = new Intent(mainActivity,SingnaturepadView2Plugin.class);
        try {
//            Bundle bundle = new Bundle();
            JSONObject jsonObject = ((JSONObject) data.jsondata);
//            bundle.putString("docpath", getDocPath(jsonObject.getString("file")));
//            bundle.putString("uuid", jsonObject.getString("file"));
//            bundle.putInt("x",jsonObject.getInt("xOffSet"));
//            bundle.putInt("y",jsonObject.getInt("yOffSet"));
//            intent.putExtras(bundle);
            singnaturepadView2Plugin = new SingnaturepadView2Plugin(mainActivity,
                    jsonObject.getInt("xOffSet"),jsonObject.getInt("yOffSet"),
                    getDocPath(jsonObject.getString("file")),jsonObject.getString("file"));
        }
        catch (Exception e)
        {e.printStackTrace();}



        mainActivity.cordovaPlugin = data;

        singnaturepadView2Plugin.show();

//        mainActivity.startActivityForResult(intent, SignaturepadViewPlugin.SIGNATUREPADRESULTREQUEST);
//        mainActivity.overridePendingTransition(android.R.anim.slide_in_left,
//                android.R.anim.slide_out_right);
    }

    //显示 签字版
    public void showPreviewWord(CordovaPlugin data)
    {
        cordovaPlugin =data;
        Intent intent  = new Intent(mainActivity,WordPreviewForImage.class);
        try {
            JSONObject jsonObject = ((JSONObject) data.jsondata);
            Bundle bundle = new Bundle();

            bundle.putString("uuid", jsonObject.getString("file"));
            intent.putExtras(bundle);
            cordovaPlugin.callbackContext.success();
        }
        catch (Exception e)
        {e.printStackTrace();}
        mainActivity.startActivity(intent);
        mainActivity.overridePendingTransition(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
    }


    @Override
    public void loadWebUrl(String Url) {

    }

    @Override
    public void showOptionMenu(View v, int menutype) {

    }

    @Override
    public void onCordovaMessage(String id, Object data) {

    }

    @Override
    public int getMenuList(JSONArray menujson) {
        return 0;
    }
}
