package com.suypower.pms.view.plugin.chat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suypower.pms.R;
import com.suypower.pms.app.ReturnData;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.configxml.GlobalConfig;
import com.suypower.pms.app.task.BaseTask;
import com.suypower.pms.app.task.FileDownload;
import com.suypower.pms.app.task.FileUpLoad;
import com.suypower.pms.app.task.IM;
import com.suypower.pms.app.task.InterfaceTask;
import com.suypower.pms.server.ControlCenter;
import com.suypower.pms.server.MsgBodyChat;
import com.suypower.pms.server.NotificationClass;
import com.suypower.pms.view.BaseActivityPlugin;
import com.suypower.pms.view.JDDetailActivity;
import com.suypower.pms.view.JDMemberStateActivity;
import com.suypower.pms.view.contacts.Contacts;
import com.suypower.pms.view.contacts.ContactsDB;
import com.suypower.pms.view.contacts.ContactsSelectActivity;
import com.suypower.pms.view.plugin.CommonPlugin;
import com.suypower.pms.view.plugin.GPS.BDGps;
import com.suypower.pms.view.plugin.Gifview;
import com.suypower.pms.view.plugin.camera.CameraHelper;
import com.suypower.pms.view.plugin.camera.CameraPlugin;
import com.suypower.pms.view.plugin.camera.PreviewPhotoViewPlugin;
import com.suypower.pms.view.plugin.fileEx.FilePlugin;
import com.suypower.pms.view.plugin.message.MessageDB;
import com.suypower.pms.view.plugin.message.MessageInfo;
import com.suypower.pms.view.plugin.message.MessageList;
import com.suypower.pms.view.user.UserInfoActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by Stereo on 16/4/7.
 */
public class ChatActivity extends BaseActivityPlugin {

    private Button btnsend;
    private ImageView btnmore;
    private ImageView btnemoji;
    private EditText chattxt;
    private ListView listView;
    private TextView chattitle;
    private ImageView btnreturn, btnchatinfo;
    private LinearLayout inputview;
    private InputView inputView;
    private ChatAdpter chatAdpter;
    private CameraPlugin cameraPlugin;
    private int ChatType;//类型 1单聊 2群聊
    private Contacts contacts;//单聊联系人
    private String groupid;// 群组id
    private String groupjson;//群组信息json
    private MessageDB messageDB;
    private Boolean isOpenChatmanger = false;

    private ChatMessage chatMessagetran;//转发对象
    private LinearLayout windowInputLayout;

    private ImageView voiceButton;

    private Button audioRecordButton;
    private Boolean isrecorder = true;

    private MediaSupport mediaSupport;
    private ChatMessageMenu chatMessageMenu;

    private View head;
    private Gifview gifview;
    private int pagescount, logcounts;
    private RelativeLayout btnjd;
    private TextView jdtitle, jdcontent, jddate;
    private ImageView jdimg;
    private JSONObject jdjsobj;
    private String sendtime;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        head = getLayoutInflater().inflate(R.layout.list_msg_foot, null);
        gifview = (Gifview) head.findViewById(R.id.gif);
        pagescount = 0;

        //jd
        btnjd = (RelativeLayout) findViewById(R.id.jdinfo);
        btnjd.setOnClickListener(onClickListenerbtnjd);
        jdtitle = (TextView) findViewById(R.id.jdtitle);
        jdcontent = (TextView) findViewById(R.id.jdcontent);
        jddate = (TextView) findViewById(R.id.jddate);
        jdimg = (ImageView) findViewById(R.id.jdimg);

        inputview = (LinearLayout) findViewById(R.id.footview);
        chattitle = (TextView) findViewById(R.id.chattitle);
        btnreturn = (ImageView) findViewById(R.id.btnreturn);
        btnchatinfo = (ImageView) findViewById(R.id.chat_info);
        btnchatinfo.setOnClickListener(onClickListenerinfo);
        chattxt = (EditText) findViewById(R.id.sendtxt);
        btnsend = (Button) findViewById(R.id.btnsend);
        btnsend.setOnClickListener(onClickListenersend);
        btnmore = (ImageView) findViewById(R.id.btnmore);
        btnemoji = (ImageView) findViewById(R.id.btnemoji);
        btnemoji.setOnClickListener(onClickListeneremoji);
        listView = (ListView) findViewById(R.id.chatlist);
        btnreturn.setOnClickListener(onClickListenerreturn);
        listView.setOnTouchListener(onTouchListenerlist);

