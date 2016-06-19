package com.suypower.pms.view.plugin;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.suypower.pms.R;
import com.suypower.pms.view.plugin.InterFace.iOptionmenu_Item_Select;
import com.suypower.pms.view.plugin.scan.ScanPlugin;


import java.util.List;
import java.util.Map;

/**
 * 分享插件 二维码
 * @author YXG 2015-04-13
 */
public class SharePlugin extends PopupWindow  {

    public static final String TAG = "SharePlugin";
    LayoutInflater layoutInflater;
    LinearLayout linearLayout;

    List<Map<String,Object>> bottonmapList; //底部菜单

    iOptionmenu_Item_Select ioptionmenuitem_select;

    View popview=null;//bottonmenu 包含view
    View backview;// 滤镜view
    Button btn_cancel;  //cancel 按钮
    ImageView imageView2D;

    //构造函数
    public SharePlugin(LayoutInflater layoutInflater)
    {
        this.layoutInflater = layoutInflater;



        popview = layoutInflater.inflate(R.layout.share_view, null);
        btn_cancel = (Button)popview.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(onClickListenerbtncancel);
        linearLayout = (LinearLayout)popview.findViewById(R.id.l1);
        imageView2D = (ImageView)popview.findViewById(R.id.scan2dimage);

        try {
            Bitmap bitmap = ScanPlugin.createQRCode("http://www.163.com", 180);
            imageView2D.setBackground(new BitmapDrawable(bitmap));

        }
        catch (Exception e)
        {e.printStackTrace();}

        this.setContentView(popview);



    }





    //点击取消
    View.OnClickListener onClickListenerbtncancel = new View.OnClickListener() {
        @Override
        public void onClick(View view) {




            new Thread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        Thread.sleep(370);
                        handler.sendEmptyMessage(1);
                    }
                    catch (Exception e)
                    {e.printStackTrace();}
                }
            }).start();

            Animation annotation = AnimationUtils.loadAnimation(
                    null,R.anim.anim_shareview2_exit);

            linearLayout.startAnimation(annotation);
            linearLayout.setVisibility(View.INVISIBLE);

            btn_cancel.startAnimation(annotation);
            btn_cancel.setVisibility(View.INVISIBLE);

        }
    };


    public void setMenuBar(List<Map<String,Object>> list,
                            iOptionmenu_Item_Select ioptionmenuitem_select) {
        //如果菜单数据为空或者为0 直接返回
        if (list.size() == 0)
            return;
        this.ioptionmenuitem_select = ioptionmenuitem_select;
        LinearLayout linearLayout = (LinearLayout)popview.findViewById(R.id.menu_linearLayout);
        linearLayout.removeAllViews();
        View view;
        bottonmapList =list;
        Map<String,Object> map =null;
        for (int i=0; i < bottonmapList.size();i ++)
        {
            view = layoutInflater.inflate(R.layout.bottom_menu_bar, null);
            map= bottonmapList .get(i);
            ((ImageView)view.findViewById(R.id.menu1_main_tabbar)).setBackground(
                    (Drawable) map.get("menu_image"));
            ((TextView)view.findViewById(R.id.menu_name)).setText(
                    map.get("menu_name").toString());

            LinearLayout.LayoutParams layoutParams =new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10,0,0,0);
            view.setTag(i);
            view.setOnClickListener(onClickListenerbottonbar);

            view.setLayoutParams(layoutParams);
            linearLayout.addView(view);
        }

    }

    View.OnClickListener onClickListenerbottonbar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            dismiss();
            if (ioptionmenuitem_select !=null)
                ioptionmenuitem_select.OnOptionMenuItemSelect((Integer)view.getTag() ,bottonmapList  .get((Integer)view.getTag()).get("uri").toString());//回调菜单选择索引

        }
    };



    Handler handler =new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {

                case 1:
                    dismiss();
                    break;
            }

        }
    };

    /**
     * 底部显示menu

     */
    public  void showWindow(View v) {
        this.backview =v;
        this.setAnimationStyle(R.style.Animationsharepopwindows);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setOutsideTouchable(true);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.showAtLocation(v, Gravity.BOTTOM,0,0);
        Animation annotation = AnimationUtils.loadAnimation(
                null,R.anim.anim_shareview2_enter);

        linearLayout.startAnimation(annotation);
        linearLayout.setVisibility(View.VISIBLE);

        btn_cancel.startAnimation(annotation);
        btn_cancel.setVisibility(View.VISIBLE);



    }


    /**
     * 关闭optionmenu
     */
    public void closePopMenu()
    {
        this.dismiss();
    }





}
