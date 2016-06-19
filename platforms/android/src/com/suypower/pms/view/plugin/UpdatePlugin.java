package com.suypower.pms.view.plugin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.suypower.pms.app.ReturnData;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.SuyHttpClient;
import com.suypower.pms.app.configxml.GlobalConfig;
import com.suypower.pms.view.MainTabView;
import com.suypower.pms.view.dlg.AlertDlg;
import com.suypower.pms.view.plugin.fileEx.FilePlugin;

import org.apache.cordova.LOG;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * 业务更新模块
 *
 * @author YXG 2015-04-13
 */
public class UpdatePlugin extends BaseViewPlugin {


    public static final String TAG = "UpdatePlugin";
    public static final String UPDATEURL = GlobalConfig.globalConfig.getAppUpgradeUrl() + "android/download";
    static final String DOWNLOADAPKURL = "";
    public final static int DATA = 0;
    public final static int NETERROR = -1;
    public final static int DATAERROR = -2;
    public final static int DATAAYSISERROR = -3;
    public final static int DOWNLAODERROR = -4;

    private IUpdateResult iUpdateResult;

    private Context context;
    private String ver;
    private int vercode;


    private AjaxHttpPlugin ajaxHttpPlugin;
    private SuyHttpClient suyHttpClient;

    private Handler handler = null;
    private Object data = null;

    private HttpURLConnection httpURLConnection;
    private HttpEntity httpEntity;

    private DownloadManager downloadManager;
    private DownloadCompleteReceiver downloadCompleteReceiver;

    public class DownloadServer {
        Context context;
        DownloadManager downloadManager;
        DownloadCompleteReceiver downloadCompleteReceiver;

        /**
         * 初始化下载器 *
         */
        public DownloadServer(Context context1, DownloadCompleteReceiver downloadCompleteReceiver1) {
            downloadCompleteReceiver = downloadCompleteReceiver1;
            context = context1;
        }

        public DownloadManager initDownloadServer(final String updateurl) {

            try {
                downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);

                //设置下载地址
                DownloadManager.Request down = new DownloadManager.Request(
                        Uri.parse(updateurl));
                down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                        | DownloadManager.Request.NETWORK_WIFI);
                down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                down.setVisibleInDownloadsUi(true);
                down.setDestinationInExternalFilesDir(context,
                        Environment.DIRECTORY_DOWNLOADS, "pms.apk");
                downloadManager.enqueue(down);

