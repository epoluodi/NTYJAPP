package com.suypower.pms.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.suypower.pms.R;
import com.suypower.pms.app.Config;
import com.suypower.pms.app.LoginAccountList;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.SuyCallBackMsg;
import com.suypower.pms.app.SuyClient;
import com.suypower.pms.app.SuyLoginResultCode;
import com.suypower.pms.app.SuyService;
import com.suypower.pms.app.SuyStatus;
import com.suypower.pms.app.SuyUserInfo;
import com.suypower.pms.app.configxml.AppConfig;
import com.suypower.pms.app.configxml.GlobalConfig;
import com.suypower.pms.view.dlg.RequestingDlg;
import com.suypower.pms.view.plugin.CustomPopWindowPlugin;

/**
 * 用户登陆界面
 * 1、同一个手机第一次登录，需要短信验证
 * 2、同一个手机第二次登录，需要输入验证码
 * 
 * 判断手机是否为第一次登录，直接从LoginAccountList中获取。若有，则不是第一次登录。否则为第一次登录。
 * 
 * 1、需要短信
 * 		a、点击“获取短信验证”：首先在服务器上验证用户名和密码的正确性。若正确，则返回短信验证码。否则，提示“用户名或密码不正确”。
 * 		b、60s内不能重复点击“获取短信验证”。
 *      c、短信输入成功后，点击“登录”，需要验证“用户名”、“密码”及“短信验证码”。若正确，登录主界面，否则，提示“用户名或密码不正确”或“验证码不正确”。
 * 2、需要图片验证
 *      a、点击“获取图片验证”：首先在服务器上验证用户名和密码的正确性。若正确，则返回验证码图片。否则，提示“用户名或密码不正确”。
 * 
 * @author liuzeren
 *
 */
public class LoginActivity extends Activity implements View.OnClickListener,TextWatcher{
	
	private SuyClient suyClient;
	
	/**
	 * 位移动画
	 */
	private Animation my_Translate;
	
	private String userName,password,verifyCode;
	private LinearLayout rl;
	RelativeLayout bg;
	private EditText etUserName,etPwd,etVerifyCode;

	private Button btnLogin,btnVerifyCode;

	/**
	 * 标志当前用户在这个手机上是否为第一次登录
	 */
	private boolean isFirstLoginInPhone = true;

