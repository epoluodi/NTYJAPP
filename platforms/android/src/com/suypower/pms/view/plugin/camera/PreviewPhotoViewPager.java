package com.suypower.pms.view.plugin.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suypower.pms.R;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.SuyClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * 照片预览 viewpager
 *
 * @author YXG
 */
public class PreviewPhotoViewPager extends Activity {

    public static final String TAG = "PreviewPhotoViewPager";

    /**
     * viewpager 环境变量
     */
    PagerAdapter pagerAdapter;
    ViewPager viewPager;
    List<RelativeLayout> viewList;
    File[] filephotos;//文件列表
    int page;
    int precounts;//限定数量
    ImageView imageleft;
    ImageView imageright;
    RelativeLayout relativeLayouttitle;
    RelativeLayout relativeLayoutFoot;
    TextView txtnumber;
    List<Integer> selectimages;
    Animation animation1;
    String[] filepath;
    String[] urlfiles;
    Button btnfinish;
    String src;
    List<Map<String, String>> mapUrlFiles;
    int model = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.previewphotoviewpager_activity);
        relativeLayouttitle = (RelativeLayout) findViewById(R.id.relativeLayout_title);
        relativeLayoutFoot = (RelativeLayout) findViewById(R.id.viewfoot);

        txtnumber = (TextView) findViewById(R.id.txt_number);
        animation1 = AnimationUtils.loadAnimation(this, R.anim.photonumber_scale);
        viewPager = (ViewPager) findViewById(R.id.photo_viewpager);
        imageleft = (ImageView) findViewById(R.id.title_left_menu_image);
        imageright = (ImageView) findViewById(R.id.pre_right_image);
        imageleft.setOnClickListener(onClickListenerleft);
        imageright.setOnClickListener(onClickListenerright);
        btnfinish = (Button) findViewById(R.id.btn_finish);
        btnfinish.setOnClickListener(onClickListenerfinish);
        File file = new File(CameraPlugin.PhotoPath);
        filephotos = file.listFiles();
        selectimages = new ArrayList<Integer>();
        model = getIntent().getExtras().getInt("model");
        precounts = getIntent().getExtras().getInt("precounts");

        //网络预览
        if (model == 3) {
            imageright.setVisibility(View.INVISIBLE);
            btnfinish.setVisibility(View.INVISIBLE);
            relativeLayoutFoot.setVisibility(View.INVISIBLE);
            urlfiles = getIntent().getExtras().getStringArray("urls");
            src = getIntent().getExtras().getString("src");
            mapUrlFiles = new ArrayList<Map<String, String>>();
            Map<String, String> map;
            for (int i = 0; i < urlfiles.length; i++) {
                if (src.equals(urlfiles[i])) {
                    page = i;
                }
                map = new Hashtable<String, String>();
                map.put("url", urlfiles[i]);

                mapUrlFiles.add(map);
            }
            new Thread(runnabledownload).start();

        }

        else if (model == 4) {
            imageright.setVisibility(View.INVISIBLE);
            btnfinish.setVisibility(View.INVISIBLE);
            // add start by Fangyuan Chen at 2016-05-25

            // add end by Fangyuan Chen at 2016-05-25
            urlfiles = getIntent().getExtras().getStringArray("urls");
            src = getIntent().getExtras().getString("src");
            mapUrlFiles = new ArrayList<Map<String, String>>();
            Map<String, String> map;
            for (int i = 0; i < urlfiles.length; i++) {
                if (src.equals(urlfiles[i])) {
                    page = i;

                }
                map = new Hashtable<String, String>();
                map.put("cachefile", getCacheDir() + "/"
                        + urlfiles[i].replace("stereo://", "") + ".jpg");
                map.put("url", urlfiles[i]);
                mapUrlFiles.add(map);
            }
//            handlerdownload.sendEmptyMessage(0);


        } else {
            int[] images = getIntent().getExtras().getIntArray("images");
            for (int i = 0; i < images.length; i++) {
                selectimages.add(images[i]);

            }
            //模式2图片预览
            if (model == 2) {
                filepath = getIntent().getExtras().getStringArray("files");
                imageright.setVisibility(View.INVISIBLE);
            }
            page = getIntent().getExtras().getInt("page");
        }
        InitViewPager();
        pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                if (model == 1)
                    return viewList.size();
                if (model == 2)
                    return filepath.length;
                if (model == 3)
                    return urlfiles.length;
                if (model == 4)
                    return mapUrlFiles.size();

                return 0;
            }


            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {

                return arg0 == arg1;
            }


            @Override
            public int getItemPosition(Object object) {

                return super.getItemPosition(object);
            }

            @Override
            public CharSequence getPageTitle(int position) {

                return "";
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewList.get(position));

                RelativeLayout relativeLayout = viewList.get(position);

                ImageView imageView = (ImageView) relativeLayout.findViewById(position);
                ProgressBar progressBar = (ProgressBar) relativeLayout.findViewById(100 + position);
                Bitmap bitmap = null;
                try {
                    BitmapDrawable bitmapDrawable = null;
                    if (model == 1) {
                        bitmap = CameraPlugin.decodeBitmap(filephotos[position].getAbsolutePath(), 4);//获得缩小比例图片
//                        bitmapDrawable = new BitmapDrawable(bitmap);
                    }
                    if (model == 2) {
                        bitmap = CameraPlugin.decodeBitmap(filepath[position], 0);//获得缩小比例图片
//                        bitmapDrawable = new BitmapDrawable(bitmap);
                    }

//
                    if (model == 3) {
                        Map<String, String> map = mapUrlFiles.get(position);
                        if (map.containsKey("cachefile")) {
                            Log.i("找到文件 ", " 1111111111");
                            bitmap = CameraPlugin.decodeBitmap(map.get("cachefile").toString(), 0);
//                            bitmapDrawable = new BitmapDrawable(bitmap);
                            progressBar.setVisibility(View.INVISIBLE);

                        }
                        // 重新设定比例

                    }
                    if (model == 4) {
                        Map<String, String> map = mapUrlFiles.get(position);
                        if (map.containsKey("cachefile")) {
                            Log.i("找到文件 ", " 1111111111");
                            bitmap = CameraPlugin.decodeBitmap(map.get("cachefile").toString(), 0);
//                            bitmapDrawable = new BitmapDrawable(bitmap);


                        }
                        // 重新设定比例

                    }

                    if (bitmap != null) {
                        // 重新设定比例
//                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
//                                bitmapDrawable.getBitmap().getWidth(),
//                                bitmapDrawable.getBitmap().getHeight()
//                        );
//                        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//                        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
//                        imageView.setMinimumWidth((int) (bitmapDrawable.getBitmap().getWidth()));
//                        imageView.setMinimumHeight((int) (bitmapDrawable.getBitmap().getHeight()));
//                        imageView.setLayoutParams(layoutParams);
//                        imageView.setBackground(bitmapDrawable);
                        imageView.setImageBitmap(bitmap);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


                return relativeLayout;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {


                RelativeLayout relativeLayout = viewList.get(position);
                ImageView imageView = (ImageView) relativeLayout.findViewById(position);
                imageView.setImageBitmap(null);
                container.removeView((View) object);


            }

        };

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(onPageChangeListener);
        viewPager.setCurrentItem(page);
        viewPager.setOnClickListener(onClickListenerviewpager);
        txtnumber.setText(String.format("%1$s/%2$s", page + 1, pagerAdapter.getCount()));


    }

    View.OnClickListener onClickListenerviewpager = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // add start by Fangyuan Chen at 2016-05-25
            //仅预览时点击图片即退出预览
            if(model == 4){
                finish();
                overridePendingTransition(R.anim.alpha,R.anim.scale_exit);
            }else{// add end by Fangyuan Chen at 2016-05-25
                if (relativeLayouttitle.getVisibility() == View.INVISIBLE) {
//                int flag= WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
//                //获得当前窗体对象
//                Window window=getWindow();
//                //设置当前窗体为全屏显示
//                window.setFlags(flag, flag);
                    relativeLayouttitle.setVisibility(View.VISIBLE);

                    relativeLayouttitle.startAnimation(AnimationUtils.loadAnimation(PreviewPhotoViewPager.this, R.anim.slide_in_from_top));
                    if (model!=3) {
                        relativeLayoutFoot.setVisibility(View.VISIBLE);
                        relativeLayoutFoot.startAnimation(AnimationUtils.loadAnimation(PreviewPhotoViewPager.this, R.anim.slide_in_from_bottom));
                    }
                }else{
                    if (relativeLayouttitle.getVisibility() == View.VISIBLE) { //定义全屏参数
//                int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//                //获得当前窗体对象
//                Window window=getWindow();
//                //设置当前窗体为全屏显示
//                window.setFlags(flag, flag);
                        relativeLayouttitle.setVisibility(View.INVISIBLE);

                        relativeLayouttitle.startAnimation(AnimationUtils.loadAnimation(PreviewPhotoViewPager.this, R.anim.slide_out_to_top));

                        if (model != 3) {
                            relativeLayoutFoot.setVisibility(View.INVISIBLE);
                            relativeLayoutFoot.startAnimation(AnimationUtils.loadAnimation(PreviewPhotoViewPager.this, R.anim.slide_out_to_bottom));
                        }
                    }
                }
            }
        }
    };


    /**
     * 完成 选自
     */
    View.OnClickListener onClickListenerfinish = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            if (model == 1) {
                String[] files = new String[selectimages.size()];

                for (int i = 0; i < selectimages.size(); i++) {

//                    files[i] = filephotos[selectimages.get(i)].getAbsolutePath();
                    files[i] = CameraPlugin.copyCacheFile(filephotos[selectimages.get(i)].getAbsolutePath());
                    Bitmap bitmap = CameraPlugin.bitbmpfrommediaLocal(files[i], 0);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(SuyApplication.getApplication().getCacheDir()
                                + "/" + files[i] + "aumb.jpg");
                        fileOutputStream.write(baos.toByteArray());
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    bitmap.recycle();
                    Log.i("图片文件", files[i]);

                }
                bundle.putStringArray("files", files);
            }
            if (model == 2) {
                bundle.putStringArray("files", filepath);

            }


            intent.putExtras(bundle);
            setResult(1, intent);

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();

        }
    };

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {

            txtnumber.setText(String.format("%1$s/%2$s", i + 1, pagerAdapter.getCount()));
            if (model == 1) {
                if (selectimages.contains(i))
                    imageright.setBackground(getResources().getDrawable(R.drawable.icon_photo_select));
                else
                    imageright.setBackground(getResources().getDrawable(R.drawable.icon_photo_unselect));
                imageright.startAnimation(animation1);
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    //返回
    View.OnClickListener onClickListenerleft = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (model == 1) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();

                int[] fileindex = new int[selectimages.size()];
                for (int i = 0; i < selectimages.size(); i++) {

                    fileindex[i] = selectimages.get(i);
                }

                bundle.putIntArray("files", fileindex);
                intent.putExtras(bundle);
                setResult(2, intent);
            }


            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        }
    };


    //点击选中图片
    View.OnClickListener onClickListenerright = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!selectimages.contains(viewPager.getCurrentItem())) {
                if (selectimages.size() == precounts && precounts != 0) {
                    Toast.makeText(PreviewPhotoViewPager.this,
                            "预览照片数量最多" + String.valueOf(precounts) + "张",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                selectimages.add(viewPager.getCurrentItem());
                imageright.setBackground(getResources().getDrawable(R.drawable.icon_photo_select));

                imageright.startAnimation(animation1);
            } else {

                selectimages.remove((Integer) viewPager.getCurrentItem());

                imageright.setBackground(getResources().getDrawable(R.drawable.icon_photo_unselect));
                imageright.startAnimation(animation1);
            }

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        File[] cachefile = getCacheDir().listFiles();
//        for (File file :cachefile)
//        {
//            file.delete();
//        }
    }

    void InitViewPager() {
        viewList = new ArrayList<RelativeLayout>();
        ImageView imageView;
        ProgressBar progressBar;
        RelativeLayout relativeLayout;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
//        layoutParams1.addRule(RelativeLayout.CENTER_VERTICAL);
//        layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);

        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(40, 40);
        layoutParams2.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams2.addRule(RelativeLayout.CENTER_HORIZONTAL);


        for (int i = 0; i < filephotos.length; i++) {

            try {
                imageView = new ImageView(this);

                relativeLayout = new RelativeLayout(this);

                relativeLayout.setBackground(getResources().getDrawable(R.color.black));
                relativeLayout.setLayoutParams(layoutParams);
                imageView.setOnClickListener(onClickListenerviewpager);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setId(i);
                relativeLayout.addView(imageView, layoutParams1);
                if (model == 3) {
                    progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleSmall);
                    relativeLayout.addView(progressBar, layoutParams2);
                    progressBar.setId(100 + i);

                }

                viewList.add(relativeLayout);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode == 4) {

            onClickListenerleft.onClick(null);


            return false;
        }

        return super.onKeyDown(keyCode, event);
    }


    Handler handlerdownload = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);


            int i = viewPager.getCurrentItem();
            viewPager.setAdapter(pagerAdapter);
            viewPager.setCurrentItem(i, false);
