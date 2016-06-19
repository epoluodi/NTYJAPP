package com.suypower.pms.app;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;


import com.suypower.pms.app.task.FileDownloadTask;
import com.suypower.pms.app.task.FileUpLoadTask;
import com.suypower.pms.app.task.LoginTask;
import com.suypower.pms.app.task.TaskManager;
import com.suypower.pms.app.task.UpLoadPhotoTask;

/**
 * 应用中心
 * 
 * @author liuzeren
 *
 */
public final class SuyClient {

	private static final String TAG = SuyClient.class.getSimpleName();

	private static final String USER_AGENT = "Mozilla/4.0 "
			+ "(compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR "
			+ "1.1.4322; .NET CLR 2.0.50727; InfoPath.2; Alexa Toolbar)";

	/**
	 * 其他线程池
	 */
	private TaskManager m_taskMgr = null;
	/**
	 * 发送消息线程池
	 */
	private TaskManager m_sendMsgTaskMgr = null;
	/**
	 * 接收消息线程池
	 */
	private TaskManager m_recvMsgTaskMgr = null;
	/**
	 * http请求对象
	 */
	private HttpClient m_httpClient;

	/**
	 * 本地用户信息
	 */
	private SuyUserInfo m_suyUserInfo = new SuyUserInfo();

	/**
	 * 初始化http请求对象
	 * 
	 * @return
	 */
	public boolean init() {
		if (null == m_httpClient) {
			try {
				KeyStore trustStore = KeyStore.getInstance(KeyStore
						.getDefaultType());
				trustStore.load(null, null);

				SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
				sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

				// 设置一些基本参数
				HttpParams params = new BasicHttpParams();
				HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
				HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
				HttpProtocolParams.setUseExpectContinue(params, false);
				HttpProtocolParams.setUserAgent(params, USER_AGENT);
				// 超时设置
				// ConnManagerParams.setTimeout(params, 1000);
				HttpConnectionParams.setConnectionTimeout(params, 10000);// 连接超时(单位：毫秒)
				// HttpConnectionParams.setSoTimeout(params, 30*1000); //
				// 读取超时(单位：毫秒)

				SchemeRegistry schReg = new SchemeRegistry();
				schReg.register(new Scheme("http", PlainSocketFactory
						.getSocketFactory(), 80));
				schReg.register(new Scheme("https", sf, 443));

				// 设置我们的HttpClient支持HTTP和HTTPS两种模式
				// SchemeRegistry schReg = new SchemeRegistry();
				// schReg.register(new Scheme("http",
				// PlainSocketFactory.getSocketFactory(), 80));
				// schReg.register(new Scheme("https",
				// SSLSocketFactory.getSocketFactory(), 443));

				// 使用线程安全的连接管理来创建HttpClient
				ClientConnectionManager connectionMgr = new ThreadSafeClientConnManager(
						params, schReg);
				m_httpClient = new DefaultHttpClient(connectionMgr, params);
			} catch (KeyStoreException e) {

			} catch (NoSuchAlgorithmException e) {

			} catch (CertificateException e) {

			} catch (IOException e) {

			} catch (KeyManagementException e) {

			} catch (UnrecoverableKeyException e) {

			}
		}

		m_taskMgr = new TaskManager();
		m_taskMgr.init(0);
		m_sendMsgTaskMgr = new TaskManager();
		m_sendMsgTaskMgr.init(1);
		m_recvMsgTaskMgr = new TaskManager();
		m_recvMsgTaskMgr.init(1);

		return true;
	}


