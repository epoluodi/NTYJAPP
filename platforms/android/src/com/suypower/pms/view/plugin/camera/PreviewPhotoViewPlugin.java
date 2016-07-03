package com.suypower.pms.view.plugin.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suypower.pms.R;
import com.suypower.pms.app.SuyApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 预览照片view
 *
 * @author YXG
 */
public class PreviewPhotoViewPlugin extends Activity {

    public static final String TAG = "PreviewPhotoViewPlugin";
    public static final int SelectPhotoNumbers = 6;
    public static final int JSCallPreviewPhtoto = 101;//js 调用 拍照插件
    GridLayout gridLayout; //图片grid
    ImageView[] customviews;//自定义view数组
    ImageView[] photoselectviews;//选择 图片 view数组
    ImageView img_return;  //返回
    Button btn_prephoto; //预览按钮
    Button btn_finsih;  //完成选定按钮
    TextView txt_photonumbers; //选定数字
    int imageselects = 0;    // 选择照片数量
    int precounts = 0;//     限定 预览数量
    Set<Integer> setimgselect;   // 选择队列
    File[] filephotos;//文件列表
    Message message;
    Animation animation1;
    private LinearLayout previewViewLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.previewphotoviewplugin_activity);
        gridLayout = (GridLayout) findViewById(R.id.photogrid);
        img_return = (ImageView) findViewById(R.id.title_left_menu_image);
        img_return.setOnClickListener(onClickListenerreturn);
        previewViewLayout = (LinearLayout) findViewById(R.id.preview_view);

        btn_prephoto = (Button) findViewById(R.id.left_bottombar_text);
        btn_finsih = (Button) findViewById(R.id.right_bottombar_text);
        txt_photonumbers = (TextView) findViewById(R.id.right_number_bottombar_text);

        setimgselect = new HashSet<Integer>();
        btn_prephoto.setEnabled(false);
        btn_prephoto.setTextColor(getResources().getColor(R.color.gray));
        btn_prephoto.setOnClickListener(onClickListenerprephoto);
        btn_finsih.setEnabled(false);
        btn_finsih.setTextColor(getResources().getColor(R.color.gray));
        btn_finsih.setOnClickListener(onClickListenerfinish);
        precounts = getIntent().getIntExtra("precounts", 0);

        Log.i("precounts:", String.valueOf(precounts));
        getPhotoFiles();
        if (filephotos.length ==0)
        {
            Toast.makeText(PreviewPhotoViewPlugin.this,"相册中没有合适的图片",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        initGridPhotoList();

        animation1 = AnimationUtils.loadAnimation(this, R.anim.photonumber_scale);

        //处理图片显示
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                    Bitmap bitmap;

                    //布局photolist
                    for (int i = 0; i < filephotos.length; i++) {

                        try {

                            bitmap = CameraPlugin.decodeBitmap(filephotos[i].getAbsolutePath(),20);
                            BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
                            message = handler.obtainMessage();
                            message.arg1 = i;
                            message.obj = bitmapDrawable;
                            handler.sendMessage(message);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    /**
     * 完成图片选择
     */
    View.OnClickListener onClickListenerfinish = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String[] files = new String[setimgselect.size()];
            Iterator<Integer> integerIterator =setimgselect.iterator();
            for (int i =0; i< setimgselect.size();i++)
            {

//                files[i]= filephotos[integerIterator.next()].getAbsolutePath();
                files[i]= CameraPlugin.copyCacheFile(filephotos[integerIterator.next()].getAbsolutePath());


                Bitmap bitmap =CameraPlugin.bitbmpfrommediaLocal(files[i],0);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(SuyApplication.getApplication().getCacheDir()
                            + "/" + files[i] + "aumb.jpg");
                    fileOutputStream.write(baos.toByteArray());
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
                catch (Exception e)
                {e.printStackTrace();}
                bitmap.recycle();
                Log.i("图片文件",files[i]);
            }
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putStringArray("files",files);
            intent.putExtras(bundle);
            setResult(1, intent);

            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        }
    };


    /**
     * 对选定的图片进行预览
     */
    View.OnClickListener onClickListenerprephoto = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();

            bundle.putInt("page", 0);
            int[] images =new int[setimgselect.size()];
            String[] filepath = new String[setimgselect.size()];
            Iterator<Integer> integerIterator =setimgselect.iterator();
            for (int i =0; i< images.length;i++)
            {
                images[i] = integerIterator.next();
                filepath[i]= filephotos[images[i]].getAbsolutePath();
            }
            bundle.putInt("model",2);//全部照片预览模式
            bundle.putIntArray("images", images);
            bundle.putInt("precounts", precounts);
            bundle.putStringArray("files",filepath);
            Intent intent = new Intent(PreviewPhotoViewPlugin.this, PreviewPhotoViewPager.class);
            intent.putExtras(bundle);

            startActivityForResult(intent, JSCallPreviewPhtoto);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    };

    //加载图片
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Animation animation1 = AnimationUtils.loadAnimation(PreviewPhotoViewPlugin.this, R.anim.prephoto_alpha);
            ImageView imageView = customviews[msg.arg1];
            imageView.setBackground((BitmapDrawable) msg.obj);
            imageView.startAnimation(animation1);
        }
    };

    //初始化photolist
    void initGridPhotoList() {
        //计算屏幕宽度
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        View view;
        ViewGroup.LayoutParams layoutParams;

        for (int i = 0; i < 4; i++) {
            view = new View(this);
            layoutParams = new ViewGroup.LayoutParams((width - 20) / 4, 150);
            gridLayout.addView(view, layoutParams);
        }


        //布局photolist
        for (int i = 0; i < filephotos.length; i++) {
            view = getLayoutInflater().inflate(R.layout.photo_framelayout, null);
            layoutParams = new ViewGroup.LayoutParams((width - 20) / 4, (int) (((width - 20) / 4) * 0.9));
            ImageView imageView = (ImageView) view.findViewById(R.id.photoiamge);
            ImageView imgselect = (ImageView) view.findViewById(R.id.btnselect);
            imageView.setTag(i);
            imgselect.setTag(i);
            gridLayout.addView(view, layoutParams);
            imageView.setOnClickListener(onClickListenerImage);
            imgselect.setOnClickListener(onClickListenerImageselect);
            customviews[i] = imageView;
            photoselectviews[i] = imgselect;
        }

    }




    //取消选择图片
    View.OnClickListener onClickListenercancelselect = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            imageselects = 0;
            setimgselect.clear();

            for (int i = 0; i < filephotos.length; i++) {
                photoselectviews[i].setBackground(getResources().getDrawable(R.drawable.icon_photo_unselect));
            }
            setPhotoNumber(0);
        }
    };


    //返回按钮
    View.OnClickListener onClickListenerreturn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setResult(0);
            finish();
            overridePendingTransition(R.anim.alpha,R.anim.slide_out_to_bottom);
        }
    };


    //选择点击
    View.OnClickListener onClickListenerImageselect = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!setimgselect.contains((Integer) view.getTag())) {
                if (imageselects == precounts && precounts !=0) {
                    Toast.makeText(PreviewPhotoViewPlugin.this,
                            "预览照片数量最多" + String.valueOf(precounts) + "张",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                setimgselect.add((Integer) view.getTag());
                imageselects++;
                setPhotoNumber(imageselects);
                view.setBackground(getResources().getDrawable(R.drawable.icon_photo_select));
                view.startAnimation(animation1);
            } else {

                setimgselect.remove((Integer) view.getTag());
                imageselects--;
                if (imageselects<0)
                    imageselects=0;
                setPhotoNumber(imageselects);
                view.setBackground(getResources().getDrawable(R.drawable.icon_photo_unselect));
                view.startAnimation(animation1);
            }
        }
    };

    // 点击 图片预览
    View.OnClickListener onClickListenerImage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putInt("page", (Integer) view.getTag());
            int[] images =new int[setimgselect.size()];
            Iterator<Integer> integerIterator =setimgselect.iterator();
            for (int i =0; i< images.length;i++)
            {
                images[i] = integerIterator.next();
            }
            bundle.putInt("model",1);//全部照片预览模式
            bundle.putIntArray("images",images);
            bundle.putInt("precounts",precounts);
            Intent intent = new Intent(PreviewPhotoViewPlugin.this, PreviewPhotoViewPager.class);
            intent.putExtras(bundle);

            startActivityForResult(intent, JSCallPreviewPhtoto);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        }
    };




    //得到照片文件数组
    void getPhotoFiles() {
        File file = new File(CameraPlugin.PhotoPath);
        filephotos = file.listFiles();

        customviews = new ImageView[filephotos.length];
        photoselectviews = new ImageView[filephotos.length];
    }


    /**
     * 显示选择图片的数量
     *
     * @param numbers
     */
    void setPhotoNumber(int numbers) {
        txt_photonumbers.startAnimation(animation1);
        txt_photonumbers.setText(String.valueOf(numbers));
        if (numbers == 0) {
            btn_prephoto.setEnabled(false);
            btn_prephoto.setTextColor(getResources().getColor(R.color.gray));
            btn_finsih.setEnabled(false);
            btn_finsih.setTextColor(getResources().getColor(R.color.gray));


            return;
        }
        if (numbers > 0) {
            btn_prephoto.setEnabled(true);
            btn_prephoto.setTextColor(getResources().getColor(R.color.black));
            btn_finsih.setEnabled(true);
            btn_finsih.setTextColor(getResources().getColor(R.color.forestgreen));

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode ==1)
        {
            setResult(1, data);

            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();

        }
        if (resultCode ==2)
        {
            setimgselect .clear();
            imageselects=0;
            int[] fileindex = data.getExtras().getIntArray("files");
            for (int i=0; i<fileindex.length;i++)
            {
                imageselects++;
                setimgselect.add(fileindex[i]);
                photoselectviews[fileindex[i]].setBackground(getResources().getDrawable(R.drawable.icon_photo_select));
            }


            setPhotoNumber(fileindex.length);
        }


    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            setResult(0);
//            finish();

        }
        return super.onKeyDown(keyCode, event);
    }
}