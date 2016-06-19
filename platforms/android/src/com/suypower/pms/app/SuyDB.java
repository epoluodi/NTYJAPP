package com.suypower.pms.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.suypower.pms.view.plugin.CommonPlugin;

/**
 * Created by Administrator on 14-6-5.
 */
public class SuyDB {

    public static String DBPath;

    public String userdbpath="";
    SQLiteDatabase db;
    private Context context;
    SQLiteOpenHelper sqLiteOpenHelper;
    static SuyDB suyDB;



    public SuyDB(Context context, String userDbpath) {
        sqLiteOpenHelper = new SQLiteOpenHelper(context,userDbpath,null,1) {
            @Override
            public void onCreate(SQLiteDatabase sqLiteDatabase) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

            }
        };

        userdbpath= userDbpath;
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        this.context = context;

        db  = sqLiteOpenHelper.getWritableDatabase();
    }





    public void closeDB() {
        if (db !=null)
            db.close();
    }


    public SQLiteDatabase getDb() {
        return db;
    }






    public void updateAppInfo(String appCode,String Ver) {

        try {
            ContentValues cv = new ContentValues();
            cv.put("AppVerCode", Integer.valueOf(Ver.replace(".","")));
            cv.put("AppVer", Ver);
            cv.put("updateDate", CommonPlugin.GetSysTime());


            db.update("t_Appinfo", cv,"AppCode = ?",new String[]{appCode});


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return;
    }


    /**
     * 更新用户信息
     * @param user
     * @param pwd
     */
    public void updateUserInfo(String user,String pwd) {

        try {
            ContentValues cv = new ContentValues();
            cv.put("t_ConfigValue", user);

            db.update("t_Config", cv, "t_ConfigName = ?", new String[]{"user"});

            cv = new ContentValues();
            cv.put("t_ConfigValue", pwd);

            db.update("t_Config", cv,"t_ConfigName = ?",new String[]{"pwd"});

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return;
    }





    /**
     * 获得appinfo 数据
     * @return
     */
    public Cursor getAppInfo() {

        try {
            Cursor cursor = db.rawQuery("select * from t_Appinfo", null);
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    /**
     * 获得 APP 全局配置信息
     * @return
     */
    public Cursor getConfig()
    {

        try {
            Cursor cursor = db.rawQuery("select * from t_Config",null);
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public String getConfig(String configname)
    {

        try {
            Cursor cursor = db.rawQuery("select t_ConfigValue from t_Config where t_ConfigName = ?",
                    new String[]{configname});
            if (cursor.getCount() ==0)
            {
                cursor.close();
                return "";
            }
            cursor.moveToNext();
            String r = cursor.getString(0);
            cursor.close();
            return r;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }




//消息处理

    /**
     * 返回appcode 消息中记录数量
     * @param appcode
     * @return
     */
    public int getAppCodeMsgCounts(String appcode)
    {

        try {
            Cursor cursor = db.rawQuery("select t_msgcounts from t_message where t_appcode = ?",
                    new String[]{appcode});
            if (cursor.getCount() ==0)
            {
                cursor.close();
                return -1;
            }
            cursor.moveToNext();
            int  counts = cursor.getInt(0);
            cursor.close();
            return counts;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }


    /**
     * 添加消息信息表
     * @param appcode
     * @param json
     * @param msgid
     */
    public void insertmsg(String appcode,String json,String msgid,String dt)
    {
        try {
            ContentValues cv = new ContentValues();
            cv.put("t_appcode",appcode);
            cv.put("t_msgid",msgid);
            cv.put("t_json",json);
            cv.put("t_dt",dt);
            cv.put("t_msgcounts", 1);
            db.insert("t_message", null, cv);

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }


    /**
     * 更新消息数量
     * @param appcode
     * @param msgcounts
     */
    public void updateMsg(String appcode,int msgcounts,String josn,String dt,String msgid) {

        try {
            ContentValues cv = new ContentValues();
            cv.put("t_msgcounts", msgcounts);
            cv.put("t_json", josn);
            cv.put("t_dt", dt);
            cv.put("t_msgid",msgid);
            db.update("t_message", cv, "t_appcode = ?", new String[]{appcode});


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return;
    }

    public void updateMsg(String appcode,int msgcounts) {

        try {
            ContentValues cv = new ContentValues();
            cv.put("t_msgcounts", msgcounts);
            db.update("t_message", cv, "t_appcode = ?", new String[]{appcode});


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return;
    }



    /**
     * 获取消息数据列表
     * @return
     */
    public Cursor getMsgData() {

        try {
            Cursor cursor = db.rawQuery("select * from t_message", null);
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }



    public Boolean checkmsgcounts() {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from t_message where t_msgcounts > 0", null);
            if(cursor.getCount() >0)
            {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    finally {
            cursor.close();
        }
    }









//
//    public List<Map<String,String>> GetTaskDetailinfo(String ContractNO,String LiftNO)
//    {
//        List<Map<String,String>> mapList = new ArrayList<Map<String, String>>();
//        Map<String,String> map;
//        try {
//
//            Cursor cursor = db.rawQuery("select * from ProjectInfo_TABLE where ContractNO = ? and LiftNO = ?",
//                    new String[]{ContractNO,LiftNO});
//            while (cursor.moveToNext()) {
//
//                map = new HashMap<String, String>();
//
//                map.put("ProjectName",cursor.getString(1));
//                map.put("ContractNO",cursor.getString(2));
//                map.put("ProjectType",cursor.getString(3));
//                map.put("LiftType",cursor.getString(4));
//                map.put("CheckDate",cursor.getString(5));
//                map.put("EffectiveDate",cursor.getString(6));
//                map.put("ContractEffectiveDate",cursor.getString(7));
//                map.put("ReachTime",cursor.getString(8));
//                map.put("LeaveTime",cursor.getString(9));
//                map.put("CheckPerson",cursor.getString(10));
//                map.put("LiftNO",cursor.getString(11));
//                map.put("LiftNOTime",cursor.getString(12));
//                map.put("FactoryNo",cursor.getString(13));
//                map.put("RegisterNO",cursor.getString(14));
//                map.put("OwnerName",cursor.getString(15));
//                map.put("OwnerConfirmStatus",cursor.getString(16));
//                map.put("OwnerConfirmTime",cursor.getString(17));
//                map.put("ScanCode",cursor.getString(18));
//                map.put("ScanInfo",cursor.getString(19));
//                map.put("ScanTime",cursor.getString(20));
//                map.put("TableType",cursor.getString(21));
//                mapList.add(map);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//
//        return mapList;
//    }









//
//    public void DelYJinfo(String ContractNO,String LiftNO)
//    {
//
//        try {
//
//            db.delete("ProjectInfo_TABLE","ContractNO = ? and LiftNO = ? ",new String[]{ContractNO,LiftNO} );
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return;
//    }
//
//
//
//    public void DelTaskinfo(String ContractNO,String LiftNO)
//    {
//
//        try {
//
//            db.delete("task","ContractNO = ? and LiftNO = ? ",new String[]{ContractNO,LiftNO} );
//            db.delete("ProjectInfo_TABLE","ContractNO = ? and LiftNO = ? ",new String[]{ContractNO,LiftNO} );
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return;
//    }
//
//















}
