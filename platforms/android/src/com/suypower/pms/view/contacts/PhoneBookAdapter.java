package com.suypower.pms.view.contacts;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.suypower.pms.R;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.configxml.GlobalConfig;
import com.suypower.pms.app.task.FileDownload;
import com.suypower.pms.app.task.InterfaceTask;
import com.suypower.pms.view.plugin.CommonPlugin;
import com.suypower.pms.view.plugin.chat.MediaSupport;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Stereo on 16/3/28.
 * 电话本数据适配器
 */
public class PhoneBookAdapter extends BaseAdapter {

    public enum ItemEnum {
        SECTION, ROW,
    }

    public enum SortEnum {
        AZ, DEPARTMENT,
    }


    private List<PhoneBookItem> phoneBookItemList;

    private LayoutInflater layoutInflater;
    private List<String> azlist;
    private List<String> selectlist;
    private Map<String, Integer> map;
    private ISearchfinsih iSearchfinsih;
    private ContactsDB contactsDB;
    public SortEnum sortEnum;
    private Boolean isSelectMode = false;

    private Set<String> selectPeople;


    public PhoneBookAdapter(LayoutInflater layoutInflater, ISearchfinsih iSearchfinsih) {
        this.iSearchfinsih = iSearchfinsih;
        this.layoutInflater = layoutInflater;
        phoneBookItemList = new ArrayList<>();
        azlist = new ArrayList<>();
        map = new HashMap<>();
        sortEnum = SortEnum.DEPARTMENT;
        contactsDB = new ContactsDB(SuyApplication.getApplication().getSuyDB().getDb());

    }

    public void setSelectlist(List<String> selectlist) {
        this.selectlist = selectlist;
    }

    public Set<String> getSelectPeople() {
        return selectPeople;
    }

    public Boolean getIsSelectMode() {
        return isSelectMode;
    }

    public void setIsSelectMode(Boolean selectMode) {
        isSelectMode = selectMode;
        selectPeople = new HashSet<>();
    }

    /**
     * 返回拼音序列
     *
     * @return
     */
    public List<String> getAzlist() {
        return azlist;
    }

    public Map<String, Integer> getMapPY() {
        return map;
    }

    public void ConverSort() {
        PhoneBookItem phoneBookItem = null;
        Cursor cursor;
        phoneBookItemList.clear();
        azlist.clear();
        map.clear();
        Map<String, String> departmentmap;

        switch (sortEnum) {
            case AZ:
                int index = -1;
                for (int i = 97; i < 123; i++) {
                    String str = String.valueOf((char) i);
                    cursor = contactsDB.getContactswhitAZ(str);
                    if (cursor.getCount() == 0) {
                        cursor.close();
                        continue;
                    }
                    phoneBookItem = new PhoneBookItem();
                    phoneBookItem.itemEnum = ItemEnum.SECTION;
                    phoneBookItem.string = str.toUpperCase();
                    phoneBookItemList.add(phoneBookItem);
                    azlist.add(str);
                    index++;
                    map.put(str, index);

                    while (cursor.moveToNext()) {
                        Contacts contacts = new Contacts();
                        contacts.setId(cursor.getString(0));
                        contacts.setName(cursor.getString(1));
                        contacts.setLoginname(cursor.getString(2));
                        contacts.setSex(cursor.getString(3));
                        contacts.setPhone(cursor.getString(4));

                        contacts.setNickimgurl(cursor.getString(5));
                        contacts.setPY(cursor.getString(8));
                        contacts.setDepartmentid(cursor.getString(6));
                        contacts.setDepartmentname(cursor.getString(7));
                        contacts.setPosition(cursor.getString(10));
                        phoneBookItem = new PhoneBookItem();
                        phoneBookItem.itemEnum = ItemEnum.ROW;
                        phoneBookItem.contacts = contacts;
                        phoneBookItemList.add(phoneBookItem);
                        index++;
                    }
                    cursor.close();


                }


                break;
            case DEPARTMENT:

                cursor = contactsDB.getDepartment();
                if (cursor.getCount() == 0) {
                    cursor.close();
                    return;
                }
                departmentmap = new HashMap<>();
                while (cursor.moveToNext()) {
                    departmentmap.put(cursor.getString(1), cursor.getString(0));
                }
                cursor.close();
                Set<String> stringSet = departmentmap.keySet();
                Iterator<String> iterator = stringSet.iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    cursor = contactsDB.getContacts(key);
                    if (cursor.getCount() != 0) {
                        phoneBookItem = new PhoneBookItem();
                        phoneBookItem.itemEnum = ItemEnum.SECTION;
                        phoneBookItem.string = departmentmap.get(key);
                        phoneBookItemList.add(phoneBookItem);
                        while (cursor.moveToNext()) {
                            Contacts contacts = new Contacts();
                            contacts.setId(cursor.getString(0));
                            contacts.setName(cursor.getString(1));
                            contacts.setLoginname(cursor.getString(2));
                            contacts.setSex(cursor.getString(3));
                            contacts.setPhone(cursor.getString(4));
                            contacts.setNickimgurl(cursor.getString(5));
                            contacts.setPY(cursor.getString(8));
                            contacts.setDepartmentid(cursor.getString(6));
                            contacts.setDepartmentname(cursor.getString(7));
                            contacts.setPosition(cursor.getString(10));
                            phoneBookItem = new PhoneBookItem();
                            phoneBookItem.itemEnum = ItemEnum.ROW;
                            phoneBookItem.contacts = contacts;
                            phoneBookItemList.add(phoneBookItem);
                        }
                        cursor.close();
                    }

                }


                break;
        }

