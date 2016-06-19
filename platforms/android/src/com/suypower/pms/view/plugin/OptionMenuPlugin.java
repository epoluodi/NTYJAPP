package com.suypower.pms.view.plugin;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.suypower.pms.R;
import com.suypower.pms.view.MainActivity;
import com.suypower.pms.view.plugin.InterFace.iOptionmenu_Item_Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 显示optionmenu菜单，动态加载
 * @author YXG 2015-04-13
 */
public class OptionMenuPlugin extends PopupWindow  {

    public static final String TAG = "OptionMenuPlugin";
    LayoutInflater layoutInflater;

    List<Map<String,Object>> leftmapList; //菜单加载堆栈 左边
    List<Map<String,Object>> rightmapList; //菜单加载堆栈 右边
    List<Map<String,Object>> bottonmapList; //底部菜单
    ListView listView;
    MyAdapter myAdapter;
    iOptionmenu_Item_Select ioptionmenuitem_select;

    View popview=null;//bottonmenu 包含view
    View backview;// 滤镜view
    Button btn_cancel;  //cancel 按钮
    int menutype;
    MainActivity mainActivity;
    //构造函数
    public OptionMenuPlugin(LayoutInflater layoutInflater,int showmodel,
                            MainActivity mainActivity)
    {
        this.layoutInflater = layoutInflater;
        this.mainActivity = mainActivity;
        //显示模式
        switch (showmodel)
        {
            case 1://  pop模式
                popview = layoutInflater.inflate(R.layout.menu_main_pop, null);
                break;
            case 2:// 底部显示
                popview = layoutInflater.inflate(R.layout.menu_main_bottom, null);
                btn_cancel = (Button)popview.findViewById(R.id.btn_cancel);
                btn_cancel.setOnClickListener(onClickListenerbtncancel);
                break;
        }

        this.setContentView(popview);

        listView = (ListView)popview.findViewById(R.id.list);
        leftmapList =new ArrayList<Map<String, Object>>();
        rightmapList =new ArrayList<Map<String, Object>>();

        this.setOnDismissListener(onDismissListener);
    }





    //取消backview
    void dissbackview()
    {
        mainActivity.setHideBackView();
    }

    OnDismissListener onDismissListener= new OnDismissListener() {
        @Override
        public void onDismiss() {
            dissbackview();
        }
    };


    //点击取消
    View.OnClickListener onClickListenerbtncancel = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            dissbackview();
            dismiss();
        }
    };
    /**
     * 加载菜单信息
     * @param list 菜单信息堆栈
     */
    public void setMenuList(List<Map<String,Object>> list,
                            iOptionmenu_Item_Select ioptionmenuitem_select,
                            int menutype)
    {
        //如果菜单数据为空或者为0 直接返回
        if (list.size()==0)
            return;
        this.ioptionmenuitem_select = ioptionmenuitem_select;
        switch (menutype)
        {
            case 1:
                leftmapList = list;
                break;
            case 2:
                rightmapList=list;
                break;
        }

        myAdapter=new MyAdapter();
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(onItemClickListener);
        this.menutype=menutype;
    }


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
            dissbackview();
            dismiss();
            if (ioptionmenuitem_select !=null)
                ioptionmenuitem_select.OnOptionMenuItemSelect((Integer)view.getTag() ,bottonmapList  .get((Integer)view.getTag()).get("uri").toString());//回调菜单选择索引

        }
    };

    /**
     * 设置菜单选择事件
     */
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (ioptionmenuitem_select !=null)
                ioptionmenuitem_select.OnOptionMenuItemSelect(i ,rightmapList .get(i).get("uri").toString());//回调菜单选择索引
        }
    };


    /**
     * 显示optionmenu popwindow
     * @param v 传入drop view
     */
    public  void showPopWindow(View v,int menutype) {
        this.menutype =menutype;
        switch (this.menutype)
        {
            case 1:
                if(leftmapList==null)
                    return;
                if( (leftmapList.size()==0 && this.menutype==1))
                    return;
                break;
            case 2:
                if(rightmapList==null)
                    return;
                if( rightmapList.size()==0 && this.menutype==2)
                    return;
                break;
        }



        this.setAnimationStyle(R.style.Animationpopwindows);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setOutsideTouchable(true);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.showAsDropDown(v);

    }

    /**
     * 底部显示menu

     */
    public  void showBotomWindow(View v) {
        this.backview =v;
        this.setAnimationStyle(R.style.Animationbottomwindows);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setOutsideTouchable(true);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.showAtLocation(v, Gravity.BOTTOM,0,0);

    }


    /**
     * 关闭optionmenu
     */
    public void closePopMenu()
    {
        this.dismiss();
    }


    /**
     * list适配器
     */
    class MyAdapter extends BaseAdapter {


        TextView menu_name;
        ImageView menu_image;




        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            switch (menutype)
            {
                case 1:
                    return leftmapList .size();

                case 2:
                    return rightmapList .size();
                default:
                    return 0;
            }

        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            switch (menutype)
            {
                case 1:
                    return leftmapList .get(arg0);
                case 2:
                    return rightmapList .get(arg0);
                default:
                    return null;
            }



        }

        @Override
        public long getItemId(int i) {
            return i;
        }




        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = layoutInflater.inflate(R.layout.list_main_optionmenu, null);
            menu_name=(TextView) view.findViewById(R.id.menu_name);
            menu_image = (ImageView) view.findViewById(R.id.menu_image);
            Map<String,Object> map =null;
            switch (menutype)
            {
                case 1:
                    map= leftmapList .get(i);
                    break;
                case 2:
                    map= rightmapList .get(i);
                    break;
            }

            menu_name.setText(map.get("menu_name").toString());
            menu_image.setBackground((Drawable)map.get("menu_image"));


            return view;
        }
    }


}