	public HttpClient initHttpClient() {
		HttpClient httpClient = null;
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			// 设置一些基本参数
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpProtocolParams.setUseExpectContinue(params, false);
			HttpProtocolParams.setUserAgent(params, USER_AGENT);
			// 超时设置
			// ConnManagerParams.setTimeout(params, 1000);
			HttpConnectionParams.setConnectionTimeout(params, 10000);// 连接超时(单位：毫秒)
			// HttpConnectionParams.setSoTimeout(params, 30*1000); //
			// 读取超时(单位：毫秒)

			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			schReg.register(new Scheme("https", sf, 443));

			// 设置我们的HttpClient支持HTTP和HTTPS两种模式
			// SchemeRegistry schReg = new SchemeRegistry();
			// schReg.register(new Scheme("http",
			// PlainSocketFactory.getSocketFactory(), 80));
			// schReg.register(new Scheme("https",
			// SSLSocketFactory.getSocketFactory(), 443));

			// 使用线程安全的连接管理来创建HttpClient
			ClientConnectionManager connectionMgr = new ThreadSafeClientConnManager(
					params, schReg);
			httpClient = new DefaultHttpClient(connectionMgr, params);
		} catch (KeyStoreException e) {

		} catch (NoSuchAlgorithmException e) {

		} catch (CertificateException e) {

		} catch (IOException e) {

		} catch (KeyManagementException e) {

		} catch (UnrecoverableKeyException e) {

		}


