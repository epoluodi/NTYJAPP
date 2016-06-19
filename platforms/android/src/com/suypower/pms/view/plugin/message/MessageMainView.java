package com.suypower.pms.view.plugin.message;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.suypower.pms.R;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.configxml.AppConfig;
import com.suypower.pms.server.ControlCenter;
import com.suypower.pms.server.NotificationClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cjw on 15/8/24.
 */
public class MessageMainView extends Activity {



    ImageView leftbutton;//左上角 控制按钮
    TextView title;
    ListView listView;
    List<Map<String,Object>> mapList;
    MyAdapter myAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messagemainview);
        leftbutton= (ImageView)findViewById(R.id.title_left_menu_image);
        leftbutton.setOnClickListener(onClickListenerreturn);
        title = (TextView)findViewById(R.id.title_text);
        title.setText("消息中心");



        listView = (ListView)findViewById(R.id.listview);
        mapList = new ArrayList<Map<String, Object>>();
        reloadMsgdata();



        myAdapter = new MyAdapter(this);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(onItemClickListener);



//        File[] cachefile = getCacheDir().listFiles();
//        for (File file :cachefile)
//        {
//            file.delete();
//        }



    }


    @Override
    protected void onResume() {
        super.onResume();
        ControlCenter.controlCenter.setiMessageControl(iMessageControl);
        ControlCenter.controlCenter.setIsNotification(false);
        NotificationClass.Clear_Notify();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ControlCenter.controlCenter.setiMessageControl(null);
        ControlCenter.controlCenter.setIsNotification(true);
    }



    void reloadMsgdata()
    {
        Cursor cursor = SuyApplication.getApplication().getSuyDB().getMsgData();
        if (cursor.getCount() == 0)
        {
            cursor.close();
            return;
        }

        Map<String, Object> map;
        mapList.clear();
        while (cursor.moveToNext())
        {
            map = new HashMap<String, Object>();
            map.put("msgid",cursor.getString(3));
            map.put("msgcounts",cursor.getInt(4));
            map.put("appcode",cursor.getString(0));
            map.put("dt",MessageInfo.GetSysTime( cursor.getString(1)));
            String json =  cursor.getString(2);
            InputStream inputStream;
            BitmapDrawable bitmapDrawable = null;
            try
            {
                String title ="",content ="";
                JSONObject jsonObject = new JSONObject(json);
                try {
                    if (jsonObject.getString("msgType").equals("text")) {

                        title = jsonObject.getString("title");
                        content = jsonObject.getString("content");

                    } else if (jsonObject.getString("msgType").equals("news")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("articles");

                        JSONObject jsonObject1 = (JSONObject) jsonArray.get(0);
                        title = jsonObject1.getString("title");
                        content = jsonObject1.getString("content");

                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }



                map.put("title", title);
                map.put("content",content);

                File fileimg = new File(getCacheDir() + "/" +
                        cursor.getString(0) + ".png");
                if (fileimg.exists()) {

                    inputStream = new FileInputStream(getCacheDir() + "/" +
                            cursor.getString(0) + ".png");
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    bitmapDrawable = new BitmapDrawable(bitmap);
                    map.put("png", bitmapDrawable);
//                    bitmap.recycle();
                    inputStream.close();
                }
                else
                {
                    Drawable drawable =getResources().getDrawable(R.drawable.ico_logo);

                    map.put("png", drawable);
                }

            }
            catch (Exception e)
            {e.printStackTrace();

            }

            mapList.add(map);
        }


        cursor.close();


    }



    View.OnClickListener onClickListenerreturn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setResult(0);
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
    };

    /**
     * 消息控制回调
     */
    ControlCenter.IMessageControl iMessageControl = new ControlCenter.IMessageControl() {
        @Override
        public void OnNewMessage(String Message) {
            reloadMsgdata();
            myAdapter =(MyAdapter)listView.getAdapter();
            myAdapter.notifyDataSetChanged();
        }

        @Override
        public void OnGetGroupList(int state) {

        }
    };



    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {




            Map<String, Object> map = mapList.get(position);
            SuyApplication.getApplication().getSuyDB().updateMsg(
                    map.get("appcode").toString(),0);
            iMessageControl.OnNewMessage("");

            Intent intent =new Intent(MessageMainView.this,MessageDetailView.class);
            intent.putExtra("appcode",map.get("appcode").toString());
            if (map.get("appcode").toString().equals("000000"))
                intent.putExtra("appname", "平台消息");
            else
                intent.putExtra("appname",
                    AppConfig.appmap.get(map.get("appcode").toString()));

            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left1);


        }
    };


    class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        TextView title;
        TextView memo;
        TextView dt;
        ImageView imageView;
        TextView txtmark;


        public MyAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub

            return mapList.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return mapList.get(arg0);

        }

        @Override
        public long getItemId(int i) {
            return i;
        }


        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            view = mInflater.inflate(R.layout.list_msg_main, null);
//
            title = (TextView) view.findViewById(R.id.title);
            memo = (TextView) view.findViewById(R.id.memo);
            dt=(TextView)view.findViewById(R.id.dt);
            txtmark =(TextView)view.findViewById(R.id.txtmark);
            imageView =(ImageView)view.findViewById(R.id.menu1_main_tabbar);

            Map<String, Object> map = mapList.get(i);
            imageView.setBackground((Drawable) map.get("png"));
            title.setText(map.get("title").toString());
            memo.setText( map.get("content").toString());
//            dt.setText( GetSysTime(map.get("dt").toString()));
            dt.setText( map.get("dt").toString());
            int counts = Integer.valueOf(map.get("msgcounts").toString());
            if (counts == 0)
            {
                txtmark.setText("");
                txtmark.setVisibility(View.INVISIBLE);
            }
            else {
                if (Integer.valueOf(map.get("msgcounts").toString()) > 9)
                    txtmark.setText("9+");
                else
                    txtmark.setText(map.get("msgcounts").toString());
                txtmark.setVisibility(View.VISIBLE);
            }
            return view;
        }




    }

}