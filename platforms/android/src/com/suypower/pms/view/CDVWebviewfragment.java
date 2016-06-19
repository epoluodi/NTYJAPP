package com.suypower.pms.view;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.suypower.pms.R;
import com.suypower.pms.app.configxml.GlobalConfig;
import com.suypower.pms.view.plugin.BaseViewPlugin;
import com.suypower.pms.view.plugin.CustomPopWindowPlugin;
import com.suypower.pms.view.plugin.fragmeMager.FragmentName;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 14-11-30.
 */
public class CDVWebviewfragment extends Fragment implements FragmentName {

    String Fragment_Name="";
    CordovaWebView cordovaWebView;
    ProgressBar progressBar;

    int viewid;

    @Override
    public void startIMessageControl() {

    }

    @Override
    public void stopIMessageControl() {

    }

    @Override
    public void selectcustomer(String guestid, String guestname) {
        return;
    }

    @Override
    public void SelectMenu(int Menuid) {

    }

    public CDVWebviewfragment(int id)
    {
        viewid=id;
    }



    @Override
    public void SetFragmentName(String name) {
        Fragment_Name=name;
    }

    @Override
    public String GetFragmentName() {
        return Fragment_Name;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cdvwebview, container, false);
        progressBar = (ProgressBar)rootView.findViewById(R.id.pb);

        cordovaWebView = (CordovaWebView)rootView.findViewById(R.id.cordovawebview);
        cordovaWebView.loadUrl("file:///android_asset/www/apps/pms/html/index.html");
        cordovaWebView.cordovaWebViewId=viewid;
//        cdvWebview_fragment1.setUrl("file:///android_asset/www/apps/pms/html/index.html");
        return rootView;
    }


    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);

            String id="";
            Object data=null;
            Message msg = null;
            CordovaPlugin cordovaPlugindata=null;
            Map<String, Object> mapmsg =null;
            switch (message.what) {
                case 0:
                    mapmsg = (Map<String, Object>) message.obj;
                    id = mapmsg.get("id").toString();
                    data = mapmsg.get("data");
                    break;
                case 1:
                    mapmsg = (Map<String, Object>) message.obj;
                    id = mapmsg.get("id").toString();
                    cordovaPlugindata = (CordovaPlugin)mapmsg.get("data");
                    break;
            }





            if (id.equals("onProgressChanged"))
            {

                Log.i("web 进度",String.valueOf(data));
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



                return ;
            }


            if (id.equals(BaseViewPlugin.GOBACK_ACTION)) //
            {
                cordovaWebView.goBack();
                return;
            }

            if (id.equals(BaseViewPlugin.DIALOG_ACTION)) //
            {

                try {
                    msg = (Message)data;
                    JSONObject jsonObject = (JSONObject)msg.obj;
                    if (jsonObject.getString("model").equals("show"))
                    {
                        int timeout=15000;
                        try {
                            timeout = jsonObject.getInt("timeout");
                        }
                        catch (Exception e)
                        {e.printStackTrace();}
                        CustomPopWindowPlugin.ShowPopWindowForTimeout(cordovaWebView,
                                getActivity().getLayoutInflater(),jsonObject.getString("info"),timeout);

                    }
                    if (jsonObject.getString("model").equals("change"))
                    {
                        CustomPopWindowPlugin.Setpoptext(jsonObject.getString("info"));

                    }
                    if (jsonObject.getString("model").equals("close"))
                    {
                        CustomPopWindowPlugin.CLosePopwindow();

                    }
//                callbackJs = jsonObject.getString("callback");


                    return;

                }
                catch (Exception e)
                {e.printStackTrace();}

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


                    CustomPopWindowPlugin.ShowPopWindow(cordovaWebView,getActivity().getLayoutInflater(),
                            "",info,timeout);


                    return;

                }
                catch (Exception e)
                {e.printStackTrace();}

            }
        }
    };

    @Override
    public void onMessage(Message message) {

        handler.sendMessage(message);


    }

    public void setUrl(String url)
    {
        cordovaWebView.loadUrl(url);
    }

    @Override
    public void returnWeb() {
        if (cordovaWebView.startOfHistory())
            return;

        cordovaWebView.goBack();
    }


    public void close() {
        cordovaWebView.handleDestroy();
//        cordovaWebView.destroy();


    }
}
