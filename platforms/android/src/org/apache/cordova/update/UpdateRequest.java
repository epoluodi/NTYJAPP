package org.apache.cordova.update;

import android.os.Message;

import com.suypower.pms.view.plugin.BaseViewPlugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * 业务升级更新
 * @author yxg
 *
 */
public class UpdateRequest extends CordovaPlugin {
	
	private static final String TAG = "UpdateRequest";

    private CallbackContext callbackContext = null;





    @Override
    public boolean execute(String action, JSONArray args,
    		CallbackContext callbackContext) throws JSONException {
    	this.callbackContext = callbackContext;




		Message message=new Message();
		message.obj =args.getJSONObject(0);//json数据
		message.arg1= this.webView.cordovaWebViewId; //前端webview  id

		if (action.equals("update"))
			this.cordova.onMessage(BaseViewPlugin.UPDATE_ACTION,message);





    	//异步请求
//    	this.callbackContext.success("OK");

//		this.callbackContext.error("失败");

    	
    	return true;
    }
}
