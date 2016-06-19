package org.apache.cordova.fileEx;

import android.util.Log;

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
public class FileExRequest extends CordovaPlugin {
	
	private static final String TAG = "FileExRequest";



    @Override
    public boolean execute(String action, JSONArray args,
    		CallbackContext callbackContext) throws JSONException {
    	this.callbackContext = callbackContext;
		JSONObject jsonObject = args.getJSONObject(0);
		String fileType;
		Log.i("callbackContext", callbackContext.getCallbackId());
		if (action.equals("create")) {

			fileType = jsonObject.getString("fileType");
			if (fileType.equals("WORD")) {
				this.jsondata = jsonObject;
				this.cordova.onMessage(BaseViewPlugin.WORD_ACTION, this);
			}
		}
		if (action.equals("edit")) {
			fileType = jsonObject.getString("fileType");
			if (fileType.equals("WORD")) {
				this.jsondata =jsonObject;
				this.cordova.onMessage(BaseViewPlugin.SIGNATURE_ACTION, this);

			}
		}
		if (action.equals("preview")) {
			this.jsondata = args.getJSONObject(0);
			this.cordova.onMessage(BaseViewPlugin.WORDPREVIEW_ACTION, this);
		}
		if (action.equals("upload")) {
			this.jsondata = args.getJSONObject(0);
			this.cordova.onMessage(BaseViewPlugin.UPLOADFILE_ACTION, this);
		}
		if (action.equals("download")) {
			this.jsondata = args.getJSONObject(0);
			this.cordova.onMessage(BaseViewPlugin.FILEDOWNLOAD_ACTION, this);
		}
		if (action.equals("cancelDownload")) {
//			this.jsondata = args.getJSONObject(0);
			this.cordova.onMessage(BaseViewPlugin.FILECANCELDOWNLOAD_ACTION, this);
		}




		if (action.equals("release"))
		{
			try
			{

				final String uuid = jsonObject.getString("file");
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
