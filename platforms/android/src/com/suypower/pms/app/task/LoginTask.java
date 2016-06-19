package com.suypower.pms.app.task;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;


import org.apache.http.client.HttpClient;
import org.json.JSONObject;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.suypower.pms.app.DesEncrypter;
import com.suypower.pms.app.ReturnData;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.SuyCallBackMsg;
import com.suypower.pms.app.SuyClient;
import com.suypower.pms.app.SuyHttpClient;
import com.suypower.pms.app.SuyLoginResultCode;
import com.suypower.pms.app.configxml.AppConfig;
import com.suypower.pms.app.configxml.GlobalConfig;
import com.suypower.pms.app.protocol.data.LoginResult;
import com.suypower.pms.view.plugin.UpdatePlugin;

/**
 * 用户登陆线程
 * @author liuzeren
 *
 */
public class LoginTask extends HttpTask {

	public TaskManager m_recvMsgTaskMgr;
	
	public LoginTask(String strTaskName, HttpClient httpClient) {
		super(strTaskName, httpClient);
	}
	

	@Override
	public void doTask() {
		if (null == m_httpClient || null == suyUserInfo)
			return;
		String loginurl;
		try {
			//登录成功后，需要启动消息轮询机制
			Looper.prepare();
			loginurl= String.format("%1$sgetToken?username=%2$s&password=%3$s",
					GlobalConfig.globalConfig.getAuthUrl(),
					DesEncrypter.encrypt(suyUserInfo.userName),
					DesEncrypter.encrypt(suyUserInfo.password));

			Log.i("loginurl",loginurl);

			m_httpClient.openRequest(loginurl, SuyHttpClient.REQ_METHOD_GET);

			if (!m_httpClient.sendRequest())
			{
				sendLoginResultMsg(SuyLoginResultCode.FAILED,
						"网络无法连接");
				return;
			}

			byte[] buffer =  m_httpClient.getRespBodyData();
			if (buffer ==null)
			{
				sendLoginResultMsg(SuyLoginResultCode.FAILED,null);
				return;
			}

			String result =new String( buffer,"utf-8");
			Log.i("登陆信息返回:" ,result);
			JSONObject jsonObject=null;
			ReturnData returnData;
			try
			{
				//解析json
				jsonObject = new JSONObject(result);
				returnData = new ReturnData(jsonObject,true);
				if (returnData.getReturnCode() != 0)
				{
					if (returnData.getReturnCode() == 10005)
					{
						UpdatePlugin.updateAPK();
						sendLoginResultMsg(SuyLoginResultCode.PASSWORD_ERROR,
								returnData.getReturnMsg());

						return;
					}
					sendLoginResultMsg(SuyLoginResultCode.PASSWORD_ERROR,
							returnData.getReturnMsg());
					return;
				}

				SuyApplication.getApplication().getSuyDB().updateUserInfo(
						suyUserInfo.userName,suyUserInfo.password);
				LoginResult loginResult = new LoginResult();
				JSONObject ajax_data = returnData.getReturnData();
				loginResult.m_strSKey = ajax_data.getString("token");
				suyUserInfo.m_loginResult=loginResult;

				Log.i("wanc","1");
//				if (! UpdatePlugin.checkUpdate())
//				{
//					sendLoginResultMsg(SuyLoginResultCode.FAILED, "新版本需要更新");
//					return;
//				}



				loginurl= String.format("%1$sprofile",
						GlobalConfig.globalConfig.getApiUrl());
				m_httpClient.openRequest(loginurl, SuyHttpClient.REQ_METHOD_GET);
				if (!m_httpClient.sendRequest())
				{
					sendLoginResultMsg(SuyLoginResultCode.FAILED,
							"网络无法连接");
					return;
				}

				buffer =  m_httpClient.getRespBodyData();
				if (buffer ==null)
				{
					sendLoginResultMsg(SuyLoginResultCode.FAILED,null);
					return;
				}
				result =new String( buffer,"utf-8");
				Log.i("登陆信息返回:" ,result);

				//解析json
				jsonObject = new JSONObject(result);
				returnData = new ReturnData(jsonObject,true);
				if (returnData.getReturnCode() != 0)
				{
					sendLoginResultMsg(SuyLoginResultCode.PASSWORD_ERROR,
							returnData.getReturnMsg());
					return;
				}
				ajax_data = returnData.getReturnData();
				loginResult.m_strCompanyNo = ajax_data.getString("orgNo");
				loginResult.m_strCompany = ajax_data.getString("orgName");
				loginResult.m_strUserName = ajax_data.getString("name");
				loginResult.m_strDeparment = ajax_data.getString("deptName");
				loginResult.m_strDeparmentNo = ajax_data.getString("deptNo");
//				loginResult.m_strSex = ajax_data.getString("sex");
				String photobase64 = ajax_data.getString("picture");
				buffer = Base64.decode(photobase64, Base64.DEFAULT);
				InputStream inputStream = new ByteArrayInputStream(buffer);
				BitmapDrawable drawable = (BitmapDrawable)Drawable.createFromStream(inputStream, "photo");

				inputStream.close();
//				loginResult.m_strPhoto = SuyClient.getCroppedBitmap((BitmapDrawable)
//						SuyClient.LoadvalidateImage(
//								ajax_data.getString("headImage")), 120);
				suyUserInfo.m_loginResult=loginResult;


//				AppConfig.initAppConfig();



			}
			catch (Exception e)
			{
				e.printStackTrace();
				sendLoginResultMsg(SuyLoginResultCode.FAILED, "解析失败");
				return;
			}



			Thread.sleep(200);
			sendLoginResultMsg(SuyLoginResultCode.SUCCESS,null);
		} catch (Exception e) {
			e.printStackTrace();


		}
	}





	public static  String singlelogintask(SuyHttpClient suyHttpClient,String user,String pwd) {
		String Token;
		String loginurl;
		try {
			//登录成功后，需要启动消息轮询机制
			Looper.prepare();
			loginurl = String.format("%1$sgetToken?username=%2$s&password=%3$s",
					GlobalConfig.globalConfig.getAuthUrl(),
					DesEncrypter.encrypt(user),
					DesEncrypter.encrypt(pwd));

			Log.i("loginurl", loginurl);

			suyHttpClient.openRequest(loginurl, SuyHttpClient.REQ_METHOD_GET);

			if (!suyHttpClient.sendRequest()) {

				return "";
			}

			byte[] buffer = suyHttpClient.getRespBodyData();
			if (buffer == null)
				return "";

			String result = new String(buffer, "utf-8");
			Log.i("登陆信息返回:", result);
			JSONObject jsonObject = null;
			ReturnData returnData;

			//解析json
			jsonObject = new JSONObject(result);
			returnData = new ReturnData(jsonObject, true);

			JSONObject ajax_data = returnData.getReturnData();
			Token = ajax_data.getString("token");





		} catch (Exception e) {
			e.printStackTrace();
			return "";

		}
		return Token;
	}



	private void sendLoginResultMsg(int nRetCode,Object object) {
		if (m_bCancel)
			nRetCode = SuyLoginResultCode.USER_CANCEL_LOGIN;//用户取消登陆
		
		sendCallBackMessage(SuyCallBackMsg.LOGIN_RESULT, nRetCode, 0, object);
	}

}