        windowInputLayout = (LinearLayout) findViewById(R.id.window_input_bar);
        voiceButton = (ImageView) findViewById(R.id.voiceButton);
        audioRecordButton = (Button) findViewById(R.id.audioRecordButton);
        btnmore.setOnClickListener(onClickListenermore);
        voiceButton.setOnClickListener(onClickListenerVoiceButton);
        audioRecordButton.setOnTouchListener(onTouchListeneraudioRecordButton);
        Bundle bundle = getIntent().getExtras();
        messageDB = new MessageDB(SuyApplication.getApplication().getSuyDB().getDb());
        if (bundle != null) {
//            //单聊
//            if (bundle.get("chattype").equals("1")) {
//                btnchatinfo.setBackground(getResources().getDrawable(R.drawable.bar_people_selector));
//                LoadChaterInfo(bundle.getString("msgid"));
//                ChatType = 1;
//                messageDB.updateMessageList(contacts.getId(), 0);
//                NotificationClass.Clear_Notify((int) (Long.valueOf(contacts.getId()) -
//                        ControlCenter.controlCenter.msgidoffet));
//                windowMenuLayout.setVisibility(View.GONE);
//
//            }
            //多聊
            if (bundle.get("chattype").equals("2")) {
                btnchatinfo.setBackground(getResources().getDrawable(R.drawable.bar_peoples_selector));
                groupid = bundle.getString("msgid");
                messageDB.updateMessageList(groupid, 0);
                ChatType = 2;
                chattitle.setText("调度信息");
                btnchatinfo.setVisibility(View.GONE);//没有获取到群信息的时候不启用
                IM im = new IM(interfaceTask, IM.QUERYGROUPINFO);
                im.setParams(groupid);
                im.startTask();
                NotificationClass.Clear_Notify(10000);
                NotificationClass.Clear_Notify(10001);

            }
        }
        chattxt.addTextChangedListener(textWatcherchattxt);
        chattxt.setOnClickListener(onClickListenerchattxt);
        chatAdpter = new ChatAdpter(this, iChat);
        ChatDB chatDB = new ChatDB(SuyApplication.getApplication().getSuyDB().getDb());
        if (ChatType == 1)
            logcounts = chatDB.getChatlogCounts(contacts.getId());
        else
            logcounts = chatDB.getChatlogCounts(groupid);
        chatAdpter.chatMessageList = getHistoryChatlog();
        listView.setAdapter(chatAdpter);
        listView.setOnScrollListener(onScrollListener);
        if (listView.getCount() > 6) {
            listView.setStackFromBottom(true);

        }

    }


    View.OnClickListener onClickListenerbtnjd = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ChatActivity.this, JDDetailActivity.class);
            intent.putExtra("json", jdjsobj.toString());
            intent.putExtra("mode", 2);
            startActivity(intent);
        }
    };

    AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {


            if (chatAdpter.chatMessageList.size() == 0)
                return;
            if (pagescount >= logcounts)
                return;
            if (listView.getFirstVisiblePosition() == 0) {
                listView.addHeaderView(head);
                List<ChatMessage> list = getHistoryChatlog();
                if (list.size() > 0) {
                    for (int i = list.size(); i > 0; i--) {
                        chatAdpter.chatMessageList.add(0, list.get(i - 1));
                    }
                }
                listView.removeHeaderView(head);
                chatAdpter.notifyDataSetChanged();

            }
        }
    };


    /**
     * 取历史记录
     *
     * @return
     */
    List<ChatMessage> getHistoryChatlog() {
        List<ChatMessage> chatMessageList = new ArrayList<>();
        ChatDB chatDB = new ChatDB(SuyApplication.getApplication().getSuyDB().getDb());
        Cursor cursor;
        if (ChatType == 1)
            cursor = chatDB.getChatlog(contacts.getId(), pagescount, pagescount + 15);
        else
            cursor = chatDB.getChatlog(groupid, pagescount, pagescount + 15);
        while (cursor.moveToNext()) {
            pagescount++;
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMessageid(cursor.getString(0));
            chatMessage.setSelf((cursor.getInt(2) == 0) ? false : true);
            chatMessage.setMsgdate(cursor.getString(5));
            chatMessage.setSender(cursor.getString(4));
            chatMessage.setMsgSendState(cursor.getInt(3));
            chatMessage.setSenderid(cursor.getString(7));
            chatMessage.setMsgid(cursor.getString(8));
            switch (cursor.getInt(6)) {
                case 0:
                    chatMessage.setMessageTypeEnum(ChatMessage.MessageTypeEnum.TEXT);
                    chatMessage.setMsg(cursor.getString(1));
                    break;
                case 1:
                    chatMessage.setMessageTypeEnum(ChatMessage.MessageTypeEnum.PICTURE);
                    chatMessage.setMediaid(cursor.getString(1));
                    break;
                case 2:
                    chatMessage.setMessageTypeEnum(ChatMessage.MessageTypeEnum.AUDIO);
                    chatMessage.setMediaid(cursor.getString(1));
                    int _t = MediaSupport.getMediaDuration(GlobalConfig.AUDIO_CACHE_PATH +
                            File.separator + chatMessage.getMediaid() + ".aac");
                    chatMessage.setEx1(String.valueOf(_t) + "\"");

                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < _t; i++) {
                        stringBuilder.append("  ");
                    }
                    chatMessage.setMsg(stringBuilder.toString());
                    break;
            }
            chatMessageList.add(0, chatMessage);
        }
        cursor.close();
        return chatMessageList;
    }


    ControlCenter.IMessageControl iMessageControl = new ControlCenter.IMessageControl() {
        @Override
        public void OnGetGroupList(int state) {

        }

        @Override
        public void OnNewMessage(String Message) {
            try {

                JSONObject jsonObject = new JSONObject(Message);
                MsgBodyChat msgBodyChat = MsgBodyChat.decodeJson(jsonObject);
                ChatMessage chatMessage = new ChatMessage();

//                if (msgBodyChat.getMsgmode() == 1 && ChatType == 1) {
//                    chatMessage.setMsgMode(1);
//                    if (contacts.getId().equals(msgBodyChat.getMsgid())) {
//                        chatMessage.setMsgSendState(1);
//
//                        if (msgBodyChat.getMsgtype() == 1) {
//                            chatMessage.setMsg(msgBodyChat.getContent());
//                            chatMessage.setMessageTypeEnum(ChatMessage.MessageTypeEnum.TEXT);
//                        }
//                        if (msgBodyChat.getMsgtype() == 2) {
//                            chatMessage.setMediaid(msgBodyChat.getContent());
//                            chatMessage.setMessageTypeEnum(ChatMessage.MessageTypeEnum.PICTURE);
//                        }
//                        if (msgBodyChat.getMsgtype() == 3) {
//                            chatMessage.setMediaid(msgBodyChat.getContent());
//                            chatMessage.setMessageTypeEnum(ChatMessage.MessageTypeEnum.AUDIO);
//                        }
//                        chatMessage.setMsgid(msgBodyChat.getId());
//                        chatMessage.setMessageid(msgBodyChat.getMsgid());
//                        chatMessage.setSenderid(msgBodyChat.getSenderid());
//                        chatMessage.setSender(msgBodyChat.getSender());
//                        chatMessage.setSelf(false);
//                        chatMessage.setMsgdate(msgBodyChat.getSendtime());
//                        chatAdpter.chatMessageList.add(chatMessage);
//                        chatAdpter.notifyDataSetChanged();
//                        messageDB.updateMessageList(contacts.getId(), 0);
//                        listView.smoothScrollToPosition(chatAdpter.getCount() - 1);
//                        return;
//                    }
//                }

                if (msgBodyChat.getMsgmode() == 2 && ChatType == 2) {
                    chatMessage.setMsgMode(2);
                    if (groupid.equals(msgBodyChat.getMsgid())) {
                        chatMessage.setMsgSendState(1);
                        if (msgBodyChat.getMsgtype() == 1) {
                            chatMessage.setMsg(msgBodyChat.getContent());
                            chatMessage.setMessageTypeEnum(ChatMessage.MessageTypeEnum.TEXT);
                        }
                        if (msgBodyChat.getMsgtype() == 2) {
                            chatMessage.setMediaid(msgBodyChat.getContent());
                            chatMessage.setMessageTypeEnum(ChatMessage.MessageTypeEnum.PICTURE);
                        }
                        if (msgBodyChat.getMsgtype() == 3) {
                            chatMessage.setMediaid(msgBodyChat.getContent());
                            chatMessage.setMessageTypeEnum(ChatMessage.MessageTypeEnum.AUDIO);
                            chatMessage.setMsgSendState(1);
                        }
                        chatMessage.setMessageid(msgBodyChat.getMsgid());
                        chatMessage.setSenderid(msgBodyChat.getSenderid());
                        chatMessage.setSender(msgBodyChat.getSender());
                        chatMessage.setSelf(false);
                        chatMessage.setMsgdate(msgBodyChat.getSendtime());
                        chatAdpter.chatMessageList.add(chatMessage);
                        chatAdpter.notifyDataSetChanged();
                        messageDB.updateMessageList(groupid, 0);
                        listView.smoothScrollToPosition(chatAdpter.getCount() - 1);
                        return;
                    }
                }

                ControlCenter.controlCenter.sendNotification(msgBodyChat.getMsgmode(),
                        msgBodyChat.getMsgtitle(),
                        msgBodyChat.getContent(),
                        msgBodyChat.getMsgid(),
                        10001
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        if (intent.getStringExtra("chattype").equals("1")) {
//            btnchatinfo.setBackground(getResources().getDrawable(R.drawable.bar_people_selector));
//            LoadChaterInfo(intent.getStringExtra("msgid"));
//            ChatType = 1;
//            messageDB.updateMessageList(contacts.getId(), 0);
//            NotificationClass.Clear_Notify((int) (Long.valueOf(contacts.getId()) -
//                    ControlCenter.controlCenter.msgidoffet));
//            NotificationClass.Clear_Notify((int) (Long.valueOf(contacts.getId()) - ControlCenter.msgidoffet));
//        }


        if (intent.getStringExtra("chattype").equals("2")) {
            btnchatinfo.setBackground(getResources().getDrawable(R.drawable.bar_peoples_selector));
            groupid = intent.getStringExtra("msgid");
            NotificationClass.Clear_Notify(10000);
            chattitle.setText(messageDB.getGroupName(groupid));
            NotificationClass.Clear_Notify(10001);
            messageDB.updateMessageList(groupid, 0);
            ChatType = 2;
            btnchatinfo.setEnabled(false);//没有获取到群信息的时候不启用
            IM im = new IM(interfaceTask, IM.QUERYGROUPINFO);
            im.setParams(groupid);
            im.startTask();
        }
        ChatDB chatDB = new ChatDB(SuyApplication.getApplication().getSuyDB().getDb());
        pagescount = 0;
        logcounts = chatDB.getChatlogCounts(groupid);
        chatAdpter.chatMessageList.clear();
        chatAdpter.chatMessageList = getHistoryChatlog();
        chatAdpter.notifyDataSetChanged();
        listView.smoothScrollToPosition(chatAdpter.getCount() - 1);
        Log.i("跳转", "打开跳转界面");
    }

    View.OnClickListener onClickListenerinfo = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            isOpenChatmanger = true;
            Intent intent = new Intent(ChatActivity.this, JDMemberStateActivity.class);
            intent.putExtra("groupid", groupid);
            intent.putExtra("sendtime", sendtime);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            Intent intent = new Intent(ChatActivity.this, ChatsMangerActivity.class);
//            if (ChatType == 1) {
//                intent.putExtra("ShowMode", 1);//自己打开
//                intent.putExtra("Contacts", (Serializable) contacts);
//                startActivityForResult(intent, ChatsMangerActivity.REQUESTCODE);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            }
//            if (ChatType == 2) {
//                intent.putExtra("ShowMode", 2);//自己打开
//                intent.putExtra("groupid", groupid);
//                intent.putExtra("groupinfo", groupjson);
//                startActivityForResult(intent, ChatsMangerActivity.REQUESTCODE);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            }
        }
    };


    /**
     * 转成msgdata进行消息列表处理
     *
     * @param chatMessage
     */
    void updateMessageInfo(ChatMessage chatMessage) {

        MsgBodyChat msgBodyChat = new MsgBodyChat();
        msgBodyChat.setContent((chatMessage.getMsg() == null) ? "" : chatMessage.getMsg().toString());
        msgBodyChat.setMsgtype(chatMessage.getMsgType() + 1);
        msgBodyChat.setSendtime(chatMessage.getMsgdateInit());
        msgBodyChat.setMsgid(chatMessage.getMessageid());
        msgBodyChat.setMsgmode(1);
        ContactsDB contactsDB = new ContactsDB(SuyApplication.getApplication().getSuyDB().getDb());
        Contacts contacts = contactsDB.LoadChaterInfo(chatMessage.getMessageid());
        msgBodyChat.setMsgtitle(contacts.getName());
        if (messageDB.isExitsMsgid(chatMessage.getMessageid()) > 0) {
            messageDB.updateMessageForServer(msgBodyChat);
        } else
            messageDB.insertGroupFortran(msgBodyChat);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle;

        if (ContactsSelectActivity.RequestCode == requestCode) {
            if (resultCode == 0)
                return;
            else if (resultCode == 1) {

                //转发
                Contacts contacts = (Contacts) data.getSerializableExtra("contacts");
                sendtran(contacts);
            } else if (resultCode == 2) {
                ContactsDB contactsDB = new ContactsDB(SuyApplication.getApplication().getSuyDB().getDb());
                String[] rev = data.getStringArrayExtra("contacts");
                for (int i = 0; i < rev.length; i++) {
                    Contacts contacts = contactsDB.LoadChaterInfo(rev[i]);
                    sendtran(contacts);
                }

            }
            return;
        }
        if (CameraHelper.JSCallCamera == requestCode)//拍照回调
        {
            //拍照返回信息
            if (resultCode == -1) {
                String mediaid = CameraPlugin.copyCacheFile(CameraHelper.PhotoPath);
                Bitmap bitmap = CameraPlugin.bitbmpfrommediaLocal(mediaid, 0);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(SuyApplication.getApplication().getCacheDir()
                            + "/" + mediaid + "aumb.jpg");
                    fileOutputStream.write(baos.toByteArray());
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                bitmap.recycle();
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setMessageTypeEnum(ChatMessage.MessageTypeEnum.PICTURE);
                chatMessage.setMediaid(mediaid);
                chatMessage.setMsgdate(CommonPlugin.GetSysTime());
//                chatMessage.setMsg(chattxt.getText().toString());
                chatMessage.setSelf(true);
                chatMessage.setMsgSendState(0);
                chatMessage.setMsgMode(ChatType);
                if (ChatType == 1)
                    chatMessage.setMessageid(contacts.getId());
                if (ChatType == 2)
                    chatMessage.setMessageid(groupid);
                chatMessage.setSender(SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_strUserId);
                chatAdpter.chatMessageList.add(chatMessage);

                MessageDB messageDB = new MessageDB(SuyApplication.getApplication().getSuyDB().getDb());
                messageDB.updateMessageForServer(chatMessage);

                FileUpLoad fileUpLoad = new FileUpLoad(interfaceTask, FileUpLoad.UPLOADFILE);
                fileUpLoad.mediaid = chatMessage.getMediaid();
                fileUpLoad.flag = chatMessage;
                fileUpLoad.mediatype = "02";
                fileUpLoad.startTask();

                chatAdpter.notifyDataSetChanged();
                chattxt.setText("");
                listView.smoothScrollToPosition(chatAdpter.getCount() - 1);


                return;
            }

        }

        if (requestCode == ChatsMangerActivity.REQUESTCODE) {
            isOpenChatmanger = false;
            if (resultCode == ChatsMangerActivity.CLEARRESULTCODE) {
                chatAdpter.chatMessageList = getHistoryChatlog();
                chatAdpter.notifyDataSetChanged();
                listView.smoothScrollToPosition(chatAdpter.getCount() - 1);
                return;
            }
            if (resultCode == ChatsMangerActivity.EXITRESULTCODE) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return;
            }
            if (resultCode == ChatsMangerActivity.CHANGETITLE) {

                chattitle.setText(data.getStringExtra("title"));
                return;
            }


        }


        if (requestCode == PreviewPhotoViewPlugin.JSCallPreviewPhtoto) {
            if (resultCode == 1) {
                bundle = data.getExtras();

                String[] files = bundle.getStringArray("files");


                for (int i = 0; i < files.length; i++) {
                    Log.i("选择照片", files[i]);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setMessageTypeEnum(ChatMessage.MessageTypeEnum.PICTURE);
                    chatMessage.setMediaid(files[i]);
                    chatMessage.setMsgdate(CommonPlugin.GetSysTime());
//                chatMessage.setMsg(chattxt.getText().toString());
                    chatMessage.setSelf(true);
                    chatMessage.setMsgSendState(0);
                    chatMessage.setMsgMode(ChatType);
                    if (ChatType == 1)
                        chatMessage.setMessageid(contacts.getId());
                    if (ChatType == 2)
                        chatMessage.setMessageid(groupid);
                    chatMessage.setSender(SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_strUserId);
                    chatAdpter.chatMessageList.add(chatMessage);

                    MessageDB messageDB = new MessageDB(SuyApplication.getApplication().getSuyDB().getDb());
                    messageDB.updateMessageForServer(chatMessage);

                    FileUpLoad fileUpLoad = new FileUpLoad(interfaceTask, FileUpLoad.UPLOADFILE);
                    fileUpLoad.mediaid = chatMessage.getMediaid();
                    fileUpLoad.flag = chatMessage;
                    fileUpLoad.mediatype = "02";
                    fileUpLoad.startTask();
//                    IM im = new IM(interfaceTask, IM.SENDMSG);
//                    im.setChatMessage(chatMessage);
//                    im.startTask();
                }

//                ChatMessage chatMessage = new ChatMessage();
//                chatMessage.setMessageTypeEnum(ChatMessage.MessageTypeEnum.PICTURE);
//                chatMessage.setMediaid("");
//                chatMessage.setMsgdate(CommonPlugin.GetSysTime());
//                chatMessage.setMsg(chattxt.getText().toString());
//                chatMessage.setSelf(true);
//                chatMessage.setMsgSendState(0);
//                chatMessage.setMsgMode(ChatType);
//                if (ChatType == 1)
//                    chatMessage.setMessageid(contacts.getId());
//                if (ChatType == 2)
//                    chatMessage.setMessageid(groupid);
//                chatMessage.setSender(SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_strUserId);
//                chatAdpter.chatMessageList.add(chatMessage);
                chatAdpter.notifyDataSetChanged();
                chattxt.setText("");
                listView.smoothScrollToPosition(chatAdpter.getCount() - 1);
//


                return;
            }
        }


    }


    /**
     * 发送转发消息
     *
     * @param contacts
     */
    private void sendtran(Contacts contacts) {
        ChatMessage chatMessage = new ChatMessage();


        chatMessage.setMessageTypeEnum(chatMessagetran.getMessageTypeEnum());
        if (chatMessagetran.getMessageTypeEnum() == ChatMessage.MessageTypeEnum.TEXT)
            chatMessage.setMsg(chatMessagetran.getMsg().toString());
        else {
            chatMessage.setMediaid(chatMessagetran.getMediaid());
        }
        chatMessage.setMsgdate(CommonPlugin.GetSysTime());
        chatMessage.setSelf(true);
        chatMessage.setMsgSendState(1);
        chatMessage.setMsgMode(1);
        chatMessage.setMessageid(contacts.getId());
        chatMessage.setSender(SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_strUserId);
        MessageDB messageDB = new MessageDB(SuyApplication.getApplication().getSuyDB().getDb());
        messageDB.updateMessageForServer(chatMessage);
        IM im = new IM(null, IM.SENDMSG);
        im.setChatMessage(chatMessage);
        im.startTask();
        updateMessageInfo(chatMessage);
    }

    /**
     * 读取聊天信息
     *
     * @param msgid
     */
    void LoadChaterInfo(String msgid) {
        ContactsDB contactsDB = new ContactsDB(SuyApplication.getApplication().getSuyDB().getDb());
        contacts = contactsDB.LoadChaterInfo(msgid);
        chattitle.setText(contacts.getName());
    }


    View.OnClickListener onClickListenerchattxt = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            btnemoji.setBackground(getResources().getDrawable(R.drawable.chat_emj_selector));
            if (inputView != null) {
                if (inputView.getChatKeyEnum() == InputView.ChatKeyEnum.KEYMORE) {
                    inputView.getInputMoreView().startAnimation(AnimationUtils.loadAnimation(ChatActivity.this,
                            R.anim.anim_bottommenu_exit));
                    inputview.removeView(inputView.getInputMoreView());
                    inputView = null;
                    return;
                }

                if (inputView.getChatKeyEnum() == InputView.ChatKeyEnum.KEYEMOJI) {
                    inputView.getInputEmojiView().startAnimation(AnimationUtils.loadAnimation(ChatActivity.this,
                            R.anim.anim_bottommenu_exit));
                    inputview.removeView(inputView.getInputEmojiView());
                    inputView = null;
                    return;
                }
            }
            listscroll();
        }
    };


    void listscroll() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                    handler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0)
                listView.smoothScrollToPosition(chatAdpter.getCount() - 1);
        }
    };


    View.OnClickListener onClickListenersend = new View.OnClickListener() {
        @Override
        public void onClick(View view) {


            if (chattxt.getText().toString().equals("")) {
                Toast.makeText(ChatActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
                return;
            }
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMessageTypeEnum(ChatMessage.MessageTypeEnum.TEXT);
            chatMessage.setMediaid("");
            chatMessage.setMsgdate(CommonPlugin.GetSysTime());
            chatMessage.setMsg(chattxt.getText().toString());
            chatMessage.setSelf(true);
            chatMessage.setMsgSendState(0);
            chatMessage.setMsgMode(ChatType);

            if (ChatType == 1)
                chatMessage.setMessageid(contacts.getId());
            if (ChatType == 2)
                chatMessage.setMessageid(groupid);
            chatMessage.setSender(SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_strUserId);

            chatAdpter.chatMessageList.add(chatMessage);
            chatAdpter.notifyDataSetChanged();
            chattxt.setText("");
            listView.smoothScrollToPosition(chatAdpter.getCount() - 1);

            MessageDB messageDB = new MessageDB(SuyApplication.getApplication().getSuyDB().getDb());
            messageDB.updateMessageForServer(chatMessage);
            IM im = new IM(interfaceTask, IM.SENDMSG);
            im.setChatMessage(chatMessage);
            im.startTask();

//            chatMessage = new ChatMessage();
//            chatMessage.setMessageTypeEnum(ChatMessage.MessageTypeEnum.PICTURE);
//            chatMessage.setMediaid("1");
//            chatMessage.setMsgdate("2016-04-12 12:24:34");
//            chatMessage.setMsg(ChatActivity.this, "好的 我看出来了，好的，马上就来，已经发了，等一会，等一下，等一下");
//            chatMessage.setSelf(false);
//            chatAdpter.chatMessageList.add(chatMessage);


//            chatAdpter.notifyDataSetChanged();
//            chattxt.setText("");
//            listView.smoothScrollToPosition(chatAdpter.getCount() - 1);


        }
    };


    Handler handlershowimg = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bitmap bitmap = BitmapFactory.decodeFile(getCacheDir() + "/" +
                    msg.obj.toString() + "aumb.jpg"); //将图片的长和宽缩小味原来的1/2
            if (bitmap != null) {
                jdimg.setVisibility(View.VISIBLE);
                jdimg.setImageBitmap(bitmap);
            }

        }
    };

    InterfaceTask interfaceTask = new InterfaceTask() {
        @Override
        public void TaskResultForMessage(Message message) {
            if (message.what == BaseTask.IMTask) {
                if (message.arg2 == IM.SENDMSG) {
                    chatAdpter.notifyDataSetChanged();
                    listView.smoothScrollToPosition(chatAdpter.getCount() - 1);
                }
                if (message.arg2 == IM.QUERYGROUPINFO) {
                    if (message.arg1 == BaseTask.SUCCESS) {
                        ReturnData returnData = (ReturnData) message.obj;
                        groupjson = returnData.getReturnData().toString();
                        btnchatinfo.setVisibility(View.VISIBLE);
                        btnjd.setVisibility(View.VISIBLE);

                        try {
                            jdjsobj = returnData.getReturnData();
                            jdtitle.setText(jdjsobj.getString("dispatch_title"));
                            jdcontent.setText(jdjsobj.getString("dispatch_content"));
                            sendtime=jdjsobj.getString("create_time");
                            IM im = new IM(null, IM.READMSG);
                            im.setPostValuesForKey("dispatch_id", jdjsobj.getString("dispatch_id"));
                            im.setPostValuesForKey("read_longitude", String.valueOf(BDGps.bdGps.getBdLocation().getLongitude()));
                            im.setPostValuesForKey("read_latitude", String.valueOf(BDGps.bdGps.getBdLocation().getLatitude()));
                            im.startTask();

                            String pics = jdjsobj.getString("pic_ids");
                            if (pics.equals(""))
                                jdimg.setVisibility(View.GONE);
                            else {


                                String _p1 = pics.split(",")[0];
                                if (!CommonPlugin.checkFileIsExits(_p1, "aumb.jpg")) {

                                    FileDownload fileDownload = new FileDownload(interfaceTask, FileDownload.StreamFile);
                                    fileDownload.mediaid = _p1;
                                    fileDownload.mediatype = ".jpg";
                                    fileDownload.imgamub = "aumb";
                                    fileDownload.suffix = "aumb";
                                    fileDownload.flag = _p1;
                                    fileDownload.startTask();
                                    return;
                                } else {
                                    Bitmap bitmap = BitmapFactory.decodeFile(getCacheDir() + "/" + _p1 + "aumb.jpg"); //将图片的长和宽缩小味原来的1/2
                                    if (bitmap != null) {
                                        jdimg.setVisibility(View.VISIBLE);
                                        jdimg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                        jdimg.setImageBitmap(bitmap);
                                    } else {
                                        File file = new File(getCacheDir() + "/" + _p1 + "aumb.jpg");
                                        file.delete();
                                    }
                                }
                            }
                            jddate.setText(MessageInfo.GetSysTime(jdjsobj.getString("send_time")));
                            listView.smoothScrollToPosition(chatAdpter.getCount() - 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(ChatActivity.this, "获取调度信息错误,请重新尝试", Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                    return;
                }
            }
            if (message.what == BaseTask.UploadFileTask) {
                if (message.arg2 == FileUpLoad.UPLOADFILE) {
                    if (message.arg1 == BaseTask.SUCCESS) {

                        IM im = new IM(interfaceTask, IM.SENDMSG);
                        im.setChatMessage((ChatMessage) message.obj);
                        im.startTask();

                    } else {

                    }
                }
            }
            if (message.what == BaseTask.DownloadFILETask) {
                if (message.arg2 == FileDownload.StreamFile) {
                    if (message.arg1 == BaseTask.SUCCESS) {
                        handlershowimg.sendMessage(message);

                    }
                }
            }


        }
    };
    /**
     * 点击取消软键盘
     */
    View.OnTouchListener onTouchListenerlist = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(chattxt.getWindowToken(), 0);
                btnemoji.setBackground(getResources().getDrawable(R.drawable.chat_emj_selector));
                if (inputView != null) {
                    if (inputView.getChatKeyEnum() == InputView.ChatKeyEnum.KEYMORE) {
                        inputView.getInputMoreView().startAnimation(AnimationUtils.loadAnimation(ChatActivity.this,
                                R.anim.anim_bottommenu_exit));
                        inputview.removeView(inputView.getInputMoreView());
                        inputView = null;
                        return false;
                    }

                    if (inputView.getChatKeyEnum() == InputView.ChatKeyEnum.KEYEMOJI) {
                        inputView.getInputEmojiView().startAnimation(AnimationUtils.loadAnimation(ChatActivity.this,
                                R.anim.anim_bottommenu_exit));
                        inputview.removeView(inputView.getInputEmojiView());
                        inputView = null;
                        return false;
                    }
                }
            }
            return false;
        }
    };

    IChat iChat = new IChat() {
        /**
         * 点击更多
         * @param chatKeyType
         */
        @Override
        public void OnMoreItem(InputView.ChatKeyType chatKeyType) {

            switch (chatKeyType) {
                case PICTURE:
                    cameraPlugin = new CameraPlugin(ChatActivity.this);
                    cameraPlugin.openPreviewPhotoForNavtive(1);
                    break;
                case MEETTING:
                    Toast.makeText(ChatActivity.this, "暂不提供此功能", Toast.LENGTH_SHORT).show();
                    break;
                case VODE:
                    Toast.makeText(ChatActivity.this, "暂不提供此功能", Toast.LENGTH_SHORT).show();
                    break;
                case GPSLOCATION:
                    Toast.makeText(ChatActivity.this, "暂不提供此功能", Toast.LENGTH_SHORT).show();
                    break;
                case SIGN:
                    Toast.makeText(ChatActivity.this, "暂不提供此功能", Toast.LENGTH_SHORT).show();
                    break;
                case CAMERA:
                    cameraPlugin = new CameraPlugin(ChatActivity.this);
                    cameraPlugin.takePictureForNaative();
                    break;
            }
            inputView.getInputMoreView().startAnimation(AnimationUtils.loadAnimation(ChatActivity.this,
                    R.anim.anim_bottommenu_exit));
            inputview.removeView(inputView.getInputMoreView());
            inputView = null;

        }

        /**
         * 点击表情
         * @param emoji
         * @param filename
         */
        @Override
        public void OnEmoji(String emoji, String filename) {

            try {
                String emojipath = String.format("emoji/%1$s.png", filename);
                InputStream inputStream = getAssets().open(emojipath);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ImageSpan imageSpan = new ImageSpan(ChatActivity.this, bitmap);
                SpannableString spannableString = new SpannableString(emoji);
                spannableString.setSpan(imageSpan, 0, emoji.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                chattxt.append(spannableString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 删除字符
         */
        @Override
        public void Ondelete() {

            String str = chattxt.getText().toString();
            if (str.length() == 1) {
                chattxt.setText("");
                return;
            }
            int selectionStart = chattxt.getSelectionStart();// 获取光标的位置
            if (selectionStart > 0) {
                String body = chattxt.getText().toString();
                if (!TextUtils.isEmpty(body)) {
                    String tempStr = body.substring(0, selectionStart);
                    if (selectionStart > 2) {
                        if (tempStr.substring(selectionStart - 2, tempStr.length()).equals("!]")) {
                            int i = tempStr.lastIndexOf("[!");// 获取最后一个表情的位置

                            if (i != -1) {
                                CharSequence cs = tempStr.subSequence(i, selectionStart);
                                chattxt.getEditableText().delete(i, selectionStart);
                                return;
                            }
                        }
                    }

                    chattxt.getEditableText().delete(selectionStart - 1, selectionStart);
                }
            }


        }

        /**
         * 点击一个聊天记录
         * @param chatMessage
         */
        @Override
        public void OnClickChatItem(ChatMessage chatMessage) {
            if (chatMessage == null) {

                chatAdpter.notifyDataSetChanged();
                listView.smoothScrollToPosition(chatAdpter.getCount() - 1);
                return;
            }
            ChatDB chatDB;
            switch (chatMessage.getMessageTypeEnum()) {
                case PICTURE:
//                    chatDB =new ChatDB(SuyApplication.getApplication().getSuyDB().getDb());
//                    List<String> listmedias =chatDB.getMediaListForMsgid(chatMessage.getMessageid());
                    CameraPlugin cameraPlugin = new CameraPlugin(ChatActivity.this);
                    cameraPlugin.openPreviewUrlPhoto(ChatActivity.this, chatMessage.getMediaid());
                    break;
                case AUDIO:
                    chatDB = new ChatDB(SuyApplication.getApplication().getSuyDB().getDb());
                    chatDB.updatemsgstate(chatMessage.getMediaid(), 2);
                    chatMessage.setMsgSendState(2);
                    MediaSupport.playAudio(GlobalConfig.AUDIO_CACHE_PATH +
                            File.separator + chatMessage.getMediaid() + ".aac", iPlayCallBack);
                    MediaSupport.object = chatAdpter.chatMessageList.indexOf(chatMessage);
                    chatAdpter.notifyDataSetChanged();
                    break;
            }
        }


        /*
        显示更多菜单
         */
        @Override
        public void OnLongClickItem(ChatMessage chatMessage, View view) {

//            int[] location;
//            int x, y;
//            switch (chatMessage.getMessageTypeEnum()) {
//                case TEXT:
//                    chatMessageMenu = new ChatMessageMenu(ChatActivity.this,ChatMessageMenu.MenuEnum.TEXT, iChatMenu);
//                    chatMessageMenu.object = chatMessage;
//                    location = new int[2];
//                    view.getLocationOnScreen(location);
//                    x = location[0];
//                    y = location[1];
//                    x = (x - 80);
//                    y = (y  - view.getHeight() -20);
//                    chatMessageMenu.showAtLocation(view, Gravity.NO_GRAVITY, x, y);
//                    break;
//                case PICTURE:
//                    chatMessageMenu = new ChatMessageMenu(ChatActivity.this,ChatMessageMenu.MenuEnum.PICTURE, iChatMenu);
//                    chatMessageMenu.object = chatMessage;
//                    location = new int[2];
//                    view.getLocationOnScreen(location);
//                    x = location[0];
//                    y = location[1];
//                    x = (x - 80);
//                    y = (y  -140);
//                    chatMessageMenu.showAtLocation(view, Gravity.NO_GRAVITY, x, y);
//                    break;
//                case AUDIO:
//                    chatMessageMenu = new ChatMessageMenu(ChatActivity.this, ChatMessageMenu.MenuEnum.RECODR, iChatMenu);
//                    chatMessageMenu.object = chatMessage;
//                    if (MediaSupport.IsSpeak) {
//                        chatMessageMenu.setBtnName(R.id.btnspeak, "听筒播放");
//                    } else {
//                        chatMessageMenu.setBtnName(R.id.btnspeak, "扬声器播放");
//                    }
//
//                    location = new int[2];
//                    view.getLocationOnScreen(location);
//                    x = location[0];
//                    y = location[1];
//                    x = (x - 120);
//                    y = (y - view.getHeight() - 20);
//                    chatMessageMenu.showAtLocation(view, Gravity.NO_GRAVITY, x, y);
//                    break;
//            }


        }


        @Override
        public void OnClickSender(String senderid) {
            Intent intent = new Intent(ChatActivity.this, UserInfoActivity.class);
            ContactsDB contactsDB = new ContactsDB(SuyApplication.getApplication().getSuyDB().getDb());
            intent.putExtra("IsSelf", false);//自己打开
            Contacts contacts = contactsDB.LoadChaterInfo(senderid);
            intent.putExtra("Contacts", (Serializable) contacts);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    };


    ChatMessageMenu.IChatMenu iChatMenu = new ChatMessageMenu.IChatMenu() {
        @Override
        public void OnClickMenuItem(int btnid, ChatMessageMenu.MenuEnum menuEnum, Object o) {
            chatMessagetran = (ChatMessage) o;
            if (btnid == R.id.btndel) {

                chatAdpter.chatMessageList.remove(chatMessagetran);
                chatAdpter.notifyDataSetChanged();
                ChatDB chatDB = new ChatDB(SuyApplication.getApplication().getSuyDB().getDb());
                chatDB.delChatLog(chatMessagetran.getMsgid());

                return;
            }
            if (btnid == R.id.btncopy) {
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", chatMessagetran.getMsg().toString());
                clipboard.setPrimaryClip(clip);

                return;
            }
//            if (btnid == R.id.btnsaveimg)
//            {
//                Toast.makeText(ChatActivity.this,"保存成功!",Toast.LENGTH_SHORT).show();
//                FilePlugin.CopyFilePhoto(ChatActivity.this,GlobalConfig.AUDIO_CACHE_PATH +
//                        File.separator + chatMessagetran.getMediaid() + "_aumb.jpg",
//                        Environment.getExternalStorageDirectory()+File.separator+chatMessagetran.getMediaid() + ".jpg");
////                Bitmap bitmap  = BitmapFactory.decodeFile(GlobalConfig.AUDIO_CACHE_PATH +
////                       File.separator + chatMessage.getMediaid() + "_aumb.jpg");
////                MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, chatMessage.getMediaid(), chatMessage.getSender() + "发来的图片");
//                Intent intent1 = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                Uri uri =Uri.parse("file://"+ Environment.getExternalStorageDirectory()); //Uri.fromFile(file);
//                intent1.setData(uri);
//                sendBroadcast(intent1);
//            }
            if (btnid == R.id.btnspeak) {
                MediaSupport.IsSpeak = MediaSupport.IsSpeak ? false : true;
                ChatDB chatDB = new ChatDB(SuyApplication.getApplication().getSuyDB().getDb());
                chatDB.updatemsgstate(chatMessagetran.getMediaid(), 2);
                chatMessagetran.setMsgSendState(2);
                MediaSupport.playAudio(GlobalConfig.AUDIO_CACHE_PATH +
                        File.separator + chatMessagetran.getMediaid() + ".aac", iPlayCallBack);
                MediaSupport.object = chatAdpter.chatMessageList.indexOf(chatMessagetran);
                chatAdpter.notifyDataSetChanged();
                return;
            }
            if (btnid == R.id.btntransend) {

                Intent intent = new Intent(ChatActivity.this, ContactsSelectActivity.class);
                intent.putExtra("mode", 1);
                intent.putExtra("senderid", chatMessagetran.getSenderid());
                startActivityForResult(intent, ContactsSelectActivity.RequestCode);
                overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.blank);
                return;
            }


        }
    };
    /**
     * 点击表情
     */
    View.OnClickListener onClickListeneremoji = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(chattxt.getWindowToken(), 0);
            if (inputView == null) {


                inputView = new InputView(ChatActivity.this, iChat);

                inputview.addView(inputView.getInputEmojiView());

                inputView.getInputEmojiView().startAnimation(AnimationUtils.loadAnimation(ChatActivity.this,
                        R.anim.anim_bottommenu_enter));
                btnemoji.setBackground(getResources().getDrawable(R.drawable.chat_keyboard_selector));
                listscroll();

            } else {
                if (inputView.getChatKeyEnum() == InputView.ChatKeyEnum.KEYMORE) {
                    inputView.getInputMoreView().startAnimation(AnimationUtils.loadAnimation(ChatActivity.this,
                            R.anim.anim_bottommenu_exit));
                    inputview.removeView(inputView.getInputMoreView());
                    inputview.addView(inputView.getInputEmojiView());
                    inputView.getInputEmojiView().startAnimation(AnimationUtils.loadAnimation(ChatActivity.this,
                            R.anim.anim_bottommenu_enter));
                    btnemoji.setBackground(getResources().getDrawable(R.drawable.chat_keyboard_selector));
                    listscroll();
                    return;
                }
                inputView.getInputEmojiView().startAnimation(AnimationUtils.loadAnimation(ChatActivity.this,
                        R.anim.anim_bottommenu_exit));
                inputview.removeView(inputView.getInputEmojiView());
                inputView = null;
                chattxt.requestFocus();
                btnemoji.setBackground(getResources().getDrawable(R.drawable.chat_emj_selector));
                im.toggleSoftInputFromWindow(chattxt.getWindowToken(), 1, 0);

            }
        }
    };

    /**
     * 点击更多
     */
    View.OnClickListener onClickListenermore = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(chattxt.getWindowToken(), 0);
            btnemoji.setBackground(getResources().getDrawable(R.drawable.chat_emj_selector));
            if (inputView == null) {
                inputView = new InputView(ChatActivity.this, iChat);
                inputview.addView(inputView.getInputMoreView());

                inputView.getInputMoreView().startAnimation(AnimationUtils.loadAnimation(ChatActivity.this,
                        R.anim.slide_in_from_bottom));
                listscroll();
            } else {
                if (inputView.getChatKeyEnum() == InputView.ChatKeyEnum.KEYEMOJI) {
                    inputView.getInputEmojiView().startAnimation(AnimationUtils.loadAnimation(ChatActivity.this,
                            R.anim.anim_bottommenu_exit));
                    inputview.removeView(inputView.getInputEmojiView());
                    inputview.addView(inputView.getInputMoreView());
                    inputView.getInputMoreView().startAnimation(AnimationUtils.loadAnimation(ChatActivity.this,
                            R.anim.anim_bottommenu_enter));
                    listscroll();
                    return;
                }
                inputView.getInputMoreView().startAnimation(AnimationUtils.loadAnimation(ChatActivity.this,
                        R.anim.anim_bottommenu_exit));
                inputview.removeView(inputView.getInputMoreView());
                inputView = null;
                chattxt.requestFocus();

                im.toggleSoftInputFromWindow(chattxt.getWindowToken(), 1, 0);

            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();

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


    TextWatcher textWatcherchattxt = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            AnimatorSet set = new AnimatorSet();
            if (chattxt.getText().length() == 0) {
                if (btnmore.getVisibility() != View.VISIBLE) {
                    set.play(ObjectAnimator.ofFloat(btnsend, View.SCALE_X, 1f, 0f))
                            .with(ObjectAnimator.ofFloat(btnsend, View.SCALE_Y, 1f, 0f))
                            .with(ObjectAnimator.ofFloat(btnsend, View.ALPHA, 1f, 0f));
                    set.setDuration(200);
                    set.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            btnsend.setVisibility(View.GONE);
                            btnmore.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            btnsend.setVisibility(View.GONE);
                            btnmore.setVisibility(View.VISIBLE);
                        }
                    });
                    set.start();
                }
            } else {
                if (btnmore.getVisibility() != View.GONE) {
                    btnsend.setVisibility(View.VISIBLE);
                    btnmore.setVisibility(View.GONE);
                    set.play(ObjectAnimator.ofFloat(btnsend, View.SCALE_X, 0f, 1f))
                            .with(ObjectAnimator.ofFloat(btnsend, View.SCALE_Y, 0f, 1f))
                            .with(ObjectAnimator.ofFloat(btnsend, View.ALPHA, 0f, 1f));
                    set.setDuration(200);
                    set.start();
                    chattxt.setText(Emoji.getEmojistring(charSequence.toString()));
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode == 4) {
            MediaSupport.stopAudio();
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }

        return super.onKeyUp(keyCode, event);
    }


    @Override
    protected void onResume() {
        super.onResume();
        ControlCenter.controlCenter.setiMessageControl(iMessageControl);
        ControlCenter.controlCenter.setIsNotification(false);


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isOpenChatmanger)
            ControlCenter.controlCenter.setiMessageControl(null);
        ControlCenter.controlCenter.setIsNotification(true);
    }


    View.OnTouchListener onTouchListeneraudioRecordButton = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                //按下
                case MotionEvent.ACTION_DOWN:
                    Log.i(ChatActivity.class.getName(), "---------开始录音---------");
                    mediaSupport = new MediaSupport(ChatActivity.this, iRecordCallBack);
                    try {
                        mediaSupport.startAudioRecord(listView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                //抬起
                case MotionEvent.ACTION_UP:
                    mediaSupport.stopAudioRecord();
                    mediaSupport = null;
                    Log.i(ChatActivity.class.getName(), "---------结束录音---------");

                    break;
                case MotionEvent.ACTION_MOVE:

                    if (event.getX() < 0 || event.getX() > audioRecordButton.getWidth() ||
                            event.getY() < 0 || event.getY() > audioRecordButton.getHeight()) {
                        mediaSupport.setOutSide();
                    } else {
                        mediaSupport.setInSide();
                    }


                    break;
            }
            return false;
        }
    };

    MediaSupport.IPlayCallBack iPlayCallBack = new MediaSupport.IPlayCallBack() {
        @Override
        public void OnPlayFinish() {
            int i = (Integer) MediaSupport.object;
            if (i == chatAdpter.chatMessageList.size() - 1)
                return;
            ChatMessage chatMessage = chatAdpter.chatMessageList.get(i + 1);
            if (chatMessage.getMessageTypeEnum() == ChatMessage.MessageTypeEnum.AUDIO && chatMessage.getMsgSendState() == 1) {

                ChatDB chatDB = new ChatDB(SuyApplication.getApplication().getSuyDB().getDb());
                chatDB.updatemsgstate(chatMessage.getMediaid(), 2);
                chatMessage.setMsgSendState(2);
                MediaSupport.playAudio(GlobalConfig.AUDIO_CACHE_PATH +
                        File.separator + chatMessage.getMediaid() + ".aac", iPlayCallBack);
                MediaSupport.object = i + 1;
                chatAdpter.notifyDataSetChanged();
            }
        }
    };

    MediaSupport.IRecordCallBack iRecordCallBack = new MediaSupport.IRecordCallBack() {


        @Override
        public void OnRecordFinish(String recordname, int t) {


            Log.i("录音 完成", recordname);
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMessageTypeEnum(ChatMessage.MessageTypeEnum.AUDIO);
            chatMessage.setMediaid(recordname);
            chatMessage.setMsgdate(CommonPlugin.GetSysTime());
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < t; i++) {
                stringBuilder.append("  ");
            }
            chatMessage.setMsg(stringBuilder.toString());
            chatMessage.setSelf(true);
            chatMessage.setMsgSendState(2);
            chatMessage.setMsgMode(ChatType);
            chatMessage.setEx1(String.valueOf(t) + "\"");
            if (ChatType == 1)
                chatMessage.setMessageid(contacts.getId());
            if (ChatType == 2)
                chatMessage.setMessageid(groupid);
            chatMessage.setSender(SuyApplication.getApplication().getSuyClient().getSuyUserInfo().m_loginResult.m_strUserId);
            chatAdpter.chatMessageList.add(chatMessage);


            MessageDB messageDB = new MessageDB(SuyApplication.getApplication().getSuyDB().getDb());
            messageDB.updateMessageForServer(chatMessage);

            FileUpLoad fileUpLoad = new FileUpLoad(interfaceTask, FileUpLoad.UPLOADFILE);
            fileUpLoad.mediaid = chatMessage.getMediaid();
            fileUpLoad.flag = chatMessage;
            fileUpLoad.startTask();

            chatAdpter.notifyDataSetChanged();
            chattxt.setText("");
            listView.smoothScrollToPosition(chatAdpter.getCount() - 1);
        }

        @Override
        public void OnRecordCancel() {

            Toast.makeText(ChatActivity.this, "取消录音", Toast.LENGTH_SHORT).show();


        }
    };
    View.OnClickListener onClickListenerVoiceButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isrecorder) {
                voiceButton.setBackground(getResources().getDrawable(R.drawable.chat_keyboard_selector));
                audioRecordButton.setVisibility(View.VISIBLE);
                chattxt.setVisibility(View.INVISIBLE);
                btnemoji.setVisibility(View.INVISIBLE);
                isrecorder = false;
            } else {
                voiceButton.setBackground(getResources().getDrawable(R.drawable.chat_voice_selector));
                audioRecordButton.setVisibility(View.INVISIBLE);
                chattxt.setVisibility(View.VISIBLE);
                btnemoji.setVisibility(View.VISIBLE);
                isrecorder = true;
            }
        }
    };

}