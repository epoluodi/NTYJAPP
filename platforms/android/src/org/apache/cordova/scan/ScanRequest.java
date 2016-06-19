package org.apache.cordova.scan;

import com.suypower.pms.view.plugin.BaseViewPlugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 *拍照插件
 * @author yxg
 *
 */
public class ScanRequest extends CordovaPlugin {
	
	private static final String TAG = "ScanRequest";




    @Override
    public boolean execute(String action, JSONArray args,
    		CallbackContext callbackContext) throws JSONException {

		this.callbackContext = callbackContext;

		this.cordova.onMessage(BaseViewPlugin.SCAN_ACTION,this);


//    	//异步请求
//    	this.callbackContext.success("OK");

//		this.callbackContext.error("失败");

    	
    	return true;
    }
}
