package com.suypower.pms.view.user;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suypower.pms.R;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.view.contacts.Contacts;
import com.suypower.pms.view.plugin.chat.ChatActivity;
import com.suypower.pms.view.plugin.chat.MediaSupport;
import com.suypower.pms.view.plugin.message.MessageDB;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by Stereo on 16/4/15.
 */
public class UserInfoActivity extends Activity {

    private RelativeLayout menu_callphone;
    private Boolean IsSelf;
    private TextView txtname, txtphone, txtdepartment, txtemail, txtpost;
    private Button btnaddcontacts;
    private Contacts contacts;
    private ImageView nickimg,btnreturn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo_activity);
        txtname = (TextView) findViewById(R.id.name);
        txtphone = (TextView) findViewById(R.id.phone);
        btnreturn = (ImageView)findViewById(R.id.btnreturn);
        btnreturn.setOnClickListener(onClickListenerreturn);
        nickimg = (ImageView)findViewById(R.id.nickimg);



        txtdepartment = (TextView) findViewById(R.id.department);
        txtemail = (TextView) findViewById(R.id.sex);
        txtpost = (TextView) findViewById(R.id.post);
        btnaddcontacts = (Button)findViewById(R.id.btn_addcontacts) ;
        btnaddcontacts.setOnClickListener(onClickListeneraddcontacts);
        menu_callphone = (RelativeLayout) findViewById(R.id.menu_callphone);
        menu_callphone.setOnClickListener(onClickListenercallphone);
        txtpost = (TextView)findViewById(R.id.post);
        IsSelf = getIntent().getBooleanExtra("IsSelf", true);

        if (IsSelf) {
//            btncamera.setVisibility(View.VISIBLE);
            btnaddcontacts.setVisibility(View.INVISIBLE);
            txtname.setText(SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_strUserName);
            txtphone.setText(SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_strMobile);
            txtdepartment.setText(SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_strDeparment);
            txtemail.setText(SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_strSex);
            txtpost.setText(SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_positionName);
            Bitmap bitmap = BitmapFactory.decodeFile(getCacheDir() + File.separator +
                    SuyApplication.getApplication().getSuyClient().
                            getSuyUserInfo().m_loginResult.m_strPhoto+".jpg");
            nickimg.setImageBitmap(bitmap);
            nickimg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
//            btncamera.setVisibility(View.INVISIBLE);
            contacts = (Contacts) getIntent().getSerializableExtra("Contacts");
            txtname.setText(contacts.getName());
            txtphone.setText(contacts.getPhone());
            txtdepartment.setText(contacts.getDepartmentname());
            txtemail.setText(contacts.getSex());
            txtpost.setText(contacts.getPosition());
            Bitmap bitmap = BitmapFactory.decodeFile(getCacheDir() + File.separator +
                    contacts.getNickimgurl()+".jpg");
            nickimg.setImageBitmap(bitmap);
            nickimg.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (checkContacts(contacts.getPhone()))
                btnaddcontacts.setVisibility(View.INVISIBLE);
        }

    }



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


    View.OnClickListener onClickListeneraddcontacts = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AddContacts();
            Toast.makeText(UserInfoActivity.this,"已添加通讯录",Toast.LENGTH_SHORT).show();
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


    View.OnClickListener onClickListenercallphone = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            //用intent启动拨打电话
            if (!IsSelf) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                        contacts.getPhone()));
                startActivity(intent);
            }


        }
    };



    /**
     * 根据来电号码获取联系人名字
     * */
    public Boolean checkContacts(String phone){

        Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + phone);
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"display_name"}, null, null, null);
        int r = cursor.getCount();
        cursor.close();
        if (r > 0)
            return true;
        else
            return false;
    }


    /**
     * 添加联系人
     * 数据一个表一个表的添加，每次都调用insert方法
     * */
    public void AddContacts(){
        /* 往 raw_contacts 中添加数据，并获取添加的id号*/
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentResolver resolver =getContentResolver();
        ContentValues values = new ContentValues();
        long contactId = ContentUris.parseId(resolver.insert(uri, values));

        /* 往 data 中添加数据（要根据前面获取的id号） */
        // 添加姓名
        uri = Uri.parse("content://com.android.contacts/data");
        values.put("raw_contact_id", contactId);
        values.put("mimetype", "vnd.android.cursor.item/name");
        values.put("data2", contacts.getName());
        resolver.insert(uri, values);

        // 添加电话
        values.clear();
        values.put("raw_contact_id", contactId);
        values.put("mimetype", "vnd.android.cursor.item/phone_v2");
//        values.put("data2", "2");
        values.put("data1", contacts.getPhone());
        resolver.insert(uri, values);

        // 添加Email
        values.clear();
        values.put("raw_contact_id", contactId);
        values.put("mimetype", "vnd.android.cursor.item/email_v2");
//        values.put("data2", "2");
        values.put("data1", contacts.getEmail());
        resolver.insert(uri, values);

        values.clear();
        Bitmap sourceBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.nick_superman);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        // 将Bitmap压缩成PNG编码，质量为100%存储
        sourceBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        byte[] avatar = os.toByteArray();
        values.put("raw_contact_id", contactId);
        values.put("mimetype", "vnd.android.cursor.item/photo");
//        values.put("data2", "2");
        values.put("data15", avatar);
        resolver.insert(uri, values);



    }







}