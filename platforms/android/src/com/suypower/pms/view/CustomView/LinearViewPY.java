package com.suypower.pms.view.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by Stereo on 16/3/30.
 */
public class LinearViewPY extends LinearLayout {

    MovewEvent movewEvent;

    public void setMovewEvent(MovewEvent movewEvent)
    {
        this.movewEvent=movewEvent;
    }
    public LinearViewPY(Context context) {
        super(context);
    }

    public LinearViewPY(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearViewPY(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LinearViewPY(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (ev.getAction() ==MotionEvent.ACTION_MOVE) {
            int h = getMeasuredHeight();
//            Log.i("h:", String.valueOf(h));
//            Log.i("y:", String.valueOf(ev.getY()));
            int index = (int) ( ev.getY() /113);
//            Log.i("移动:", String.valueOf(index));
            movewEvent.movew(index);

        }
        return super.onInterceptTouchEvent(ev);
    }

  public interface MovewEvent
  {
      void movew(int index);
  }

}
