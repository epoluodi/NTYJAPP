package com.suypower.pms.view;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.suypower.pms.R;
import com.suypower.pms.app.ReturnData;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.task.BaseTask;
import com.suypower.pms.app.task.IM;
import com.suypower.pms.app.task.InterfaceTask;
import com.suypower.pms.app.task.PublishNotics;
import com.suypower.pms.server.ControlCenter;
import com.suypower.pms.view.contacts.ContactsSelectActivity;
import com.suypower.pms.view.dlg.IMenu;
import com.suypower.pms.view.dlg.Menu_Custom;
import com.suypower.pms.view.plugin.CustomPopWindowPlugin;
import com.suypower.pms.view.plugin.chat.ChatActivity;
import com.suypower.pms.view.plugin.chat.Emoji;
import com.suypower.pms.view.plugin.fragmeMager.FragmentName;
import com.suypower.pms.view.plugin.message.MessageDB;
import com.suypower.pms.view.plugin.message.MessageDetailView;
import com.suypower.pms.view.plugin.message.MessageInfo;
import com.suypower.pms.view.plugin.message.MessageList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 14-11-30.
 */
public class MessageCenterfragment extends Fragment implements FragmentName {





    private String Fragment_Name = "";
    private ImageView topRightButton;
    private Animation animationEnter;
    private Animation animationExit;
    private Animation aninenter;
    private Animation animexit;

    private TextView title;
    private ImageView btnsearch;
    private ImageView btncancel;
    private EditText searchtxt;
    private FrameLayout frameLayout;
    private ListView listView;
    private Menu_Custom menu_custom;
    private MyAdapter myAdapter;
    private List<MessageList> messageLists;
    private MessageDB messageDB = null;


    private String appovejson="";//审批json


    @Override
    public void selectcustomer(String guestid, String guestname) {
        return;
    }

    @Override
    public void SelectMenu(int Menuid) {

    }

    @Override
    public void returnWeb() {

    }

    @Override
    public void onMessage(Message message) {

    }

    @Override
    public void SetFragmentName(String name) {
        Fragment_Name = name;
    }

    @Override
    public String GetFragmentName() {
        return Fragment_Name;
    }


    @Override
    public void startIMessageControl() {
        ControlCenter.controlCenter.setiMessageControl(iMessageControl);
    }

    @Override
    public void stopIMessageControl() {
        ControlCenter.controlCenter.setiMessageControl(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_message, container, false);
        frameLayout = (FrameLayout) rootView.findViewById(R.id.frameview);
        title = (TextView) rootView.findViewById(R.id.title);
        topRightButton = (ImageView) rootView.findViewById(R.id.topRightButton);
        topRightButton.setOnClickListener(onClickListenercreatechat);
        btncancel = (ImageView) rootView.findViewById(R.id.btncancel);
        btncancel.setOnClickListener(onClickListenercancel);
        btnsearch = (ImageView) rootView.findViewById(R.id.btnsearch);
        btnsearch.setOnClickListener(onClickListenersearch);

        searchtxt = (EditText) rootView.findViewById(R.id.searchtxt);

        listView = (ListView) rootView.findViewById(R.id.list);

        animationEnter = AnimationUtils.loadAnimation(getActivity(), R.anim.bar_anim_enter);
        animationEnter.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                searchtxt.clearAnimation();
                title.clearAnimation();
                topRightButton.clearAnimation();
                btncancel.clearAnimation();
//                searchtxt.setVisibility(View.VISIBLE);
//                title.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animationExit = AnimationUtils.loadAnimation(getActivity(), R.anim.bar_anim_exit);
        animationExit.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                searchtxt.clearAnimation();
                title.clearAnimation();
                topRightButton.clearAnimation();
                btncancel.clearAnimation();
//                searchtxt.setVisibility(View.GONE);
//                title.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        aninenter = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha);
        animexit = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_exit);
        menu_custom = new Menu_Custom(getActivity(), iMenu);
        menu_custom.addMenuItem(R.drawable.publishjd, "发布调度", 0);
        menu_custom.addMenuItem(R.drawable.historyjd, "历史调度", 1);

        messageDB = new MessageDB(SuyApplication.getApplication().getSuyDB().getDb());


        messageLists = new ArrayList<>();
        myAdapter = new MyAdapter(getActivity());
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(onItemClickListener);
        listView.setOnItemLongClickListener(onItemLongClickListener);

