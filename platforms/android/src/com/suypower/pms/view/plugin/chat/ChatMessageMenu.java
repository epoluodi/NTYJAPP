package com.suypower.pms.view.plugin.chat;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.suypower.pms.R;
import com.suypower.pms.view.plugin.CommonPlugin;

/**
 * Created by Bingdor on 2016/5/31.
 */
public class ChatMessageMenu extends PopupWindow{
    public enum MenuEnum
    {TEXT,PICTURE,RECODR}
    private LayoutInflater layoutInflater;
    private MenuEnum _menuEnum;
    private Button btn1,btn2,btn3;
    private IChatMenu iChatMenu;
    private LinearLayout msgMenu=null;
    public Object object;
    public ChatMessageMenu(Context context,MenuEnum menuEnum,IChatMenu iChatMenu) {
        super(context);
        this.layoutInflater = LayoutInflater.from(context);

        _menuEnum= menuEnum;
        this.iChatMenu=iChatMenu;
        switch (_menuEnum)
        {
            case TEXT:
                msgMenu= (LinearLayout) layoutInflater.inflate(R.layout.chat_text_menu_layout, null);
                btn1 = (Button)msgMenu.findViewById(R.id.btncopy);
                btn1.setOnClickListener(onClickListenerbtn);
                break;
            case PICTURE:
                msgMenu= (LinearLayout) layoutInflater.inflate(R.layout.chat_pricture_menu_layout, null);
                btn1 = (Button)msgMenu.findViewById(R.id.btnsaveimg);
                btn1.setOnClickListener(onClickListenerbtn);
                break;
            case RECODR:
                msgMenu= (LinearLayout) layoutInflater.inflate(R.layout.chat_recodr_menu_layout, null);
                btn1 = (Button)msgMenu.findViewById(R.id.btnspeak);
                btn1.setOnClickListener(onClickListenerbtn);
                break;
        }
        btn3 = (Button)msgMenu.findViewById(R.id.btntransend);
        btn2 = (Button)msgMenu.findViewById(R.id.btndel);
        btn3.setOnClickListener(onClickListenerbtn);
        btn2.setOnClickListener(onClickListenerbtn);
        this.setContentView(msgMenu);
    }
    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        this.setAnimationStyle(R.style.Animationpopwindows);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(CommonPlugin.dip2px(70));
        this.setOutsideTouchable(false);
        this.setFocusable(true);
        super.showAtLocation(parent,gravity,x,y);
    }


    public  void setBtnName(int r,String s)
    {
        Button btn = (Button)msgMenu.findViewById(r);
        btn.setText(s);
    }

    View.OnClickListener onClickListenerbtn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            iChatMenu.OnClickMenuItem(view.getId(),_menuEnum,object);
            dismiss();
        }
    };



    public interface IChatMenu
    {
        void OnClickMenuItem(int btnid,MenuEnum menuEnum,Object o);
    }

}
