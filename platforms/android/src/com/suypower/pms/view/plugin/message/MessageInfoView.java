package com.suypower.pms.view.plugin.message;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.suypower.pms.R;

/**
 * Created by cjw on 15/8/27.
 */
public class MessageInfoView extends Activity {
    ImageView leftbutton;//左上角 控制按钮
    TextView titletxt;

    TextView title,context,dt;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messageinfoview);
        leftbutton= (ImageView)findViewById(R.id.title_left_menu_image);
        leftbutton.setOnClickListener(onClickListenerreturn);

        Bundle bundle = getIntent().getExtras();

        titletxt = (TextView)findViewById(R.id.title_text);

        titletxt.setText(bundle.getString("appname"));

        MessageInfo messageInfo = (MessageInfo)bundle.getSerializable("msg");

        title  = (TextView)findViewById(R.id.title);
        title.setText(messageInfo.getTitle());

        context  = (TextView)findViewById(R.id.context);
        context.setText(messageInfo.getContext());
        dt  = (TextView)findViewById(R.id.dt);
        dt.setText(messageInfo.getMsgDT());


    }

    View.OnClickListener onClickListenerreturn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
    };

}