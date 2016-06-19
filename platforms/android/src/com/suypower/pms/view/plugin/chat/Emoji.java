package com.suypower.pms.view.plugin.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;

import com.suypower.pms.app.SuyApplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Stereo on 16/4/12.
 */
public class Emoji {
    /**
     * 全局表情表
     */
    public static Map<String, String> stringMapemoji;


    /**
     * 初始化表情表
     *
     * @param context
     */
    public static void initemoji(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("emoji/emoji.json");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            String jsonarry = new String(buffer);
            inputStream.close();

            JSONArray emojijsonlist = new JSONArray(jsonarry);
            JSONObject jsonObject;
            stringMapemoji =new HashMap<>();
            for (int i = 0; i < emojijsonlist.length(); i++) {
                jsonObject = emojijsonlist.getJSONObject(i);
                stringMapemoji.put(jsonObject.getString("emojiwildcard"), jsonObject.getString("emojifile"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static SpannableString getEmojistring(String msg)
    {
        SpannableString spannableString=new SpannableString(msg);

        Pattern p = Pattern.compile("\\[\\!\\w*\\!\\]");
        Matcher m = p.matcher(msg);
        int startindex=0;
        while (m.find())
        {
            Log.i("表情",m.group());
            int i= msg.indexOf(m.group(),startindex);
            String str = msg.substring(i,i+m.group().length());
            try {
                String emojifile = Emoji.stringMapemoji.get(str);
                String emojipath = String.format("emoji/%1$s.png", emojifile);
                InputStream inputStream = SuyApplication.getApplication().getAssets().open(emojipath);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ImageSpan imageSpan = new ImageSpan(SuyApplication.getApplication(), bitmap);
                spannableString.setSpan(imageSpan, i, i+m.group().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            startindex = i + m.group().length();
            Log.i("表情str",str);



        }
        return spannableString;
    }


}
