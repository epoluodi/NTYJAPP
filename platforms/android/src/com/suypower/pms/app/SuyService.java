package com.suypower.pms.app;

import java.io.File;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;



/**
 * 应用服务中心
 * @author liuzeren
 *
 */
public class SuyService extends Service {
	
	/**
	 * 服务名
	 */
	private static final String SUYSERVICE_NAME = "com.suypower.stereo.SuyService";
	/**
	 * 登陆用户列表文件配置
	 */
	public static final String LOGIN_USER_LIST_FILE = "login_user_list_file";
	
	private SuyClient suyClient = null;
	/**
	 * 服务是否初始化完毕
	 */
	private boolean m_bInit = false;
	/**
	 * 数据存储根目录
	 */
	private String m_strAppPath;


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		suyClient = SuyApplication.getApplication().getSuyClient();

		
//		if (FileUtil.hasSDCard()) {//初始化本地存储根目录
//        	m_strAppPath = FileUtil.getSDCardPath()+File.separator + Config.getString(Config.ROOT_PATH)+File.separator;
//        } else {
//        	m_strAppPath = FileUtil.getRamFilePath(this);
//        }



		SuyApplication.getApplication().setAppPath(m_strAppPath);
		String strFileName = m_strAppPath + Config.getString(LOGIN_USER_LIST_FILE);

        
//        suyClient.setProxyHandler(m_handlerProxy);
        
		m_bInit = suyClient.init();//初始化本地http连接及线程池
        if (!m_bInit)
        	suyClient.uninit();
        

        SuyApplication.getApplication().setSuyServiceInit(m_bInit);
	}
	
//	private Handler m_handlerProxy = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			suyClient.handleProxyMsg(msg);
//			if (SuyApplication.getApplication().isShowNotify()) {
//				switch (msg.what) {
//				case SuyCallBackMsg.BUDDY_MSG: {            // 好友消息
//
//					break;
//				}
//				}
//			} else {
//				//m_nNewMsgCnt = 0;
//			}
//
//			if (SuyCallBackMsg.LOGOUT_RESULT == msg.what) {//注销
//				SuyService.this.stopSelf();
//			}
//		}
//	};
	
	/* 
		onStartCommand有4种返回值：
		START_STICKY：如果service进程被kill掉，保留service的状态为开始状态，但不保留递送的intent对象。随后系统会尝试重新创建service，由于服务状态为开始状态，所以创建服务后一定会调用onStartCommand(Intent,int,int)方法。如果在此期间没有任何启动命令被传递到service，那么参数Intent将为null。
		START_NOT_STICKY：“非粘性的”。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统不会自动重启该服务。
		START_REDELIVER_INTENT：重传Intent。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统会自动重启该服务，并将Intent的值传入。
		START_STICKY_COMPATIBILITY：START_STICKY的兼容版本，但不保证服务被kill后一定能重启。
	 */
	@Override  
    public int onStartCommand(Intent intent, int flags, int startId) {
        Handler handler = SuyApplication.getApplication().getServiceHandler();
        if (null == handler)
        	return START_STICKY;
                
        if (!suyClient.isOffline()) {
        	handler.sendEmptyMessage(2);	// 已经登录
        } else if (m_bInit) {
        	handler.sendEmptyMessage(1);	// 初始化成功
        } else {						
        	handler.sendEmptyMessage(0);	// 初始化失败
        }
        return START_NOT_STICKY;
    }
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		suyClient.uninit();
		m_bInit = false;
		SuyApplication.getApplication().setSuyServiceInit(m_bInit);
	}
	
	/**
	 * 启动SuyService
	 * @param context
	 * @param handler
	 */
	public static void startSuyService(Context context, Handler handler) {
		SuyApplication.getApplication().setServiceHandler(handler);
		
		Intent intent = new Intent(SUYSERVICE_NAME);
		context.startService(intent);
	}
	
	/**
	 * 停止SuyService
	 * @param context
	 */
	public static void stopSuyService(Context context) {
		Intent intent = new Intent(SUYSERVICE_NAME);
		context.stopService(intent);
	}
}
