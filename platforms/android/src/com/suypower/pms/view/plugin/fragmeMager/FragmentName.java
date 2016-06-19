package com.suypower.pms.view.plugin.fragmeMager;

import android.os.Message;

/**
 * Created by Administrator on 14-11-30.
 */
public interface FragmentName {
     void SetFragmentName(String name);
     String GetFragmentName();
     void SelectMenu(int Menuid);
     void selectcustomer(String guestid, String guestname);
    void onMessage(Message message);
    void  returnWeb();

    void startIMessageControl();
    void stopIMessageControl();
}
