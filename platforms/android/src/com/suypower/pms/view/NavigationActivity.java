package com.suypower.pms.view;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.suypower.pms.R;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.SuyService;
import com.suypower.pms.app.configxml.AppConfig;
import com.suypower.pms.app.configxml.GlobalConfig;
import com.suypower.pms.server.ControlCenter;
import com.suypower.pms.server.StereoService;
import com.suypower.pms.view.plugin.message.MessageMainView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cjw on 15/6/10.
 */
public class NavigationActivity extends Activity {


    TextView username ;//用户姓名
    TextView usercompany;//用户公司
    TextView userdepartment;// 部门职位
    ImageView userphoto;//用户头像
    ImageView settingimage;


    ListView listView;
    List<Map<String,Object>> mapList;

    MyAdapter myAdapter;
    ImageView imageViewmark;
    ImageView imageViewmessage;
    FrameLayout frameLayout;
    Animation animation1;
    Animation animation2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_activity);
        frameLayout = (FrameLayout)findViewById(R.id.image_message);
        if (GlobalConfig.globalConfig.getMessage() == 1) {
            Intent intent = new Intent(this, StereoService.class);
            intent.putExtra("mode", "2");
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
            imageViewmark = (ImageView)findViewById(R.id.mark1_main_image_tabbar);
            imageViewmessage = (ImageView)findViewById(R.id.menu1_main_tabbar);
            imageViewmessage.setOnClickListener(onClickListenerimagemark);
        }
        else
        {

            frameLayout.setVisibility(View.GONE);
        }
        username  = (TextView)findViewById(R.id.setting_name);
        usercompany =(TextView)findViewById(R.id.usercompany);
        userphoto = (ImageView)findViewById(R.id.main_setting_photo);
        userdepartment = (TextView)findViewById(R.id.department);
        listView=(ListView)findViewById(R.id.list);
        settingimage = (ImageView)findViewById(R.id.image_setting);



        username.setText(SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_strUserName);
        usercompany.setText(SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_strCompany);
