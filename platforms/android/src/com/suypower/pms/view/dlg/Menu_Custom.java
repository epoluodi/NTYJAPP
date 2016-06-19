package com.suypower.pms.view.dlg;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.suypower.pms.R;

/**
 * 菜单定义
 *
 * @author YXG
 */
public class Menu_Custom extends PopupWindow {


    LinearLayout linearLayout;
    View menuview;
    IMenu iMenu;
    Context context;


    public Menu_Custom(Context context,IMenu iMenu) {
        menuview = LayoutInflater.from(context).inflate(R.layout.menu_dialog, null);
        this.setContentView(menuview);
        linearLayout = (LinearLayout) menuview.findViewById(R.id.menu_container);
        this.context=context;
        this.iMenu = iMenu;

    }


    /**
     * 添加菜单
     * @param imgid
     * @param menuitmename
     * @param itemid 菜单id
     */
    public void addMenuItem(int imgid, String menuitmename, int itemid)
    {


        View item = LayoutInflater.from(context).inflate(R.layout.menu_item,null);
        item.setTag(itemid);
        ((TextView)item.findViewById(R.id.item)).setText(menuitmename);
        ((ImageView)item.findViewById(R.id.menu_img)).setBackground(context.getResources().getDrawable(imgid));
        item.setOnClickListener(onClickListeneritem);
        linearLayout.addView(item);
    }



    View.OnClickListener onClickListeneritem = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            iMenu.ClickMenu((Integer)view.getTag());
            dismiss();
        }
    };


    /**
     * 显示菜单
     * @param v
     */
    public void ShowMenu(View v) {
        this.setAnimationStyle(R.style.AnimationMenu);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setOutsideTouchable(false);
        this.setFocusable(true);
        this.showAsDropDown(v,-300,50,Gravity.LEFT);
    }







    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