	Thread thread;//界面线程
	ProgressBar progressBar;//读取 验证码进度
	String crccode;
	ImageView image1;
	Thread threaddisplay;
	Animation animation1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.login_activity);
		initView();


		threaddisplay = new Thread(runnable);
		threaddisplay.start();

	}

	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what)
			{
				case 1:



					slideview(new Rect(image1.getLeft(),image1.getTop(),
							image1.getRight(),image1.getBottom()));

					break;
				case 2:
					anim();
					rl.startAnimation(my_Translate);
					btnLogin.startAnimation(my_Translate);
					break;
			}
		}
	};

	public void slideview(final Rect rect) {
//		AnimationSet animationSet = new AnimationSet(true);
//		animationSet.setInterpolator(new AccelerateInterpolator());
//		AlphaAnimation alphaAnimation =new AlphaAnimation(0,1);

//		alphaAnimation.setDuration(300);
//		alphaAnimation.setStartOffset(0);
//		alphaAnimation.setRepeatCount(0);
//		alphaAnimation.setFillAfter(true);
//		animationSet.addAnimation(alphaAnimation);

		TranslateAnimation animation = new TranslateAnimation(
				0, 0, 0, -340);
        animation.setInterpolator(new AccelerateInterpolator());
		animation.setDuration(400);
		animation.setStartOffset(280);
		animation.setRepeatCount(0);
//		animation.setRepeatMode(Animation.REVERSE);
		animation.setFillAfter(true);
//		animationSet.setFillAfter(true);
//		animationSet.addAnimation(animation);
		image1.startAnimation(animation);
	}


	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			try
			{
				Thread.sleep(200);
				handler.sendEmptyMessage(1);
				Thread.sleep(300);
				handler.sendEmptyMessage(2);

			}
			catch (Exception e)
			{e.printStackTrace();}
		}
	};
	/**
	 * 初始化视图控件
	 */
	private void initView() {
		suyClient = SuyApplication.getApplication().getSuyClient();
		suyClient.setCallBackHandler(m_Handler);
		bg = (RelativeLayout)findViewById(R.id.bg);
		rl = (LinearLayout)findViewById(R.id.rl);
		image1 = (ImageView)findViewById(R.id.image1);
		etUserName = (EditText)findViewById(R.id.etUserName);
		etUserName.addTextChangedListener(this);
		etPwd = (EditText)findViewById(R.id.etPwd);
		etPwd.addTextChangedListener(this);
		etVerifyCode = (EditText)findViewById(R.id.etVerifyCode);
		btnLogin = (Button)findViewById(R.id.btnLogin);
		progressBar=(ProgressBar)findViewById(R.id.pbar);
		progressBar.setVisibility(View.VISIBLE);
		btnVerifyCode = (Button)findViewById(R.id.btnVerifyCode);

		btnLogin.setOnClickListener(this);
		btnVerifyCode.setOnClickListener(this);

		thread=new Thread(runnablegetvalidateImage);
		thread.start();
		initLoginingDlg();
	}


	Runnable runnablegetvalidateImage = new Runnable() {
		@Override
		public void run() {
			Message message = handlergetvalidateImage.obtainMessage();
			crccode = Number().toLowerCase();

			String strurl = String.format("%1$sverfyImage/%2$s",
					GlobalConfig.globalConfig.getAuthUrl(),

					crccode);

			message.obj = SuyClient.LoadvalidateImage(strurl);
			handlergetvalidateImage.sendMessage(message);
		}
	};
	/**
	 * 获得验证图片 handle
	 */
	Handler handlergetvalidateImage = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.obj==null) {
				btnVerifyCode.setText("刷新");
				btnVerifyCode.setBackground(null);
				progressBar.setVisibility(View.INVISIBLE);
			}
			else {
				btnVerifyCode.setBackground((Drawable) msg.obj);
				btnVerifyCode.setText("");

				progressBar.setVisibility(View.INVISIBLE);
			}

		}
	};


	private RequestingDlg m_dlgLogining;
	
	private void initLoginingDlg() {
		m_dlgLogining = new RequestingDlg(this);
	}
	
	/**
	 * 初始化动画
	 */
	private void anim() {
		my_Translate = AnimationUtils.loadAnimation(this, R.anim.alpha);
	}
	
	/**
	 * 与服务交互
	 */
	private Handler m_hService = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (1 == msg.what) {	// 初始化成功
				suyClient.setUser(userName, password);
				suyClient.setLoginStatus(SuyStatus.ONLINE);

				suyClient.login();				
			} else {					// 初始化失败
				Toast.makeText(getBaseContext(), 
						R.string.service_init_err, Toast.LENGTH_LONG).show();
				suyClient.setNullCallBackHandler(m_Handler);
				SuyService.stopSuyService(LoginActivity.this);
				finish();
			}
		}
	};

	/**
	 * 与task进行交互
	 */
	private Handler m_Handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SuyCallBackMsg.LOGIN_RESULT:
				closeLoginingDlg();
				if (msg.arg1 == SuyLoginResultCode.SUCCESS) {	// 登录成功

					SuyUserInfo userInfo = suyClient.getSuyUserInfo();


					//存储app环境信息存储
			    	Config.setKeyShareVar(LoginActivity.this,"username",userInfo.userName);
					Config.setKeyShareVar(LoginActivity.this,"userpwd",userInfo.password);
					Config.setKeyShareVar(LoginActivity.this,"isautologin",true);
//			    	String strAppPath = SuyApplication.getApplication().getAppPath();
//			    	String strFileName = strAppPath + Config.getString(SuyService.LOGIN_USER_LIST_FILE);
//			    	accountList.saveFile(strFileName);

			    	suyClient.setNullCallBackHandler(null);
					startActivity(new Intent(LoginActivity.this, NavigationActivity.class));
					finish();
				} else if (msg.arg1 == SuyLoginResultCode.FAILED) {	// 登录失败
					Toast.makeText(getBaseContext(), 
							msg.obj.toString(), Toast.LENGTH_LONG).show();
				} else if (msg.arg1 == SuyLoginResultCode.PASSWORD_ERROR) {	// 密码错误
					Toast.makeText(getBaseContext(),
							msg.obj.toString(), Toast.LENGTH_LONG).show();
				} else if (msg.arg1 == SuyLoginResultCode.NEED_VERIFY_CODE
						|| msg.arg1 == SuyLoginResultCode.VERIFY_CODE_ERROR) {	// 需要输入验证码
					suyClient.setNullCallBackHandler(null);
					finish();
				} 
				break;
				
			default:
				break;
			}
		}
	};


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLogin:	// “登录”按钮
			userName = etUserName.getText().toString();
			password = etPwd.getText().toString();
			verifyCode = etVerifyCode.getText().toString().toLowerCase();

			
			if (TextUtils.isEmpty(userName)) {
				Toast.makeText(getBaseContext(), 
						R.string.enter_id, Toast.LENGTH_LONG).show();
				return;
			}
			
			if (TextUtils.isEmpty(password)) {
				Toast.makeText(getBaseContext(), 
						R.string.enter_pwd, Toast.LENGTH_LONG).show();
				return;
			}
			
			if(TextUtils.isEmpty(verifyCode)){
				Toast.makeText(getBaseContext(), 
						R.string.enter_msg_verify_code, Toast.LENGTH_LONG).show();
				return;
			}
			if (!crccode.equals(verifyCode))
			{
				Toast.makeText(getBaseContext(),
						R.string.verify_result, Toast.LENGTH_LONG).show();
				return;
			}
			AppConfig.appConfig = null;
			showLoginingDlg();