//        userphoto.setBackground(SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_strPhoto);
        userdepartment.setText(SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_strDeparment);

        init();
        animation1 = AnimationUtils.loadAnimation(this, R.anim.scale);
        animation2 = AnimationUtils.loadAnimation(this,R.anim.scale_exit);

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (GlobalConfig.globalConfig.getMessage() == 1) {
            if (SuyApplication.getApplication().getSuyDB().checkmsgcounts()) {
                imageViewmark.startAnimation(animation2);
                imageViewmark.setVisibility(View.INVISIBLE);
            } else {
                imageViewmark.startAnimation(animation1);
                imageViewmark.setVisibility(View.VISIBLE);
            }
        }


    }

    void init()
    {
        mapList=new ArrayList<Map<String, Object>>();

        for (int i =0; i < GlobalConfig.globalConfig.getAppConfigs().length;i++)
        {

            Map<String,Object> map = new HashMap<String, Object>();
            map.put("title", GlobalConfig.globalConfig.getAppConfigs()[i].getAppname());
            map.put("memo",GlobalConfig.globalConfig.getAppConfigs()[i].getAppintroduce());

            BitmapDrawable bitmapDrawable = null;
            try {
                InputStream inputStream;
                if (!GlobalConfig.globalConfig.getIsCachefile())
                    inputStream = getAssets().open("www/apps/" + GlobalConfig.globalConfig.getAppInfos()[i].getAppName() + "/" +
                        GlobalConfig.globalConfig.getAppConfigs()[i].getApptitlepng());
                else
                    inputStream = new FileInputStream( getFilesDir() + "/www/apps/" +
                            GlobalConfig.globalConfig.getAppInfos()[i].getAppCode() + "/" +
                            GlobalConfig.globalConfig.getAppConfigs()[i].getApptitlepng());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                bitmapDrawable = new BitmapDrawable(bitmap);
                inputStream.close();

                //app图标写入缓存目录下面
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] buffer = baos.toByteArray();

                FileOutputStream fileOutputStream= new FileOutputStream(getCacheDir()+"/"+
                GlobalConfig.globalConfig.getAppInfos()[i].getAppCode() + ".png" );
                fileOutputStream.write(buffer);
                fileOutputStream.flush();
                fileOutputStream.close();


//                bitmap.recycle();
            }
            catch (Exception e)
            {e.printStackTrace();}
            map.put("image", bitmapDrawable);
            mapList.add(map);


        }

        myAdapter = new MyAdapter(this);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(onItemClickListener);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode ==100) {
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left1);
            Intent intent = new Intent(this,
                    LoginActivity.class);
            startActivity(intent);
        }


        //消息重新注册通知
        if (requestCode == 101)
        {
            ControlCenter.controlCenter.setiMessageControl(iMessageControl);
        }
    }


    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            switch (position) {
//                case 0:
            if (!GlobalConfig.globalConfig.getIsCachefile())
                SuyApplication.getApplication().getAbsluteAppPath = "file:///android_asset/www/apps/" + GlobalConfig.globalConfig.getAppInfos()[position].getAppName() + "/";
            else
                SuyApplication.getApplication().getAbsluteAppPath = "file:///" + getFilesDir() + "/www/apps/" +
                        GlobalConfig.globalConfig.getAppInfos()[position].getAppCode() + "/";

            if (!GlobalConfig.globalConfig.getIsCachefile())
                SuyApplication.getApplication().getRelAppPath = "www/apps/" + GlobalConfig.globalConfig.getAppInfos()[position].getAppName() + "/";
            else
                SuyApplication.getApplication().getRelAppPath = getFilesDir() + "/www/apps/" +
                        GlobalConfig.globalConfig.getAppInfos()[position].getAppCode() + "/";
            AppConfig.appConfig = GlobalConfig.globalConfig.getAppConfigs()[position];
            Intent intent = new Intent(NavigationActivity.this, MainActivity.class);
            intent.putExtra("displaytype","main");
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left1);
//                    break;
//
//            }
        }
    };

    class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        TextView projectname;
        TextView memo;
        ImageView imageView;


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

            view = mInflater.inflate(R.layout.list_navigation_srv_view, null);

            projectname = (TextView) view.findViewById(R.id.title);
            memo = (TextView) view.findViewById(R.id.memo);
            imageView =(ImageView)view.findViewById(R.id.image);

            imageView.setBackground((Drawable)mapList.get(i).get("image"));
            projectname.setText(mapList.get(i).get("title").toString());
            memo.setText("介绍：" + mapList.get(i).get("memo").toString());

            return view;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode ==4)
        {

            return false;
        }
        return super.onKeyUp(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //删除所有 临时照片
        File[] cachefile = getCacheDir().listFiles();
        for (File file :cachefile)
        {
            file.delete();
        }


        SuyService.stopSuyService(this);
        unbindService(serviceConnection);
    }

    /**
     * 服务
     */

    ServiceConnection serviceConnection=  new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ControlCenter.controlCenter = (ControlCenter)iBinder;
            ControlCenter.controlCenter.setiMessageControl(iMessageControl);
            ControlCenter.controlCenter.init(SuyApplication.getApplication()
                    .getSuyClient().getSuyUserInfo().m_loginResult.m_strSKey);
            ControlCenter.controlCenter.setIsRunAPP(true);
            ControlCenter.controlCenter.LoopMsgStart();

        }


        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            ControlCenter.controlCenter.setIsRunAPP(false);
            ControlCenter.controlCenter.LoopMsgStop();
            ControlCenter.controlCenter=null;


        }
    };


    View.OnClickListener onClickListenerimagemark = new View.OnClickListener() {
        @Override
        public void onClick(View v) {




            Intent intent = new Intent(NavigationActivity.this, MessageMainView.class);
            startActivityForResult(intent, 101);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left1);


        }
    };


    /**
     * 消息控制回调
     */
    ControlCenter.IMessageControl iMessageControl = new ControlCenter.IMessageControl() {

        @Override
        public void OnGetGroupList(int state) {

        }

        @Override
        public void OnNewMessage(String Message) {
            imageViewmark.startAnimation(animation1);
            imageViewmark.setVisibility(View.VISIBLE);
        }
    };


}