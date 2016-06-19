package com.suypower.pms.view.plugin.message;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suypower.pms.R;
import com.suypower.pms.app.ReturnData;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.SuyHttpClient;
import com.suypower.pms.app.configxml.AppConfig;
import com.suypower.pms.app.configxml.GlobalConfig;
import com.suypower.pms.app.task.BaseTask;
import com.suypower.pms.app.task.FileDownload;
import com.suypower.pms.app.task.InterfaceTask;
import com.suypower.pms.server.ControlCenter;
import com.suypower.pms.server.MsgBodyChat;
import com.suypower.pms.server.NotificationClass;
import com.suypower.pms.view.CordovaWebActivity;
import com.suypower.pms.view.MainActivity;
import com.suypower.pms.view.PublishNoticsActivity;
import com.suypower.pms.view.contacts.ContactsSelectActivity;
import com.suypower.pms.view.plugin.AjaxHttpPlugin;
import com.suypower.pms.view.plugin.CommonPlugin;
import com.suypower.pms.view.plugin.Gifview;
import com.suypower.pms.view.plugin.chat.ChatDB;
import com.suypower.pms.view.plugin.chat.ChatMessage;
import com.suypower.pms.view.plugin.chat.MediaSupport;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cjw on 15/8/25.
 */
public class MessageDetailView extends Activity {

