package com.suypower.pms.app;

import java.io.File;
import java.text.SimpleDateFormat;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.provider.Settings;


import com.suypower.pms.R;
import com.suypower.pms.app.configxml.GlobalConfig;
import com.suypower.pms.server.MsgBodyChat;
import com.suypower.pms.view.plugin.fileEx.FilePlugin;
import com.suypower.pms.view.plugin.message.MessageDB;

import org.json.JSONObject;

public class SuyApplication  extends Application {

	private static SuyApplication app;

	SuyDB suyDB;
	private SuyClient m_suyClient;
	/**
	 * SuyService是否初始化完毕
	 */
	private boolean m_bServiceInit;
	private Handler m_hService;

	public String getAbsluteAppPath;//业务应用绝对路径
	public String getRelAppPath;//业务应用相对路径


	public String AppVerName() {
		try {
			PackageManager pm = getPackageManager();
			PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);//getPackageName()是你当前类的包名，0代表是获取版本信息

			return pi.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}


	public int AppVerCode() {
		try {
			PackageManager pm = getPackageManager();
			PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);//getPackageName()是你当前类的包名，0代表是获取版本信息

			return pi.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 设备UUID
	 */

	public String getUuid() {
		return DesEncrypter.encrypt(Settings.Secure.getString(
				getContentResolver(), android.provider.
						Settings.Secure.ANDROID_ID));

	}


	/**
	 * 是否显示通知
	 */
	private boolean m_bShowNotify;


	/**
	 * 程序存储目录（SD卡或存储器内存）
	 */
	private String m_strAppPath;

	public synchronized static SuyApplication getApplication() {
		return app;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		app = this;

		m_suyClient = new SuyClient();


		//加载数据库
		File file = new File(getFilesDir() + "/db.sqlite");
		if (!file.exists())
			FilePlugin.CopyDb(this, R.raw.db, "/db.sqlite");


		suyDB = new SuyDB(this, getFilesDir() + "/db.sqlite");

		//检查www目录不存在创建
//		File appFile = new File(getFilesDir() + "/www/");
//		if (!appFile.exists()) {
//			appFile.mkdir();
//			try {
//				FilePlugin.CopyDb(this, R.raw.www, "/www.zip");
//				FilePlugin.upZipFile(new File(getFilesDir() + "/www.zip"),
//						getFilesDir().getAbsolutePath() + "/www/");
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}


		//读取APP 信息环境，第一次运行创建
		Boolean isrun = Config.getKeyShareVarForBoolean(getApplicationContext(),"IsRun");//判断程序是否运行
		if (!isrun)
		{

			Config.setKeyShareVar(getApplicationContext(),"username","null");
			Config.setKeyShareVar(getApplicationContext(),"userpwd","null");
//			Config.setKeyShareVar(getApplicationContext(),"isautologin",true);
			Config.setKeyShareVar(getApplicationContext(),"IsRun",true);
			Config.setKeyShareVar(getApplicationContext(),"disturb",true);//通知开关
			Config.setKeyShareVar(getApplicationContext(),"msgdisturb",true);//消息通知开关

		}

		GlobalConfig.loadConfig();


//		GlobalConfig.globalConfig.setMessage(0);

		/**
		 * 加载配置文件信息
		 */
//		InputStream inputStream;
//		byte[] buffers =null;
//		String str;
//		try {

//			inputStream=getAssets().open("www/apps/config.xml");
//			inputStream = new FileInputStream(getFilesDir() + "/www/apps/config.xml");
//			buffers =new byte[inputStream.available()];
//			inputStream.read(buffers);
//			str = EncodingUtils.getString(buffers, "utf-8");
//			GlobalConfig.deserialize(str);
//			inputStream.close();
//			GlobalConfig.loadDBConfig();
//			GlobalConfig.loadDBAppInfo();


//			Log.i("api url",GlobalConfig.globalConfig.getApiUrl());
//			Log.i("app url",GlobalConfig.globalConfig.getAppUrl());
//			Log.i("auth url",GlobalConfig.globalConfig.getAuthUrl());
//			Log.i("upgrade url",GlobalConfig.globalConfig.getAppUpgradeUrl());
//			GlobalConfig.globalConfig.setIsCachefile(true);
//			GlobalConfig.globalConfig.setDebug(false);
//			GlobalConfig.globalConfig.setMessage(0);
//			Logger.debug = GlobalConfig.globalConfig.isDebug();
//
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}




	}




	public SuyClient getSuyClient() {
		return m_suyClient;
	}


	public SuyDB getSuyDB() {
		return suyDB;
	}

	public Handler getServiceHandler() {
		return m_hService;
	}

	public void setServiceHandler(Handler handler) {
		m_hService = handler;
	}

	public boolean isSuyServiceInit() {
		return m_bServiceInit;
	}

	public void setSuyServiceInit(boolean bServiceInit) {
		m_bServiceInit = bServiceInit;
	}

	public String getAppPath() {
		return m_strAppPath;
	}

	public void setAppPath(String strAppPath) {
		m_strAppPath = strAppPath;
	}



	public boolean isShowNotify() {
		return m_bShowNotify;
	}

	public void showNotify(int nId, Context context,
						   String tickerText, String strTitle, String strText) {
		/*if (null == m_notifyMgr) {
			m_notifyMgr = (NotificationManager)context.getSystemService(
	        		android.content.Context.NOTIFICATION_SERVICE);
			if (null == m_notifyMgr)
				return;
		}

		int icon = R.drawable.notify_newmessage;
		long when = System.currentTimeMillis();
		
		if (null == m_notify) {
			RemoteViews contentView = new RemoteViews(context.getPackageName(),
					R.layout.notifybar);
			contentView.setImageViewResource(R.id.notify_icon,
					R.drawable.icon);
			contentView.setImageViewResource(R.id.notify_smallicon,
					R.drawable.notify_newmessage);
			
			m_notify = new Notification(icon, tickerText, when);
			m_notify.flags = Notification.FLAG_ONGOING_EVENT 
					| Notification.FLAG_NO_CLEAR;
			m_notify.contentView = contentView;
			
			Intent intent = new Intent(context, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);
			m_notify.contentIntent = contentIntent;
		}
		
		m_notify.tickerText = tickerText;
		m_notify.contentView.setTextViewText(R.id.notify_title, strTitle);
		m_notify.contentView.setTextViewText(R.id.notify_text, strText);
		m_notify.contentView.setLong(R.id.notify_time, "setTime", when);
		m_notifyMgr.notify(nId, m_notify);
		m_bShowNotify = true;*/
	}

	public NotificationManager getNotificationManager() {
		return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}


	public void cancelNotify(int nId) {
		/*if (m_notifyMgr != null)
			m_notifyMgr.cancel(nId);*/
		m_bShowNotify = false;
	}

	public static String GetSysTime()
	{
		SimpleDateFormat sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String   date   =   sDateFormat.format(new   java.util.Date());
		return date;
	}

}
