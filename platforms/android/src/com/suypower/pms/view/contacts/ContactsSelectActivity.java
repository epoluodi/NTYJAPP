package com.suypower.pms.view.contacts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suypower.pms.R;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.task.*;
import com.suypower.pms.view.CustomView.LinearViewPY;
import com.suypower.pms.view.CustomView.ListForRefresh;
import com.suypower.pms.view.dlg.IMenu;
import com.suypower.pms.view.dlg.Menu_Custom;
import com.suypower.pms.view.plugin.CustomPopWindowPlugin;
import com.suypower.pms.view.user.UserInfoActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Stereo on 16/4/26.
 */
public class ContactsSelectActivity extends Activity {

    public static final int RequestCode = 2000;//选择后返回


    private ImageView btnmore;
    private Menu_Custom menu_custom;
    private ListView lv;
    private PhoneBookAdapter phoneBookAdapter;
    private LinearViewPY linearLayoutpy;
    private EditText searchtxt;
    private Button btnreturn;
    private HorizontalScrollView horizontalScrollView;
    private LinearLayout linearLayout;
    private Button btnok;
    private Map<String, View> stringViewMap;
    private int mode=0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactsselect_activity);

        btnreturn = (Button) findViewById(R.id.btnreturn);
        btnreturn.setOnClickListener(onClickListenerreturn);
        btnmore = (ImageView) findViewById(R.id.btnmore);
        btnmore.setOnClickListener(onClickListenermore);
        searchtxt = (EditText) findViewById(R.id.searchtxt);
        searchtxt.addTextChangedListener(textWatcher);
        lv = (ListView) findViewById(R.id.listEx);
        phoneBookAdapter = new PhoneBookAdapter(getLayoutInflater(), iSearchfinsih);
        phoneBookAdapter.setIsSelectMode(true);
        ArrayList<String> stringArrayList = null;
        try {

            stringArrayList = getIntent().getExtras().getStringArrayList("contactslist");
            if (stringArrayList==null)
                stringArrayList = new ArrayList<>();
        } catch (Exception e) {
            stringArrayList = new ArrayList<>();
        }
        try {
            mode = getIntent().getIntExtra("mode",0);
            if (mode ==1) {
                stringArrayList = new ArrayList<>();
                stringArrayList.add(getIntent().getStringExtra("senderid"));
            }
        } catch (Exception e) {
            stringArrayList = new ArrayList<>();
        }

        phoneBookAdapter.setSelectlist(stringArrayList);
        phoneBookAdapter.sortEnum = PhoneBookAdapter.SortEnum.DEPARTMENT;
        phoneBookAdapter.ConverSort();
        lv.setAdapter(phoneBookAdapter);
        lv.setOnItemClickListener(onItemClickListener);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.selectview);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        stringViewMap = new HashMap<>();
        btnok = (Button) findViewById(R.id.btnok);
        btnok.setOnClickListener(onClickListenerbtnok);


        menu_custom = new Menu_Custom(this, iMenu);
        menu_custom.addMenuItem(R.drawable.menu_department, "部门排序", 0);
        menu_custom.addMenuItem(R.drawable.menu_az, "拼音排序", 1);
        linearLayoutpy = (LinearViewPY) findViewById(R.id.listpy);
        linearLayoutpy.setVisibility(View.GONE);


    }

    /**
     * 点击返回
     */
    View.OnClickListener onClickListenerreturn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
            overridePendingTransition(R.anim.blank, R.anim.slide_out_to_bottom);
        }
    };

    /**
     * 点击确定返回到消息界面
     */
