package com.suypower.pms.view.plugin.fileEx;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.PopupWindow;

import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.SuyClient;
import com.suypower.pms.view.MainActivity;
import com.suypower.pms.view.plugin.BaseViewPlugin;
import com.suypower.pms.view.plugin.CordovaWebViewPlugin;
import com.suypower.pms.view.plugin.CustomPopWindowPlugin;

import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * 显示拍照功能
 * 拍照
 * 预览
 * 照片上传
 *
 * @author YXG 2015-04-13
 */
public class FilePlugin extends BaseViewPlugin {


    /**
     * 上传成功
     */
    public final static int SUCCESS = 0;
    /**
     * 上传失败
     */
    public final static int FAIL = 1;
    /**
     * 上传超时
     */
    public final static int TIMEOUT = 2;
    /**
     * 上传结束
     */
    public final static int UPLOAD_FINISH = 3;


    public static final String TAG = "FilePlugin";
    public static final String PhotoPath = Environment.getExternalStorageDirectory() + "/DCIM/Camera/";
    public static final String FileCache = SuyApplication.getApplication().getCacheDir() + File.separator;

    private SuyClient suyClient;

    MainActivity mainActivity;
    BaseViewPlugin baseViewPlugin = null;
    PopupWindow popupWindow = null;
    CordovaPlugin cordovaPlugin;


    /**
     * 拍照狗展函数
     *
     * @param mainActivity
     * @param baseViewPlugin 传入调用的基础插件
     */
    public FilePlugin(MainActivity mainActivity, BaseViewPlugin baseViewPlugin, CordovaPlugin cordovaPlugin) {
        this.mainActivity = mainActivity;
        this.baseViewPlugin = baseViewPlugin;
        this.cordovaPlugin = cordovaPlugin;

    }


    /**
     * 上传信息回馈
     */
    Handler handlerUpload = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case SUCCESS:
                    CustomPopWindowPlugin.Setpoptext("上传中... " + msg.obj.toString());
                    break;
                case FAIL:
                    CustomPopWindowPlugin.CLosePopwindow();
//                    Toast.makeText(mainActivity, "文件上传失败", Toast.LENGTH_SHORT).show();
                    cordovaPlugin.callbackContext.error(msg.obj.toString());
                    break;
                case UPLOAD_FINISH:
//                    CustomPopWindowPlugin.CLosePopwindow();
//                    Toast.makeText(mainActivity, "文件上传成功", Toast.LENGTH_SHORT).show();
//                    CallBackCordovaJS("UploadPhotoResult", msg.obj);
                    suyClient.setNullCallBackHandler(handlerUpload);
                    String filelist = "";
                    JSONArray jsonArray = (JSONArray) msg.obj;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            filelist += jsonArray.getString(i) + ",";
                        } catch (Exception e) {
                            e.printStackTrace();
                            cordovaPlugin.callbackContext.error("结果解析错误");
                        }
                    }
                    filelist = filelist.substring(0, filelist.length() - 1);
                    Log.i("文件上传返回id",filelist);
                    cordovaPlugin.callbackContext.success(filelist);

                    break;
            }


        }
    };


    Handler handlerfiledownload = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case SUCCESS:
                    CustomPopWindowPlugin.Setpoptext("下载... " + msg.obj.toString());
                    break;
                case FAIL:
                    CustomPopWindowPlugin.CLosePopwindow();
//                    Toast.makeText(mainActivity, "文件下载失败", Toast.LENGTH_SHORT).show();
                    cordovaPlugin.callbackContext.error(msg.obj.toString());
                    break;
                case UPLOAD_FINISH:
