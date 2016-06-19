package com.suypower.pms.view.plugin.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.suypower.pms.R;
import com.suypower.pms.app.Config;
import com.suypower.pms.app.ReturnData;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.task.IM;
import com.suypower.pms.app.task.InterfaceTask;
import com.suypower.pms.server.ControlCenter;
import com.suypower.pms.view.contacts.Contacts;
import com.suypower.pms.view.contacts.ContactsSelectActivity;
import com.suypower.pms.view.plugin.CommonPlugin;
import com.suypower.pms.view.plugin.CustomPopWindowPlugin;
import com.suypower.pms.view.plugin.message.MessageDB;
import com.suypower.pms.view.user.UserInfoActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stereo on 16/4/28.
 */
public class ChatsMangerActivity extends Activity {

    public static final int REQUESTCODE = 80;
    public static final int CLEARRESULTCODE = 1;
    public static final int EXITRESULTCODE = 2;
    public static final int CHANGETITLE = 3;


    private ImageView btnreturn;
    private TextView title;
    private GridLayout gridLayout;
    private EditText editchatname;
    private Switch aSwitch1, aSwitch2;
    private RelativeLayout relativeLayoutclear, menu_chatname;
    private Button btndelete;
    private Contacts contacts;
    private List<String> contactslists;
    int showmode;
    private String groupid;
    private JSONObject groupinfo;
    private String groupmanger;

