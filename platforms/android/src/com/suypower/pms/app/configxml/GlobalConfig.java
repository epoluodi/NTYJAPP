package com.suypower.pms.app.configxml;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Xml;


import com.suypower.pms.app.SuyApplication;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Global-config.xml 全局配置xml
 * @author YXG
 */
public class GlobalConfig {
    public static final String Tag = "GlobalConfig";

    /**
     * 服务器地址
     */
//    public static final String AuthUrl = "http://om.suypower.com/stereo-webapp/auth/";
//    public static final String AppUrl = "http://om.suypower.com/stereo-webapp/app/";
//    public static final String IMUrl = "http://om.suypower.com/stereo-webapp/im/";
//    public static final String MQTTServer = "tcp://218.94.111.38:61613";
//    public static final String ApiUrl = "http://om.suypower.com/stereo-webapp/api/";
//    public static final String AppUpgrade = "http://om.suypower.com/stereo-webapp/upgrade/";
//    public static final String ServerHost = "http://om.suypower.com/stereo-webapp/";
//    public static final String ArticleUrl = "http://om.suypower.com/stereo-webapp/article/";
//    测试
    public static final String AuthUrl = "http://15b2060o22.iask.in:18080/ntyj/auth/";
    public static final String AppUrl = "http://15b2060o22.iask.in:18080/ntyj/app/";
    public static final String IMUrl = "http://15b2060o22.iask.in:18080/ntyj/im/";
    public static final String MQTTServer = "tcp://222.95.131.64:61613";
    public static final String ApiUrl = "http://15b2060o22.iask.in:18080/ntyj/api/";
    public static final String AppUpgrade = "http://15b2060o22.iask.in:18080/ntyj/upgrade/";
    public static final String ServerHost = "http://15b2060o22.iask.in:18080/ntyj";
    public static final String ArticleUrl = "http://15b2060o22.iask.in:18080/ntyj/article/";

    public static final String AUDIO_CACHE_PATH = SuyApplication.getApplication().getCacheDir().getAbsolutePath();

    String splashimage ="";//http 获取json
    AppInfo[] appInfos=null;
    Boolean debug;
    AppConfig[] appConfigs=null;
    String appUpgradeUrl = "";
    String apiUrl="";
    String imUrl="";
    String authUrl="";
    String appUrl="";
    String mqttServer="";
    Boolean IsCachefile =false;
    String updateDT;

    String mqttUserName="";
    String mqttPwd="";


    int server=0;
    int message=0;


    public  String getArticleUrl() {
        return ArticleUrl;
    }

    public String getMqttUserName() {
        return mqttUserName;
    }

    public void setMqttUserName(String mqttUserName) {
        this.mqttUserName = mqttUserName;
    }

    public String getMqttPwd() {
        return mqttPwd;
    }

    public void setMqttPwd(String mqttPwd) {
        this.mqttPwd = mqttPwd;
    }

    public String getMqttServer() {
        return mqttServer;
    }

    public void setMqttServer(String mqttServer) {
        this.mqttServer = mqttServer;
    }

    public String getImUrl() {
        return imUrl;
    }

