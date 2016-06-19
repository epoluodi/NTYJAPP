package org.apache.cordova.system;

import android.os.Message;
import android.util.Log;

import com.suypower.pms.app.ReturnData;
import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.app.SuyHttpClient;
import com.suypower.pms.app.configxml.GlobalConfig;
import com.suypower.pms.view.plugin.AjaxHttpPlugin;
import com.suypower.pms.view.plugin.BaseViewPlugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * app 系统插件
 * @author yxg
 *
 */
public class SystemRequest extends CordovaPlugin {
	
	private static final String TAG = "SystemRequest";

    private CallbackContext callbackContext = null;





    @Override
    public boolean execute(String action, JSONArray args,
    		CallbackContext callbackContext) throws JSONException {
    	this.callbackContext = callbackContext;


		//初始插件
		AjaxHttpPlugin ajaxHttpPlugin ;
		//http初始
		SuyHttpClient suyHttpClient ;
		ReturnData returnData;
		Message message=new Message();

		message.arg1= this.webView.cordovaWebViewId; //前端webview  id

		//指示器
		if (action.equals("dialog")) {
			message.obj =args.getJSONObject(0);//json数据
			this.cordova.onMessage(BaseViewPlugin.DIALOG_ACTION, message);
		}

		//web返回
		if (action.equals("goback")) {
			message.obj=null;
			this.cordova.onMessage(BaseViewPlugin.GOBACK_ACTION, message);
		}
		//指示器
		if (action.equals("info")) {
			message.obj=args.getJSONObject(0);
			this.cordova.onMessage(BaseViewPlugin.INFO_ACTION, message);
		}
		//web标题
		if (action.equals("title")) {
			message.obj=args.getJSONObject(0);
			this.cordova.onMessage(BaseViewPlugin.TITLE_ACTION, message);
		}

		//获得APP版本信息
		if (action.equals("getAppInfo")) {
//			message.obj=args.getJSONObject(0);
			try
			{
				JSONObject jsonObject = new JSONObject();

				jsonObject.put("appVer","APP版本：" +  SuyApplication.getApplication().AppVerName());
				jsonObject.put("updateDT", "更新日期：" + GlobalConfig.globalConfig.getUpdateDT());

				callbackContext.success(jsonObject);
			}
			catch (Exception e)
			{e.printStackTrace();}
		}


		//获得APP业务信息集
		if (action.equals("getAppInfos")) {
//			message.obj=args.getJSONObject(0);
			try
			{
				JSONArray jsonArray=new JSONArray();
				JSONObject jsonObject ;

				jsonObject = new JSONObject();
				jsonObject.put("appName", "APP应用");
				jsonObject.put("appCode", "000000");
				jsonObject.put("appId", "100000000000");
				jsonArray.put(jsonObject);


				for (int i = 0;
					 i<GlobalConfig.globalConfig.getAppInfos().length;i++)
				{
					jsonObject = new JSONObject();
					jsonObject.put("appName",
							GlobalConfig.globalConfig.getAppConfigs()[i].getAppname());
					jsonObject.put("appCode",
							GlobalConfig.globalConfig.getAppConfigs()[i].getAppcode());
					jsonObject.put("appId",
							GlobalConfig.globalConfig.getAppConfigs()[i].getAppID());
					jsonArray.put(jsonObject);

				}


				callbackContext.success(jsonArray);

			}
			catch (Exception e)
			{e.printStackTrace();}
		}



		//账号信息
		if (action.equals("getaccount")) {

			try
			{
				JSONObject jsonObject = new JSONObject();

				jsonObject.put("orgNo", SuyApplication.getApplication().getSuyClient().getSuyUserInfo()
						.m_loginResult.m_strCompanyNo);
				jsonObject.put("orgName", SuyApplication.getApplication().getSuyClient().getSuyUserInfo()
						.m_loginResult.m_strCompany);
				jsonObject.put("name", SuyApplication.getApplication().getSuyClient().getSuyUserInfo()
						.m_loginResult.m_strUserName);
				jsonObject.put("deptName", SuyApplication.getApplication().getSuyClient().getSuyUserInfo()
						.m_loginResult.m_strDeparment);
				jsonObject.put("deptNo", SuyApplication.getApplication().getSuyClient().getSuyUserInfo()
						.m_loginResult.m_strDeparmentNo);
				callbackContext.success(jsonObject);
			}
			catch (Exception e)
			{e.printStackTrace();}
		}


		//获得APP版本信息
		if (action.equals("submitSuggestion")) {

			ajaxHttpPlugin = new AjaxHttpPlugin();
			suyHttpClient = ajaxHttpPlugin.initHttp();
			try
			{

				//组合请求
				UrlEncodedFormEntity urlEncodedFormEntity =ajaxHttpPlugin.postUrlString(args);
				if (urlEncodedFormEntity == null)
				{

					this.callbackContext.error("调用失败");
					return false;
				}


				String uploadurl= String.format("%1$ssubmitSuggestion",
						GlobalConfig.globalConfig.getApiUrl());
				//打开请求
				suyHttpClient.openRequest(uploadurl,
						SuyHttpClient.REQ_METHOD_POST);
				suyHttpClient.setEntity(urlEncodedFormEntity);
				if (!suyHttpClient.sendRequest())//发送请求
				{

					this.callbackContext.error("发送失败");
					return false;
				}

				byte[] buffer = suyHttpClient.getRespBodyData();
				if (buffer == null) {
					this.callbackContext.error("返回信息错误");
					return false;
				}

				String result = new String(buffer, "utf-8");

				Log.i("上传返回", result);
				JSONObject jsonObject = new JSONObject(result);
				returnData =new ReturnData(jsonObject,true);


				if (returnData.getReturnCode() != 0) {
					this.callbackContext.error(returnData.getReturnMsg());
					return false;
				}

				callbackContext.success(returnData.getReturnMsg());
				suyHttpClient.closeRequest();
			}
			catch (Exception e)
			{e.printStackTrace();
				callbackContext.error("提交失败");
			}
		}



		//获得反馈建议信息列表
		if (action.equals("getSuggestions")) {

			ajaxHttpPlugin = new AjaxHttpPlugin();
			suyHttpClient = ajaxHttpPlugin.initHttp();
			try
			{

				JSONObject jsonObjectparams = (JSONObject)args.get(0);


				String uploadurl= String.format("%1$squerySuggestions?pageNo=%2$s&pageSize=%3$s",
						GlobalConfig.globalConfig.getApiUrl(),
						jsonObjectparams.getString("pageNo"),
						jsonObjectparams.getString("pageSize"));
				//打开请求
				suyHttpClient.openRequest(uploadurl,
						SuyHttpClient.REQ_METHOD_POST);

				if (!suyHttpClient.sendRequest())//发送请求
				{

					this.callbackContext.error("接收信息失败");
					return false;
				}

				byte[] buffer = suyHttpClient.getRespBodyData();
				if (buffer == null) {
					this.callbackContext.error("返回信息错误");
					return false;
				}

				String result = new String(buffer, "utf-8");
				Log.i("上传返回", result);
				JSONObject jsonObject = new JSONObject(result);
				returnData =new ReturnData(jsonObject,true);
				if (returnData.getReturnCode() != 0) {
					this.callbackContext.error(returnData.getReturnMsg());
					return false;
				}

				callbackContext.success(returnData.getReturnData());
				suyHttpClient.closeRequest();
			}
			catch (Exception e)
			{e.printStackTrace();
				this.callbackContext.error("返回信息错误");}
		}


		//获得反馈建议详细信息
		if (action.equals("getSuggestionDetail")) {

			ajaxHttpPlugin = new AjaxHttpPlugin();
			suyHttpClient = ajaxHttpPlugin.initHttp();
			try
			{

				JSONObject jsonObjectparams = (JSONObject)args.get(0);

				String uploadurl= String.format("%1$squerySuggestionDetail?suggestionId=%2$s",
						GlobalConfig.globalConfig.getApiUrl(),
						jsonObjectparams.get("suggestionId"));
				//打开请求
				suyHttpClient.openRequest(uploadurl,
						SuyHttpClient.REQ_METHOD_GET);

				if (!suyHttpClient.sendRequest())//发送请求
				{

					this.callbackContext.error("接收信息失败");
					return false;
				}

				byte[] buffer = suyHttpClient.getRespBodyData();
				if (buffer == null) {
					this.callbackContext.error("返回信息错误");
					return false;
				}

				String result = new String(buffer, "utf-8");
				Log.i("上传返回", result);
				JSONObject jsonObject = new JSONObject(result);
				returnData =new ReturnData(jsonObject,true);
				if (returnData.getReturnCode()!=0) {
					this.callbackContext.error(returnData.getReturnMsg());
					return false;
				}

				callbackContext.success(returnData.getReturnData());
				suyHttpClient.closeRequest();
			}
			catch (Exception e)
			{e.printStackTrace();
				this.callbackContext.error("返回信息错误");}
		}



    	return true;
    }
}
