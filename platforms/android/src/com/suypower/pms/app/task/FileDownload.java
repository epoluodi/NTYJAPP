package com.suypower.pms.app.task;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import com.suypower.pms.app.ReturnData;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.SuyCallBackMsg;
import com.suypower.pms.app.SuyHttpClient;
import com.suypower.pms.app.configxml.GlobalConfig;
import com.suypower.pms.view.plugin.camera.CameraPlugin;
import com.suypower.pms.view.plugin.fileEx.FilePlugin;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 上传照片 任务类
 * @author YXG
 *
 */
public class FileDownload extends BaseTask {

	InterfaceTask interfaceTask;
	public static final int Base64File=0;
	public static final int StreamFile=1;

	Boolean isCancel;
	int filetype;
	public String mediaid;
	public String imgamub="";
	public String mediatype;
	public Object flag;
	@Override
	public void stopTask() {
		m_httpClient.closeRequest();
		m_httpClient = null;
		isCancel= true;
	}

	@Override
	public void startTask() {
		m_ThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					switch (filetype)
					{
						case Base64File:
							base64DownloadTask();
							break;
						case StreamFile:
							streamDownLoadFile();
							break;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	String[] files;

	public void setFiles(String[] files) {
		this.files = files;
	}

	public FileDownload(InterfaceTask interfaceTask, int FileType) {
		super();
		this.interfaceTask =interfaceTask;
		this.filetype=FileType;

	}






	

	public void base64DownloadTask() {
		if (null == m_httpClient )
			return;
		Message message;
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

				if (isCancel)
					return;
				pairList = new ArrayList<NameValuePair>();
				basicNameValuePair = new BasicNameValuePair("mediaID",
						files[i]);
				pairList.add(basicNameValuePair);

				urlEncodedFormEntity = new UrlEncodedFormEntity(pairList, HTTP.UTF_8);

				m_httpClient.setEntity(urlEncodedFormEntity);
				if (!m_httpClient.sendRequest()) {


					if (interfaceTask !=null) {
						message=new Message();
						message.what=DownloadFILETask;
						message.arg2=Base64File;
						message.arg1=DOWNLOAD_FAIL;

						interfaceTask.TaskResultForMessage(message);
					}




					return;
				}

				byte[] buffer = m_httpClient.getRespBodyData();
				if (buffer == null) {
					if (interfaceTask !=null) {
						message=new Message();
						message.what=DownloadFILETask;
						message.arg2=Base64File;
						message.arg1=DOWNLOAD_FAIL;

						interfaceTask.TaskResultForMessage(message);
					}
					return;
				}

				String result = new String(buffer, "utf-8");
				Log.i("上传返回", result);
				jsonObject = new JSONObject(result);
				ReturnData returnData = new ReturnData(jsonObject,true);
				if (returnData.getReturnCode() != 0) {
					if (interfaceTask !=null) {
						message=new Message();
						message.what=DownloadFILETask;
						message.arg2=Base64File;
						message.arg1=DOWNLOAD_FAIL;

						interfaceTask.TaskResultForMessage(message);
					}
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
				if (isCancel)
					return;
				if (interfaceTask !=null) {
					message=new Message();
					message.what=DownloadFILETask;
					message.arg2=Base64File;
					message.arg1=DOWNLOAD_FINISH_SINGLE;
					message.obj = files[i];

					interfaceTask.TaskResultForMessage(message);
				}
			}
			if (isCancel)
				return;

			if (interfaceTask !=null) {
				message=new Message();
				message.what=DownloadFILETask;
				message.arg2=Base64File;
				message.arg1=DOWNLOAD_FINISH_ALL;
				interfaceTask.TaskResultForMessage(message);
			}

		}
		catch (Exception e) {
			e.printStackTrace();
			if (interfaceTask !=null) {
				message=new Message();
				message.what=DownloadFILETask;
				message.arg2=Base64File;
				message.arg1=DOWNLOAD_FAIL;
				interfaceTask.TaskResultForMessage(message);
			}
		}
	}







	public void streamDownLoadFile() {

		String uploadurl = String.format("%1$sdownload/%2$s",
				GlobalConfig.globalConfig.getApiUrl(),mediaid);

		m_httpClient.openRequest(uploadurl, SuyHttpClient.REQ_METHOD_POST);
//		m_httpClient.setPostValuesForKey("mediaid", mediaid);
//		m_httpClient.setPostValuesForKey("mediatype", mediatype);
//		m_httpClient.setPostValuesForKey("isaumb", imgamub);
//		m_httpClient.setEntity(m_httpClient.getPostData());

		m_httpClient.sendRequest();
		Message message = new Message();
		HttpEntity httpEntity = m_httpClient.getHttpResponse().getEntity();
		if (httpEntity == null) {
			message.what = DownloadFILETask;
			message.arg1 = FAILED;
			message.arg2= StreamFile;
			message.obj = flag;
			if (interfaceTask !=null) {
				interfaceTask.TaskResultForMessage(message);
			}
		}
		InputStream inStream;
		ByteArrayOutputStream outStream;
		byte[] bufferfile = null;
		try {
			inStream = httpEntity.getContent();
			outStream = new ByteArrayOutputStream();
			Log.i("下载文件大小inStream:" ,String.valueOf(inStream.available()));
			int maxbuff = 1024 * 5000;
			byte[] buffer = new byte[maxbuff];
			int len = 0;

			if (inStream == null) {
				message.what = DownloadFILETask;
				message.arg1 = FAILED;
				message.arg2= StreamFile;

				message.obj = flag;
				if (interfaceTask !=null) {
					interfaceTask.TaskResultForMessage(message);
				}
			}
			System.gc();
			while ((len = inStream.read(buffer)) !=-1) {
				outStream.write(buffer,0,len);

			}

			bufferfile = outStream.toByteArray();
			outStream.close();
			inStream.close();
			if (bufferfile==null)
				throw  new Exception();
			Log.i("下载文件大小:" ,String.valueOf(bufferfile.length));
//			SuyApplication.getApplication().getCacheDir()
			File fIle = new File(SuyApplication.getApplication().getCacheDir(),mediaid + imgamub+mediatype);
			if (fIle.exists())
				fIle.delete();
			FileOutputStream fileOutputStream = new FileOutputStream(fIle);
			fileOutputStream.write(bufferfile);
			fileOutputStream.close();
			message.what = DownloadFILETask;
			message.arg1 = SUCCESS;
			message.arg2= StreamFile;
			message.obj = flag;
			Bundle bundle=new Bundle();
			bundle.putString("mediaid",mediaid);
			message.setData(bundle);
			if (interfaceTask !=null) {
				interfaceTask.TaskResultForMessage(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
			message.what = DownloadFILETask;
			message.arg1 = FAILED;
			message.arg2= StreamFile;
			message.obj = flag;
			if (interfaceTask !=null) {
				interfaceTask.TaskResultForMessage(message);
			}
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





}
