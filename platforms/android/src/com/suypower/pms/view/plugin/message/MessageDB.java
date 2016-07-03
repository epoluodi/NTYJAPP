package com.suypower.pms.view.plugin.message;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.suypower.pms.server.MsgBodyChat;
import com.suypower.pms.view.contacts.ContactsDB;
import com.suypower.pms.view.plugin.CommonPlugin;
import com.suypower.pms.view.plugin.chat.ChatMessage;

import org.fusesource.hawtbuf.codec.IntegerCodec;
import org.json.JSONObject;

/**
 * Created by Stereo on 16/4/5.
 */
public class MessageDB {

    SQLiteDatabase db;


    public MessageDB(SQLiteDatabase db)
    {
        this.db=db;
    }


    /**
     * 判断消息通知信息是否存在
     * @param msgid
     * @return
     */
    public int isExitsMsgid(String msgid) {
        Cursor cursor=null;
        int count=0;
        try {
            cursor = db.rawQuery("select * from message where msgid =?",
                    new String[]{msgid});
            count = cursor.getCount();
            cursor.close();
            return  count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }


    }



    //个人消息免打扰名单
    public void adddisturblist(String msgid,String userid)
    {
        ContentValues cv = new ContentValues();
        cv.put("groupid",msgid);
        cv.put("userid",userid);
        db.insert("disturblist", null, cv);
    }

    //删除名单
    public void deldisturblist(String msgid,String userid)
    {

        db.delete("disturblist", "groupid = ? and userid = ?", new String[]{msgid,userid});
    }

    //判断是否存在
    public int isExitsdisturblist(String msgid,String userid) {
        Cursor cursor=null;
        int count=0;
        try {
            cursor = db.rawQuery("select * from disturblist where groupid =? and userid = ?",
                    new String[]{msgid,userid});
            count = cursor.getCount();
            cursor.close();
            return  count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }


    }




    /**
     * 得到消息通知标记数
     * @param msgid
     * @return
     */
    public int getMsgMark(String msgid) {
        Cursor cursor=null;
        int count=0;
        try {
            cursor = db.rawQuery("select msgmark from message where msgid =?",
                    new String[]{msgid});
            cursor.moveToNext();
            count = cursor.getInt(0);
            cursor.close();
            return  count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }


    }