        return;
    }


    public void SearchPeople(String key) {
        PhoneBookItem phoneBookItem = null;
        Cursor cursor;
        phoneBookItemList.clear();
        azlist.clear();
        map.clear();

        cursor = contactsDB.getContactswhitSearch(key);
        if (cursor.getCount() == 0) {
            cursor.close();
            return;
        }
        while (cursor.moveToNext()) {
            Contacts contacts = new Contacts();
            contacts.setId(cursor.getString(0));
            contacts.setName(cursor.getString(1));
            contacts.setLoginname(cursor.getString(2));
            contacts.setSex(cursor.getString(3));
            contacts.setPhone(cursor.getString(4));
            contacts.setNickimgurl(cursor.getString(5));
            contacts.setPY(cursor.getString(8));
            contacts.setDepartmentid(cursor.getString(6));
            contacts.setDepartmentname(cursor.getString(7));
            contacts.setPosition(cursor.getString(10));
            phoneBookItem = new PhoneBookItem();
            phoneBookItem.itemEnum = ItemEnum.ROW;
            phoneBookItem.contacts = contacts;
            phoneBookItem.IsSelected = false;
            phoneBookItemList.add(phoneBookItem);

        }
        cursor.close();

        if (phoneBookItemList.size() > 0) {
            iSearchfinsih.SearchFinish();
        }
    }


    @Override
    public int getCount() {
        return phoneBookItemList.size();
    }

    @Override
    public Object getItem(int i) {
        return phoneBookItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        PhoneBookItem phoneBookItem = phoneBookItemList.get(i);
        View v = null;
        TextView textViewsection;
        TextView name, position;
        ImageView nickimg;
        CheckBox checkBox;
        switch (phoneBookItem.itemEnum) {
            case SECTION:
                v = layoutInflater.inflate(R.layout.phonebook_list_sction, null);
                textViewsection = (TextView) v.findViewById(R.id.section);
                textViewsection.setText(phoneBookItem.string);

                break;
            case ROW:
                v = layoutInflater.inflate(R.layout.phonebook_list_personal, null);
                name = (TextView) v.findViewById(R.id.name);
                nickimg = (ImageView) v.findViewById(R.id.nickimg);
                position = (TextView) v.findViewById(R.id.position);
                checkBox = (CheckBox) v.findViewById(R.id.check);
                if (isSelectMode) {


                    checkBox.setVisibility(View.VISIBLE);
                    checkBox.setChecked(phoneBookItem.IsSelected);
                    checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
                    checkBox.setTag(i);
                    if (selectPeople.size() > 0) {
                        if (selectPeople.contains(phoneBookItem.contacts.getId())) {
                            phoneBookItem.IsSelected = true;
                            checkBox.setChecked(phoneBookItem.IsSelected);
                        }
                    }
                    if (selectlist.contains(phoneBookItem.contacts.getId())) {
                        checkBox.setEnabled(false);
                    }
                    if (phoneBookItem.contacts.getId().equals(SuyApplication.getApplication().
                            getSuyClient().getSuyUserInfo().m_loginResult.m_strUserId)) {
                        checkBox.setEnabled(false);
                    }


                }
                if (!CommonPlugin.checkFileIsExits(phoneBookItem.contacts.getNickimgurl(), "40.jpg")) {

                    FileDownload fileDownload = new FileDownload(interfaceTask, FileDownload.StreamFile);
                    fileDownload.mediaid = phoneBookItem.contacts.getNickimgurl();
                    fileDownload.mediatype = ".jpg";
                    fileDownload.suffix="40";
                    fileDownload.startTask();
                } else {
                    System.gc();
                    Bitmap bitmap = BitmapFactory.decodeFile(SuyApplication.getApplication().getCacheDir()+ "/" +
                            phoneBookItem.contacts.getNickimgurl() + "40.jpg"); //将图片的长和宽缩小味原来的1/2
                    nickimg.setImageBitmap(bitmap);
                    nickimg.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
                name.setText(phoneBookItem.contacts.getName());
                if (sortEnum == SortEnum.AZ) {
                    position.setVisibility(View.VISIBLE);
                    String desc = phoneBookItem.contacts.getDepartmentname();   //String.format("%1$s(%2$s)", phoneBookItem.contacts.getPosition(), phoneBookItem.contacts.getDepartmentname());
                    position.setText(desc);
                } else {
                    position.setVisibility(View.GONE);
                    position.setText("");
                }

                break;
        }

        return v;
    }

    InterfaceTask interfaceTask = new InterfaceTask() {
        @Override
        public void TaskResultForMessage(Message message) {
//            try {
//                Thread.sleep(400);
//            }
//            catch (Exception e)
//            {e.printStackTrace();}

            handler.sendEmptyMessage(0);

        }
    };


    Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            notifyDataSetChanged();
        }
    };

    /**
     * 点击列表
     *
     * @param i
     */
    public void setContactsSelected(int i) {
        PhoneBookItem phoneBookItem = (PhoneBookItem) getItem(i);


        //判断自己
        if (selectlist.contains(phoneBookItem.contacts.getId()) ||
                phoneBookItem.contacts.getId().equals(SuyApplication.getApplication().
                        getSuyClient().getSuyUserInfo().m_loginResult.m_strUserId)
                ) {
            return;
        }
        phoneBookItem.IsSelected = phoneBookItem.IsSelected ? false : true;
        setContactsList(phoneBookItem.IsSelected, phoneBookItem);

        notifyDataSetChanged();
    }

    public void delContactsSelect(String id) {
        for (PhoneBookItem phoneBookItem1 : phoneBookItemList) {
            if (phoneBookItem1.itemEnum == ItemEnum.ROW) {
                if (phoneBookItem1.contacts.getId().equals(id)) {
                    phoneBookItem1.IsSelected = false;
                    setContactsList(phoneBookItem1.IsSelected, phoneBookItem1);

                }
            }
        }
        notifyDataSetChanged();

    }

    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            PhoneBookItem phoneBookItem = (PhoneBookItem) getItem((Integer) compoundButton.getTag());
            phoneBookItem.IsSelected = b;
            setContactsList(b, phoneBookItem);

        }
    };


    /**
     * 设置选择状态
     *
     * @param b
     * @param phoneBookItem
     */
    void setContactsList(Boolean b, PhoneBookItem phoneBookItem) {


        //添加
        if (b) {
            if (selectPeople.add(phoneBookItem.contacts.getId()))
                iSearchfinsih.OnSelected(phoneBookItem);
        } else {
            selectPeople.remove(phoneBookItem.contacts.getId());
            iSearchfinsih.OnDelSelected(phoneBookItem);
        }

        if (selectPeople.size() == 0)
            iSearchfinsih.OnDisSelected();
    }

    @Override
    public boolean isEnabled(int position) {

        PhoneBookItem phoneBookItem = phoneBookItemList.get(position);
        if (phoneBookItem.itemEnum == ItemEnum.SECTION)
            return false;
        return super.isEnabled(position);
    }

    public class PhoneBookItem {
        public ItemEnum itemEnum = ItemEnum.SECTION;
        public Contacts contacts = null;
        public String string = null;
        public Boolean IsSelected = false;
    }


    public interface ISearchfinsih {
        void SearchFinish();

        void OnSelected(PhoneBookItem phoneBookItem);

        void OnDelSelected(PhoneBookItem phoneBookItem);

        void OnDisSelected();
    }
}
