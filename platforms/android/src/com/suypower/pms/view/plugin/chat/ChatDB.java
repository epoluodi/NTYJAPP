package com.suypower.pms.view.plugin.chat;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.view.contacts.ContactsDB;
import com.suypower.pms.view.plugin.CommonPlugin;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stereo on 16/4/5.
 */
public class ChatDB {

    SQLiteDatabase db;


    public ChatDB(SQLiteDatabase db) {
        this.db = db;
    }


    /**
     * 插入聊天日志
     *
     * @param chatMessage
     */
    public void insertChatlog(ChatMessage chatMessage) {
        try {

            ContentValues cv = new ContentValues();
            cv.put("groupid", chatMessage.getMessageid());
            switch (chatMessage.getMessageTypeEnum()) {
                case TEXT:
                case RICHTXT1:
                    cv.put("content", chatMessage.getMsg().toString());
                    break;
                case PICTURE:
                case AUDIO:
                    cv.put("content", chatMessage.getMediaid());
                    break;


            }
            cv.put("isself", (chatMessage.getSelf()) ? 1 : 0);
            cv.put("sender", chatMessage.getSender());
            cv.put("msgsendstate", chatMessage.getMsgSendState());
            cv.put("msgdate", chatMessage.getMsgdateInit());
            cv.put("msgType", chatMessage.getMsgType());
            cv.put("senderid", chatMessage.getSenderid());
            cv.put("msgid", chatMessage.getMsgid());
            db.insert("chatlog", null, cv);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 获取图片历史数据
     *
     * @param msgid
     * @return
     */
    public List<String> getMediaListForMsgid(String msgid) {
        List<String> medialist = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from chatlog where groupid =? and msgType =1 order by 6 desc",
                    new String[]{msgid});
            if (cursor.getCount() == 0)
                return null;
            while (cursor.moveToNext()) {

                try {
                    File file = new File(SuyApplication.getApplication().getCacheDir() + File.separator
                            + cursor.getString(1) + ".jpg");
                    if (file.exists())
                        medialist.add(cursor.getString(1));
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
            cursor.close();
            return medialist;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 更新消息状态
     *
     * @param content
     */
    public void updatemsgstate(String content, int state) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("msgsendstate", state);
            db.update("chatlog", cv, "content = ?", new String[]{content});

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 获得聊天记录数据
     *
     * @param msgid
     * @return
     */
    public Cursor getChatlog(String msgid) {
        Cursor cursor = null;
        Log.i("msgid 获取历史", msgid);
        try {
            cursor = db.rawQuery("select * from chatlog where groupid =? order by 6 desc limit 0,15",
                    new String[]{msgid});
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }


    public Cursor getChatlog(String msgid, int start, int end) {
        Cursor cursor = null;
        Log.i("msgid 获取历史", msgid);
        String sql = String.format("select * from chatlog where groupid =? order by 6 desc limit %1$s,%2$s", start, end);
        try {
            cursor = db.rawQuery(sql,
                    new String[]{msgid});
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public int getChatlogCounts(String msgid)
    {
        Cursor cursor = null;
        Log.i("msgid 获取历史", msgid);

        try {
            cursor = db.rawQuery("select * from chatlog where groupid =?",
                    new String[]{msgid});
            int i = cursor.getCount();
            cursor.close();
            return i;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }



//    /**
//     * 删除聊天记录
//     * @param userid
//     */
//    public void delChatLog(String userid)
//    {
//        try {
//
//            db.delete("chatlog","groupid = ?",new String[]{userid});
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//
//    }


    public void delChatLog(String msgid)
    {
        try {

            db.delete("chatlog","msgid = ? ",new String[]{msgid});

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }


    public void delChatLog()
    {
        try {

            db.delete("chatlog",null,null);

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

}
