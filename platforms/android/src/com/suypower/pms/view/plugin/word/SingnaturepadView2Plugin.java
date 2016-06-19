package com.suypower.pms.view.plugin.word;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.ImageSaveOptions;
import com.aspose.words.SaveFormat;
import com.aspose.words.WrapType;

import com.suypower.pms.R;
import com.suypower.pms.view.MainActivity;
import com.suypower.pms.view.plugin.CustomPopWindowPlugin;
import com.suypower.pms.view.plugin.word.signaturepad.views.SignaturePad;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by cjw on 15/6/11.
 */
public class SingnaturepadView2Plugin extends Dialog {

    public static String TAG ="SingnaturepadView2Plugin";

    Button buttonclear;
    Button buttonok;
    Button buttonreturn;
    SignaturePad signaturePad;
    String docname;
    String uuid;
    Thread thread;
    MainActivity mainActivity;
    String image6uuid;
    LinearLayout linearLayout;

    int x,y;

    public SingnaturepadView2Plugin(Context context,int x,int y
    ,String docpath,String uuid) {
        super(context, R.style.dialog);
        setContentView(R.layout.singnaturepadview2_activity);
        linearLayout = (LinearLayout)findViewById(R.id.linearLayout);
        signaturePad = (SignaturePad)findViewById(R.id.signpad);
        signaturePad.setMinWidth(7);
        signaturePad.setMaxWidth(12);
        buttonclear = (Button)findViewById(R.id.btncanecl);
        buttonclear.setOnClickListener(onClickListenerclear);
        buttonok = (Button)findViewById(R.id.btnok);
        buttonok.setOnClickListener(onClickListenerok);
        buttonreturn = (Button)findViewById(R.id.btnreturn);
        buttonreturn.setOnClickListener(onClickListenerreturn);
        docname = docpath;
        this.uuid  = uuid;
        this.x = x;
        this.y = y;
        mainActivity=(MainActivity)context;
        setCanceledOnTouchOutside(false);	//设置点击Dialog外部任意区域不能关闭Dialog
        setCancelable(false);		// 设置为false，按返回键不能退出
    }





    View.OnClickListener onClickListenerreturn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mainActivity.cordovaPlugin.callbackContext.error("没有签字");
            dismiss();
        }
    };

    View.OnClickListener onClickListenerclear = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            signaturePad.clear();
        }
    };

    View.OnClickListener onClickListenerok = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            linearLayout.setVisibility(View.INVISIBLE);
            CustomPopWindowPlugin.ShowPopWindow(signaturePad, getLayoutInflater(), "请稍候...");

            thread = new Thread(runnable);
            thread.start();
        }
    };


    Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CustomPopWindowPlugin.CLosePopwindow();

            switch (msg.what)
            {
                case 1:

                    JSONObject jsonObject= new JSONObject();
                    try {
                        jsonObject.put("file", uuid);
                        jsonObject.put("image", "stereo://" + image6uuid );
                        jsonObject.put("fileType", "WORD");
                    }
                    catch (Exception e)
                    {e.printStackTrace();}

                    mainActivity.cordovaPlugin.callbackContext.success(jsonObject);

                    dismiss();
                    break;
                case 0:

                    mainActivity.cordovaPlugin.callbackContext.error("生成失败");
                    dismiss();
                    break;
            }

        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                Bitmap signatureBitmap = signaturePad.getSignatureBitmap();
                String pngname = String.format("%1$s.png", uuid);
                File file = new File(getContext().getCacheDir(), pngname);
                saveBitmapToJPG(signatureBitmap, file);

                String docpath = WordPlugin.getDocPath(uuid);
                InputStream docinputstream = new FileInputStream(docpath);
                Document doc = new Document(docinputstream);
                docinputstream.close();
                InputStream inputStream = new FileInputStream(file);
                DocumentBuilder documentBuilder = new DocumentBuilder(doc);
                documentBuilder.moveToBookmark("sigin1", false, true);

                documentBuilder.insertImage(inputStream, 0, documentBuilder.getPageSetup().getPageWidth() - x
                        , 0, y, 110, 50, WrapType.NONE);
                doc.save(WordPlugin.getDocPathForImage(uuid));
//                doc.save(Environment.getExternalStorageDirectory()
//                +File.separator + "123.docx");
//                doc.save(Environment.getExternalStorageDirectory()
//                        +File.separator + "123.pdf");


                inputStream.close();
                System.gc();
                ImageSaveOptions imageOptions = new ImageSaveOptions(SaveFormat.JPEG);
                image6uuid = UUID.randomUUID().toString();
                String filename = String.format("%1$s.jpg", image6uuid);
                imageOptions.setPageIndex(6);
                imageOptions.setJpegQuality(100);
                doc.save(mainActivity.getCacheDir()
                        + File.separator + filename, imageOptions);

                System.gc();


                handler.sendEmptyMessage(1);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                handler.sendEmptyMessage(0);
            }
        }
    };




    //存png
    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
//        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(newBitmap);
//        canvas.drawColor(Color.TRANSPARENT);
//        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
        stream.flush();
        stream.close();

    }


    public static Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }
}