package com.suypower.pms.view.contacts;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.suypower.pms.app.SuyApplication;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Stereo on 16/4/5.
 */
public class ContactsDB {

    SQLiteDatabase db;


    public ContactsDB(SQLiteDatabase db) {
        this.db = db;
    }


    /**
     * 获得联系人信息
     *
     * @param msgid
     * @return
     */
    public Contacts LoadChaterInfo(String msgid) {
        Contacts contacts = new Contacts();

        Cursor cursor = getContactsForUserid(msgid);
        cursor.moveToNext();
        contacts.setId(cursor.getString(0));
        contacts.setName(cursor.getString(1));
        contacts.setPhone(cursor.getString(2));
        contacts.setDepartmentid(cursor.getString(3));
        contacts.setPY(cursor.getString(4));
        contacts.setPosition(cursor.getString(5));
        contacts.setNickimgurl(cursor.getString(6));
        contacts.setDepartmentname(getDepartmentname(contacts.getDepartmentid()));
        contacts.setEmail(cursor.isNull(7) ? "" : cursor.getString(7));
        cursor.close();
        return contacts;
    }


    /**
     * 添加通讯录联系人
     *
     * @param jsonObject
     */
    public void insertcontacts(JSONObject jsonObject) {
        try {


            ContentValues cv = new ContentValues();
            cv.put("tel", jsonObject.getString("tel"));
            cv.put("userid", jsonObject.getString("accountId"));
            cv.put("name", jsonObject.getString("name"));
            cv.put("loginname", jsonObject.getString("sysUserName"));
            cv.put("sex", jsonObject.getString("sex"));//jsonObject.getString("position")
            cv.put("img", jsonObject.getString("picture"));
            cv.put("positionid", jsonObject.getString("positionId"));//jsonObject.getString("nickimg")
            cv.put("positionname", jsonObject.getString("positionName"));
            cv.put("py", jsonObject.getString("pinyin"));
            JSONArray jsonArray = jsonObject.getJSONArray("groups");
            String groupids = "";
            String groupname = "";
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                groupids += jsonObject1.getString("GROUP_ID") + ",";
                groupname += jsonObject1.getString("GROUP_NAME") + ",";

            }
            if (!groupids.equals(""))
                groupids = groupids.substring(0, groupids.length() - 1);
            if (!groupname.equals(""))
                groupname = groupname.substring(0, groupname.length() - 1);
            cv.put("groupid", groupids);//jsonObject.getString("nickimg")
            cv.put("groupname", groupname);


            db.insert("contacts", null, cv);

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    /**
     * 删除联系人
     */
    public void delcontacts() {
        try {

            db.delete("contacts", null, null);

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    /**
     * 插入部门信息
     *
     * @param jsonObject
     */
    public void insertDepartment(JSONObject jsonObject) {
        try {

            ContentValues cv = new ContentValues();
            cv.put("name", jsonObject.getString("GROUP_NAME"));
            cv.put("id", jsonObject.getString("GROUP_ID"));

            db.insert("Department", null, cv);

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    /**
     * 删除部门
     */
    public void delDepartment() {
        try {
            db.delete("Department", null, null);

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }


    /**
     * 根据用户id得到用户信息
     *
     * @param userid
     * @return
     */
    public Cursor getContactsForUserid(String userid) {

        try {
            Cursor cursor = db.rawQuery("select * from contacts where userid =?",
                    new String[]{userid});
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    /**
     * 根据部门获取联系人信息
     *
     * @return
     */
    public Cursor getContacts(String departmentid) {

        try {
            Cursor cursor = db.rawQuery("select * from contacts where groupid like ?",
                    new String[]{"%" + departmentid + "%"});
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    public Cursor getContactswhitAZ(String az) {

        try {
            Cursor cursor = db.rawQuery("select * from contacts where py like ?",
                    new String[]{az + "%"});
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public Cursor getContactswhitSearch(String key) {

        try {
            Cursor cursor = db.rawQuery("select * from contacts where py like ? or name like ?",
                    new String[]{"%" + key + "%", "%" + key + "%"});
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public Cursor getDepartment() {

        try {
            Cursor cursor = db.rawQuery("select * from Department", null);
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public String getDepartmentname(String departmentid) {
        String name;
        try {
            Cursor cursor = db.rawQuery("select name from Department where id = ?", new String[]{departmentid});
            cursor.moveToNext();
            name = cursor.getString(0);
            cursor.close();
            return name;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

}