        return rootView;
    }





    @Override
    public void onResume() {
        super.onResume();


        Cursor cursor = messageDB.getMessageList();
        getMessageList(cursor);
        cursor.close();
        IM im = new IM(interfaceTask,IM.QUERYAPPOVE);
        im.startTask();

    }





    public void refreshList()
    {
        IM im = new IM(interfaceTask,IM.QUERYAPPOVE);
        im.startTask();
        Cursor cursor = messageDB.getMessageList();
        getMessageList(cursor);

    }


    ControlCenter.IMessageControl iMessageControl = new ControlCenter.IMessageControl() {
        @Override
        public void OnNewMessage(String Message) {

            refreshList();
        }

        @Override
        public void OnGetGroupList(int state) {
            Cursor cursor = messageDB.getMessageList();
            getMessageList(cursor);
            cursor.close();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ContactsSelectActivity.RequestCode == requestCode)
        {
            if (resultCode==0)
                return;
            String[] stringlist = data.getStringArrayExtra("contactslist");
            addChatGroup(stringlist);

        }
    }

    AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {



            return false;
        }
    };

    /**
     * 消息列表点击
     */
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            MessageList messageList = messageLists.get(i);

            messageList.setMsgmark(0);
            myAdapter.notifyDataSetChanged();

            //传递信息到聊天窗口
            Bundle bundle = new Bundle();
            if (messageList.getMessageEnum() == MessageList.MessageEnum.CHATS)
                bundle.putString("chattype", "2");
//            if (messageList.getMessageEnum() == MessageList.MessageEnum.CHAT)
//                bundle.putString("chattype", "1");
//            if (messageList.getMessageEnum() == MessageList.MessageEnum.PUBLICNOTICE)
//                bundle.putString("chattype", "3");
            if (messageList.getMessageEnum() == MessageList.MessageEnum.PUBLICNOTICE ||
                    messageList.getMessageEnum() == MessageList.MessageEnum.CHATS) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                bundle.putString("msgid",messageList.getMsgid());
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
//            if (messageList.getMessageEnum() == MessageList.MessageEnum.PUBLICNOTICE)  {
//
//                Intent intent = new Intent(getActivity(), MessageDetailView.class);
//                bundle.putString("msgid",messageList.getMsgid());
//                intent.putExtras(bundle);
//                getActivity().startActivity(intent);
//                getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
//            }


        }
    };



    /**
     * 获取消息信息
     * 通过本地数据库中获得
     * 数据库由消息控制服务插入数据
     */
    void getMessageList(Cursor cursor) {

        messageLists.clear();
        if (cursor.getCount() == 0) {
            myAdapter.notifyDataSetChanged();
            return;
        }

        MessageList messageList;

        while (cursor.moveToNext()) {
            messageList = new MessageList();
            messageList.setMessageEnum(MessageList.MessageEnum.CHATS);
            messageList.setMsgid(cursor.getString(0));
            messageList.setTitle(cursor.getString(2));
            int mark = messageDB.getMsgMark(cursor.getString(0));
            messageList.setMsgmark(mark);
            messageList.setMsgdate(cursor.getString(4));
//            messageList.setMsgType(cursor.getInt(3));
//
//            messageList.setContent(cursor.getString(3));
            if (cursor.getString(3).equals("01"))
                messageLists.add(0,messageList);
            else
                messageLists.add(messageList);
        }
        cursor.close();
        myAdapter.notifyDataSetChanged();


        //通过数据库获取数据


    }


    /**
     * 点击更多
     */
   View.OnClickListener onClickListenercreatechat = new View.OnClickListener() {
        @Override
        public void onClick(View view) {



//            if (SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_userType==1)
                menu_custom.ShowMenu(topRightButton);
//            else {
//                Intent intent = new Intent(getActivity(), ContactsSelectActivity.class);
//                startActivityForResult(intent, ContactsSelectActivity.RequestCode);
//                getActivity().overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.blank);
//            }
        }

    };



    /**
     * 菜单点击回调
     */
    IMenu iMenu = new IMenu() {
        @Override
        public void ClickMenu(int itemid) {
            Intent intent;
            switch (itemid) {
                case 0://发起聊天
                    intent = new Intent(getActivity(), PublishNoticsActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.alpha_exit);
                    break;
                case 1://发起公告
                    intent = new Intent(getActivity(), HistoryJDActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.alpha);
                    break;
            }


        }
    };

    /**
     * 点击搜索
     */
    View.OnClickListener onClickListenersearch = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent intent=new Intent(getActivity(),AppoveActivity.class);
            intent.putExtra("json",appovejson);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.alpha);