//            RelativeLayout relativeLayout = viewList.get(viewPager.getCurrentItem());
//
//            ImageView imageView = (ImageView)relativeLayout.findViewById(viewPager.getCurrentItem());
//            ProgressBar progressBar = (ProgressBar)relativeLayout.findViewById(100+viewPager.getCurrentItem());
//            Bitmap bitmap=null;
//            try {
//                BitmapDrawable bitmapDrawable =null;
//
//
//
//                if (model == 3) {
//                    Map<String, String> map = mapUrlFiles.get(viewPager.getCurrentItem());
//                    if (map.containsKey("cachefile")) {
//                        Log.i("找到文件 ", " 1111111111");
//                        bitmap = CameraPlugin.decodeBitmap(map.get("cachefile").toString(), 0);
//                        bitmapDrawable = new BitmapDrawable(bitmap);
//                        progressBar.setVisibility(View.INVISIBLE);
//
//                    }
//
//
//                }
//
//                if (bitmapDrawable != null) {
//                    // 重新设定比例
//                    imageView.setMinimumWidth((int) (bitmapDrawable.getBitmap().getWidth() ));
//                    imageView.setMinimumHeight((int) (bitmapDrawable.getBitmap().getHeight()));
//                    imageView.setBackground(bitmapDrawable);
////                    imageView.startAnimation(AnimationUtils.loadAnimation(PreviewPhotoViewPager.this,R.anim.alpha));
//                }
//
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }


        }
    };

    /**
     * 下载 图片
     */
    Runnable runnabledownload = new Runnable() {
        @Override
        public void run() {


            try {
                for (int i = 0; i < urlfiles.length; i++) {

                    URL url = new URL(urlfiles[i]);
                    File file = new File(url.getFile());

                    File cachefile = new File(getCacheDir() + "/"
                            + file.getName());
                    if (!cachefile.exists()) {
                        byte[] buffer = SuyClient.DownloadImage(urlfiles[i]);
                        if (buffer != null) {


                            FileOutputStream fileOutputStream = new FileOutputStream(
                                    getCacheDir() + "/"
                                            + file.getName());
                            fileOutputStream.write(buffer);

                            fileOutputStream.close();
                        } else {
                            continue;
                        }
                    }

                    Map<String, String> map = mapUrlFiles.get(i);

                    map.put("cachefile", getCacheDir() + "/"
                            + file.getName());
                    Log.i("图片下载 ", getCacheDir().getAbsolutePath() + "/"
                            + file.getName());


                    handlerdownload.sendEmptyMessage(0);

                }


            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    };
}