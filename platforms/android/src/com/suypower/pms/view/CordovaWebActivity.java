package com.suypower.pms.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suypower.pms.R;
import com.suypower.pms.view.plugin.CordovaWebViewPlugin;

import org.apache.cordova.CordovaWebView;

/**
 * Created by Stereo on 16/3/17.
 */
public class CordovaWebActivity extends BaseActivity {

    private ImageView returnpng;
    private CordovaWebView cordovaWebView;
    private TextView textView;
    private RelativeLayout webheadview;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cordovawebactivity);
        linearLayout = (LinearLayout) findViewById(R.id.mainview);
        returnpng = (ImageView) findViewById(R.id.btnreturn);
        returnpng.setOnClickListener(onClickListenerreturn);
        textView = (TextView) findViewById(R.id.webtitle);
        webheadview = (RelativeLayout) findViewById(R.id.headview);

        cordovaWebView = (CordovaWebView) findViewById(R.id.cordovawebview);
        progressBar = (ProgressBar) findViewById(R.id.pb);
        String url = getIntent().getStringExtra("url");
        cordovaWebViewPlugin = new CordovaWebViewPlugin(linearLayout, this);
        cordovaWebViewPlugin.setCordova(cordovaWebView, url);
        cordovaWebView.cordovaWebViewId = 1;
        cordovaWebView.loadUrl(url);

    }


    @Override
    void OnTitle(String title) {
        textView.setText(title);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cordovaWebView.clearCache(true);
        cordovaWebView.clearFormData();
        cordovaWebView.clearHistory();
        cordovaWebView.handleDestroy();
//        cordovaWebView.destroy();
        cordovaWebView=null;
        cordovaWebViewPlugin = null;
        System.gc();
    }

    View.OnClickListener onClickListenerreturn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    };


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return false;
        }
        return super.onKeyUp(keyCode, event);


    }
}