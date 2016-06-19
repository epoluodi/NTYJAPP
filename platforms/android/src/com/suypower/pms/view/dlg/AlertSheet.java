package com.suypower.pms.view.dlg;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suypower.pms.R;

/**
 * 定义Alert对话框
 *
 * @author YXG
 */
public class AlertSheet extends Dialog {


    Button buttonCancel;
    LinearLayout linearLayout;
    int btns = 0;
    Context context;


    public AlertSheet(Context context) {
        super(context, R.style.dialog);
        this.context = context;
        setContentView(R.layout.alert_bottom_sheet);

        linearLayout = (LinearLayout) findViewById(R.id.menu_linearLayout);
        buttonCancel = (Button) findViewById(R.id.btn_cancel);
        buttonCancel.setOnClickListener(onClickListenerCancel);

        setCanceledOnTouchOutside(false);    //设置点击Dialog外部任意区域不能关闭Dialog
        setCancelable(false);        // 设置为false，按返回键不能退出
    }


    @Override
    public void show() {

        Window win = this.getWindow();
        WindowManager.LayoutParams params = win.getAttributes();
        WindowManager wm =  (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int cxScreen =wm.getDefaultDisplay().getWidth();
        int cyScreen =wm.getDefaultDisplay().getHeight();
        win.setWindowAnimations(R.style.Animationbottomwindows);
        params.x = 0;
        params.y = cyScreen - params.height;
        params.width = cxScreen;
        super.show();


    }

    public void addSheetButton(String title, View.OnClickListener onClickListener) {


        if (btns>0)
            addline();
        Button button = new Button(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(params);
        button.setBackground(context.getResources().getDrawable(R.drawable.btn_cancel_selector));
        button.setOnClickListener(onClickListener);
        button.setId(btns);
        button.setText(title);
        button.setTextSize(14);
        linearLayout.addView(button);

        btns++;
    }


    void addline() {
        View line = new View(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        line.setLayoutParams(params);
        params.height=1;
        line.setBackground(context.getResources().getDrawable(R.color.blackTransparent1));
        linearLayout.addView(line);
    }


    View.OnClickListener onClickListenerCancel = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
        }
    };


}
