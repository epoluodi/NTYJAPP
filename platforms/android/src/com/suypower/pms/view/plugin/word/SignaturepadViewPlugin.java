package com.suypower.pms.view.plugin.word;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.WrapType;

import com.suypower.pms.R;
import com.suypower.pms.view.plugin.CustomPopWindowPlugin;
import com.suypower.pms.view.plugin.word.signaturepad.views.SignaturePad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 签名版view
 * @author YXG
 */
public class SignaturepadViewPlugin extends Activity {

    public static String TAG ="SignaturepadViewPlugin";
    public static int SIGNATUREPADRESULTREQUEST = 20;
    Button buttonclear;
    Button buttonok;
    SignaturePad signaturePad;
    String docname;
    String uuid;
    Thread thread;

    int x,y;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signaturepad_activity);
        signaturePad = (SignaturePad)findViewById(R.id.signpad);
        signaturePad.setMinWidth(7);
        signaturePad.setMaxWidth(12);
        buttonclear = (Button)findViewById(R.id.btncanecl);
        buttonclear.setOnClickListener(onClickListenerclear);
        buttonok = (Button)findViewById(R.id.btnok);
        buttonok.setOnClickListener(onClickListenerok);
        docname = getIntent().getExtras().getString("docpath");
        uuid  = getIntent().getExtras().getString("uuid");
        x = getIntent().getExtras().getInt("x");
        y = getIntent().getExtras().getInt("y");
    }

    View.OnClickListener onClickListenerclear = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            signaturePad.clear();
        }
    };

    View.OnClickListener onClickListenerok = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            CustomPopWindowPlugin.ShowPopWindow(signaturePad, getLayoutInflater(), "正在处理");

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
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            switch (msg.what)
            {
                case 1:

                    bundle.putString("file", uuid);
                    bundle.putString("fileType", "WORD");
                    intent.putExtras(bundle);
                    setResult(1, intent);
                    finish();
                    break;
                case 0:

                    setResult(0);
                    finish();
                    break;
            }

        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try
            {
                Bitmap signatureBitmap = signaturePad.getSignatureBitmap();
                String pngname = String.format("%1$s.png",uuid);
                File file = new File(getCacheDir(),pngname);
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
                doc.save(docpath);
//                doc.save(Environment.getExternalStorageDirectory()
//                +File.separator + "123.docx");
//                doc.save(Environment.getExternalStorageDirectory()
//                        +File.separator + "123.pdf");

                inputStream.close();
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