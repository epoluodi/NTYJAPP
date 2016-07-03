package com.suypower.pms.app.task;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import com.suypower.pms.app.Config;
import com.suypower.pms.app.ReturnData;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.SuyCallBackMsg;
import com.suypower.pms.app.SuyHttpClient;
import com.suypower.pms.app.configxml.GlobalConfig;
import com.suypower.pms.view.plugin.camera.CameraPlugin;
import com.suypower.pms.view.plugin.chat.ChatMessage;
import com.suypower.pms.view.plugin.fileEx.FilePlugin;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 上传照片 任务类
 *
 * @author YXG
 */
public class FileUpLoad extends BaseTask {


    public static final int UPLOADFILE = 1;//文件上传


    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 30 * 1000;   //超时时间
    private static final String CHARSET = "utf-8"; //设置编码

    //    public String[] files;
    public String mediaid;
    public String mediaidtype = ".jpg";
    public Object flag;
    public String mediatype;
    public Boolean IsNotics = false;
    List<NameValuePair> pairList;
    InterfaceTask interfaceTask;
    int type;


    public FileUpLoad(InterfaceTask interfaceTask, int type) {
        super();
        this.interfaceTask = interfaceTask;
        this.type = type;
        pairList = new ArrayList<>();
    }


    @Override
    public void startTask() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    switch (type) {
                        case UPLOADFILE:
                            uploadfile();
                            break;

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void stopTask() {

    }


    public void setPostValuesForKey(String Key, String value) {
        BasicNameValuePair basicNameValuePair = new BasicNameValuePair(Key, value);
        pairList.add(basicNameValuePair);


    }


    UrlEncodedFormEntity getPostData() {
        try {
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(pairList, HTTP.UTF_8);
            return urlEncodedFormEntity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void uploadfile() {

        String filetype = "";
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";   //内容类型

        String BOUNDARY = UUID.randomUUID().toString();  //边界标识   随机生成

        String RequestURL = String.format("%1$supload", GlobalConfig.globalConfig.getApiUrl());
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);  //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false);  //不允许使用缓存
            conn.setRequestMethod("POST");  //请求方式

            conn.setRequestProperty("Charset", CHARSET);  //设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            conn.setRequestProperty("token", SuyApplication.getApplication()
                    .getSuyClient().getSuyUserInfo().m_loginResult.m_strSKey);
            conn.setRequestProperty("deviceID", SuyApplication.getApplication()
                    .getUuid());
            conn.setRequestProperty("deviceType", "02");


            ByteArrayOutputStream baos = null;
            InputStream is = null;
            if (!IsNotics) {
                switch (((ChatMessage) flag).getMessageTypeEnum()) {
                    case PICTURE:
                        conn.setRequestProperty("mediaId", mediaid);
                        conn.setRequestProperty("mediaType",mediatype);
                        filetype = ".jpg";
//                        is = new FileInputStream(SuyApplication.getApplication().getCacheDir() + File.separator + mediaid + filetype);
                        Bitmap bitmap = CameraPlugin.bitbmpfrommediaLocal(mediaid, 0);
                        baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        bitmap.recycle();
                        break;
                    case AUDIO:
                        conn.setRequestProperty("mediaId", mediaid);
                        conn.setRequestProperty("mediaType",mediatype);
                        filetype = ".aac";
                        is = new FileInputStream(SuyApplication.getApplication().getCacheDir() + File.separator + mediaid + filetype);

                        break;
                }
            } else {
                conn.setRequestProperty("mediaId", mediaid);
                conn.setRequestProperty("mediaType",mediatype);
                filetype = mediaidtype;
                if (filetype.equals(".jpg")) {
                    Bitmap bitmap = CameraPlugin.bitbmpfrommediaLocal(mediaid, 0);
                    baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    bitmap.recycle();
                } else {
                    is = new FileInputStream(SuyApplication.getApplication().getCacheDir() + File.separator + mediaid + filetype);
                }
            }
            /**
             * 当文件不为空，把文件包装并且上传
             */
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            StringBuffer sb = new StringBuffer();
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINE_END);
            /**
             * 这里重点注意：
             * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
             * filename是文件的名字，包含后缀名的   比如:abc.png
             */

            sb.append("Content-Disposition: form-data; name=\"img\";filename=\"" + mediaid + filetype + "\"" + LINE_END);
            sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
            sb.append(LINE_END);
            dos.write(sb.toString().getBytes());

            if (baos != null) {
                dos.write(baos.toByteArray());
                baos.close();
            } else {
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
            }

            dos.write(LINE_END.getBytes());
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
            dos.write(end_data);
            dos.flush();
            dos.close();
            /**
             * 获取响应码  200=成功
             * 当响应成功，获取响应的流
             */
            int res = conn.getResponseCode();
            Log.e(TAG, "response code:" + res);
            Log.e(TAG, "request success");
            InputStream input = conn.getInputStream();

            BufferedReader reader_post = new BufferedReader(new InputStreamReader(
                    input, "utf-8"));
            String result = reader_post.readLine();
            Log.e(TAG, "result : " + result);
            input.close();
            reader_post.close();

            ReturnData returnData = new ReturnData(new JSONObject(result), true);
            Message message = handler.obtainMessage();
            if (returnData.getReturnCode() == 0) {
                message.what = UploadFileTask;
                message.arg1 = SUCCESS;
                message.arg2 = UPLOADFILE;
                message.obj = flag;
                handler.sendMessage(message);
            } else {
                message.what = UploadFileTask;
                message.arg1 = FAILED;
                message.arg2 = UPLOADFILE;
                message.obj = flag;
                handler.sendMessage(message);
            }


        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = UploadFileTask;
            message.arg1 = FAILED;
            message.arg2 = UPLOADFILE;
            message.obj = flag;
            handler.sendMessage(message);
        }

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (interfaceTask != null)
                interfaceTask.TaskResultForMessage(msg);

        }
    };


    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }


    byte[] GetFileBytes(String filepath) {
        byte[] buffer;
        try {

            FileInputStream fileInputStream = new FileInputStream(filepath);

            if (fileInputStream.available() > (1024 * 1024)) {
                fileInputStream.close();
                Bitmap bitmap = CameraPlugin.decodeBitmap(filepath, 2);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                bitmap.recycle();
                return baos.toByteArray();
            }
//			FileInputStream fileInputStream =new FileInputStream(filepath);
//
//
            buffer = new byte[fileInputStream.available()];
            fileInputStream.read(buffer);
            fileInputStream.close();
            return buffer;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
