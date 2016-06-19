package com.suypower.pms.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.suypower.pms.R;
import com.suypower.pms.view.plugin.BaseViewPlugin;
import com.suypower.pms.view.plugin.CordovaWebViewPlugin;
import com.suypower.pms.view.plugin.CustomPopWindowPlugin;
import com.suypower.pms.view.plugin.fragmeMager.FragmentName;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Stereo on 16/3/17.
 */
public abstract class BaseActivity extends Activity implements CordovaInterface {

    CordovaWebViewPlugin cordovaWebViewPlugin;
    ProgressBar progressBar;
    Message msg = null;
    LinearLayout linearLayout;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();



    abstract void OnTitle(String title);

    @Override
    public void startActivityForResult(CordovaPlugin command, Intent intent, int requestCode) {

    }

    @Override
    public void setActivityResultCallback(CordovaPlugin plugin) {

    }


    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public Object onMessage(String id, Object data) {

        if (id.equals("onProgressChanged")) {
            int p = (Integer)data;

            if (p != 100)
            {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(p);
            }
            else
            {
                progressBar.setProgress(0);
                progressBar.setVisibility(View.GONE);
            }
            return null;
        }
        if (id.equals("onReceivedTitle"))
        {
            OnTitle((String)data);
            return null;
        }
        if (id.equals(BaseViewPlugin.GOBACK_ACTION)) //
        {
            cordovaWebViewPlugin.cordovaWebView.goBack();
            return null;
        }

        if (id.equals(BaseViewPlugin.INFO_ACTION)) //
        {

            try {
                msg = (Message)data;
                JSONObject jsonObject = (JSONObject)msg.obj;
                String title = jsonObject.getString("title");
                if (title.equals(""))
                    title = "信息";
                String info = jsonObject.getString("info");
                int timeout= jsonObject.getInt("timeout");

                CustomPopWindowPlugin.ShowPopWindow(linearLayout,getLayoutInflater(),
                        title,info,timeout);

                return null;

            }
            catch (Exception e)
            {e.printStackTrace();}

        }
        return null;
    }


    @Override
    public ExecutorService getThreadPool() {
        return threadPool;
    }
}
