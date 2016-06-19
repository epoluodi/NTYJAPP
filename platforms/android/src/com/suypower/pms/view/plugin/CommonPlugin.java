package com.suypower.pms.view.plugin;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.SupplicantState;
import android.os.Vibrator;
import android.util.Log;

import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.server.StereoService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cjw on 15/6/29.
 */
public class CommonPlugin {


    public static String GetSysTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    public static void PlaysoundScan(Context context) {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//系统自带提示音

        Ringtone rt = RingtoneManager.getRingtone(context, uri);
        rt.play();
    }


    public static void Vibrator(Context context, long time) {
        ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(time);
    }


    public static String CheckGetWebURL(String barcode) {
        Pattern pattern = Pattern.compile("((http|https)://)([A-Za-z0-9-]+.)+[A-Za-z]{2,}(:[0-9]+)?");

        Matcher m = pattern.matcher(barcode);
        if (!m.find()) {

            return "";
        }
        return m.group();


    }


    public static String GetSysTime(String format) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(format);
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        final float scale = SuyApplication.getApplication().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        final float scale = SuyApplication.getApplication().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 判断时间间隔是否小于1分钟
     * @param dt1
     * @param dt2
     * @return
     */
    public static Boolean isInOneMin(String dt1,String dt2)
    {
        try {
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = sDateFormat.parse(dt1);
            Date date2 = sDateFormat.parse(dt2);
            long l = date2.getTime() - date1.getTime();
            if (l> 60*1000)
                return true;
            else
                return false;

        }catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 检查文件是否存在
     *
     * @param mediaid
     * @return
     */
    public static Boolean checkFileIsExits(String mediaid,String filetype) {
        File file = new File(SuyApplication.getApplication().getCacheDir() , mediaid + filetype);
        return file.exists();
    }


    /**
     * 计算MD5 值
     *
     * @param bitmap
     * @return
     */
    public static String imgToMD5(Bitmap bitmap) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] buffer = baos.toByteArray();
            messageDigest.update(buffer);
            byte[] bytemd5 = messageDigest.digest();
            StringBuilder strbuf = new StringBuilder();
            for (int i = 0; i < bytemd5.length; i++) {
                if (Integer.toHexString(0xff & bytemd5[i]).length() == 1) {
                    strbuf.append("0").append(Integer.toHexString(0xff & bytemd5[i]));
                } else {
                    strbuf.append(Integer.toHexString(0xff & bytemd5[i]));
                }
            }
            return strbuf.toString();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return "";
    }

    public static String imgToMD5(byte[] buffer) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(buffer);
            byte[] bytemd5 = messageDigest.digest();
            StringBuilder strbuf = new StringBuilder();
            for (int i = 0; i < bytemd5.length; i++) {
                if (Integer.toHexString(0xff & bytemd5[i]).length() == 1) {
                    strbuf.append("0").append(Integer.toHexString(0xff & bytemd5[i]));
                } else {
                    strbuf.append(Integer.toHexString(0xff & bytemd5[i]));
                }
            }
            return strbuf.toString();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return "";
    }


    /**
     * 检查服务运行
     *
     * @param classname
     * @return
     */
    public static boolean isCoreServiceRunning(String classname) {

        ActivityManager manager = (ActivityManager) SuyApplication.getApplication().getSystemService(Service.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))

        {

            if (classname.equals(service.service.getClassName())) {
                Log.i("发现核心服务", service.service.getClassName());
                return true;
            }

        }
        return false;
    }


    private static Bitmap big(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(1.5f, 1.5f); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    /**
     * Bitmap缩小的方法
     */
    private static Bitmap small(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.8f, 0.8f); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }


    /**
     * 遮盖图片
     *
     * @param bitmap
     * @param mMaskSource
     * @return
     */
    public static Bitmap MaskImage(Bitmap bitmap, Bitmap mMaskSource) {

        // 获得图片的宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) mMaskSource.getWidth()) / width;
        float scaleHeight = ((float) mMaskSource.getHeight()) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newMask = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        bitmap.recycle();
        Log.i("图片宽度",String.valueOf(bitmap.getWidth()));
        Log.i("图片高度",String.valueOf(bitmap.getHeight()));
        Log.i("遮盖图片宽度",String.valueOf(mMaskSource.getWidth()));
        Log.i("遮盖图片高度",String.valueOf(mMaskSource.getHeight()));
        Bitmap result = Bitmap.createBitmap(mMaskSource.getWidth(), mMaskSource.getHeight(), Bitmap.Config.ARGB_8888);
        //将遮罩层的图片放到画布中
        Canvas mCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));//叠加重复的部分，显示下面的
        mCanvas.drawBitmap(newMask, 0, 0, null);
        mCanvas.drawBitmap(mMaskSource, 0, 0, paint);
        paint.setXfermode(null);
        return result;
    }


}
