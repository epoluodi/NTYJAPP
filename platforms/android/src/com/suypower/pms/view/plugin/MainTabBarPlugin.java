package com.suypower.pms.view.plugin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.suypower.pms.R;
import com.suypower.pms.app.configxml.AppConfig;
import com.suypower.pms.app.configxml.TabBar;
import com.suypower.pms.view.MainActivity;
import com.suypower.pms.view.plugin.InterFace.ITabbar_Item_Click;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;

/**
 * 主界面 tabbar 操作类
 * @author YXG 2015-04-13
 */
public class MainTabBarPlugin
{



    //当前标题索引
    public int itemindex=0;

    public static final String Tag = "MainTabBarPlugin";
    MainActivity mainActivity;//主Avtivity 上下文

    LinearLayout tabbarlayyout;  //主布局
    //tabbar image view
    FrameLayout menu;  //最外层  框架
    FrameLayout[] frameLayouts;   //外层框架组
    ImageView imageView_main_tabbar;    //tabbar  image
    ImageView[] imageViews_tabbars;// image 组

    //tabbar 角标view
//    ImageView imageView_tabbar_mark;
//    ImageView imageView1_tabbar_mark;
//    ImageView imageView2_tabbar_mark;
//    TextView textView3_tabbar_main;
//    TextView textView4_tabbar_main;

    //设置tabbar点击接口
    ITabbar_Item_Click itabbarimageclick =null;

    //获得Appconfig tabbar
    public TabBar[] tabBars = AppConfig.appConfig.getTabBar();
    /**
     * 构造函数
     * @param context 传递主界面 上下文关系
     */
    public MainTabBarPlugin(Context context)
    {
        mainActivity = (MainActivity)context;


        tabbarlayyout = (LinearLayout)mainActivity.findViewById(R.id.tabbarlayout);
        if (tabBars==null || tabBars.length==0)
        {
            tabbarlayyout.setVisibility(View.GONE);
            return;
        }

        InputStream inputStream;
        Bitmap bitmap;

        //加载tabbar view布局
        View view;

        frameLayouts=new FrameLayout[tabBars.length];
        imageViews_tabbars = new ImageView[tabBars.length];// 加载tabbar数

        //设置 布局样式
        LinearLayout.LayoutParams layoutParamsImageMain;

        if (tabBars.length==1)
        {
            tabbarlayyout.setVisibility(View.GONE);
        }
        else {
            for (int i = 0; i < tabBars.length; i++) {
                view = mainActivity.getLayoutInflater().inflate(R.layout.tabbar_framelayout, null);
                menu = (FrameLayout) mainActivity.findViewById(R.id.menu1);//加载tabbar外层框架
                frameLayouts[i] = menu;
                imageView_main_tabbar = (ImageView) view.findViewById(R.id.menu1_main_tabbar);//加载 imageviwe
                imageView_main_tabbar.setTag(i);
                imageView_main_tabbar.setOnClickListener(onClickListener_tabbar);
                imageViews_tabbars[i] = imageView_main_tabbar;
//            imageView_tabbar_mark=(ImageView)view.findViewById(R.id.mark1_main_image_tabbar); //加载角标

                try {
                    inputStream = context.getAssets().open(tabBars[i].getIcon_tab_normal());
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
                    imageViews_tabbars[i].setBackground(bitmapDrawable);        //设置默认图片

                } catch (IOException e) {

                }

                layoutParamsImageMain = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
                layoutParamsImageMain.weight = 1;

                tabbarlayyout.addView(view, layoutParamsImageMain);//加载到主布局中
            }

        }
    }


    public void setOnTabBarClickItem(ITabbar_Item_Click itabbarimageclick)
    {
        this.itabbarimageclick = itabbarimageclick;
    }
    /**
     * Tabbar image点击事件
     */
    View.OnClickListener onClickListener_tabbar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            InputStream inputStream;
            Bitmap bitmap;

            for (int i=0;i< AppConfig.appConfig.getTabBar().length;i++)
            {
                try {
                    inputStream = mainActivity.getAssets().open(tabBars[i].getIcon_tab_normal());
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);

                    imageViews_tabbars[i].setBackground(bitmapDrawable);
                }
                catch (IOException e)
                {
                   e.printStackTrace();
                }
            }

            try {
                inputStream = mainActivity.getAssets().open(tabBars[(Integer) view.getTag()].getIcon_tab_active());
                bitmap = BitmapFactory.decodeStream(inputStream);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);

                imageViews_tabbars[(Integer)view.getTag()].setBackground(bitmapDrawable);
            }
            catch (IOException e)
            {
               e.printStackTrace();
            }
            //设置接口回调到主activity
            if (itabbarimageclick != null)
                itabbarimageclick.OnTabBarClickItem(view,(Integer)view.getTag());

        }
    };


    public void setTabBarItemstate(int index)
    {
        if (tabBars.length==1)
            return;
        itemindex =index;
        onClickListener_tabbar.onClick(imageViews_tabbars[index]);


    }




//    /**
//     * 设置Maintabbar状态栏中角标的现实状态
//     *
//     * @param markindex 按照tabbar 从0开始 控制标签
//     * @param visibility true 现实，false 隐藏
//     */
//    public void SetMarkVisibility(int markindex ,Boolean visibility)
//    {
//        Animation animation1 = AnimationUtils.loadAnimation(mainActivity,R.anim.scale);
//        Animation animation2 = AnimationUtils.loadAnimation(mainActivity,R.anim.scale_exit);
//
//
//        switch (markindex)
//        {
//            case 0:
//                if (visibility) {
//                    imageView1_tabbar_mark.startAnimation(animation2);
//                    imageView1_tabbar_mark.setVisibility(View.INVISIBLE);
//                }
//                else {
//                    imageView1_tabbar_mark.startAnimation(animation1);
//                    imageView1_tabbar_mark.setVisibility(View.VISIBLE);
//                }
//                break;
//            case 1:
//                if (visibility)
//                    imageView2_tabbar_mark.setVisibility(View.INVISIBLE);
//                else
//                    imageView2_tabbar_mark.setVisibility(View.VISIBLE);
//                break;
//            case 2:
//                if (visibility) {
//                    textView3_tabbar_main.startAnimation(animation2);
//                    textView3_tabbar_main.setVisibility(View.INVISIBLE);
//                }
//                else {
//                    textView3_tabbar_main.startAnimation(animation1);
//                    textView3_tabbar_main.setVisibility(View.VISIBLE);
//                }
//                break;
//            case 3:
//                if (visibility)
//                    textView4_tabbar_main.setVisibility(View.INVISIBLE);
//                else
//                    textView4_tabbar_main.setVisibility(View.VISIBLE);
//
//                break;
//        }
//    }






}