    public void setImUrl(String imUrl) {
        this.imUrl = imUrl;
    }

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }

    public String getUpdateDT() {
        return updateDT;
    }

    public void setUpdateDT(String updateDT) {
        this.updateDT = updateDT;
    }

    public Boolean getIsCachefile() {
        return IsCachefile;
    }

    public void setIsCachefile(Boolean isCachefile) {
        IsCachefile = isCachefile;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getAppUpgradeUrl() {
        return appUpgradeUrl;
    }

    public void setAppUpgradeUrl(String appUpgradeUrl) {
        this.appUpgradeUrl = appUpgradeUrl;
    }

    public int getServer() {
        return server;
    }

    public void setServer(int server) {
        this.server = server;
    }

    public String getAppCodes()
    {
        String str="";
        for (AppInfo appInfo : appInfos)
        {
            str += appInfo.appCode + ",";

        }
        str = str.substring(0,str.length()-1);
        return str;
    }
    /**
     * 设置调试信息
     * @return
     */
    public Boolean isDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;

    }

    public static GlobalConfig globalConfig=null;



    /**
     * 获得开机图片
     * @return
     */
    public String getSplashimage() {
        return splashimage;
    }

    public void setSplashimage(String splashimage) {
        this.splashimage = splashimage;
    }


    /** APP 配置
     *
     * @return
     */
    public AppConfig[] getAppConfigs() {
        return appConfigs;
    }

    public void setAppConfigs(AppConfig appConfigs, int index) {
        this.appConfigs[index] = appConfigs;
    }
    public void  setAppConfigs(int count)
    {
        appConfigs = new AppConfig[count];
    }

    /**
     * app 数组队列显示
     * @return
     */
    public AppInfo[] getAppInfos() {
        return appInfos;
    }

    public void setAppInfos(AppInfo[] appInfos) {
        this.appInfos = appInfos;
    }


        /**
     * 序列化
     * @param xmlString
     * @return true 成功，false 失败
     */
    public static Boolean deserialize(String xmlString) {
        GlobalConfig globalConfig1 = new GlobalConfig();

        try {
            InputStream inputStream = new ByteArrayInputStream(xmlString.getBytes());
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "UTF-8");

            List<AppInfo> appInfoList = new ArrayList<AppInfo>();
            AppInfo appInfo = null;

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String elementName = parser.getName();

                        if (elementName .equals("global-splash-image")) {
                            globalConfig1.setSplashimage(parser.nextText());
                        }else if (elementName.equals( "app")) {

                            appInfo = new AppInfo();
                        } else if (elementName.equals("stereo-app-upgrade-http")) {
                            globalConfig1.setAppUpgradeUrl(parser.nextText());
                        }
                        else if (elementName.equals( "global-debug")) {

                            globalConfig1.setDebug((parser.nextText().equals("1") ? true : false));
                        } else if (elementName.equals("app-path")) {
                            appInfo.setAppName(parser.nextText());
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        String endElementName = parser.getName();
                        if (endElementName .equals( "app")) {
                            appInfoList.add(appInfo);
                            appInfo = null;
                        }
                        break;
                }
                eventType = parser.next();
            }
            globalConfig1.setAppInfos(appInfoList.toArray(new AppInfo[0]));
            globalConfig1.setAppConfigs(appInfoList.size());
        } catch (XmlPullParserException e) {
            e.printStackTrace();

            return false;
        } catch (NullPointerException e) {
            e.printStackTrace();

            return false;
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }
        globalConfig=globalConfig1;
        return true;
    }




    public static void loadConfig() {
        GlobalConfig globalConfig1 = new GlobalConfig();
        globalConfig1.setAuthUrl(AuthUrl);
        globalConfig1.setAppUrl(AppUrl);
        globalConfig1.setImUrl(IMUrl);
        globalConfig1.setApiUrl(ApiUrl);
        globalConfig1.setAppUpgradeUrl(AppUpgrade);
        globalConfig1.setMqttServer(MQTTServer);
        globalConfig1.setServer(0);
        globalConfig1.setMessage(1);
        globalConfig1.setDebug(false);
        globalConfig = globalConfig1;
    }



//    public static void loadDBConfig() {
//        GlobalConfig globalConfig1 = new GlobalConfig();
//        Cursor cursor = SuyApplication.getApplication().getSuyDB().getConfig();
//        while (cursor.moveToNext()) {
//            if (cursor.getString(0).equals("global-splash-image")) {
//                globalConfig1.setSplashimage(cursor.getString(1));
//            } else if (cursor.getString(0).equals("stereo-app-upgrade-http")) {
//                globalConfig1.setAppUpgradeUrl(cursor.getString(1));
//            }else if (cursor.getString(0).equals("stereo-app-app-http")) {
//                globalConfig1.setAppUrl(cursor.getString(1));
//            }
//            else if (cursor.getString(0).equals("stereo-app-auth-http")) {
//                globalConfig1.setAuthUrl(cursor.getString(1));
//            }else if (cursor.getString(0).equals("stereo-app-api-http")) {
//                globalConfig1.setApiUrl(cursor.getString(1));
//            }else if (cursor.getString(0).equals("updateDT")) {
//                globalConfig1.setUpdateDT(cursor.getString(1));
//            }else if (cursor.getString(0).equals("Server")) {
//                globalConfig1.setServer(Integer.valueOf( cursor.getString(1)));
//            }else if (cursor.getString(0).equals("debug")) {
//                globalConfig1.setDebug(
//                        (cursor.getString(1).equals("1"))?true :false);
//            }else if (cursor.getString(0).equals("message")) {
//                globalConfig1.setMessage(Integer.valueOf( cursor.getString(1)));
//            }
//        }
//        cursor.close();
//
//        globalConfig = globalConfig1;
//        return ;
//    }


    public static void loadDBAppInfo() {
        AppInfo appInfo ;

        Cursor cursor = SuyApplication.getApplication().getSuyDB().getAppInfo();
        AppInfo[] appInfos1 =new AppInfo[cursor.getCount()];
        GlobalConfig.globalConfig.setAppConfigs(cursor.getCount());
        int i=0;
        while (cursor.moveToNext()) {
            appInfo = new AppInfo();

            appInfo.setAppName(cursor.getString(2));

            appInfo.setAppCode(cursor.getString(0));

            appInfo.setAppVerCode(cursor.getInt(1));

            appInfo.setAppVer(cursor.getString(3));

            appInfos1[i] = appInfo;
            i++;
        }
        cursor.close();
        GlobalConfig.globalConfig.setAppInfos(appInfos1);

    }


}
