package com.suypower.pms.app.task;

import android.os.Looper;
import android.util.Log;

import com.suypower.pms.app.ReturnData;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.SuyCallBackMsg;
import com.suypower.pms.app.SuyHttpClient;
import com.suypower.pms.app.configxml.GlobalConfig;
import com.suypower.pms.view.plugin.camera.CameraPlugin;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 上传照片 任务类
 * @author YXG
 *
 */
public class UpLoadPhotoTask extends HttpTask {

	public int photos;
	public JSONArray photofiles;
	JSONArray uploadResult;

	public UpLoadPhotoTask (String strTaskName, HttpClient httpClient) {
		super(strTaskName, httpClient);

	}



	
	@Override
	public void doTask() {
		if (null == m_httpClient )
			return;
		
		try {

			Looper.prepare();
			//组合url地址
			String loginurl= String.format("%1$suploadImage",
					GlobalConfig.globalConfig.getAuthUrl());

			m_httpClient.openRequest(loginurl, SuyHttpClient.REQ_METHOD_POST);


			String filename;
			String Base64image;
			UrlEncodedFormEntity urlEncodedFormEntity;
			BasicNameValuePair basicNameValuePair;
			List<NameValuePair> pairList;
			JSONObject jsonObject;
			uploadResult = new JSONArray();
			for (int i =0; i <photos;i++)
			{
				sendResultMsg(CameraPlugin.SUCCESS,
						String.format("%1$s/%2$s",i+1,photos));
				filename = UUID.randomUUID().toString() + ".jpg";
				if (photofiles == null)
				{
					sendResultMsg(CameraPlugin.FAIL,null);
					return;
				}


				//图片赚base64
				Base64image = CameraPlugin.GetPhotoBase64( photofiles.get(i).toString().replace("file:///",""));
				pairList = new ArrayList<NameValuePair>();
				basicNameValuePair = new BasicNameValuePair("key",
						SuyApplication.getApplication().getSuyClient().getSuyUserInfo()
				.m_loginResult.m_strSKey);
				pairList.add(basicNameValuePair);
				basicNameValuePair = new BasicNameValuePair("imageName",
						filename);
				pairList.add(basicNameValuePair);
				basicNameValuePair = new BasicNameValuePair("image",
						Base64image);
				pairList.add(basicNameValuePair);
				urlEncodedFormEntity = new UrlEncodedFormEntity(pairList, HTTP.UTF_8);
				m_httpClient.setEntity(urlEncodedFormEntity);
				if (!m_httpClient.sendRequest())
				{
					sendResultMsg(CameraPlugin.FAIL,null);
					return;
				}

				byte[] buffer =  m_httpClient.getRespBodyData();
				if (buffer ==null)
				{
					sendResultMsg(CameraPlugin.FAIL, null);
					return;
				}

				String result =new String(buffer,"utf-8");
				Log.i("上传返回",result);
				jsonObject = new JSONObject(result);
				ReturnData returnData = new ReturnData(jsonObject,true);
				if (returnData.getReturnCode() != 1000)
				{
					sendResultMsg(CameraPlugin.FAIL, jsonObject.get("returnMsg"));
					return;
				}
				//imageid 放入队列
				uploadResult.put(returnData.getReturnData().getString("imageId"));


			}

			sendResultMsg(CameraPlugin.UPLOAD_FINISH, uploadResult);
		} catch (Exception e) {
			e.printStackTrace();
			sendResultMsg(CameraPlugin.FAIL, null);
		} finally {
			
		}
	}





	private void sendResultMsg(int nRetCode,Object object) {
		if (m_bCancel)
			nRetCode = CameraPlugin.FAIL;//用户取消登陆
		
		sendCallBackMessage(SuyCallBackMsg.UPLOADPHOTO_RESULT, nRetCode, 0, object);
	}

}
