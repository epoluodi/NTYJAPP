package com.suypower.pms.view.plugin.word;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aspose.words.Document;
import com.aspose.words.ImageSaveOptions;
import com.aspose.words.SaveFormat;
import com.suypower.pms.R;
import com.suypower.pms.view.plugin.CustomPopWindowPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * word 预览 界面
 * @author YXG
 */
public class WordPreviewForImage extends Activity {
    ZoomImageView zoomImageView ;
    TextView title;
    ImageView imageViewReturn;
    SeekBar seekBar;
    String uuid;
    String docpath;
    int pagecount;
    Thread thread;
    List<String> stringList;
    Animation animation1;
    Animation animation2;
    Boolean leftOrright = true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wordpreviewforimage_activity);
        imageViewReturn = (ImageView)findViewById(R.id.title_left_menu_image);
        imageViewReturn.setOnClickListener(onClickListenerreturn);
        title = (TextView)findViewById(R.id. txt_number);
        title.setText("0/0");
        seekBar =(SeekBar)findViewById(R.id.seek);
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        uuid = getIntent().getExtras().getString("uuid").replace("stereo://","");
        docpath = WordPlugin.getDocPathForImage(uuid);
        stringList = new ArrayList<String>();
        zoomImageView = (ZoomImageView)findViewById(R.id.wordimage);
        zoomImageView.setOnTranPage(iTranPage);
        animation1 = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        animation2 = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);

        try
        {
            File file = new File(docpath);
            if (!file.exists())
            {

                Toast.makeText(this,"没有找到文件合同",Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            thread = new Thread(runnable);
            thread.start();

        }
        catch (Exception e)
        {e.printStackTrace();}
    }

    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            Log.i("seek count :",String.valueOf(i));

            if (stringList.size() >0) {
                Bitmap bitmap = BitmapFactory.decodeFile(stringList.get(i));
                if (bitmap == null) {
                    Log.i("图片："," 空");
                    return;
                }
                title.setText(String.valueOf(i + 1) + "/" + String.valueOf(pagecount));

                zoomImageView.setImage(bitmap);
                if (leftOrright)
                    zoomImageView.startAnimation(animation2);
                else
                    zoomImageView.startAnimation(animation1);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    View.OnClickListener onClickListenerreturn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };


    /**
     * 手势接口
     */
    ZoomImageView.ITranPage iTranPage =new ZoomImageView.ITranPage() {
        @Override
        public void OnTranPage(int upordown) {
            if (upordown == 0)
            {
                leftOrright = false;
                if (seekBar.getProgress() == 0)
                    return;
                seekBar.setProgress(seekBar.getProgress() - 1);

            }
            if (upordown == 1)
            {
                leftOrright = true;
                if (seekBar.getProgress() == pagecount)
                    return;
                seekBar.setProgress(seekBar.getProgress() + 1);

            }



        }
    };




    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case -1:
                    CustomPopWindowPlugin.ShowPopWindow(zoomImageView,
                            getLayoutInflater(),"请稍侯...");
                    break;
                case 0:
                    CustomPopWindowPlugin.CLosePopwindow();
                    Toast.makeText(WordPreviewForImage.this,
                            "预览错误",Toast.LENGTH_SHORT).show();

                    break;
                case 1:
                    pagecount = msg.arg1;
                    title.setText("0/" + String.valueOf(pagecount));
                    seekBar.setMax(pagecount-1);

                    break;
                case 2:
                    CustomPopWindowPlugin.CLosePopwindow();

                    Bitmap bitmap = BitmapFactory.decodeFile(stringList.get(0));
                    if (bitmap == null)
                        return;
                    zoomImageView.setImage(bitmap);
                    title.setText( "1/" + String.valueOf(pagecount));
                    break;
            }


        }
    };


    /**处理预览照片线程
     *
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Message message;
            String filename;
            try {
                System.gc();
                Thread.sleep(500);
                handler.sendEmptyMessage(-1);
                Thread.sleep(500);
                message = handler.obtainMessage();
                System.gc();
                InputStream inputStream = new FileInputStream(docpath);
                Document document = new Document(inputStream);
                inputStream.close();
                System.gc();
                message.arg1 = document.getPageCount();
                System.gc();
                message.what=1;
                handler.sendMessage(message);
                ImageSaveOptions imageOptions= new ImageSaveOptions(SaveFormat.JPEG);
                for (int i = 0; i<document.getPageCount();i++)
                {
                    filename = String.format("%1$s_%2$d.jpg",uuid,i);
                    File file = new File(getCacheDir() + File.separator
                    + filename);
                    if (!file.exists() || i == 6) {
                        imageOptions.setPageIndex(i);
                        imageOptions.setJpegQuality(100);
                        document.save(getCacheDir()
                                + File.separator + filename, imageOptions);
                        Log.i("线程开始", "图片:" + String.valueOf(i));
                        System.gc();
                    }
                    stringList.add(getCacheDir()
                            + File.separator + filename);
                }
                message = handler.obtainMessage();
                message.what=2;
                handler.sendMessage(message);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                handler.sendEmptyMessage(0);
            }
        }
    };
}