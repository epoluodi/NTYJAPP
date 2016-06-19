package org.apache.cordova.anysc;

import android.util.Log;

import com.suypower.pms.app.ReturnData;
import com.suypower.pms.app.SuyHttpClient;
import com.suypower.pms.app.configxml.GlobalConfig;
import com.suypower.pms.view.plugin.AjaxHttpPlugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 封装消息异步请求插件
 * @author liuzeren
 *
 */
public class AnyscRequest extends CordovaPlugin {
	
	private static final String TAG = "AnyscRequest";

    private CallbackContext callbackContext = null;
    
    @Override
    public boolean execute(String action, JSONArray args,
    		CallbackContext callbackContext) throws JSONException {
    	this.callbackContext = callbackContext;



		//初始插件
		AjaxHttpPlugin ajaxHttpPlugin = new AjaxHttpPlugin();
		//http初始
		SuyHttpClient suyHttpClient =  ajaxHttpPlugin.initHttp();

		if (suyHttpClient == null)
		{
//			jsonObject = new JSONObject();
//			jsonObject.put("return","1001");
//			jsonObject.put("returnMsg","初始化失败");
			this.callbackContext.error("调用失败");
			return false;
		}

		if (action.equals("get"))
		{
			//组合请求
			String url =ajaxHttpPlugin.getUrlString(args);
			if (url.equals(""))
			{
				//			jsonObject = new JSONObject();
//			jsonObject.put("return","1001");
//			jsonObject.put("returnMsg","初始化失败");
				this.callbackContext.error("调用失败");
				return false;
			}
			//打开请求

			suyHttpClient.openRequest(url, SuyHttpClient.REQ_METHOD_GET);
			if (!suyHttpClient.sendRequest())//发送请求
			{
				//			jsonObject = new JSONObject();
//			jsonObject.put("return","1001");
//			jsonObject.put("returnMsg","初始化失败");
				this.callbackContext.error("发送失败");
				return false;
			}
			this.callbackContext.success();
		}

		if (action.equals("post"))
		{
			//组合请求
			UrlEncodedFormEntity urlEncodedFormEntity =ajaxHttpPlugin.postUrlString(args);
			if (urlEncodedFormEntity == null)
			{
				//			jsonObject = new JSONObject();
//			jsonObject.put("return","1001");
//			jsonObject.put("returnMsg","初始化失败");
				this.callbackContext.error("调用失败");
				return false;
			}

			//打开请求
			suyHttpClient.openRequest(GlobalConfig.globalConfig.getAppUrl(),
					SuyHttpClient.REQ_METHOD_POST);
			suyHttpClient.setEntity(urlEncodedFormEntity);
			if (!suyHttpClient.sendRequest())//发送请求
			{
				//			jsonObject = new JSONObject();
//			jsonObject.put("return","1001");
//			jsonObject.put("returnMsg","初始化失败");
				this.callbackContext.error("发送失败");
				return false;
			}
		}


		//获得返回数据
		byte[] buffer =  suyHttpClient.getRespBodyData();
		if (buffer ==null)
		{
			//			jsonObject = new JSONObject();
//			jsonObject.put("return","1001");
//			jsonObject.put("returnMsg","初始化失败");
			this.callbackContext.error("接受数据失败");
			return false;
		}

		try {
			String result = new String(buffer, "utf-8");
			Log.i("信息返回:", result);
			JSONObject jsonObject=new JSONObject(result);
			ReturnData returnData =new ReturnData(jsonObject,true);
			if (returnData.getReturnCode() ==0)
				this.callbackContext.success(returnData.getReturnData());
			else
			{
				this.callbackContext.error(returnData.getReturnMsg());
			}
			return true;
		}
		catch (Exception e)
		{e.printStackTrace();
//			jsonObject = new JSONObject();
//			jsonObject.put("return","1001");
//			jsonObject.put("returnMsg","初始化失败");
			this.callbackContext.error("数据转换失败");
			return false;
		}

//		this.callbackContext.error("失败");

//    	return super.execute(action, args, callbackContext);
    }
}