//            searchtxt.setText("");
//            searchtxt.startAnimation(animationEnter);
//            btncancel.startAnimation(animationEnter);
//            searchtxt.setVisibility(View.VISIBLE);
//            btncancel.setVisibility(View.VISIBLE);
//            searchtxt.setOnKeyListener(onKeyListenersearch);
//            searchtxt.addTextChangedListener(textWatcher);
//            title.startAnimation(animationExit);
//            title.setVisibility(View.GONE);
//            topRightButton.startAnimation(animationExit);
//            topRightButton.setVisibility(View.GONE);
//
//            searchtxt.requestFocus();
//            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//            im.toggleSoftInputFromWindow(searchtxt.getWindowToken(), 1, 0);


        }
    };

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.toString().equals(""))
                return;
            Cursor cursor = messageDB.getMessageForKeySearch(searchtxt.getText().toString());
            getMessageList(cursor);
            cursor.close();

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    View.OnKeyListener onKeyListenersearch = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (i == keyEvent.KEYCODE_SEARCH || i == KeyEvent.KEYCODE_ENTER) {

                InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(searchtxt.getWindowToken(), 0);

            }
            return false;
        }
    };


    /**
     * 点击取消
     */
    View.OnClickListener onClickListenercancel = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            searchtxt.startAnimation(animationExit);
            btncancel.startAnimation(animationExit);
            searchtxt.setVisibility(View.GONE);
            btncancel.setVisibility(View.GONE);
            title.startAnimation(animationEnter);
            topRightButton.startAnimation(animationEnter);
            title.setVisibility(View.VISIBLE);
            topRightButton.setVisibility(View.VISIBLE);
            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(searchtxt.getWindowToken(), 0);


            Cursor cursor = messageDB.getMessageList();
            getMessageList(cursor);
            cursor.close();
        }
    };


    /**
     * 添加群组聊天
     */
    public void addChatGroup(String[] contactslist)
    {
        if (contactslist.length ==0)
            return;

        Bundle bundle = new Bundle();
        if(contactslist.length==1)
        {
            //单聊
            messageDB = new MessageDB(SuyApplication.getApplication().getSuyDB().getDb());
            if (messageDB.isExitsMsgid(contactslist[0])>0)//判断是否存在
                messageDB.updateMessageDate(contactslist[0]);//存在更新列表时间
            else
                messageDB.insertGroup(contactslist[0],1);//没有插入新的记录
            getMessageList(messageDB.getMessageList());

            //传递信息到聊天窗口
            bundle.putString("chattype", "1");
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            bundle.putString("msgid",contactslist[0]);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            return;
        }

        //创建群聊
        String str="";
        for (int i=0; i<contactslist.length;i++)
        {
            str += contactslist[i]+",";
        }

        str = str.substring(0,str.length()-1);
        Log.i("选择的人：",str);

        CustomPopWindowPlugin.ShowPopWindow(listView,getActivity().getLayoutInflater(),"请稍后");
        IM im=new IM(interfaceTask,IM.CREATEGROUP);
        im.setParams(str);
        im.startTask();

    }




    InterfaceTask interfaceTask=new InterfaceTask() {
        @Override
        public void TaskResultForMessage(Message message) {
            CustomPopWindowPlugin.CLosePopwindow();


            if (message.what==BaseTask.IMTask)
            {
                if (message.arg2 == IM.QUERYAPPOVE)
                {
                    if (message.arg1 == BaseTask.SUCCESS)
                    {
                        appovejson = message.obj.toString();
                        Log.i("json ",appovejson);
                        try {
                            JSONObject jsonObject=new JSONObject(appovejson);
                            ReturnData returnData = new ReturnData(jsonObject, false);
                            JSONArray jsonArray=returnData.getJsonArray();
                            if (jsonArray.length()>0)
                                btnsearch.setVisibility(View.VISIBLE);
                            else
                                btnsearch.setVisibility(View.GONE);

                        }
                        catch (Exception e)
                        {
                            btnsearch.setVisibility(View.GONE);
                        }

                    }
                }
            }

//            if (message.what== BaseTask.IMTask)
//            {
//                if (message.arg1==BaseTask.FAILED)
//                {
//                    Toast.makeText(getActivity(),"创建群组失败",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//
//                ReturnData returnData = (ReturnData)message.obj;
//                MessageDB messageDB=new MessageDB(SuyApplication.getApplication().getSuyDB().getDb());
//                try {
//                    returnData.getReturnData().getJSONObject("groupInfo").put("msgContent","");
//                    messageDB.insertGroup(returnData.getReturnData().getJSONObject("groupInfo"), 2);
//                    getMessageList(messageDB.getMessageList());
//                    if (message.arg2 == IM.CREATEGROUP)
//                    {
//                        ControlCenter.controlCenter.publishNotics(
//                                returnData.getReturnData().getJSONObject("groupInfo").getString("groupId"));
//                    }
//                }
//
//                catch (Exception e)
//                {e.printStackTrace();}
//            }
        }
    };
    class MyAdapter extends BaseAdapter {
        LayoutInflater mInflater;
        ImageView msgimg;
        TextView msgmark;
        TextView msgtitle, msgsubcontent;
        TextView msgdate;


        public MyAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return messageLists.size();
        }

        @Override
        public Object getItem(int i) {
            return messageLists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }


        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            View v = mInflater.inflate(R.layout.message_list, null);
            msgimg = (ImageView) v.findViewById(R.id.msgimg);
            msgmark = (TextView) v.findViewById(R.id.txtmark);
            msgtitle = (TextView) v.findViewById(R.id.title);
//            msgsubcontent = (TextView) v.findViewById(R.id.subcontent);
            msgdate = (TextView) v.findViewById(R.id.dt);
            MessageList messageList = messageLists.get(i);
//            msgimg.setBackground(getResources().getDrawable(.));
//            msgsubcontent.setText(messageList.getContent());
            if (messageList.getMsgmark() > 0) {
                msgmark.setVisibility(View.VISIBLE);
            } else
                msgmark.setVisibility(View.INVISIBLE);
            msgtitle.setText(messageList.getTitle());
            msgdate.setText(MessageInfo.GetSysTime(messageList.getMsgdate()));
            return v;
        }
    }


}
