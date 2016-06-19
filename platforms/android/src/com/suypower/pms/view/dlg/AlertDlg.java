package com.suypower.pms.view.dlg;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.suypower.pms.R;

/**
 * 定义Alert对话框
 * @author YXG
 *
 */
public class AlertDlg extends Dialog {

	public  enum AlertEnum
	{
		ALERT,//警告
		INFO,//信息
		TIPS, // 提示
		ALTERTYPE,

	}
	TextView title;
	TextView content;
	Button buttonOk;
	Button buttonCancel;




	public AlertDlg(Context context, AlertEnum alertEnum) {
		super(context, R.style.dialog);
		setContentView(R.layout.alertview_layout);

		title = (TextView)findViewById(R.id.title);
		content = (TextView)findViewById(R.id.content);
		buttonOk= (Button)findViewById(R.id.btn_ok);
		buttonCancel= (Button)findViewById(R.id.btn_cancel);
		buttonOk.setOnClickListener(onClickListenerOk);
		buttonCancel.setOnClickListener(onClickListenerCancel);

		if (alertEnum == AlertEnum.ALTERTYPE)
		{
			title.setText("提示");
			buttonCancel.setVisibility(View.GONE);
			buttonOk.setBackground(context.getResources().getDrawable(R.drawable.alert_btn_selector3));
		}

		setTitleText(alertEnum);
		setCanceledOnTouchOutside(false);	//设置点击Dialog外部任意区域不能关闭Dialog
		setCancelable(false);		// 设置为false，按返回键不能退出
	}





	public void setOKTitle(String s)
	{
		buttonOk.setText(s);
	}
	public void setCancelTitle(String s)
	{
		buttonCancel.setText(s);
	}
	public void setOkClickLiseter(View.OnClickListener onClickListener)
	{
		buttonOk.setOnClickListener(onClickListener);
	}
	public void setCancelClickLiseter(View.OnClickListener onClickListener)
	{
		buttonCancel.setOnClickListener(onClickListener);
	}

	View.OnClickListener onClickListenerOk = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			dismiss();
		}
	};

	View.OnClickListener onClickListenerCancel = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			dismiss();
		}
	};

	public void setTitleText(AlertEnum alertEnum){
		switch (alertEnum)
		{
			case ALERT:
				title.setText("警告");
				break;
			case INFO:
				title.setText("信息");
				break;
			case TIPS:
				title.setText("提示");
				break;
		}

	}

	public void setContentText(String txt){
		content.setText(txt);
	}
}
