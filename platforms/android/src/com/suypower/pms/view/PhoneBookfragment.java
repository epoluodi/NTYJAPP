package com.suypower.pms.view;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.suypower.pms.R;
import com.suypower.pms.app.ReturnData;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.task.BaseTask;
import com.suypower.pms.app.task.Common;
import com.suypower.pms.app.task.Contacts;
import com.suypower.pms.app.task.InterfaceTask;
import com.suypower.pms.view.CustomView.LinearViewPY;
import com.suypower.pms.view.CustomView.ListForRefresh;
import com.suypower.pms.view.contacts.ContactsDB;
import com.suypower.pms.view.contacts.ContactsSelectActivity;
import com.suypower.pms.view.contacts.PhoneBookAdapter;
import com.suypower.pms.view.dlg.IMenu;
import com.suypower.pms.view.dlg.Menu_Custom;
import com.suypower.pms.view.plugin.fragmeMager.FragmentName;
import com.suypower.pms.view.user.UserInfoActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import asposewobfuscated.GO;

/**
 * Created by Administrator on 14-11-30.
 */
public class PhoneBookfragment extends Fragment implements FragmentName {

    String Fragment_Name = "";
    ImageView btnmore;
    Menu_Custom menu_custom;

    private ListForRefresh lv;
    PhoneBookAdapter phoneBookAdapter;
    Animation animationEnter;
    Animation animationExit;
    Animation aninenter;
    Animation animexit;
    LinearViewPY linearLayoutpy;

    TextView title;
    ImageView btnsearch;
    ImageView btncancel;
    EditText searchtxt;
    FrameLayout frameLayout;

    View backview;

    Boolean isRefresh;


    public PhoneBookfragment() {
        isRefresh = true;

    }

    @Override
    public void startIMessageControl() {

    }

    @Override
    public void stopIMessageControl() {

    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_phonebook, container, false);
        frameLayout = (FrameLayout) rootView.findViewById(R.id.frameview);
        title = (TextView) rootView.findViewById(R.id.title);
        btnmore = (ImageView) rootView.findViewById(R.id.btnmore);
        btnmore.setOnClickListener(onClickListenermore);
        btncancel = (ImageView) rootView.findViewById(R.id.btncancel);
        btncancel.setOnClickListener(onClickListenercancel);
        btnsearch = (ImageView) rootView.findViewById(R.id.btnsearch);
        btnsearch.setOnClickListener(onClickListenersearch);

        searchtxt = (EditText) rootView.findViewById(R.id.searchtxt);

        lv = (ListForRefresh) rootView.findViewById(R.id.listEx);
        phoneBookAdapter = new PhoneBookAdapter(getActivity().getLayoutInflater(), iSearchfinsih);
        lv.setAdapter(phoneBookAdapter);
        lv.setonRefreshListener(onRefreshListener);
        lv.setOnItemClickListener(onItemClickListener);

        menu_custom = new Menu_Custom(getActivity(), iMenu);
        menu_custom.addMenuItem(R.drawable.menu_department, "部门排序", 0);
        menu_custom.addMenuItem(R.drawable.menu_az, "拼音排序", 1);
        linearLayoutpy = (LinearViewPY) rootView.findViewById(R.id.listpy);
        linearLayoutpy.setVisibility(View.GONE);


        animationEnter = AnimationUtils.loadAnimation(getActivity(), R.anim.bar_anim_enter);
        animationEnter.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                searchtxt.clearAnimation();
                title.clearAnimation();
                btnmore.clearAnimation();
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
                btnmore.clearAnimation();
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

        isRefresh = false;
        phoneBookAdapter.ConverSort();

