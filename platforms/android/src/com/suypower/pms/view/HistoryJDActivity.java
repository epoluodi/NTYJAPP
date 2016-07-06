package com.suypower.pms.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.suypower.pms.R;
import com.suypower.pms.app.ReturnData;
import com.suypower.pms.app.task.InterfaceTask;
import com.suypower.pms.view.dlg.IMenu;
import com.suypower.pms.view.dlg.Menu_Custom;
import com.suypower.pms.view.plugin.chat.MediaSupport;
import com.suypower.pms.view.plugin.message.MessageInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cjw on 16/6/30.
 */
public class HistoryJDActivity extends Activity {
    private ImageView btnreturn;
    private TextView title;
    private ListView list;
    private Myadpter myadpter;
    private List<Map<String,String>> maps;
    private Menu_Custom menu_custom;
    private ImageView topRightButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.histroyjd_activity);

        title = (TextView) findViewById(R.id.title);
        btnreturn = (ImageView) findViewById(R.id.btnreturn);
        btnreturn.setOnClickListener(onClickListenerreturn);
        list = (ListView) findViewById(R.id.list);
        topRightButton = (ImageView) findViewById(R.id.topRightButton);
        topRightButton.setOnClickListener(onClickListenercreatechat);
        menu_custom = new Menu_Custom(this, iMenu);
        menu_custom.addMenuItem(R.drawable.month, "一个月内", 0);
        menu_custom.addMenuItem(R.drawable.month3, "三个月内", 1);
        menu_custom.addMenuItem(R.drawable.year, "一年内", 1);
        maps = new ArrayList<>();
        myadpter = new Myadpter(this);
        list.setAdapter(myadpter);

        list.setOnItemClickListener(onItemClickListener);
        refreshJson(1);


    }

    /**
     * 菜单点击回调
     */
    IMenu iMenu = new IMenu() {
        @Override
        public void ClickMenu(int itemid) {
            Intent intent;
            switch (itemid) {
                case 0://1个月
                    refreshJson(1);
                    break;
                case 1://3个月
                    refreshJson(3);
                    break;
                case 2://一年
                    refreshJson(12);
                    break;
            }


        }
    };



    /**
     * 点击更多
     */
    View.OnClickListener onClickListenercreatechat = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            menu_custom.ShowMenu(topRightButton);

        }

    };

    AdapterView.OnItemClickListener onItemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Map<String,String>map = maps.get(i);
            Intent intent=new Intent(HistoryJDActivity.this,JDDetailActivity.class);
            intent.putExtra("json",map.get("json").toString());
            intent.putExtra("mode",1);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.alpha);
            finish();
        }
    };



    InterfaceTask interfaceTask=new InterfaceTask() {
        @Override
        public void TaskResultForMessage(Message message) {

        }
    };

    private void refreshJson(int querytype)
    {
        try {

            myadpter.notifyDataSetChanged();

        }
        catch (Exception e)
        {
            Toast.makeText(HistoryJDActivity.this,"获取审核信息错误",Toast.LENGTH_SHORT).show();
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        }

    }




    /**
     * 返回
     */
    View.OnClickListener onClickListenerreturn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MediaSupport.stopAudio();
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    };


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {


        if (keyCode == 4) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return false;
        }
        return super.onKeyUp(keyCode, event);
    }


    class Myadpter extends BaseAdapter {
        LayoutInflater layoutInflater;
        TextView title,dt;

        public Myadpter(Context context) {
            layoutInflater = LayoutInflater.from(context);


        }

        @Override
        public int getCount() {
            return maps.size();
        }

        @Override
        public Object getItem(int i) {
            return maps.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            view = layoutInflater.inflate(R.layout.appove_list,null);
            title = (TextView)view.findViewById(R.id.title);
            dt = (TextView)view.findViewById(R.id.dt);
            Map<String,String> map= maps.get(i);
            title.setText(map.get("DISPATCH_TITLE"));
            dt.setText(MessageInfo.GetSysTime( map.get("CREATE_TIME")));

            return view;
        }
    }

}