    private ImageView leftbutton;//左上角 控制按钮
    private ListView listView;
    private List<MessageInfo> mapList;
    private MyAdapter myAdapter;
    private int pages = 1;
    private Boolean isover = false;
    private ImageView topRightButton;
    private Boolean isOpenChatmanger;
    private int pagescount, logcounts;
    private View head;
    private Gifview gifview;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messagedetailview);

        topRightButton = (ImageView) findViewById(R.id.topRightButton);
        topRightButton.setOnClickListener(onClickListenercreatechat);
        leftbutton = (ImageView) findViewById(R.id.btnreturn);
        leftbutton.setOnClickListener(onClickListenerreturn);
        listView = (ListView) findViewById(R.id.listview);
        mapList = new ArrayList<MessageInfo>();
        isOpenChatmanger = false;
        head = getLayoutInflater().inflate(R.layout.list_msg_foot, null);
        gifview = (Gifview) head.findViewById(R.id.gif);
        pagescount = 0;
        MessageDB messageDB = new MessageDB(SuyApplication.getApplication().getSuyDB().getDb());
        messageDB.updateMessageList("10000", 0);

        myAdapter = new MyAdapter(this);
        listView.setAdapter(myAdapter);
        listView.setOnScrollListener(onScrollListener);
        ChatDB chatDB = new ChatDB(SuyApplication.getApplication().getSuyDB().getDb());
        logcounts = chatDB.getChatlogCounts("10000");
        mapList = getHistoryChatlog("10000");
        if (mapList.size() > 1)
            listView.setStackFromBottom(true);
    }


    /**
     * 点击更多
     */
    View.OnClickListener onClickListenercreatechat = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            isOpenChatmanger = true;
            Intent intent = new Intent(MessageDetailView.this, PublishNoticsActivity.class);
            startActivityForResult(intent, ContactsSelectActivity.RequestCode);
            overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.alpha_exit);
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mapList.clear();
        NotificationClass.Clear_Notify(10000);
        myAdapter = new MyAdapter(this);
        listView.setAdapter(myAdapter);
        mapList.clear();
        mapList = getHistoryChatlog("10000");

    }

    AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            if (mapList.size() == 0)
                return;
            if (pagescount >= logcounts)
                return;
            if (listView.getFirstVisiblePosition() == 0) {
                listView.addHeaderView(head);
                List<MessageInfo> list = getHistoryChatlog("10000");
                if (list.size()>0)
                {
                    for (int i=list.size();i>0;i--)
                    {
                        mapList.add(0,list.get(i-1));
                    }
                }
                listView.removeHeaderView(head);
                myAdapter.notifyDataSetChanged();
            }
        }
    };


    /**
     * 取历史记录
     *
     * @return
     */
    List<MessageInfo> getHistoryChatlog(String srvid) {
        List<MessageInfo> messageInfos = new ArrayList<>();

        ChatDB chatDB = new ChatDB(SuyApplication.getApplication().getSuyDB().getDb());
        Cursor cursor = chatDB.getChatlog(srvid,pagescount,pagescount+15);


        while (cursor.moveToNext()) {
            MessageInfo messageInfo = new MessageInfo();
            pagescount++;
            try {
                JSONArray jsonArray = new JSONArray(cursor.getString(1));
                JSONObject jsonObject;
                if (jsonArray.length() > 1) {
                    messageInfo.setModel("newsEx");
                    MessageInfo[] messageInfos1 = new MessageInfo[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        MessageInfo messageInfo1 = new MessageInfo();
                        messageInfo1.setTitle(jsonObject.getString("title"));
                        messageInfo1.setMediaid(jsonObject.getString("pic"));
                        messageInfo1.setMediaUrl(jsonObject.getString("url"));
                        messageInfo1.setContext(jsonObject.getString("description"));
                        messageInfos1[i] = messageInfo1;
                    }
                    messageInfo.setMessageInfos(messageInfos1);
                } else {
                    messageInfo.setModel("news");
                    jsonObject = jsonArray.getJSONObject(0);
                    messageInfo.setTitle(jsonObject.getString("title"));
                    messageInfo.setMediaid(jsonObject.getString("pic"));
                    messageInfo.setMediaUrl(jsonObject.getString("url"));
                    messageInfo.setContext(jsonObject.getString("description"));
                }
                messageInfo.setArticleCount(jsonArray.length());
                messageInfo.setMsgDT(cursor.getString(5));
            } catch (Exception e) {
                e.printStackTrace();
            }
            messageInfos.add(0, messageInfo);
        }
        cursor.close();

        return messageInfos;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isOpenChatmanger = false;
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


    ControlCenter.IMessageControl iMessageControl = new ControlCenter.IMessageControl() {
        @Override
        public void OnGetGroupList(int state) {

        }

        @Override
        public void OnNewMessage(String Message) {

//            myAdapter = new MyAdapter(MessageDetailView.this);
//            listView.setAdapter(myAdapter);
            Log.i("新消息 :", Message);
            try {
                JSONObject jsonObject = new JSONObject(Message);
                MsgBodyChat msgBodyChat = MsgBodyChat.decodeJson(jsonObject);
                //公告

                MessageInfo messageInfo = new MessageInfo();
                JSONArray jsonArray = msgBodyChat.getaList();

                if (jsonArray.length() > 1) {
                    messageInfo.setModel("newsEx");
                    MessageInfo[] messageInfos1 = new MessageInfo[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        MessageInfo messageInfo1 = new MessageInfo();
                        messageInfo1.setTitle(jsonObject.getString("title"));
                        messageInfo1.setMediaid(jsonObject.getString("pic"));
                        messageInfo1.setMediaUrl(jsonObject.getString("url"));
                        messageInfo1.setContext(jsonObject.getString("description"));
                        messageInfos1[i] = messageInfo1;
                    }
                    messageInfo.setMessageInfos(messageInfos1);
                } else {
                    messageInfo.setModel("news");
                    jsonObject = jsonArray.getJSONObject(0);
                    messageInfo.setTitle(jsonObject.getString("title"));
                    messageInfo.setMediaid(jsonObject.getString("pic"));
                    messageInfo.setMediaUrl(jsonObject.getString("url"));
                    messageInfo.setContext(jsonObject.getString("description"));
                }
                messageInfo.setArticleCount(jsonArray.length());
                messageInfo.setMsgDT(msgBodyChat.getSendtime());

                mapList.add(messageInfo);

            } catch (Exception e) {
                e.printStackTrace();
            }
            myAdapter.notifyDataSetChanged();
            MessageDB messageDB = new MessageDB(SuyApplication.getApplication().getSuyDB().getDb());
            messageDB.updateMessageList("10000", 0);
            listView.smoothScrollToPosition(myAdapter.getCount() - 1);
//            myAdapter = new MyAdapter(MessageDetailView.this);
//            listView.setAdapter(myAdapter);


        }
    };


    View.OnClickListener onClickListenerreturn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode ==4)
        {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return false;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isover = true;
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == BaseTask.DownloadFILETask) {
                if (msg.arg2 == FileDownload.StreamFile) {
                    myAdapter.notifyDataSetChanged();
                }
            }
        }
    };


    InterfaceTask interfaceTask = new InterfaceTask() {
        @Override
        public void TaskResultForMessage(Message message) {
            handler.sendMessage(message);

        }
    };


    class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;


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


        /**
         * 单文本，单图信息类
         */
        class ViewHolder {
            TextView title;
            TextView memo;
            TextView dt;
            RelativeLayout r2;
            ImageView image1;
            OnClick onClick;
            RelativeLayout r1;
        }

        /**
         * 多媒体信息类
         */
        class ViewHolderEx {
            TextView[] title;

            TextView dt;
            RelativeLayout[] child;
            FrameLayout head;
            OnClick[] onClicks;
            ImageView[] image1;
            View childview[];
            LinearLayout l2;

            public ViewHolderEx(int counts) {
                title = new TextView[counts];
                image1 = new ImageView[counts];
                child = new RelativeLayout[counts - 1];
                childview = new View[counts - 1];
                onClicks = new OnClick[counts];
            }
        }




        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            ViewHolderEx viewHolderEx = null;
            MessageInfo messageInfo = mapList.get(i);
            String predt;
            view = null;
            System.gc();
            if (messageInfo.getModel() == 3) {
                viewHolderEx = new ViewHolderEx(messageInfo.getArticleCount());
                if (view == null) {
                    view = mInflater.inflate(R.layout.list_msg_detail2, null);
                    viewHolderEx.head = (FrameLayout) view.findViewById(R.id.head);
                    viewHolderEx.title[0] = (TextView) view.findViewById(R.id.title);
                    viewHolderEx.image1[0] = (ImageView) view.findViewById(R.id.image1);
                    viewHolderEx.dt = (TextView) view.findViewById(R.id.txtdt);
                    viewHolderEx.l2 = (LinearLayout) view.findViewById(R.id.l2);
                    viewHolderEx.onClicks[0] = new OnClick();
                    viewHolderEx.head.setOnClickListener(viewHolderEx.onClicks[0]);
                    view.setTag(viewHolderEx.head.getId(), viewHolderEx.onClicks[0]);
                    for (int c = 0; c < messageInfo.getArticleCount() - 1; c++) {
                        viewHolderEx.childview[c] = mInflater.inflate(R.layout.list_msg_detail_child, null);
                        viewHolderEx.child[c] = (RelativeLayout) viewHolderEx.childview[c].findViewById(R.id.rmain);
                        viewHolderEx.image1[c + 1] = (ImageView) viewHolderEx.childview[c].findViewById(R.id.image);
                        viewHolderEx.title[c + 1] = (TextView) viewHolderEx.childview[c].findViewById(R.id.title);
                        viewHolderEx.l2.addView(viewHolderEx.childview[c]);
                        viewHolderEx.onClicks[c + 1] = new OnClick();
                        viewHolderEx.child[c].setOnClickListener(viewHolderEx.onClicks[c + 1]);
                        view.setTag(viewHolderEx.child[c].getId(), viewHolderEx.onClicks[c + 1]);
                    }
                    view.setTag(viewHolderEx);
                } else {
                    viewHolderEx = (ViewHolderEx) view.getTag();
                    viewHolderEx.onClicks[0] = (OnClick) view.getTag(viewHolderEx.head.getId());
                    for (int c = 0; c < messageInfo.getArticleCount() - 1; c++) {
                        viewHolderEx.onClicks[c + 1] = (OnClick) view.getTag(viewHolderEx.child[c].getId());
                    }
                }
            } else {
                viewHolder = new ViewHolder();
                if (view == null) {

                    view = mInflater.inflate(R.layout.list_msg_detail1, null);
//
                    viewHolder.title = (TextView) view.findViewById(R.id.title);
                    viewHolder.memo = (TextView) view.findViewById(R.id.memo);
                    viewHolder.dt = (TextView) view.findViewById(R.id.txtdt);
                    viewHolder.r2 = (RelativeLayout) view.findViewById(R.id.r2);
                    viewHolder.r1 = (RelativeLayout) view.findViewById(R.id.r1);
                    viewHolder.image1 = (ImageView) view.findViewById(R.id.image1);
                    view.setTag(viewHolder);
                    viewHolder.onClick = new OnClick();
                    viewHolder.r1.setOnClickListener(viewHolder.onClick);
                    view.setTag(viewHolder.r1.getId(), viewHolder.onClick);
//
                } else {
                    viewHolder = (ViewHolder) view.getTag();
                    viewHolder.onClick = (OnClick) view.getTag(viewHolder.r1.getId());

                }
            }


            switch (messageInfo.getModel()) {
                case 1:
                    viewHolder.title.setText(messageInfo.getTitle());
                    viewHolder.memo.setText(messageInfo.getContext());
                    viewHolder.dt.setText(MessageInfo.GetSysTime(messageInfo.getMsgDT()));
                    viewHolder.r1.setOnClickListener(null);
                    //文本
                    viewHolder.r2.setVisibility(View.GONE);
                    viewHolder.image1.setVisibility(View.GONE);
                    break;
                case 2:
                    viewHolder.r2.setVisibility(View.VISIBLE);
                    viewHolder.image1.setVisibility(View.VISIBLE);
                    viewHolder.title.setText(messageInfo.getTitle());
                    viewHolder.memo.setText(messageInfo.getContext());

                    viewHolder.onClick.setPosition(i);
                    viewHolder.onClick.setChild(-1);
                    viewHolder.onClick.setShowmode(1);
                    predt = mapList.get((i == 0) ? 0 : i - 1).getMsgDTInit();
                    if (i == 0) {
                        viewHolder.dt.setVisibility(View.VISIBLE);
                        viewHolder.dt.setText(messageInfo.getMsgDT());
                    } else {
                        if (!CommonPlugin.isInOneMin(predt, messageInfo.getMsgDTInit()))
                            viewHolder.dt.setVisibility(View.GONE);
                        else {
                            viewHolder.dt.setVisibility(View.VISIBLE);
                            viewHolder.dt.setText(messageInfo.getMsgDT());
                        }
                    }
                    viewHolder.image1.setImageBitmap(null);
                    viewHolder.image1.setBackground(null);
                    if (messageInfo.getMediaid().equals("")) {
                        viewHolder.image1.setVisibility(View.GONE);
                    }
                    else {
                        File file = new File(getCacheDir() + "/" + messageInfo.getMediaid() + "_aumb.jpg");
                        if (file.exists()) {
                            Bitmap bitmap = BitmapFactory.decodeFile(getCacheDir() + "/" + messageInfo.getMediaid() + "_aumb.jpg"); //将图片的长和宽缩小味原来的1/2
                            viewHolder.image1.setImageBitmap(bitmap);
                            viewHolder.image1.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        } else {
                            FileDownload fileDownload = new FileDownload(interfaceTask, FileDownload.StreamFile);
                            fileDownload.mediaid = messageInfo.getMediaid();
                            fileDownload.mediatype = ".jpg";
                            fileDownload.imgamub = "_aumb";
                            fileDownload.startTask();
                        }
                    }
                    break;
                case 3:
                    viewHolderEx.dt.setText(MessageInfo.GetSysTime(messageInfo.getMsgDT()));
                    for (int c = 0; c < messageInfo.getArticleCount(); c++) {
                        MessageInfo messageInfo2 = messageInfo.getMessageInfos()[c];
                        File file1 = new File(getCacheDir() + "/" + messageInfo2.getMediaid().replace("-", "") + ".jpg");
                        if (file1.exists()) {
                            messageInfo2.setMediaUrl(getCacheDir() + "/" + messageInfo2.getMediaid().replace("-", "") + ".jpg");
                        }


                        if (messageInfo2.getMediaUrl().equals("")) {


//                            downliadfile(messageInfo2.getMediaid(), i, c);
                        } else {
                            try {
                                FileInputStream fileInputStream = new FileInputStream(messageInfo2.getMediaUrl());
                                Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
                                BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
                                viewHolderEx.image1[c].setBackground(bitmapDrawable);
                            } catch (Exception e) {
                                e.printStackTrace();
                                messageInfo2.setMediaUrl("");
                            }
                        }


                        viewHolderEx.title[c].setText(messageInfo2.getTitle());
                        viewHolderEx.onClicks[c].setPosition(i);
                        viewHolderEx.onClicks[c].setChild(c);

                    }
                    break;
            }

            return view;
        }

        class OnClick implements View.OnClickListener {
            int position;
            int child;
            int showmode;//1web 2 other


            public int getShowmode() {
                return showmode;
            }

            public void setShowmode(int showmode) {
                this.showmode = showmode;
            }

            public void setPosition(int position) {
                this.position = position;
            }

            public void setChild(int child) {
                this.child = child;
            }


            @Override
            public void onClick(View v) {


                MessageInfo messageInfo = mapList.get(position);//.getMessageInfos()[child];
                Intent intent;


                String url = String.format("%1$s%2$s", GlobalConfig.ServerHost, messageInfo.getMediaUrl());


                switch (showmode) {
                    case 1:
                        intent = new Intent(MessageDetailView.this, CordovaWebActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("url", url);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;
                }

//                if (messageInfo.getUrl().equals("")) {
//                    intent = new Intent(MessageDetailView.this, MessageInfoView.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("msg", messageInfo.getMessageInfos()[child]);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left1);
//
//                } else {
//                    String url = messageInfo.getUrl();
//
//                    intent = new Intent(MessageDetailView.this, MainActivity.class);
//                    if (url.contains("stereo")) {
//                        intent.putExtra("displaytype", "inside");
//                        intent.putExtra("url", url.replace("stereo://", ""));
//                    } else {
//                        intent.putExtra("displaytype", "outside");
//                        intent.putExtra("url", url);
//                    }
//                    startActivity(intent);
//                    overridePendingTransition(android.R.anim.fade_in,
//                            android.R.anim.fade_out);
//                }

            }
        }

    }


}