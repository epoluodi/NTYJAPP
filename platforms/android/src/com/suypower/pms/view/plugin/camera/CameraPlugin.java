package com.suypower.pms.view.plugin.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;


import com.suypower.pms.R;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.SuyClient;
import com.suypower.pms.app.configxml.GlobalConfig;
import com.suypower.pms.view.BaseActivity;
import com.suypower.pms.view.BaseActivityPlugin;
import com.suypower.pms.view.MainActivity;
import com.suypower.pms.view.plugin.BaseViewPlugin;
import com.suypower.pms.view.plugin.CommonPlugin;
import com.suypower.pms.view.plugin.CordovaWebViewPlugin;
import com.suypower.pms.view.plugin.CustomPopWindowPlugin;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

/**
 * 显示拍照功能
 * 拍照
 * 预览
 * 照片上传
 *
 * @author YXG 2015-04-13
 */
public class CameraPlugin extends BaseViewPlugin {


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


    public static final String TAG = "CameraPlugin";
    public static final String PhotoPath = Environment.getExternalStorageDirectory()+"/DCIM/Camera/";

    private SuyClient suyClient;


    MainActivity mainActivity = null;
    BaseViewPlugin baseViewPlugin = null;
    PopupWindow popupWindow=null;


    BaseActivityPlugin baseActivityPlugin;
    Button btn_camera;
    Button btn_image;
    Button btn_cancel;
    int chooseimages=0; //照片 数量限制



    /**
     * 拍照狗展函数
     *
     * @param mainActivity
     * @param baseViewPlugin 传入调用的基础插件
     */
    public CameraPlugin(MainActivity mainActivity, BaseViewPlugin baseViewPlugin) {
        this.mainActivity = mainActivity;
        this.baseViewPlugin = baseViewPlugin;
    }


    /**
     * 拍照构造
     * @param baseActivityPlugin
     */
    public CameraPlugin(BaseActivityPlugin baseActivityPlugin) {
        this.baseActivityPlugin = baseActivityPlugin;
    }