		return httpClient;
	}



	public void uninit() {
		m_sendMsgTaskMgr.shutdown();
		m_recvMsgTaskMgr.shutdown();
		m_taskMgr.shutdown();
		// 由于以下HttpClient关闭连接代码需要在子线程调用，否则会报android.os.NetworkOnMainThreadException，所以这里不作释放了
		// if (m_httpClient != null && m_httpClient.getConnectionManager() !=
		// null)
		// m_httpClient.getConnectionManager().shutdown();
		new Thread() {
			@Override
			public void run() {
				if (m_httpClient != null
						&& m_httpClient.getConnectionManager() != null)
					m_httpClient.getConnectionManager().shutdown();
				m_httpClient = null;
			}
		}.start();
	}

	/**
	 * 用户登录
	 * @return
	 */
	public boolean login() {
		 if (!isOffline()
		 || TextUtils.isEmpty(m_suyUserInfo.userName)
		 || TextUtils.isEmpty(m_suyUserInfo.password))
		 return false;
		
		 LoginTask task = new LoginTask("LoginTask", m_httpClient);
		
		 task.suyUserInfo = m_suyUserInfo;
		 task.m_recvMsgTaskMgr = m_recvMsgTaskMgr;
		
		 return m_taskMgr.addTask(task);
	}

	/**
	 * 上传照片
	 * @return
	 */
	public boolean uploadPhoto(int photos,JSONArray jsonArray) {

		UpLoadPhotoTask  upLoadPhotoTask = new UpLoadPhotoTask("UpLoadPhotoTask", initHttpClient());
		upLoadPhotoTask.photos=photos;
		upLoadPhotoTask.photofiles = jsonArray;
		upLoadPhotoTask.suyUserInfo = m_suyUserInfo;

		return m_taskMgr.addTask(upLoadPhotoTask);
	}


	//上传文件
	public boolean uploadFile(String[] files) {

		FileUpLoadTask fileUpLoadTask = new FileUpLoadTask("FileUpLoadTask", initHttpClient());
		fileUpLoadTask.files = files;

		fileUpLoadTask.suyUserInfo = SuyApplication.getApplication().getSuyClient()
				.getSuyUserInfo();
		return m_taskMgr.addTask(fileUpLoadTask);
	}

	//下载文件
	public boolean filedownload(String[] files) {

		FileDownloadTask fileDownloadTask = new FileDownloadTask("FileDownloadTask", initHttpClient());
		fileDownloadTask.files = files;

		fileDownloadTask.suyUserInfo = SuyApplication.getApplication().getSuyClient()
				.getSuyUserInfo();
		return m_taskMgr.addTask(fileDownloadTask);
	}
	//下载文件
	public void cancelDownloadTask() {


		 m_taskMgr.cancelTask("FileDownloadTask");
	}


	// 注销
	public boolean logout() {
		// if (isOffline())
		// return false;
		//
		// LogoutTask task = new LogoutTask("LogoutTask", m_httpClient);
		// task.m_QQUser = m_QQUser;
		// return m_taskMgr.addTask(task);

		return true;
	}

	// 取消登录
	public void cancelLogin() {
		m_taskMgr.delAllTask();
		m_sendMsgTaskMgr.delAllTask();
		m_recvMsgTaskMgr.delAllTask();
	}
	
	public void setProxyHandler(Handler handler) {
		m_suyUserInfo.setProxyHandler(handler);
	}

	public void setNullProxyHandler(Handler handler) {
		m_suyUserInfo.setNullProxyHandler(handler);
	}
	
	public void handleProxyMsg(Message msg) {
		switch (msg.what) {
		case SuyCallBackMsg.LOGIN_RESULT:			// 登录返回消息
		case SuyCallBackMsg.LOGOUT_RESULT:			// 注销返回消息
		case SuyCallBackMsg.UPDATE_USER_INFO:		// 更新用户信息
		case SuyCallBackMsg.UPDATE_BUDDY_HEADPIC:	// 更新好友头像
		case SuyCallBackMsg.UPDATE_GMEMBER_HEADPIC:	// 更新群成员头像
		case SuyCallBackMsg.UPDATE_GROUP_HEADPIC:	// 更新群头像
			sendCallBackMsg(msg.what, msg.arg1, msg.arg2, msg.obj);
			break;
		/*case SuyCallBackMsg.UPDATE_BUDDY_LIST:		// 更新好友列表消息
			onUpdateBuddyList(msg);
			break;
		case SuyCallBackMsg.UPDATE_GROUP_LIST:		// 更新群列表消息
			onUpdateGroupList(msg);
			break;
		case SuyCallBackMsg.UPDATE_RECENT_LIST:		// 更新最近联系人列表消息
			onUpdateRecentList(msg);
			break;
		case SuyCallBackMsg.BUDDY_MSG:				// 好友消息
			onBuddyMsg(msg);
			break;
		case SuyCallBackMsg.GROUP_MSG:				// 群消息
			onGroupMsg(msg);
			break;
		case SuyCallBackMsg.SESS_MSG:				// 临时会话消息
			onSessMsg(msg);
			break;
		case SuyCallBackMsg.STATUS_CHANGE_MSG:		// 好友状态改变消息
			onStatusChangeMsg(msg);
			break;
		case SuyCallBackMsg.KICK_MSG:				// 被踢下线消息
			onKickMsg(msg);
			break;
		case SuyCallBackMsg.SYS_GROUP_MSG:			// 群系统消息
			onSysGroupMsg(msg);
			break;
		case SuyCallBackMsg.UPDATE_BUDDY_NUMBER:	// 更新好友号码
			onUpdateBuddyNumber(msg);
			break;
		case SuyCallBackMsg.UPDATE_GMEMBER_NUMBER:	// 更新群成员号码
			onUpdateGMemberNumber(msg);
			break;
		case SuyCallBackMsg.UPDATE_GROUP_NUMBER:	// 更新群号码
			onUpdateGroupNumber(msg);
			break;
		case SuyCallBackMsg.UPDATE_BUDDY_SIGN:		// 更新好友个性签名
			onUpdateBuddySign(msg);
			break;
		case SuyCallBackMsg.UPDATE_GMEMBER_SIGN:	// 更新群成员个性签名
			onUpdateGMemberSign(msg);
			break;
		case SuyCallBackMsg.UPDATE_BUDDY_INFO:		// 更新好友信息
			onUpdateBuddyInfo(msg);
			break;
		case SuyCallBackMsg.UPDATE_GMEMBER_INFO:	// 更新群成员信息
			onUpdateGMemberInfo(msg);
			break;
		case SuyCallBackMsg.UPDATE_GROUP_INFO:		// 更新群信息
			onUpdateGroupInfo(msg);
			break;
		case SuyCallBackMsg.UPDATE_C2CMSGSIG:		// 更新临时会话信令
			onUpdateC2CMsgSig(msg);
			break;
		case SuyCallBackMsg.CHANGE_STATUS_RESULT:	// 改变在线状态返回消息
			onChangeStatusResult(msg);
			break;

		case SuyCallBackMsg.INTERNAL_GETBUDDYDATA:
			onInternal_GetBuddyData(msg);
			break;
		case SuyCallBackMsg.INTERNAL_GETGROUPDATA:
			onInternal_GetGroupData(msg);
			break;
		case SuyCallBackMsg.INTERNAL_GETGMEMBERDATA:
			onInternal_GetGMemberData(msg);
			break;
		case SuyCallBackMsg.INTERNAL_GROUPID2CODE:
			onInternal_GroupId2Code(msg);
			break;*/
	
		default:
			break;
		}
	}
	
	public void setUser(String strUsername, String strPwd) {
		if (!isOffline())
			return;

		m_suyUserInfo.userName = strUsername;
		m_suyUserInfo.password = strPwd;
	}
	
	/**
	 * 是否离线状态
	 * @return
	 */
	public boolean isOffline() {
		return (SuyStatus.OFFLINE == m_suyUserInfo.m_nStatus) ? true : false;
	}

	/**
	 * 获取在线状态
	 * @return
	 */
	public int getStatus() {
		return m_suyUserInfo.m_nStatus;
	}
		
	/**
	 * 设置登录状态
	 * @param nStatus
	 */
	public void setLoginStatus(int nStatus) {
		m_suyUserInfo.m_nLoginStatus = nStatus;
	}
	
	public boolean sendCallBackMsg(int nMsgId, 
			int nArg1, int nArg2, Object obj) {
		return m_suyUserInfo.sendCallBackMsg(nMsgId, nArg1, nArg2, obj);
	}
	
	public void setNullCallBackHandler(Handler handler) {
		m_suyUserInfo.setNullCallBackHandler(handler);
	}
	
	public void setCallBackHandler(Handler handler) {
		m_suyUserInfo.setCallBackHandler(handler);
	}
	
	public SuyUserInfo getSuyUserInfo()
	{
		return m_suyUserInfo;
	}


	/**
	 * 获得验证码

	 * @return
	 */
	public static Drawable LoadvalidateImage(String strurl)
	{
		try
		{

			SuyHttpClient suyHttpClient = new SuyHttpClient(SuyApplication.getApplication()
			.getSuyClient().m_httpClient);

			suyHttpClient.openRequest(strurl, SuyHttpClient.REQ_METHOD_GET);
			suyHttpClient.sendRequest();


			InputStream input = suyHttpClient.getRespBodyDataInputStream();
			Drawable drawable = Drawable.createFromStream(input, "validateImage");
			input.close();
////
//			URL url = new URL(strurl);
//			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//			connection.addRequestProperty("deviceID",SuyApplication.getApplication()
//					.getUuid());
//			connection.addRequestProperty("deviceType", "02");
//
//			connection.setDoInput(true);
//			connection.connect();
//			InputStream input = connection.getInputStream();

//			Drawable drawable = Drawable.createFromStream(input, "validateImage");

			return drawable;


		}catch (Exception e) {
			System.out.println("Exc="+e);
			return null;
		}
	}

	/**
	 * 下载图片
	 * @param strurl
	 * @return
	 */
	public static byte[] DownloadImage(String strurl)
	{
		try
		{

			URL url = new URL(strurl);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("token",SuyApplication.getApplication()
					.getSuyClient().getSuyUserInfo().m_loginResult.m_strSKey);
			connection.setRequestProperty("deviceID",SuyApplication.getApplication()
					.getUuid());
			connection.setRequestProperty("deviceType","02");
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();

			byte[] buffer = new byte[connection.getContentLength()];


			byte[] temp=new byte[1024*50000];
			int readLen=0;
			int destPos=0;
			while((readLen=input.read(temp))>0) {
				System.arraycopy(temp, 0, buffer, destPos, readLen);
				destPos += readLen;
			}
			return buffer;


		}catch (Exception e) {
			System.out.println("Exc="+e);
			return null;
		}
	}


	/**
	 * 得到圆角图片
	 * @param drawable
	 * @param radius 宽度
	 * @return
	 */
	public static Drawable getCroppedBitmap(BitmapDrawable drawable, int radius) {
		Bitmap sbmp;
		Bitmap bmp = drawable.getBitmap();
		if(bmp.getWidth() != radius || bmp.getHeight() != radius)
			sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
		else
			sbmp = bmp;
		Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
				sbmp.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);


		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.parseColor("#BAB399"));
		canvas.drawCircle(sbmp.getWidth() / 2+0.7f, sbmp.getHeight() / 2+0.7f,
				sbmp.getWidth() / 2+0.1f, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(sbmp, rect, rect, paint);

		BitmapDrawable bitmapDrawable = new BitmapDrawable(output);
		return bitmapDrawable;
	}


}
