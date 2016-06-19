package com.suypower.pms.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by Bingdor on 2016/5/27.
 */
public class SimpleViewPager extends ViewPager {
    private boolean scrollble = true;

    public SimpleViewPager(Context context) {
        super(context);
    }

    public SimpleViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isScrollble() {
        return scrollble;
    }

    public void setScrollble(boolean scrollble) {
        this.scrollble = scrollble;
    }
}
