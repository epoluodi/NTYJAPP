package com.suypower.pms.view.dlg;


import com.suypower.pms.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 * 封装请求的忙对话框
 * @author liuzeren
 *
 */
public class RequestingDlg extends Dialog {


	private TextView loadingTips;

	public RequestingDlg(Context context) {
		this(context,false);
	}
	
	public RequestingDlg(Context context, boolean cancelable) {
		this(context, cancelable,null);
	}
	
	public RequestingDlg(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, R.style.dialog);

		loadingTips = (TextView)findViewById(R.id.loadingTips);

		setCancelable(cancelable);
		setOnCancelListener(cancelListener);
		
		setContentView(R.layout.requesting_dlg);
		
		Window win = this.getWindow();
		WindowManager.LayoutParams params = win.getAttributes();
		
		int cxScreen =0;// ABAppUtil.getDeviceWidth(context);
		int cyScreen =0;// ABAppUtil.getDeviceHeight(context);
		
		int cy = (int)context.getResources().getDimension(R.dimen.cyloginingdlg);
		int lrMargin = (int)context.getResources().getDimension(R.dimen.loginingdlg_lr_margin);
		int tMargin = (int)context.getResources().getDimension(R.dimen.loginingdlg_t_margin);
		
		params.x = -(cxScreen-lrMargin*2)/2;
		params.y = (-(cyScreen-cy)/2)+tMargin;
		params.width = cxScreen;
		params.height = cy;
		
		setCanceledOnTouchOutside(false);	//设置点击Dialog外部任意区域不能关闭Dialog
		setCancelable(false);		// 设置为false，按返回键不能退出
	}

	public void setText(String txt){
		loadingTips.setText(txt);
	}
	
	public void setText(int txtId){
		loadingTips.setText(txtId);
	}
}