//                    CustomPopWindowPlugin.CLosePopwindow();
//                    Toast.makeText(mainActivity, "文件下载成功", Toast.LENGTH_SHORT).show();
//                    CallBackCordovaJS("UploadPhotoResult", msg.obj);
                    suyClient.setNullCallBackHandler(handlerUpload);
                    String filelist = "";
                    JSONArray jsonArray = (JSONArray) msg.obj;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            filelist += "stereo://"+jsonArray.getString(i) + ",";
                        } catch (Exception e) {
                            e.printStackTrace();
                            cordovaPlugin.callbackContext.error("结果解析错误");
                        }
                    }
                    filelist = filelist.substring(0, filelist.length() - 1);
                    Log.i("文件上传返回id",filelist);
                    cordovaPlugin.callbackContext.success(filelist);

                    break;
            }


        }
    };


    /**
     * 下载文件
     */
    public void fileDownload()
    {
        JSONObject jsonObject1 = (JSONObject) cordovaPlugin.jsondata;
        try {

//            CustomPopWindowPlugin.ShowPopWindow(
//                    ((CordovaWebViewPlugin) baseViewPlugin).cordovaWebView
//                    , mainActivity.getLayoutInflater()
//                    , "准备文件上传");



//            try {
//                if (jsonObject1.getString("showProcessDialog").equals("open"))
//                {
//                    CustomPopWindowPlugin.ShowPopWindow(
//                            ((CordovaWebViewPlugin) baseViewPlugin).cordovaWebView
//                            , mainActivity.getLayoutInflater()
//                            , "文件下载");
//                }
//            }
//            catch (Exception e)
//            {e.printStackTrace();}

            JSONArray jsonArray = jsonObject1.getJSONArray("mediaID");
            suyClient = SuyApplication.getApplication().getSuyClient();
            suyClient.setCallBackHandler(handlerfiledownload);

            String[] files = new String[jsonArray.length()];


            for (int i = 0; i < files.length; i++) {
                files[i] = jsonArray.getString(i);

            }
            suyClient.filedownload(files);


        } catch (Exception e) {
            suyClient.setNullCallBackHandler(handlerUpload);
            cordovaPlugin.callbackContext.error("下载失败");
            CustomPopWindowPlugin.CLosePopwindow();
            e.printStackTrace();
        }

    }


    /**
     * 释放下载线程
     */
    public void cancelDownloadTask()
    {
        suyClient = SuyApplication.getApplication().getSuyClient();
        suyClient.cancelDownloadTask();
    }

    /**
     * 上传照片
     */
    public void uploadFile() {
        JSONObject jsonObject1 = (JSONObject) cordovaPlugin.jsondata;
        try {

//            CustomPopWindowPlugin.ShowPopWindow(
//                    ((CordovaWebViewPlugin) baseViewPlugin).cordovaWebView
//                    , mainActivity.getLayoutInflater()
//                    , "准备文件上传");
            String fileExt = "";


            try {
                if (jsonObject1.getString("showProcessDialog").equals("open"))
                {
                    CustomPopWindowPlugin.ShowPopWindow(
                            ((CordovaWebViewPlugin) baseViewPlugin).cordovaWebView
                            , mainActivity.getLayoutInflater()
                            , "准备文件上传");
                }
            }
            catch (Exception e)
            {e.printStackTrace();}
            if (jsonObject1.getString("fileType").equals("WORD"))
                fileExt = ".docx";
            if (jsonObject1.getString("fileType").equals("JPG"))
                fileExt = ".jpg";
            JSONArray jsonArray = jsonObject1.getJSONArray("files");
            suyClient = SuyApplication.getApplication().getSuyClient();
            suyClient.setCallBackHandler(handlerUpload);

            String[] files = new String[jsonArray.length()];

            String filename;
            for (int i = 0; i < files.length; i++) {
                if (jsonArray.getString(i).contains("stereo://"))
                {
                    filename = jsonArray.getString(i).replace("stereo://"
                    ,"");
                }
                else
                    filename= jsonArray.getString(i);
                if (fileExt.equals(".docx")){
                    files[i] = mainActivity.getCacheDir() + File.separator +
                            filename+"_iamge" + fileExt;
                }
                else
                    files[i] = mainActivity.getCacheDir() + File.separator +
                        filename + fileExt;

            }
            suyClient.uploadFile(files);


        } catch (Exception e) {
            suyClient.setNullCallBackHandler(handlerUpload);
            cordovaPlugin.callbackContext.error("上传失败");
            CustomPopWindowPlugin.CLosePopwindow();
            e.printStackTrace();
        }
    }


    public static int upZipFile(File zipFile, String folderPath) throws ZipException, IOException {
        //public static void upZipFile() throws Exception{
        ZipFile zfile = new ZipFile(zipFile);
        Enumeration zList = zfile.entries();
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                Log.d("upZipFile", "ze.getName() = " + ze.getName());
                String dirstr = folderPath + ze.getName();
                //dirstr.trim();
                dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                Log.d("upZipFile", "str = " + dirstr);
                File f = new File(dirstr);
                f.mkdir();
                continue;
            }
            Log.d("upZipFile", "ze.getName() = " + ze.getName());
            OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
            InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
            int readLen = 0;
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
        }
        zfile.close();
        Log.d("upZipFile", "finishssssssssssssssssssss");
        return 0;
    }

    /**
     * 40     * 给定根目录，返回一个相对路径所对应的实际文件名.
     * 41     * @param baseDir 指定根目录
     * 42     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * 43     * @return java.io.File 实际的文件
     * 44
     */
    public static File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                try {
                    //substr.trim();
                    substr = new String(substr.getBytes("8859_1"), "GB2312");

                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ret = new File(ret, substr);

            }
            Log.d("upZipFile", "1ret = " + ret);
            if (!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length - 1];
            try {
                //substr.trim();
                substr = new String(substr.getBytes("8859_1"), "GB2312");
                Log.d("upZipFile", "substr = " + substr);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            ret = new File(ret, substr);
            Log.d("upZipFile", "2ret = " + ret);
            return ret;
        }


        return  new File(baseDir, absFileName);
    }


    /**
     * 文件拷贝
     * @param context
     * @param Raw 资源文件
     * @param filename 目标文件名称
     */
    public static void CopyDb(Context context, int Raw,String filename) {
        InputStream inputStream;
        try {
            inputStream = context.getResources().openRawResource(Raw);
            byte[] bytebuff = new byte[inputStream.available()];
            inputStream.read(bytebuff);
            File file = new File(context.getFilesDir() + filename );
            if (file.exists())
                file.delete();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytebuff);
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    public static void CopyDb2(Context context,String filename) {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(filename);
            byte[] bytebuff = new byte[inputStream.available()];
            inputStream.read(bytebuff);

            File file = new File(Environment.getExternalStorageDirectory(),"db2.sqlite");
            FileOutputStream fileOutputStream = new FileOutputStream(
                    file );
            fileOutputStream.write(bytebuff);
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static void CopyFilePhoto(Context context, String srcfile,String filename) {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(srcfile);
            byte[] bytebuff = new byte[inputStream.available()];
            inputStream.read(bytebuff);
            File file = new File( filename );
            if (file.exists())
                file.delete();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytebuff);
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    public static void CopyFile(Context context, String srcfile,String filename) {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(srcfile);
            byte[] bytebuff = new byte[inputStream.available()];
            inputStream.read(bytebuff);
            File file = new File(context.getFilesDir() + filename );
            if (file.exists())
                file.delete();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytebuff);
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
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