//			SuyService.startSuyService(this, m_hService);
			Message message = m_hService.obtainMessage();
			message.what=1;
			m_hService.sendMessageAtTime(message,500);
			
			break;
		case R.id.btnVerifyCode:{
			progressBar.setVisibility(View.VISIBLE);
			thread=new Thread(runnablegetvalidateImage);
			thread.start();

			if(isFirstLoginInPhone)
			{
				//首先需要登录验证用户名和密码，用户名和密码验证通过之后，再次返回验证
			}
			break;
		}
		}
	}
	
	private void showLoginingDlg() {
//		if (m_dlgLogining != null)
//			m_dlgLogining.show();

		CustomPopWindowPlugin.ShowPopWindow(btnLogin,getLayoutInflater(),
				"正在登录");
	}
	
	private void closeLoginingDlg() {
//		if (m_dlgLogining != null && m_dlgLogining.isShowing())
//			m_dlgLogining.dismiss();
		CustomPopWindowPlugin.CLosePopwindow();
	}
	
	@Override
	protected void onDestroy() {
		suyClient.setNullCallBackHandler(m_Handler);
		
		super.onDestroy();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void afterTextChanged(Editable s) {
		String strName = etUserName.getText().toString();
		String strPwd = etPwd.getText().toString();



		if(TextUtils.isEmpty(strName) || TextUtils.isEmpty(strPwd)){
			btnVerifyCode.setEnabled(false);
		}else{
			btnVerifyCode.setEnabled(true);
		}
	}




	/**
	 * 随机验证码
	 * @return
	 */
	public static String Number() {
		String str="";
		char ca;
		for (int i = 1; i <= 4; i++) {

			int ii =(int) (Math.random()*4+1);

			switch (ii)
			{
				case 1:
				case 3:
					ca=(char) (Math.random()*9+48);
					str +=ca+"";
					break;
				case 2:
				case 4:
					ca=(char) (Math.random()*25+65);
					str +=ca+"";
					break;

			}


		}

		return str;
	}


}
