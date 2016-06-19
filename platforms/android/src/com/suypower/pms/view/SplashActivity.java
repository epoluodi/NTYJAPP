package com.suypower.pms.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.suypower.pms.R;
import com.suypower.pms.app.task.AppContextLauncher;

import java.util.ArrayList;

/**
 * Created by Bingdor on 2016/5/26.
 */
public class SplashActivity extends Activity implements View.OnClickListener,
        ViewPager.OnPageChangeListener {
    // 定义ViewPager对象
    private SimpleViewPager viewPager;
    // 定义ViewPager适配器
    private ViewPagerAdapter vpAdapter;
    // 定义一个ArrayList来存放View
    private ArrayList<View> views;
    // 引导图片资源
//    private static final int[] pics = { R.drawable.guide1, R.drawable.guide1,
//            R.drawable.guide1, R.drawable.guide1 };
    private static final int[] pics = {R.drawable.splash};
    // 底部小点的图片
    private ImageView[] points;
    // 记录当前选中位置
    private int currentIndex;

    private LinearLayout pointbar;

    private Intent nextIntent;

    private Boolean guideMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucentStatus(true);
        setContentView(R.layout.splash_activity);

        initView();
        initData();
        AppContextLauncher appContextLauncher = new AppContextLauncher(this, new AppContextLauncher.AppLaucherCallback() {
            @Override
            public void onInitOver(Intent intent) {
                nextIntent = intent;
                if(!guideMode){
                    handler.sendEmptyMessage(0);
                }
            }
        });

    }



    private void setTranslucentStatus(Boolean b) {
        if (b) {
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        }
//        else
//        {
//            this.requestWindowFeature(65);//去掉标题栏
//            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);//去掉信息栏
//
//        }
    }

    /**
     * 初始化组件
     */
    private void initView() {
        // 实例化ArrayList对象
        views = new ArrayList<View>();
        // 实例化ViewPager
        // 实例化ViewPager适配器
        vpAdapter = new ViewPagerAdapter(views);
        viewPager = (SimpleViewPager) findViewById(R.id.viewpager);
        guideMode = pics.length > 1;
        //判断引导页面的数量 当仅为1时 则不显示引导
        if(pics.length  > 1){
            pointbar = (LinearLayout) findViewById(R.id.pointbar);
            pointbar.setVisibility(View.VISIBLE);
            for (int i = 0 ;i < pics.length;i++){
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams imgLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                imgLayout.weight = 1;
                imgLayout.gravity = Gravity.CENTER_HORIZONTAL;
                imageView.setPadding(15,15,15,15);
                imageView.setLayoutParams(imgLayout);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.point_selector));
                pointbar.addView(imageView);
            }
        }else{
            viewPager.setScrollble(false);
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            startNextView();
        }
    };

    /**
     * 初始化数据
     */
    private void initData() {
        // 初始化引导图片列表
        LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        //加载图片资源
        for (int i = 0; i < pics.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(imageLayoutParams);
            imageView.setImageResource(pics[i]);
            //防止图片不能填满屏幕
            imageView.setScaleType(ScaleType.FIT_XY);
            if(i == pics.length - 1 && guideMode){
                RelativeLayout.LayoutParams btnLayoutParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                btnLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
                btnLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
                btnLayoutParams.setMargins(0,0,0,200);
                Button enterButton = new Button(this);
                enterButton.setTextColor(Color.WHITE);
                enterButton.setAlpha(0.8f);
                enterButton.setPadding(200,30,200,30);
                enterButton.setTextSize(16);
                enterButton.setBackground(getResources().getDrawable(R.drawable.splash_enter_selector));
                enterButton.setText("立即开启");
                enterButton.setLayoutParams(btnLayoutParams);
                enterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handler.sendEmptyMessage(0);
                    }
                });
                RelativeLayout relativeLayout = new RelativeLayout(this);
                RelativeLayout.LayoutParams relativelayoutParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT);
                relativeLayout.setLayoutParams(relativelayoutParams);
                relativeLayout.addView(imageView);
                relativeLayout.addView(enterButton);
                views.add(relativeLayout);
            }else{
                views.add(imageView);
            }
        }
        // 设置数据
        viewPager.setAdapter(vpAdapter);
        // 设置监听
        viewPager.setOnPageChangeListener(this);

        // 初始化底部小点
        if (pics.length >1){
            initPoint();
        }
    }

    /**
     * 初始化底部小点
     */
    private void initPoint() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.pointbar);
        points = new ImageView[pics.length];
        // 循环取得小点图片
        for (int i = 0; i < pics.length; i++) {
            // 得到一个LinearLayout下面的每一个子元素
            points[i] = (ImageView) linearLayout.getChildAt(i);
            // 默认都设为灰色
            points[i].setEnabled(true);
            // 给每个小点设置监听
            points[i].setOnClickListener(this);
            // 设置位置tag，方便取出与当前位置对应
            points[i].setTag(i);
        }

        // 设置当面默认的位置
        currentIndex = 0;
        // 设置为白色，即选中状态
        points[currentIndex].setEnabled(false);
    }

    /**
     * 滑动状态改变时调用
     */
    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    /**
     * 当前页面滑动时调用
     */
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        /*if(rightEdge!=null&&!rightEdge.isFinished()){//到了最后一张并且还继续拖动，出现蓝色限制边条了
            startNextView();
        }*/
    }

    /**
     * 进入下一视图
     */
    private void startNextView(){
        if(nextIntent != null){
            setTranslucentStatus(false);

            startActivity(nextIntent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    /**
     * 新的页面被选中时调用
     */
    @Override
    public void onPageSelected(int arg0) {
        // 设置底部小点选中状态
        setCurDot(arg0);
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        setCurView(position);
        setCurDot(position);
    }

    /**
     * 设置当前页面的位置
     */
    private void setCurView(int position) {
        if (position < 0 || position >= pics.length) {
            return;
        }
        viewPager.setCurrentItem(position);
    }

    /**
     * 设置当前的小点的位置
     */
    private void setCurDot(int positon) {
        if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
            return;
        }
        points[positon].setEnabled(false);
        points[currentIndex].setEnabled(true);
        currentIndex = positon;
    }
}