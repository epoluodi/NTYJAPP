package org.apache.cordova.menu;

import android.os.Message;

import com.suypower.pms.view.plugin.BaseViewPlugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * optionmenu 菜单插件
 * @author yxg
 *
 */
public class MenuRequest extends CordovaPlugin {
	
	private static final String TAG = "MenuRequest";

    private CallbackContext callbackContext = null;





    @Override
    public boolean execute(String action, JSONArray args,
    		CallbackContext callbackContext) throws JSONException {
    	this.callbackContext = callbackContext;


		//通知插件加载菜单
		Message message=new Message();
		message.obj =args;//json数据
		message.arg1= this.webView.cordovaWebViewId; //前端webview  id

		this.cordova.onMessage(BaseViewPlugin.MENU_ACTION,message);

    	//异步请求
//    	this.callbackContext.success("OK");

//		this.callbackContext.error("失败");

    	
    	return true;
    }
}
