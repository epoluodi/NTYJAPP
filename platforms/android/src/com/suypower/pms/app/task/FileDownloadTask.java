package com.suypower.pms.app.task;

import android.util.Base64;
import android.util.Log;

import com.suypower.pms.app.ReturnData;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.SuyCallBackMsg;
import com.suypower.pms.app.SuyHttpClient;
import com.suypower.pms.app.configxml.GlobalConfig;
import com.suypower.pms.view.plugin.camera.CameraPlugin;
import com.suypower.pms.view.plugin.fileEx.FilePlugin;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 上传照片 任务类
 * @author YXG
 *
 */
public class FileDownloadTask extends HttpTask {


	public String[] files;



	public FileDownloadTask(String strTaskName, HttpClient httpClient) {
		super(strTaskName, httpClient);

	}



	
	@Override
	public void doTask() {
		if (null == m_httpClient )
			return;
		
		try {


			//组合url地址
			String uploadurl= String.format("%1$sdownload",
					GlobalConfig.globalConfig.getApiUrl());
//			String uploadurl ="http://192.168.0.178/stereo-dispatch/api/upload";
			m_httpClient.openRequest(uploadurl, SuyHttpClient.REQ_METHOD_POST);



			String Base64file;
			UrlEncodedFormEntity urlEncodedFormEntity;
			BasicNameValuePair basicNameValuePair;
			List<NameValuePair> pairList;
			JSONObject jsonObject;
			JSONArray jsonArray = new JSONArray();

			for (int i=0;i<files.length ;i++) {

				pairList = new ArrayList<NameValuePair>();
				basicNameValuePair = new BasicNameValuePair("mediaID",
						files[i]);
				pairList.add(basicNameValuePair);

				urlEncodedFormEntity = new UrlEncodedFormEntity(pairList, HTTP.UTF_8);
				m_httpClient.setEntity(urlEncodedFormEntity);
				if (!m_httpClient.sendRequest()) {
					sendResultMsg(FilePlugin.FAIL, "网络请求失败");
					return;
				}

				byte[] buffer = m_httpClient.getRespBodyData();
				if (buffer == null) {
					sendResultMsg(FilePlugin.FAIL, "数据接收异常");
					return;
				}

				String result = new String(buffer, "utf-8");
				Log.i("上传返回", result);
				jsonObject = new JSONObject(result);
				ReturnData returnData = new ReturnData(jsonObject,true);
				if (returnData.getReturnCode() != 0) {
					sendResultMsg(FilePlugin.FAIL, returnData.getReturnMsg());
					return;
				}

				Base64file = returnData.getReturnData().getString("fileData");
				byte[] imagebuffer = Base64.decode(Base64file, Base64.DEFAULT);


				String filename = returnData.getReturnData().
						getString("fileName");

				String uuid = filename.substring(0,
						filename.indexOf("."));
				String ext = filename.substring(filename.indexOf("."), filename.length());
				File file;
				if (ext.equals(".docx"))
					file = new File(SuyApplication.getApplication().getCacheDir() +
						File.separator + uuid + "_iamge" + ext.toLowerCase());
				else
					file = new File(SuyApplication.getApplication().getCacheDir() +
						File.separator + uuid + ext.toLowerCase());

				OutputStream outputStream = new FileOutputStream(file);
				outputStream.write(imagebuffer);
				outputStream.flush();
				outputStream.close();


				jsonArray.put(uuid);
				if (m_bCancel)
					return;
				sendResultMsg(FilePlugin.SUCCESS, String.format("%1$s/%2$s", i + 1, files.length));
			}
			if (m_bCancel)
				return;


			sendResultMsg(FilePlugin.UPLOAD_FINISH, jsonArray);
		}
		catch (JSONException jsonEx)
		{
			jsonEx.printStackTrace();
			sendResultMsg(FilePlugin.FAIL, "返回结果错误");
		}
		catch (Exception e) {
			e.printStackTrace();
			sendResultMsg(FilePlugin.FAIL, "执行异常");
		} finally {
			
		}
	}

	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot >-1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}



	byte[] GetFileBytes(String filepath)
	{
		byte[] buffer;
		try {
			FileInputStream fileInputStream =new FileInputStream(filepath);
			buffer= new byte[fileInputStream.available()];
			fileInputStream.read(buffer);
			fileInputStream.close();
			return buffer;
		}
		catch (Exception e)
		{e.printStackTrace();
		return null;}
	}



	private void sendResultMsg(int nRetCode,Object object) {
		if (m_bCancel)
			nRetCode = CameraPlugin.FAIL;//用户取消登陆
		
		sendCallBackMessage(SuyCallBackMsg.UPLOADPHOTO_RESULT, nRetCode, 0, object);
	}

}