        phoneBookAdapter.notifyDataSetChanged();
        return rootView;
    }


    /**
     * 刷新联系人
     */
    void refreshContacts() {
        Common common = new Common(interfaceTask, Common.GETDEPTS);
        common.startTask();
    }

    InterfaceTask interfaceTask = new InterfaceTask() {
        @Override
        public void TaskResultForMessage(Message message) {

            if (message.what == Common.CommonTask) {
                if (message.arg2 == Common.GETDEPTS) {
                    if (message.arg1 != BaseTask.SUCCESS) {
                        Toast.makeText(getActivity(), message.obj.toString(), Toast.LENGTH_SHORT).show();
                        lv.onRefreshComplete();
                        isRefresh = false;
                        return;
                    }

                    try {

                        Contacts contacts = new Contacts(interfaceTask);
                        contacts.startTask();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                return;
            }
            if (message.what == Common.ContactsTask) {
                if (message.arg2 == Contacts.GETCONTACTS) {
                    if (message.arg1 != BaseTask.SUCCESS) {
                        Toast.makeText(getActivity(), message.obj.toString(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {

                        phoneBookAdapter.ConverSort();
                        if (phoneBookAdapter.sortEnum == PhoneBookAdapter.SortEnum.AZ) {
                            linearLayoutpy.setVisibility(View.VISIBLE);
                            initPYListView();
                        } else
                            linearLayoutpy.setVisibility(View.GONE);
                        phoneBookAdapter.notifyDataSetChanged();
                        lv.onRefreshComplete();
                        isRefresh = false;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                return;
            }


        }
    };

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
     * 点击搜索
     */
    View.OnClickListener onClickListenersearch = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (backview != null)
                return;
            searchtxt.setText("");
            searchtxt.startAnimation(animationEnter);
            btncancel.startAnimation(animationEnter);
            searchtxt.setVisibility(View.VISIBLE);
            btncancel.setVisibility(View.VISIBLE);
            searchtxt.setOnKeyListener(onKeyListenersearch);
            searchtxt.addTextChangedListener(textWatcher);
            title.startAnimation(animationExit);
            title.setVisibility(View.GONE);
            btnmore.startAnimation(animationExit);
            btnmore.setVisibility(View.GONE);
            searchtxt.requestFocus();
            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.toggleSoftInputFromWindow(searchtxt.getWindowToken(), 1, 0);
            backview = new View(getActivity());
            ViewGroup.LayoutParams params = lv.getLayoutParams();
            backview.setLayoutParams(params);
            frameLayout.addView(backview);
            backview.setBackgroundColor(R.color.white);
            backview.setAlpha(0.95f);
            backview.setOnClickListener(onClickListenercancel);
            backview.startAnimation(aninenter);
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
            phoneBookAdapter.SearchPeople(searchtxt.getText().toString());
            phoneBookAdapter.notifyDataSetChanged();
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
                if (backview != null) {
                    frameLayout.removeView(backview);
                    backview.startAnimation(animexit);
                    backview.setOnClickListener(null);
                    backview = null;
                }
            }
            return false;
        }
    };


    PhoneBookAdapter.ISearchfinsih iSearchfinsih = new PhoneBookAdapter.ISearchfinsih() {
        @Override
        public void SearchFinish() {
            if (backview != null) {
                frameLayout.removeView(backview);
                backview.startAnimation(animexit);
                backview.setOnClickListener(null);
                backview = null;
            }
        }

        @Override
        public void OnSelected(PhoneBookAdapter.PhoneBookItem phoneBookItem) {

        }

        @Override
        public void OnDisSelected() {

        }

        @Override
        public void OnDelSelected(PhoneBookAdapter.PhoneBookItem phoneBookItem) {

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
            btnmore.startAnimation(animationEnter);
            title.setVisibility(View.VISIBLE);
            btnmore.setVisibility(View.VISIBLE);
            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(searchtxt.getWindowToken(), 0);
            if (backview != null) {
                frameLayout.removeView(backview);
                backview.startAnimation(animexit);
                backview.setOnClickListener(null);
                backview = null;
            }
            phoneBookAdapter.ConverSort();
            phoneBookAdapter.notifyDataSetChanged();
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


    ListForRefresh.OnRefreshListener onRefreshListener = new ListForRefresh.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (!isRefresh) {
                isRefresh = true;
                refreshContacts();
            }
        }
    };


    /**
     * 添加拼音
     */
    void initPYListView() {
        linearLayoutpy.removeAllViews();
        List<String> listpy = phoneBookAdapter.getAzlist();
        for (String py : listpy) {
            RelativeLayout v = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.pyview, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
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
            PhoneBookAdapter.PhoneBookItem phoneBookItem = (PhoneBookAdapter.PhoneBookItem) phoneBookAdapter.getItem((int) l);
            Log.i("user_name", phoneBookItem.contacts.getName());
            Intent intent = new Intent(getActivity(), UserInfoActivity.class);
            intent.putExtra("IsSelf", false);//自己打开
            intent.putExtra("Contacts", (Serializable) phoneBookItem.contacts);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    };

}
