package com.suypower.pms.view.plugin.scan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import com.suypower.pms.view.MainActivity;
import com.suypower.pms.view.MainTabView;
import com.suypower.pms.view.plugin.BaseViewPlugin;
import com.suypower.pms.view.plugin.CordovaWebViewPlugin;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;

/**
 * 一维二维扫描
 *
 * @author YXG 2015-04-13
 */
public class ScanPlugin extends BaseViewPlugin {

    public static final String TAG = "ScanPlugin";



    MainActivity mainActivity;
    BaseViewPlugin baseViewPlugin = null;



    public ScanPlugin()
    {}

    /**
     * 拍照狗展函数
     *
     * @param mainActivity
     * @param baseViewPlugin 传入调用的基础插件
     */
    public ScanPlugin(MainActivity mainActivity, BaseViewPlugin baseViewPlugin) {
        this.mainActivity = mainActivity;
        this.baseViewPlugin = baseViewPlugin;


    }



    public void showScanActivity()
    {
        Intent intent = new Intent(mainActivity,ScanActivity.class);
        mainActivity.startActivityForResult(intent, ScanActivity.SCANRESULTREQUEST);
        mainActivity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

    }

    public void showScanActivity(Activity activity)
    {
        Intent intent = new Intent(activity,ScanActivity.class);
        activity.startActivityForResult(intent, ScanActivity.SCANRESULTREQUEST);
        activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

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


    public static Bitmap createQRCode(String str, int widthAndHeight)
            throws WriterException {
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix matrix = new MultiFormatWriter().encode(str,
                BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
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
