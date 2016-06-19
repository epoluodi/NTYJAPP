package org.apache.cordova.word;

import android.os.Message;

import com.suypower.pms.app.SuyApplication;
import com.suypower.pms.view.plugin.BaseViewPlugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;

/**
 * optionmenu Word 操作控制类
 * @author yxg
 *
 */
public class WordRequest extends CordovaPlugin {
	
	private static final String TAG = "WordRequest";

    private CallbackContext callbackContext = null;





    @Override
    public boolean execute(String action, JSONArray args,
    		CallbackContext callbackContext) throws JSONException {
    	this.callbackContext = callbackContext;




		Message message=new Message();
		message.obj =args.getJSONObject(0);//json数据
		message.arg1= this.webView.cordovaWebViewId; //前端webview  id

		if (action.equals("create"))
			this.cordova.onMessage(BaseViewPlugin.WORD_ACTION,message);
		if (action.equals("sign"))
			this.cordova.onMessage(BaseViewPlugin.SIGNATURE_ACTION,message);
		if (action.equals("preview"))
			this.cordova.onMessage(BaseViewPlugin.WORDPREVIEW_ACTION,message);
		if (action.equals("clear"))
		{
			try
			{
				JSONObject jsonObject = args.getJSONObject(0);
				final String uuid = jsonObject.getString("uuid");
				FileFilter fileFilter = new FileFilter() {
					@Override
					public boolean accept(File file) {
						if (file.getAbsolutePath().contains(uuid))
							return true;
						else
							return false;
					}
				};
				File file = SuyApplication.getApplication().getCacheDir();
				File[] files = file.listFiles(fileFilter);
				for (File f:files)
				{
					f.delete();
				}
				callbackContext.success();

			}
			catch (Exception e)
			{e.printStackTrace();

			callbackContext.error("失败");
			}
		}




    	//异步请求
//    	this.callbackContext.success("OK");

//		this.callbackContext.error("失败");

    	
    	return true;
    }
}