//    public  void onClickListenerbtnok(View view){
//        Set<String> strings=phoneBookAdapter.getSelectPeople();
//
//        if (strings.size()==0)
//        {
//            setResult(0,null);
//        }
//        else {
//            String[] selectcontacts = new String[strings.size()];
//            int i=0;
//            for (String s : strings) {
//                Log.i("选中的人:", s);
//                selectcontacts[i] = s;
//                i++;
//            }
//
//            Intent intent=new Intent();
//            intent.putExtra("contactslist",selectcontacts);
//            setResult(1, intent);
//
//        }
//        finish();
//        overridePendingTransition(R.anim.blank, R.anim.slide_out_to_bottom);
//    }
    View.OnClickListener onClickListenerbtnok = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Set<String> strings = phoneBookAdapter.getSelectPeople();

            if (!strings.isEmpty()) {
                String[] selectcontacts = new String[strings.size()];
                int i = 0;
                for (String s : strings) {
                    Log.i("选中的人:", s);
                    selectcontacts[i] = s;
                    i++;

                }

                if (mode==1)
                {
                    Intent intent=new Intent();
                    intent.putExtra("contacts",selectcontacts);
                    setResult(2,intent);
                    finish();
                    overridePendingTransition(R.anim.blank, R.anim.slide_out_to_bottom);
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("contactslist", selectcontacts);
                setResult(1, intent);
                finish();
                overridePendingTransition(R.anim.blank, R.anim.slide_out_to_bottom);
            }
        }
    };


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode == 4) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return false;
        }
        return super.onKeyUp(keyCode, event);

    }

    /**
     * 点击更多
     */
    View.OnClickListener onClickListenermore = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            menu_custom.ShowMenu(btnmore);
        }

    };


    /**
     * 搜索
     */
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.toString().equals("")) {
                phoneBookAdapter.ConverSort();
            } else
                phoneBookAdapter.SearchPeople(searchtxt.getText().toString());
            phoneBookAdapter.notifyDataSetChanged();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    PhoneBookAdapter.ISearchfinsih iSearchfinsih = new PhoneBookAdapter.ISearchfinsih() {
        @Override
        public void SearchFinish() {

        }

        @Override
        public void OnSelected(PhoneBookAdapter.PhoneBookItem phoneBookItem) {
            horizontalScrollView.setVisibility(View.VISIBLE);
            btnok.setClickable(true);
            btnok.setTextColor(Color.WHITE);
            ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.list_contacts_nick, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(145, 145);
            params.setMargins(12, 12, 12, 12);
            imageView.setLayoutParams(params);

            imageView.setBackground(getResources().getDrawable(R.drawable.default_avatar));
            linearLayout.addView(imageView);
            stringViewMap.put(phoneBookItem.contacts.getId(), imageView);
            imageView.setTag(phoneBookItem.contacts.getId());
            imageView.setOnClickListener(onClickListenernick);
        }

        @Override
        public void OnDisSelected() {
            horizontalScrollView.setVisibility(View.GONE);
            btnok.setClickable(false);
            btnok.setTextColor(Color.parseColor("#A9B7B7"));
            stringViewMap.clear();
            linearLayout.removeAllViews();
        }

        @Override
        public void OnDelSelected(PhoneBookAdapter.PhoneBookItem phoneBookItem) {
            View v = stringViewMap.get(phoneBookItem.contacts.getId());
            linearLayout.removeView(v);
            stringViewMap.remove(phoneBookItem.contacts.getId());

        }
    };


    View.OnClickListener onClickListenernick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            phoneBookAdapter.delContactsSelect((String) view.getTag());
        }
    };


    /**
     * 菜单点击回调
     */
    IMenu iMenu = new IMenu() {
        @Override
        public void ClickMenu(int itemid) {
            Log.i("item id:", String.valueOf(itemid));
            switch (itemid) {
                case 0:
                    phoneBookAdapter.sortEnum = PhoneBookAdapter.SortEnum.DEPARTMENT;
                    break;
                case 1:
                    phoneBookAdapter.sortEnum = PhoneBookAdapter.SortEnum.AZ;

                    break;
            }
            phoneBookAdapter.ConverSort();
            if (phoneBookAdapter.sortEnum == PhoneBookAdapter.SortEnum.AZ) {
                linearLayoutpy.setVisibility(View.VISIBLE);
                initPYListView();
            } else
                linearLayoutpy.setVisibility(View.GONE);
            phoneBookAdapter.notifyDataSetChanged();

        }
    };


    /**
     * 添加拼音
     */
    void initPYListView() {
        linearLayoutpy.removeAllViews();
        List<String> listpy = phoneBookAdapter.getAzlist();

        for (String py : listpy) {
            RelativeLayout v = (RelativeLayout) getLayoutInflater().inflate(R.layout.pyview, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            v.setLayoutParams(params);
            TextView txtpy = (TextView) v.findViewById(R.id.py);
            int rowidnex = phoneBookAdapter.getMapPY().get(py);
            txtpy.setTag(rowidnex);
            txtpy.setOnClickListener(onClickListenerpy);

            txtpy.setText(py.toUpperCase());
            linearLayoutpy.addView(v);
        }
        linearLayoutpy.setMovewEvent(movewEvent);
    }

    LinearViewPY.MovewEvent movewEvent = new LinearViewPY.MovewEvent() {
        @Override
        public void movew(int index) {
            List<String> listpy = phoneBookAdapter.getAzlist();
            if (index < 0)
                index = 0;
            if (index > listpy.size() - 1)
                index = listpy.size() - 1;

            int rowidnex = phoneBookAdapter.getMapPY().get(listpy.get(index));
            lv.smoothScrollToPositionFromTop(rowidnex, 0);
        }
    };

    View.OnClickListener onClickListenerpy = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i("rowindex", String.valueOf(view.getTag()));

            lv.smoothScrollToPositionFromTop((Integer) view.getTag(), 0);
        }
    };


    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            if (mode ==1)//单选
            {
                PhoneBookAdapter.PhoneBookItem phoneBookItem = (PhoneBookAdapter.PhoneBookItem)phoneBookAdapter.getItem(i);
                Intent intent=new Intent();
                intent.putExtra("contacts",phoneBookItem.contacts);
                setResult(1,intent);
                finish();
                return;
            }

            phoneBookAdapter.setContactsSelected((int) l);
        }
    };

}