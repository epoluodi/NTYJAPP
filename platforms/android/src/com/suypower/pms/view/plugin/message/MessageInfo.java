package com.suypower.pms.view.plugin.message;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by cjw on 15/8/25.
 */
public class MessageInfo implements Serializable {


    String title;
    String context;
    String url;
    String mediaid;
    String mediaUrl;
    int model =0;
    String msgDT;
    int articleCount;



    MessageInfo[] messageInfos;


    public int getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(int articleCount) {
        this.articleCount = articleCount;
    }

    public MessageInfo[] getMessageInfos() {
        return messageInfos;
    }

    public void setMessageInfos(MessageInfo[] messageInfos) {
        this.messageInfos = messageInfos;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMediaid() {
        return mediaid;
    }

    public void setMediaid(String mediaid) {
        mediaUrl = "";
        this.mediaid = mediaid;


    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMsgDT() {
        return MessageInfo.GetSysTime(msgDT);
    }
    public String getMsgDTInit()
    {
        return msgDT;
    }

    public void setMsgDT(String msgDT) {
        this.msgDT = msgDT;
    }

    public int getModel() {
        return model;
    }

    public void setModel(String msgType) {
        if (msgType.equals("text"))
            this.model = 1;//文本
        if (msgType.equals("news"))
            this.model = 2;//图片文本
        if (msgType.equals("newsEx"))
            this.model = 3;//图片文本
    }


    /**
     * 解析活的messageinfo对象
     * @param json
     * @return
     */
    public static MessageInfo getMessageInfo (String json)
    {
        MessageInfo messageInfo = new MessageInfo();
        try
        {
            JSONObject jsonObject = new JSONObject(json);
            messageInfo.setModel(jsonObject.getString("msgType"));



            switch (messageInfo.getModel())
            {
                case 1://文本
                    messageInfo.setTitle(jsonObject.getString("title"));
                    messageInfo.setContext(jsonObject.getString("content"));
                    messageInfo.setUrl(jsonObject.getString("url"));
                    messageInfo.setMsgDT(jsonObject.getString("timestamp"));
                    break;
                case 2://多媒体
                    int articleCount=jsonObject.getInt("articleCount");
                    messageInfo.setMsgDT(jsonObject.getString("timestamp"));
//                    messageInfo.setTitle(jsonObject.getString("title"));
                    messageInfo.setArticleCount(articleCount);
                    MessageInfo[] messageInfos = new MessageInfo[articleCount];
                    JSONArray jsonArray = jsonObject.getJSONArray("articles");
                    for (int i = 0;i<articleCount;i++) {
                        JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
                        messageInfos[i] = new MessageInfo();
                        messageInfos[i].setTitle(jsonObject1.getString("title"));
                        messageInfos[i].setContext(jsonObject1.getString("content"));
                        messageInfos[i].setMediaid(jsonObject1.getString("media"));
                        messageInfos[i].setUrl(jsonObject1.getString("url"));
                        messageInfos[i].setMsgDT(messageInfo.getMsgDT());
                    }
                    if (messageInfo.getArticleCount() > 1)
                    {
                        messageInfo.setModel("newsEx");
                    }
                    messageInfo.setMessageInfos(messageInfos);
                    break;
            }





            return messageInfo;
        }
        catch (Exception e)
        {e.printStackTrace();}

        return null;
    }


    public static  String GetSysTime(String dt) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strdate = "";
        try {
            Date date = sDateFormat.parse(dt);

            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(date);
            if (cal1.get(Calendar.DAY_OF_MONTH) == cal2 .get(Calendar.DAY_OF_MONTH))
            {
                sDateFormat = new SimpleDateFormat("今天 HH:mm");
                strdate = sDateFormat.format(date);
                return strdate;
            }

            if (isSameWeekDates(new Date(),date))
            {
                sDateFormat = new SimpleDateFormat("HH:mm");
                strdate = sDateFormat.format(date);
                strdate = getWeekStr(date) +" " + strdate;
                return strdate;

            }
            else
            {
                sDateFormat = new SimpleDateFormat("MM-dd HH:mm");
                strdate = sDateFormat.format(date);
            }



            return strdate;
        }
        catch (Exception e)
        {e.printStackTrace();}
        return  "";
    }

    /**
     * 判断二个时间是否在同一个周
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameWeekDates(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        int subYear = cal1.get(Calendar.YEAR)-cal2.get(Calendar.YEAR);
        if (0 == subYear) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        } else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) {
// 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        } else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        }
        return false;
    }


    /**
     * 根据一个日期，返回是星期几的字符串
     *
     * @param sdate
     * @return
     */

    public static String getWeekStr(Date sdate){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sdate);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String today = "";
        if (day == 2) {
            today = "星期一";
        } else if (day == 3) {
            today = "星期二";
        } else if (day == 4) {
            today = "星期三";
        } else if (day == 5) {
            today = "星期四";
        } else if (day == 6) {
            today = "星期五";
        } else if (day == 7) {
            today = "星期六";
        } else if (day == 1) {
            today = "星期日";
        }
        return today;
    }



}
