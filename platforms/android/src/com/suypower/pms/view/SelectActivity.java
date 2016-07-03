package com.suypower.pms.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.suypower.pms.R;
import com.suypower.pms.app.ReturnData;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.task.BaseTask;
import com.suypower.pms.app.task.Contacts;
import com.suypower.pms.app.task.FileDownload;
import com.suypower.pms.app.task.InterfaceTask;
import com.suypower.pms.app.task.Right;
import com.suypower.pms.view.contacts.ContactsDB;
import com.suypower.pms.view.plugin.CommonPlugin;
import com.suypower.pms.view.plugin.CustomPopWindowPlugin;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cjw on 16/6/25.
 */
public class SelectActivity extends Activity {
    public static final int ApproveUsers = 2;
    public static final int GroupInfo = 1;
    private ImageView btnreturn, btnok;
    private TextView title;
    private ListView list;
    private Mydapter mydapter;
    private List<Map<String, String>> mapList;
    private int showmode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_activity);
        title = (TextView) findViewById(R.id.title);
        btnreturn = (ImageView) findViewById(R.id.btnreturn);
        btnreturn.setOnClickListener(onClickListenerreturn);
        btnok = (ImageView) findViewById(R.id.btnok);

        list = (ListView) findViewById(R.id.list);
        //设置标题

        mapList = new ArrayList<>();

        title.setText(getIntent().getStringExtra("title"));
        mydapter = new Mydapter(this);

        showmode = getIntent().getIntExtra("mode", 0);


        //选择群组
        if (showmode == 1) {
            title.setText("选择调度组");
            btnok.setOnClickListener(onClickListenerbtnok);
            ContactsDB contactsDB = new ContactsDB(SuyApplication.getApplication().getSuyDB().getDb());
            Cursor cursor = contactsDB.getDepartment();
            while (cursor.moveToNext()) {
                Map<String, String> map = new HashMap<>();
                map.put("name", cursor.getString(0));
                map.put("id", cursor.getString(1));
                map.put("check", "0");
                mapList.add(map);
            }
            mydapter.notifyDataSetChanged();

        }

        if (showmode == 2)// 选择审批人
        {
            list.setOnItemClickListener(onItemClickListener);
            title.setText("选择审批人");
            btnok.setVisibility(View.INVISIBLE);
            new Thread(runnable).start();
        }
        list.setAdapter(mydapter);
    }


    View.OnClickListener onClickListenerbtnok = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String groupid = "";
            String groupname = "";
            for (int i = 0; i < mapList.size(); i++) {
                Map<String, String> map = mapList.get(i);
                if (map.get("check").equals("1")) {
                    groupid+=map.get("id")+",";
                    groupname += map.get("name") + ",";
                }
            }
            Intent intent = new Intent();
            intent.putExtra("groupid",groupid.substring(0,groupid.length()-1));
            intent.putExtra("name", groupname.substring(0, groupname.length() - 1));
            setResult(2, intent);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);


        }
    };
    /**
     * 点击选择后返回
     */
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Map<String, String> map = mapList.get((int) l);
            Intent intent = new Intent();

            if (showmode == 2) {
                intent.putExtra("sp", map.get("userid"));
                intent.putExtra("name", map.get("name"));
                setResult(2, intent);
            }
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(400);
                handler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    InterfaceTask interfaceTask = new InterfaceTask() {
        @Override
        public void TaskResultForMessage(Message message) {
            if (message.what == BaseTask.RIGHT) {
                CustomPopWindowPlugin.CLosePopwindow();
                if (message.arg2 == Right.GETApproveUsers) {
                    if (message.arg1 == BaseTask.SUCCESS) {
                        mapList.clear();
                        JSONArray jsonArray = ((ReturnData) message.obj).getJsonArray();
                        ContactsDB contactsDB = new ContactsDB(SuyApplication.getApplication().getSuyDB().getDb());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Map<String, String> map = new HashMap<>();
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                map.put("name", jsonObject.getString("USER_NAME"));
                                map.put("userid", jsonObject.getString("ACCOUNT_ID"));
                                com.suypower.pms.view.contacts.Contacts contacts = contactsDB.
                                        LoadChaterInfo(jsonObject.getString("ACCOUNT_ID"));
                                map.put("Position", contacts.getPosition());
                                map.put("nick", contacts.getNickimgurl());
                                mapList.add(map);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        mydapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(SelectActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                }
            }
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    CustomPopWindowPlugin.ShowPopWindow(list, getLayoutInflater(), "正在加载");
                    Right right = new Right(interfaceTask, Right.GETApproveUsers);
                    right.startTask();
                    break;
            }

        }
    };


    /**
     * 返回
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


    class Mydapter extends BaseAdapter {
        TextView name, position;
        ImageView nickimg;
        CheckBox checkBox;
        LayoutInflater layoutInflater;

        public Mydapter(Context context) {
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

            Map<String, String> map = mapList.get(i);

            View v = null;


            if (showmode == 1) {
                v = layoutInflater.inflate(R.layout.phonebook_list_personal, null);
                checkBox = (CheckBox) v.findViewById(R.id.check);
                checkBox.setVisibility(View.VISIBLE);
                if (map.get("check").equals("1"))
                    checkBox.setChecked(true);
                else
                    checkBox.setChecked(false);
                checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
                checkBox.setTag(i);
                name = (TextView) v.findViewById(R.id.name);
                nickimg = (ImageView) v.findViewById(R.id.nickimg);
                position = (TextView) v.findViewById(R.id.position);
                position.setText(map.get("Position"));
                if (CommonPlugin.checkFileIsExits(map.get("nick"), "40.jpg")) {
                    Bitmap bitmap = BitmapFactory.decodeFile(SuyApplication.getApplication().getCacheDir() + "/" +
                            map.get("nick") + "40.jpg"); //将图片的长和宽缩小味原来的1/2
                    nickimg.setImageBitmap(bitmap);
                    nickimg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
                name.setText(map.get("name"));
            }
            if (showmode == 2) {
                v = layoutInflater.inflate(R.layout.phonebook_list_personal, null);
                name = (TextView) v.findViewById(R.id.name);
                nickimg = (ImageView) v.findViewById(R.id.nickimg);
                position = (TextView) v.findViewById(R.id.position);
                position.setText(map.get("Position"));
                if (CommonPlugin.checkFileIsExits(map.get("nick"), "40.jpg")) {
                    Bitmap bitmap = BitmapFactory.decodeFile(SuyApplication.getApplication().getCacheDir() + "/" +
                            map.get("nick") + "40.jpg"); //将图片的长和宽缩小味原来的1/2
                    nickimg.setImageBitmap(bitmap);
                    nickimg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
                name.setText(map.get("name"));


            }
            return v;
        }

        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int i = (Integer) compoundButton.getTag();
                Map<String, String> map = mapList.get(i);
                map.remove("check");
                if (b)
                    map.put("check", "1");
                else
                    map.put("check", "0");

            }
        };
    }
}