    int selfdisturb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatsmanger_activity);
        btnreturn = (ImageView) findViewById(R.id.btnreturn);
        title = (TextView) findViewById(R.id.chatsname);
        gridLayout = (GridLayout) findViewById(R.id.chatergrid);
        editchatname = (EditText) findViewById(R.id.name);
        editchatname.addTextChangedListener(textWatcher);
        aSwitch1 = (Switch) findViewById(R.id.switch1);
        aSwitch2 = (Switch) findViewById(R.id.switch2);
        relativeLayoutclear = (RelativeLayout) findViewById(R.id.menu_msgclear);
        menu_chatname = (RelativeLayout) findViewById(R.id.menu_chatname);
        btndelete = (Button) findViewById(R.id.menu_delete);

        contactslists = new ArrayList<>();
        showmode = getIntent().getIntExtra("ShowMode", 1);
        MessageDB messageDB = new MessageDB(SuyApplication.getApplication().getSuyDB().getDb());

        if (showmode == 1) {
            menu_chatname.setVisibility(View.GONE);
            contacts = (Contacts) getIntent().getSerializableExtra("Contacts");
            title.setText("聊天详情");
            selfdisturb = messageDB.isExitsdisturblist(contacts.getId(),
                    SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_strUserId);
            if (selfdisturb == 1)
                aSwitch1.setChecked(true);
            else
                aSwitch1.setChecked(false);
            aSwitch1.setOnCheckedChangeListener(onCheckedChangeListener);
        }
        if (showmode == 2) {

            try {
                groupinfo = new JSONObject(getIntent().getStringExtra("groupinfo"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        aSwitch2.setOnCheckedChangeListener(onCheckedChangeListener);


        relativeLayoutclear.setOnClickListener(onClickListenerclear);
        btnreturn.setOnClickListener(onClickListenerreturn);
        btndelete.setOnClickListener(onClickListenerdelete);


        reloadContacts();
    }


    /**
     * 监控群组名称变化
     */
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            MessageDB messageDB = new MessageDB(SuyApplication.getApplication().getSuyDB().getDb());
            if (showmode == 1)
                messageDB.updategroupName(contacts.getId(), editable.toString());
            if (showmode == 2)
                messageDB.updategroupName(groupid, editable.toString());
            Intent intent = new Intent();
            intent.putExtra("title", editable.toString());
            setResult(CHANGETITLE, intent);
        }
    };

    /**
     * 读取成员信息
     */
    void reloadContacts() {
        View itme;
        contactslists.clear();
        gridLayout.removeAllViews();
        TextView textView;
        ImageView imageView;
        LinearLayout.LayoutParams params;
        if (showmode == 1) {
            itme = getLayoutInflater().inflate(R.layout.chat_item_view, null);
            params = new LinearLayout.LayoutParams(CommonPlugin.dip2px(85), CommonPlugin.dip2px(70));
            params.setMargins(5, 5, 5, 5);
            itme.setLayoutParams(params);
            textView = (TextView) itme.findViewById(R.id.item_title);
            imageView = (ImageView) itme.findViewById(R.id.item_img);
            textView.setText(contacts.getName());
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.default_avatar));
            imageView.setTag(1);
            imageView.setOnClickListener(onClickListenercontacts);
            contactslists.add(contacts.getId());
            gridLayout.addView(itme);
            gridLayout.addView(addview());
            return;
        }
        if (showmode == 2) {
            try {
                JSONObject groupjson = groupinfo.getJSONObject("groupInfo");
                JSONArray members = groupinfo.getJSONArray("members");
                MessageDB messageDB = new MessageDB(SuyApplication.getApplication().getSuyDB().getDb());
                title.setText("聊天信息(" + members.length() + ")");
                groupid = groupjson.getString("groupId");
                groupmanger = groupjson.getString("groupOwner");

                editchatname.setText(messageDB.getGroupName(groupjson.getString("groupId")));
                selfdisturb = messageDB.isExitsdisturblist(groupid,
                        SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_strUserId);
                if (selfdisturb == 1)
                    aSwitch1.setChecked(true);
                else
                    aSwitch1.setChecked(false);
                aSwitch1.setOnCheckedChangeListener(onCheckedChangeListener);
                for (int i = 0; i < members.length(); i++) {
                    JSONObject contactsjson = members.getJSONObject(i);
                    itme = getLayoutInflater().inflate(R.layout.chat_item_view, null);
                    params = new LinearLayout.LayoutParams(CommonPlugin.dip2px(85), CommonPlugin.dip2px(70));
                    params.setMargins(10, 10, 10, 10);
                    itme.setLayoutParams(params);
                    textView = (TextView) itme.findViewById(R.id.item_title);
                    imageView = (ImageView) itme.findViewById(R.id.item_img);
                    textView.setText(contactsjson.getString("userName"));
                    contactslists.add(contactsjson.getString("userId"));
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.default_avatar));
                    imageView.setTag(1);
                    imageView.setOnClickListener(onClickListenercontacts);
                    gridLayout.addView(itme);
                }
                gridLayout.addView(addview());
                if(SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_strUserId
                        .equals(groupmanger)){
                    gridLayout.addView(createRemoveView());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


    View.OnClickListener onClickListenercontacts = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int i = (Integer) view.getTag();
            Intent intent;
            if (i == 99) {

                if (showmode == 1) {
                    Toast.makeText(ChatsMangerActivity.this, "暂时不能添加成员，转为群聊天", Toast.LENGTH_SHORT).show();
                    return;
                }

                intent = new Intent(ChatsMangerActivity.this, ContactsSelectActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("contactslist", (ArrayList<String>) contactslists);
                intent.putExtras(bundle);
                startActivityForResult(intent, ContactsSelectActivity.RequestCode);

                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return;
            }
            if (showmode == 1) {
                intent = new Intent(ChatsMangerActivity.this, UserInfoActivity.class);
                intent.putExtra("IsSelf", false);//自己打开
                intent.putExtra("Contacts", (Serializable) contacts);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return;
            }

        }
    };

    View.OnClickListener onClickListenerRemoveMembers = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Toast.makeText(ChatsMangerActivity.this,"该功能暂未开放",Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ContactsSelectActivity.RequestCode == requestCode) {
            if (resultCode == 0)
                return;
            String[] stringlist = data.getStringArrayExtra("contactslist");
            //创建群聊
            String str = "";
            for (int i = 0; i < stringlist.length; i++) {
                str += stringlist[i] + ",";
            }

            str = str.substring(0, str.length() - 1);
            Log.i("选择的人：", str);

            CustomPopWindowPlugin.ShowPopWindow(gridLayout, getLayoutInflater(), "请稍后");
            IM im = new IM(interfaceTask, IM.ADDEXGROUP);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("members", str);
                jsonObject.put("groupid", groupid);
            } catch (Exception e) {
                e.printStackTrace();
            }
            im.setParams(jsonObject.toString());
            im.startTask();

        }
    }

    /**
     * 更多添加按钮
     *
     * @return
     */
    private View addview() {
        View itme;
        LinearLayout.LayoutParams params;

        TextView textView;
        ImageView imageView;
        itme = getLayoutInflater().inflate(R.layout.chat_item_view, null);
        params = new LinearLayout.LayoutParams(CommonPlugin.dip2px(85), CommonPlugin.dip2px(70));
        params.setMargins(10, 10, 10, 10);

        itme.setLayoutParams(params);
        itme.setPadding(15, 15, 15, 15);
        textView = (TextView) itme.findViewById(R.id.item_title);
        imageView = (ImageView) itme.findViewById(R.id.item_img);
        textView.setVisibility(View.INVISIBLE);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.contacts_add_selector));
        imageView.setTag(99);
        imageView.setOnClickListener(onClickListenercontacts);
        return itme;
    }

    private View createRemoveView(){
        View itme;
        LinearLayout.LayoutParams params;

        TextView textView;
        ImageView imageView;
        itme = getLayoutInflater().inflate(R.layout.chat_item_view, null);
        params = new LinearLayout.LayoutParams(CommonPlugin.dip2px(85), CommonPlugin.dip2px(70));
        params.setMargins(10, 10, 10, 10);

        itme.setLayoutParams(params);
        itme.setPadding(15, 15, 15, 15);
        textView = (TextView) itme.findViewById(R.id.item_title);
        imageView = (ImageView) itme.findViewById(R.id.item_img);
        textView.setVisibility(View.INVISIBLE);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.contacts_remove_selector));
        imageView.setTag(99);
        imageView.setOnClickListener(onClickListenerRemoveMembers);
        return itme;
    }


    /**
     * 开关
     */
    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            switch (compoundButton.getId()) {
                case R.id.switch1:
                    MessageDB messageDB = new MessageDB(SuyApplication.getApplication().getSuyDB().getDb());
                    if (b) {
                        if (showmode == 1) {
                            messageDB.adddisturblist(contacts.getId(),
                                    SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_strUserId);
                        }
                        if (showmode == 2) {
                            messageDB.adddisturblist(groupid, SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_strUserId);
                        }
                    } else {
                        if (showmode == 1) {
                            messageDB.deldisturblist(contacts.getId(),
                                    SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_strUserId);
                        }
                        if (showmode == 2) {
                            messageDB.deldisturblist(groupid, SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_strUserId);
                        }
                    }
                    break;
                case R.id.switch2:
                    Toast.makeText(ChatsMangerActivity.this, "此功能没有开放", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    /**
     * 清除聊天记录
     */
    View.OnClickListener onClickListenerclear = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ChatDB chatDB = new ChatDB(SuyApplication.getApplication().getSuyDB().getDb());


            if (showmode == 1) {
                chatDB.delChatLog(contacts.getId());
            }
            if (showmode == 2) {
                chatDB.delChatLog(groupid);

            }
            Toast.makeText(ChatsMangerActivity.this, "已清除", Toast.LENGTH_SHORT).show();
            setResult(CLEARRESULTCODE);
        }
    };

    InterfaceTask interfaceTask = new InterfaceTask() {
        @Override
        public void TaskResultForMessage(Message message) {
            CustomPopWindowPlugin.CLosePopwindow();
            if (message.arg2 == IM.ADDEXGROUP) {
                groupinfo = ((ReturnData) message.obj).getReturnData();
                reloadContacts();
            }
        }
    };


    /**
     * 删除并且退出
     */
    View.OnClickListener onClickListenerdelete = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MessageDB messageDB = new MessageDB(SuyApplication.getApplication().getSuyDB().getDb());
            ChatDB chatDB = new ChatDB(SuyApplication.getApplication().getSuyDB().getDb());

            //单聊
            if (showmode == 1) {
                messageDB.delMessage(contacts.getId());
                chatDB.delChatLog(contacts.getId());

            }

            if (showmode == 2) {
                messageDB.delMessage(groupid);
                chatDB.delChatLog(groupid);
                //删除退出群
                IM im = new IM(interfaceTask, IM.REMOVEGROUP);
                im.setPostValuesForKey("groupid", groupid);
                im.setPostValuesForKey("userid", SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_strUserId);
                im.startTask();
            }
            setResult(EXITRESULTCODE);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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

}