package com.suypower.pms.view.plugin.InterFace;

import android.view.View;

/**
 * Created by epoluodi on 15/4/13.
 */
public interface ITabbar_Item_Click {

    /**
     * tabbar 标签选择事件
     * @param v 标签view
     * @param index 标签索引
     */
    void OnTabBarClickItem(View v, int index);
}