    /**
     * 上传信息回馈
     */
    Handler handleriploadphoto =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1)
            {
                case SUCCESS:
                    CustomPopWindowPlugin.Setpoptext("上传中... " + msg.obj.toString());
                    break;
                case FAIL:
                    CustomPopWindowPlugin.CLosePopwindow();
                    Toast.makeText(mainActivity,"照片上传失败",Toast.LENGTH_SHORT).show();
                    break;
                case UPLOAD_FINISH:
                    CustomPopWindowPlugin.CLosePopwindow();
                    Toast.makeText(mainActivity,"照片上传成功",Toast.LENGTH_SHORT).show();
                    CallBackCordovaJS("UploadPhotoResult", msg.obj);
                    suyClient.setNullCallBackHandler(handleriploadphoto);
                    break;
            }


        }
    };


    public static Bitmap reSizeImage(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 计算出缩放比
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 矩阵缩放bitmap
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    /**
     * 上传照片
     * @param jsonObject
     */
    public void uploadPhoto(String jsonObject)
    {
        JSONObject jsonObject1;
        try
        {
            jsonObject1 =new JSONObject(jsonObject);
            int photos = jsonObject1.getInt("photos");
        CustomPopWindowPlugin.ShowPopWindow(
                ((CordovaWebViewPlugin) baseViewPlugin).cordovaWebView
                , mainActivity.getLayoutInflater()
                , "准备上传照片");
            JSONArray jsonArray = jsonObject1.getJSONArray("photofile");
            suyClient = SuyApplication.getApplication().getSuyClient();
            suyClient.setCallBackHandler(handleriploadphoto);
            suyClient.uploadPhoto(photos,jsonArray);
        }
        catch (Exception e)
        {
            suyClient.setNullCallBackHandler(handleriploadphoto);
            CustomPopWindowPlugin.CLosePopwindow();
            e.printStackTrace();
        }
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


    /**
     * 打开预览窗口
     */
    public void openPreviewPhoto(int precounts)
    {
        chooseimages=precounts;
        Intent intent = new Intent(mainActivity,PreviewPhotoViewPlugin.class);
        intent.putExtra("precounts", precounts);
        mainActivity.startActivityForResult(intent, PreviewPhotoViewPlugin.JSCallPreviewPhtoto);
        mainActivity.overridePendingTransition(R.anim.slide_in_from_bottom,R.anim.alpha_exit);
    }

    public void openPreviewPhotoForNavtive(int precounts)
    {
        chooseimages=precounts;
        Intent intent = new Intent(baseActivityPlugin,PreviewPhotoViewPlugin.class);
        intent.putExtra("precounts", precounts);
        baseActivityPlugin.startActivityForResult(intent, PreviewPhotoViewPlugin.JSCallPreviewPhtoto);
        baseActivityPlugin.overridePendingTransition(R.anim.slide_in_from_bottom,R.anim.alpha_exit);
    }

    /**
     * 打开URL
     * @param json
     */
    public void openPreviewUrlPhoto(Object json)
    {
        Intent intent = new Intent(mainActivity,PreviewPhotoViewPager.class);
        Bundle bundle = new Bundle();



        JSONObject jsonObject = (JSONObject)json;
        String src="";
        String[] urlfiles=null;
        try
        {
            src = jsonObject.getString("src");
            JSONArray jsonArray = jsonObject.getJSONArray("images");
            urlfiles = new String[jsonArray.length()];
            for (int i =0; i<jsonArray.length();i++)
            {
                urlfiles[i]  =jsonArray.getString(i);

            }


        }
        catch (Exception e)
        {e.printStackTrace();}
        if (urlfiles[0].contains("stereo://"))
            bundle.putInt("model",4);//本地浏览
        else
            bundle.putInt("model",3);//全部照片预览模式
        bundle.putStringArray("urls", urlfiles);
        bundle.putString("src", src);
        intent.putExtras(bundle);
        mainActivity.startActivity(intent);
        mainActivity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
//        mainActivity.cordovaPlugin.callbackContext.success("ok");
    }


    public void openPreviewUrlPhoto(Context context, String mediaid)
    {
        Intent intent = new Intent(context,PreviewPhotoViewPager.class);
        Bundle bundle = new Bundle();

        String[] urlfiles=new String[1];
        urlfiles[0]=mediaid;
        if (!CommonPlugin.checkFileIsExits(mediaid,".jpg"))
        {
            bundle.putInt("model",3);//全部照片预览模式
            urlfiles[0] = String.format("%1$s", GlobalConfig.globalConfig.getApiUrl() +"download?mediatype=.jpg&isaumb=&mediaid="+mediaid);
        }
        else
        {
            bundle.putInt("model",4);//本地浏览
        }
        bundle.putStringArray("urls", urlfiles);
        bundle.putString("src", "");
        intent.putExtras(bundle);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.scale, R.anim.alpha_exit);

    }


    /**
     * 打开预览界面
     * @param context
     * @param stringList
     * @param mediaid
     * @param isdelete 是否提供删除
     */
    public void openPreviewUrlPhoto(Context context, List<String> stringList,String mediaid,Boolean isdelete)
    {
        Intent intent = new Intent(context,PreviewMediaView.class);
        Bundle bundle = new Bundle();

        String[] urlfiles=new String[stringList.size()];
        int pages = 0;
        for (int i=0; i<stringList.size();i++)
        {
            urlfiles[i] = stringList.get(i);
            if (mediaid.equals(stringList.get(i)))
                pages = i;
        }
        bundle.putStringArray("mediaids", urlfiles);
        bundle.putInt("src", pages);
        bundle.putBoolean("isdelete",isdelete);
        intent.putExtras(bundle);
        ((BaseActivityPlugin)context).startActivityForResult(intent,PreviewMediaView.REQUESTCODE);
        ((Activity)context).overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

    }



    /**
     * 显示选择图像窗口
     */
    public  void openChooseImage(int precounts) {
        chooseimages=precounts;
        popupWindow=new PopupWindow(mainActivity);
        View popview = mainActivity.getLayoutInflater().inflate(
                R.layout.chooseimage_menu, null);
        popupWindow.setContentView(popview);
        btn_cancel =(Button)popview.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(onClickListenerbtncancel);
        btn_camera=(Button)popview.findViewById(R.id.btn_camera);
        btn_camera.setOnClickListener(onClickListenerbtncamera);
        btn_image = (Button)popview.findViewById(R.id.btn_image);
        btn_image.setOnClickListener(onClickListenerbtnimage);

        mainActivity.setShowBackView();
        popupWindow.setOnDismissListener(onDismissListener);
        popupWindow.setAnimationStyle(R.style.Animationbottomwindows);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(mainActivity.backview, Gravity.BOTTOM, 0, 0);

    }








    View.OnClickListener onClickListenerbtnimage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            openPreviewPhoto(chooseimages);
            popupWindow.dismiss();
        }
    };



    View.OnClickListener onClickListenerbtncamera = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            takePicture();
            popupWindow.dismiss();
        }
    };



    View.OnClickListener onClickListenerbtncancel = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mainActivity.setHideBackView();
            popupWindow.dismiss();
        }
    };

    PopupWindow.OnDismissListener onDismissListener = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            mainActivity.setHideBackView();
        }
    };

    //缩略图
    public static  Bitmap decodeBitmap(String realbitmap,int inSampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(realbitmap,options);//此时返回bm为空



        options.inJustDecodeBounds = false;
        int w = options.outWidth;
        int h = options.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 600;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (options.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (options.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        if (inSampleSize==0)
            options.inSampleSize = (int)(be*0.9);
        else
            options.inSampleSize =inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(realbitmap, options);
        return bitmap;
    }


    /**
     * 从media 获得图片对象
     * @param mediaid
     * @return
     */
    public static Bitmap bitbmpfrommediaLocal(String mediaid,int inSampleSize)
    {
        String realbitmap = SuyApplication.getApplication().getCacheDir() +  "/" +  mediaid + ".jpg";


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(realbitmap,options);//此时返回bm为空
        options.inJustDecodeBounds = false;
        int w = options.outWidth;
        int h = options.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800 / 5;//这里设置高度为800f
        float ww = 480 /5 ;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (options.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (options.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        if (inSampleSize==0)
            options.inSampleSize = (int)(be/2);
        else
            options.inSampleSize =inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(realbitmap, options);
        return bitmap;
    }

    /**
     * 图片转base64
     * @param filepath
     * @return
     */
    public static String GetPhotoBase64(String filepath)
    {
        try {


            Bitmap bitmap = decodeBitmap(filepath,5);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] buffer = baos.toByteArray();
            bitmap.recycle();
            return Base64.encodeToString(buffer,Base64.DEFAULT);

        }
        catch (Exception e)
        {e.printStackTrace();
        return "";}
    }

    public static String copyCacheFile(byte[] bytebuff)
    {
        try {

            String uuid =UUID.randomUUID().toString(); //CommonPlugin.imgToMD5(bytebuff);

            File outfile = new File(SuyApplication.getApplication().getCacheDir()  +
                    File.separator + uuid.toString().replace("-","") + ".jpg");
            OutputStream outputStream = new FileOutputStream(outfile);
            outputStream.write(bytebuff);
            outputStream.close();
            Log.i("文件",outfile.getAbsolutePath());
            Log.i("file",outfile.exists()==true? "1":"0");
            return uuid.toString().replace("-","");
        }
        catch (Exception e)
        {e.printStackTrace();
            return "";}
    }


    public static String copyCacheFile(String filepath)
    {
        try {


            UUID uuid= UUID.randomUUID();

            File fIle = new File(filepath);
            InputStream inputStream = new FileInputStream(fIle);
            byte[] bytebuff = new byte[inputStream.available()];
            inputStream.read(bytebuff);
            inputStream.close();
//            String uuid = CommonPlugin.imgToMD5(bytebuff);

            File outfile = new File(SuyApplication.getApplication().getCacheDir()  +
            File.separator + uuid.toString().replace("-","") + ".jpg");
            OutputStream outputStream = new FileOutputStream(outfile);
            outputStream.write(bytebuff);
            outputStream.close();
            Log.i("文件",outfile.getAbsolutePath());
            Log.i("file",outfile.exists()==true? "1":"0");
            return uuid.toString().replace("-","");

        }
        catch (Exception e)
        {e.printStackTrace();
            return "";}
    }


    @Override
    public void loadWebUrl(String Url) {

    }

    /**
     * 调用拍照
     */
    public Boolean takePicture() {
        return CameraHelper.takePhoto(mainActivity);
    }

    public Boolean takePictureForNaative() {
        return CameraHelper.takePhoto(baseActivityPlugin);
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