                return downloadManager;
            } catch (Exception e) {
                return null;
            }

        }


    }


    public class DownloadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("下载 返回的状态",intent.getAction());
            //判断是否下载完成的广播
            if (intent.getAction().equals(
                    DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {

                //获取下载的文件id
                long downId = intent.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                //自动安装apk
                context.unregisterReceiver(downloadCompleteReceiver);

                installAPK(downloadManager.getUriForDownloadedFile(downId));
            }


        }

        /**
         * 安装apk文件
         */
        private void installAPK(Uri apk) {

            // 通过Intent安装APK文件
            if (apk == null) {

                Toast.makeText(context, "下载更新失败，请重新尝试", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intents = new Intent();
            intents.setAction("android.intent.action.VIEW");
            intents.addCategory("android.intent.category.DEFAULT");
            intents.setType("application/vnd.android.package-archive");
            intents.setData(apk);
            intents.setDataAndType(apk, "application/vnd.android.package-archive");
            intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intents);
//            ((MainTabView)getActivity()).FinishAPP();


        }

    }


    public void downloadAPK(String url) {
        String updateurl = String.format(url, DOWNLOADAPKURL);
        if (downloadCompleteReceiver != null)
            context.unregisterReceiver(downloadCompleteReceiver);
        downloadCompleteReceiver = new DownloadCompleteReceiver();
        DownloadServer downloadServer = new DownloadServer(context, downloadCompleteReceiver);
        context.registerReceiver(downloadCompleteReceiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        downloadManager = downloadServer.initDownloadServer(updateurl);
        if (downloadManager == null) {
            context.unregisterReceiver(downloadCompleteReceiver);
            Toast.makeText(context, "更新失败", Toast.LENGTH_SHORT).show();
            if (iUpdateResult != null)
                iUpdateResult.DownloadResult(-1, "");
        }

    }


    public Object getData() {
        return data;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    /**
     * 初始化更新
     */
    public UpdatePlugin(Context context, IUpdateResult iUpdateResult) {
        this.context = context;

        //初始插件
        ajaxHttpPlugin = new AjaxHttpPlugin();
        //http初始
        suyHttpClient = ajaxHttpPlugin.initHttp();
        this.iUpdateResult = iUpdateResult;

        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            ver = info.versionName;
            vercode = info.versionCode;
            Log.i("APP版本:", ver + " " + String.valueOf(vercode));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public Boolean CheckVerison() {

        String checkUrl = String.format("%1$snewVersion",
                GlobalConfig.globalConfig.getAppUpgradeUrl());
        suyHttpClient.openRequest(checkUrl, SuyHttpClient.REQ_METHOD_GET);
        if (!suyHttpClient.sendRequest())//发送请求
        {
            if (iUpdateResult != null)
                iUpdateResult.CheckResult(NETERROR, "网络错误");
            return false;
        }

        //获得返回数据
        byte[] buffer = suyHttpClient.getRespBodyData();
        if (buffer == null) {
            if (iUpdateResult != null)
                iUpdateResult.CheckResult(DATAERROR, "数据错误");
            return false;
        }
        try {
            String result = new String(buffer, "utf-8");
            Log.i("更新信息返回:", result);
            if (iUpdateResult != null)
                iUpdateResult.CheckResult(DATAERROR, "");
            JSONObject jsonObject = new JSONObject(result);
            ReturnData returnData = new ReturnData(jsonObject, true);
            if (returnData.getReturnCode() != 0) {
                if (iUpdateResult != null)
                    iUpdateResult.CheckResult(DATAAYSISERROR, "检查错误");
                return false;
            }

            int _vercode = Integer.valueOf(returnData.getReturnData().getString("versionNum"));

            if (vercode < _vercode) {
                data = returnData.getReturnData();
                return true;
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            if (iUpdateResult != null)
                iUpdateResult.CheckResult(DATAAYSISERROR, "数据解析错误");
            return false;
        }
    }


    /**
     * 打开提示框
     * @param context
     * @param ver
     * @param isupdate
     */
    public void showAlertDialog(Context context, String ver, Boolean isupdate) {


        AlertDlg alertDlg;

        if (!isupdate) {
            alertDlg = new AlertDlg(context, AlertDlg.AlertEnum.TIPS);
            alertDlg.setCancelClickLiseter(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (iUpdateResult != null)
                        iUpdateResult.DownloadResult(0, "");
                    alertDlg.dismiss();
                }
            });
        }
        else
            alertDlg = new AlertDlg(context, AlertDlg.AlertEnum.ALTERTYPE);
        alertDlg.setContentText("发现最新版本：" + ver + ",请更新");
        alertDlg.setOkClickLiseter(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iUpdateResult != null)
                    iUpdateResult.DownloadResult(1, "");
                alertDlg.dismiss();
            }
        });

        alertDlg.show();

    }


    public Boolean DownloadDB(String appCode, String ver, String ext) {
        try {
            String Fileurl = String.format("%1$s/download?appCode=%2$S",
                    GlobalConfig.globalConfig.getAppUpgradeUrl(), appCode);
            Log.i("下载链接:", Fileurl);
            if (!GetHttpInputstream(Fileurl)) {
                if (iUpdateResult != null)
                    iUpdateResult.DownloadResult(NETERROR, "网络错误");
                return false;
            }

            byte[] buffer = readStreamtoBytes();
            Log.i("文件下载长度==", String.valueOf(buffer.length));


            if (buffer == null) {
                if (iUpdateResult != null)
                    iUpdateResult.DownloadResult(DOWNLAODERROR, "下载失败");
                return false;
            }
            String filename;
            if (ext.equals("apk"))
                filename = Environment.getExternalStorageDirectory() + File.separator + ver.replace(".", "") + "." + ext;
            else
                filename = context.getFilesDir() + File.separator + ver.replace(".", "") + "." + ext;
            Log.i("下载文件名称:", filename);
            Log.i("下载文件大小:", String.valueOf(buffer.length));
            File fIle = new File(filename);
            if (fIle.exists())
                fIle.delete();
            FileOutputStream fileOutputStream = new FileOutputStream(fIle);
            fileOutputStream.write(buffer);
            fileOutputStream.close();

            return true;
        } catch (Exception e) {
            return false;
        } finally {
//            if (httpURLConnection !=null)
//                httpURLConnection.disconnect();


        }
    }


    byte[] readStreamtoBytes() {
        try {


            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//            int FileLen = httpURLConnection.getContentLength();
//            int FileLen = (int)httpEntity.getContentLength();
//            InputStream inStream = httpURLConnection.getInputStream();
            InputStream inStream = httpEntity.getContent();
//            Log.i("下载文件大小：",String.valueOf(FileLen));
//            if (FileLen ==0) {
//                FileLen = inStream.available();
//
//            }
            int maxbuff = 1024 * 100;
            byte[] buffer = new byte[maxbuff];
            int len = 0;

            if (inStream == null)
                throw new Exception();
            while ((len = inStream.read(buffer)) != -1) {

                outStream.write(buffer, 0, len);
//                if (outStream.size() < FileLen)
//                {
//                    if ((FileLen - outStream.size()) < maxbuff)
//                    {
//                        maxbuff= (FileLen - outStream.size());
//                        buffer = new byte[maxbuff];
//                    }
//                    else
//                        buffer = new byte[maxbuff];
//
//                }

            }

            outStream.close();
            inStream.close();

            return outStream.toByteArray();
        } catch (Exception e) {

            return null;
        } finally {
//            httpURLConnection.disconnect();
            suyHttpClient.closeRequest();
        }
    }


    Boolean GetHttpInputstream(String urlpath) {
        try {

            suyHttpClient.openRequest(urlpath, SuyHttpClient.REQ_METHOD_GET);
            suyHttpClient.sendRequest();

            HttpResponse httpResponse = suyHttpClient.getHttpResponse();
            if (httpResponse.getHeaders("Content-disposition").length == 0) {
                return false;
            }
            httpEntity = httpResponse.getEntity();
            if (httpEntity == null)
                return false;


//            URL url = new URL(urlpath);
//
//
//            httpURLConnection = (HttpURLConnection) url.openConnection();
//            httpURLConnection.setConnectTimeout(10000);
//
//            httpURLConnection.connect();


//            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                return true;
//            }


            return true;
        }
//        catch (IOException e)
//        {
//            httpURLConnection.disconnect();
//            e.printStackTrace();
//
//            return false;
//        }
        catch (Exception e) {
//            httpURLConnection.disconnect();
            suyHttpClient.closeRequest();

            return false;
        }

    }


    public static void updateAPK() {
        UpdatePlugin updatePlugin;
        updatePlugin = new UpdatePlugin(
                SuyApplication.getApplication().getApplicationContext()
                , null);
        if (updatePlugin.DownloadDB("000000", "new", "apk")) {
            /**
             * 安装apk文件
             */

//			final String apkVer = jsonObjectVer.getString("currentVersion");
            AlertDlg alertDlg = new AlertDlg(SuyApplication.getApplication(), AlertDlg.AlertEnum.ALTERTYPE);
            alertDlg.setContentText("发现新的版本");
            alertDlg.setOkClickLiseter(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File file1 = new File(Environment.getExternalStorageDirectory() + File.separator +
                            "new.apk");
                    Intent intents = new Intent();
                    intents.setAction("android.intent.action.VIEW");
                    intents.addCategory("android.intent.category.DEFAULT");
                    intents.setType("application/vnd.android.package-archive");
                    intents.setData(Uri.fromFile(file1));
                    intents.setDataAndType(Uri.fromFile(file1), "application/vnd.android.package-archive");
                    intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    SuyApplication.getApplication().startActivity(intents);

                }
            });

            alertDlg.show();

            return;

        }
    }


    public static Boolean checkUpdate() {
        try {
            InputStream inputStream;
            byte[] buffers = null;
            String str;

            File file;
            File filever;
            UpdatePlugin updatePlugin;
            updatePlugin = new UpdatePlugin(
                    SuyApplication.getApplication().getApplicationContext()
                    , null);
            try {

                if (updatePlugin.CheckVerison()) {
                    JSONArray vers = (JSONArray) updatePlugin.getData();
                    JSONObject jsonObjectVer;

                    for (int i = 0; i < vers.length(); i++) {

                        jsonObjectVer = vers.getJSONObject(i);
                        int ver = Integer.valueOf(jsonObjectVer.getString("currentVersion").replace(".", ""));
                        String strver = jsonObjectVer.getString("currentVersion");
                        if (jsonObjectVer.getString("appCode").equals("000000")) {

                            if (!strver.equals(SuyApplication.getApplication().AppVerName())) {//程序更新
                                //开始更新APP
                                if (updatePlugin.DownloadDB("000000",
                                        jsonObjectVer.getString("currentVersion"), "apk")) {
                                    /**
                                     * 安装apk文件
                                     */

                                    final String apkVer = jsonObjectVer.getString("currentVersion");
//                                    AlertDlg alertDlg = new AlertDlg(SuyApplication.getApplication().getApplicationContext(), AlertDlg.AlertEnum.ALTERTYPE);
//                                    alertDlg.setContentText("发现新的版本:" +apkVer +
//                                            "，系统将为您更新应用" );
//                                    alertDlg.setOkClickLiseter(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
                                    File file1 = new File(Environment.getExternalStorageDirectory() + File.separator +
                                            apkVer.replace(".", "") + ".apk");
                                    Intent intents = new Intent();
                                    intents.setAction("android.intent.action.VIEW");
                                    intents.addCategory("android.intent.category.DEFAULT");
                                    intents.setType("application/vnd.android.package-archive");
                                    intents.setData(Uri.fromFile(file1));
                                    intents.setDataAndType(Uri.fromFile(file1), "application/vnd.android.package-archive");
                                    intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    SuyApplication.getApplication().startActivity(intents);

//                                        }
//                                    });
//
//                                    alertDlg.show();

                                    return false;

                                }
                            }
                            continue;
                        } else {


                            if (!strver.equals(GlobalConfig.globalConfig.getAppInfos()[i - 1].getAppVer())) {
                                if (updatePlugin.DownloadDB(GlobalConfig.globalConfig.getAppInfos()[i - 1].getAppCode(),
                                        jsonObjectVer.getString("currentVersion"), "zip"))

                                {
                                    //开始更新


                                    file = new File(SuyApplication.getApplication().getFilesDir() + "/www/apps/" +
                                            GlobalConfig.globalConfig.getAppInfos()[i - 1].getAppCode() + "/");
                                    if (!file.exists())
                                        file.mkdir();
                                    filever = new File(SuyApplication.getApplication().getFilesDir() + "/www/apps/" +
                                            GlobalConfig.globalConfig.getAppInfos()[i - 1].getAppCode() +
                                            "/");
                                    filever.delete();
                                    filever.mkdir();


//							FilePlugin.CopyDb(SplashActivitybak.this
//									, getFilesDir() + File.separator + vers[i] + ".zip", "/www/apps/" + GlobalConfig.globalConfig.getAppInfos()[i-1].getAppCode() +
//									"/" +vers[i] + ".zip");

                                    FilePlugin.upZipFile(new File(SuyApplication.getApplication().getFilesDir() + File.separator + jsonObjectVer.getString("currentVersion").replace(".", "") + ".zip"),
                                            SuyApplication.getApplication().getFilesDir() + "/www/apps/" +
                                                    GlobalConfig.globalConfig.getAppInfos()[i - 1].getAppCode() + "/");
                                    SuyApplication.getApplication().getSuyDB().updateAppInfo(
                                            jsonObjectVer.getString("appCode"),
                                            jsonObjectVer.getString("currentVersion"));
                                    GlobalConfig.globalConfig.getAppInfos()[i - 1].setAppVer(jsonObjectVer.getString("currentVersion"));
                                    GlobalConfig.globalConfig.getAppInfos()[i - 1].setAppVerCode(ver);

                                }
                            }


                        }
                    }
                }

                return true;

                //先判断是否需要升级
            } catch (Exception e) {
                e.printStackTrace();
            }


            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return true;

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


    public interface IUpdateResult {


        void CheckResult(int state, String msg);

        void DownloadResult(int state, String msg);
    }


}
