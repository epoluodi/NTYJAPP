package com.suypower.pms.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.suypower.pms.R;
import com.suypower.pms.app.ReturnData;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.task.BaseTask;
import com.suypower.pms.app.task.IM;
import com.suypower.pms.app.task.InterfaceTask;
import com.suypower.pms.view.plugin.CommonPlugin;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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

        IM im=new IM(interfaceTask,IM.GETUSERSTATE);
        im.setParams(groupid);
        im.startTask();

    }


    InterfaceTask interfaceTask=new InterfaceTask() {
        @Override
        public void TaskResultForMessage(Message message) {
            if (message.what== BaseTask.IMTask)
            {
                if (message.arg2 == IM.GETUSERSTATE)
                {
                    if (message.arg1==BaseTask.SUCCESS)
                    {
                        ReturnData returnData = (ReturnData)message.obj;
                        JSONArray jsonArray=returnData.getJsonArray();
                        mapList.clear();
                        for (int i=0; i<jsonArray.length();i++)
                        {
                            try{
                                JSONObject jsonObject=jsonArray.getJSONObject(i);
                                Map<String,String>map = new HashMap<>();
                                map.put("READ_STATUS",jsonObject.getString("READ_STATUS"));
                                map.put("POSITION_NAME",jsonObject.getString("POSITION_NAME"));
                                map.put("READ_TIME",jsonObject.getString("READ_TIME"));
                                map.put("USER_NAME",jsonObject.getString("USER_NAME"));
                                map.put("SEX",jsonObject.getString("SEX"));
                                map.put("GROUP_NAMES",jsonObject.getString("GROUP_NAMES"));
                                map.put("PICTURE",jsonObject.getString("PICTURE"));
                                mapList.add(map);
                            }
                            catch (Exception e)
                            {e.printStackTrace();}


                        }
                        myAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };




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
        private ImageView state,nickimg;
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

            view = layoutInflater.inflate(R.layout.member_list, null);
            state = (ImageView) view.findViewById(R.id.state);
            name = (TextView) view.findViewById(R.id.name);
            position = (TextView) view.findViewById(R.id.position);
            deparment = (TextView) view.findViewById(R.id.department);
            nickimg = (ImageView) view.findViewById(R.id.nickimg);
            Map<String, String> map = mapList.get(i);
            if (map.get("READ_STATUS").equals("01"))
                state.setBackground(getResources().getDrawable(R.drawable.ok_s));
            else
                state.setBackground(getResources().getDrawable(R.drawable.no_s));

            name.setText(map.get("USER_NAME"));
            position.setText(map.get("POSITION_NAME"));
            deparment.setText(map.get("GROUP_NAMES"));
            if (CommonPlugin.checkFileIsExits(map.get("PICTURE"), "40.jpg")) {
                Bitmap bitmap = BitmapFactory.decodeFile(SuyApplication.getApplication().getCacheDir() + "/" +
                        map.get("PICTURE") + "40.jpg"); //将图片的长和宽缩小味原来的1/2
                if (bitmap !=null) {
                    nickimg.setImageBitmap(bitmap);
                    nickimg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            } else
            {
                nickimg.setImageDrawable(getResources().getDrawable(R.drawable.default_avatar));
            }



            return view;
        }
    }


}