    //删除一个聊天记录
    public void delMessage(String userid)
    {
        try {

            db.delete("message","msgid = ?",new String[]{userid});

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }





    /**
     * 添加单聊
     * @param msgid
     */
    public void insertGroup(String msgid,int msgtype)
    {
        try {

            ContactsDB contactsDB=new ContactsDB(db);
            Cursor cursor = contactsDB.getContactsForUserid(msgid);
            cursor.moveToNext();
            ContentValues cv = new ContentValues();
            cv.put("msgid",msgid);
            cv.put("msgtype",msgtype);
            cv.put("msgtitle",cursor.getString(1));
            cv.put("msgcontent","");
            cv.put("msgmark", 0);
            cv.put("msgstate",1);
            cv.put("msgdate", CommonPlugin.GetSysTime());//jsonObject.getString("nickimg")
            cv.put("msgico", cursor.getShort(6));
            cursor.close();
            db.insert("message", null, cv);

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    /**
     * 更新聊天记录
     * @param userid
     */
    public void updateMessageDate(String userid)
    {
        try {
            ContentValues cv = new ContentValues();
            cv.put("msgdate", CommonPlugin.GetSysTime());
            db.update("message",cv,"msgid = ?",new String[]{userid});

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    /**
     *  插入一个新的消息通知
     * @param msgBodyChat
     */
    public void insertGroupForSigle(MsgBodyChat msgBodyChat)
    {
        try {

            ContentValues cv = new ContentValues();
            cv.put("msgid",msgBodyChat.getMsgid());
            cv.put("msgtype", msgBodyChat.getMsgmode());
            cv.put("msgtitle",msgBodyChat.getMsgtitle());
            cv.put("msgcontent",msgBodyChat.getContent());
            cv.put("msgmark",1);
            cv.put("msgstate",msgBodyChat.getMsgtype());
            cv.put("msgdate",msgBodyChat.getSendtime());
            cv.put("msgico","");
            db.insert("message", null, cv);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void insertjdinfo(String jdid,String title,String istop,String senddt)
    {
        try {

            ContentValues cv = new ContentValues();
            cv.put("jdif",jdid);
            cv.put("json", "");
            cv.put("title",title);
            cv.put("istop", istop);
            cv.put("senddt",senddt);
            db.insert("t_jdinfo", null, cv);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deletejdinfo()
    {
       db.delete("t_jdinfo",null,null);
    }




    public void insertGroupFortran(MsgBodyChat msgBodyChat)
    {
        try {

            ContentValues cv = new ContentValues();
            cv.put("msgid",msgBodyChat.getMsgid());
            cv.put("msgtype", msgBodyChat.getMsgmode());
            cv.put("msgtitle",msgBodyChat.getMsgtitle());
            cv.put("msgcontent",msgBodyChat.getContent());
            cv.put("msgmark",0);
            cv.put("msgstate",msgBodyChat.getMsgtype());
            cv.put("msgdate",msgBodyChat.getSendtime());
            cv.put("msgico","");
            db.insert("message", null, cv);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     *  增加一个群聊
     * @param jsonObject
     */
    public void insertGroup(JSONObject jsonObject,int msgtype)
    {
        try {

            ContentValues cv = new ContentValues();
            cv.put("msgid",jsonObject.getString("groupId"));
            cv.put("msgtype",msgtype);
            cv.put("msgtitle",jsonObject.getString("GROUP_NAME"));
            cv.put("msgcontent",jsonObject.getString("msgContent"));
            cv.put("msgmark",0);
            cv.put("msgstate",1);
            cv.put("msgdate",CommonPlugin.GetSysTime());
            cv.put("msgico","");
            db.insert("message", null, cv);

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }


    /**
     *  插入一个新的消息通知
     * @param jsonObject
     */
    public void insertMessageList(JSONObject jsonObject)
    {
        try {

            ContentValues cv = new ContentValues();
            cv.put("msgid",jsonObject.getString("msgID"));
            cv.put("msgtype",jsonObject.getString("msgType"));
            cv.put("msgtitle",jsonObject.getString("msgTitle"));
            cv.put("msgcontent",jsonObject.getString("msgContent"));
            cv.put("msgmark",0);
            cv.put("msgstate",1);
            cv.put("msgdate",jsonObject.getString("msgDate"));
            cv.put("msgico",jsonObject.getString("msgico"));
            db.insert("message", null, cv);

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    /**
     * 更新消息通知
     * @param msgBodyChat
     * @param mark
     */
    public void updateMessageForServer(MsgBodyChat msgBodyChat, int mark)
    {
        try {

            ContentValues cv = new ContentValues();
            cv.put("msgcontent",msgBodyChat.getContent());
            cv.put("msgico","");
            cv.put("msgmark",mark+1);
            cv.put("msgstate",msgBodyChat.getMsgtype());
            cv.put("msgdate",msgBodyChat.getSendtime());
            db.update("message", cv,"msgid = ?",new String[]{msgBodyChat.getMsgid()});

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }



    public void updateMessageForServer(MsgBodyChat msgBodyChat)
    {
        try {

            ContentValues cv = new ContentValues();
            cv.put("msgcontent",msgBodyChat.getContent());
            cv.put("msgico","");
            cv.put("msgmark",0);
            cv.put("msgstate",msgBodyChat.getMsgtype());
            cv.put("msgdate",msgBodyChat.getSendtime());
            db.update("message", cv,"msgid = ?",new String[]{msgBodyChat.getMsgid()});

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }


    public void updateMessageForServer(ChatMessage chatMessage)
    {
        try {

            ContentValues cv = new ContentValues();
            switch (chatMessage.getMessageTypeEnum())
            {
                case TEXT:
                    cv.put("msgcontent",chatMessage.getMsg().toString());
                    cv.put("msgstate",1);
                    break;
                case PICTURE:
                    cv.put("msgcontent","一张图片");
                    cv.put("msgstate",1);
                    break;
                case AUDIO:
                    cv.put("msgcontent","一段语音");
                    cv.put("msgstate",1);
                    break;
            }
            cv.put("msgico","");
            cv.put("msgmark",0);
            cv.put("msgdate",chatMessage.getMsgdateInit());
            db.update("message", cv,"msgid = ?",new String[]{chatMessage.getMessageid()});

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }
    /**
     * 更新消息通知列表标记
     * @param msgid
     * @param mark
     */
    public void updateMessageList(String msgid,int mark)
    {
        try {

            ContentValues cv = new ContentValues();
            cv.put("msgmark",mark);
            db.update("message", cv,"msgid = ?",new String[]{msgid});

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }
    /**
     * 清除所有暂存消息通知
     */
    public void delMessage()
    {
        try {

            db.delete("message",null,null);
            db.delete("t_jdinfo",null,null);
            db.delete("chatlog",null,null);

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }


    /**
     * 更新群组名称
     * @param groupid
     * @param groupname
     */
    public void updategroupName(String groupid,String groupname)
    {
        try {

            ContentValues cv = new ContentValues();
            cv.put("msgtitle",groupname);
            db.update("message", cv,"msgid = ?",new String[]{groupid});

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    /**
     * 获得群组名称
     * @param groupid
     * @return
     */
    public String getGroupName(String groupid)
    {
        Cursor cursor=null;
        try {
            cursor = db.rawQuery("select msgtitle from message where msgid = ?",
                    new String[]{groupid});
            if (cursor.getCount()==0)
                return "";
            cursor.moveToNext();
            return cursor.getString(0);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        finally {
            cursor.close();
        }
    }



    public void deljdinfo()
    {
        db.delete("t_jdinfo",null,null);
    }
    /**
     * 获得消息通知列表数据
     * @return
     */
    public Cursor getMessageList() {

        try {
            Cursor cursor = db.rawQuery("select * from t_jdinfo order by senddt desc",
                    null);
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public Cursor getMessageForKeySearch(String key) {

        try {
            Cursor cursor = db.rawQuery("select * from message where msgcontent like ? or msgtitle like ? order by msgdate desc",
                    new String[]{"%" +key + "%","%" +key + "%"});
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
