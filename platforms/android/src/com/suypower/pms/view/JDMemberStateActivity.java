package com.suypower.pms.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.suypower.pms.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Stereo on 16/7/5.
 */
public class JDMemberStateActivity extends Activity {

    private ImageView btnreturn;
    private String groupid;
    private ListView listView;
    private List<Map<String,String>> mapList;
    private MyAdapter myAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jdmemberstate_activity);
        btnreturn = (ImageView) findViewById(R.id.btnreturn);
        btnreturn.setOnClickListener(onClickListenerreturn);
        groupid = getIntent().getStringExtra("groupid");
        listView = (ListView)findViewById(R.id.list);
        mapList = new ArrayList<>();
        myAdapter = new MyAdapter(this);
        listView.setAdapter(myAdapter);


    }






    /**
     * 点击返回
     */
    View.OnClickListener onClickListenerreturn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    };


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode == 4) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }

        return super.onKeyUp(keyCode, event);
    }



    class MyAdapter extends BaseAdapter
    {
        private ImageView state;
        private TextView name,position,deparment;

        private LayoutInflater layoutInflater;
        public MyAdapter(Context context)
        {
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mapList.size();
        }

        @Override
        public Object getItem(int i) {
            return mapList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            view = layoutInflater.inflate(R.layout.member_list,null);
            state = (ImageView)view.findViewById(R.id.state);
            name = (TextView)view.findViewById(R.id.name);
            position = (TextView)view.findViewById(R.id.position);
            deparment = (TextView)view.findViewById(R.id.department);
            Map<String,String> map=mapList.get(i);
            if (map.get("state").equals("01"))
                state.setBackground(getResources().getDrawable(R.drawable.ok_s));
            else
                state.setBackground(getResources().getDrawable(R.drawable.no_s));

            name.setText(map.get("name"));
            position.setText(map.get("position"));
            deparment.setText(map.get("deparment"));

            return view;
        }
    }


}