package com.suypower.pms.app.configxml;

import android.util.Xml;


import com.suypower.pms.app.SuyApplication;

import org.apache.http.util.EncodingUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * app-config.xml 序列化类
 * @author YXG
 */
public class AppConfig {
    public static final String Tag = "AppConfig";

    /**
     * 个人信息配置URI
     */
    public static final String userprofileplugin="stereo://stereo.native.userprofilePlugin";
    /**
     * CordovaWebView URI
     */
    public static final String cordovawebviewplugin="stereo://stereo.native.cordovawebviewPlugin";

    /**P
     * 页面自定义菜单 URI
     */
    public static final String optionmenuplugin="stereo://stereo.native.optionmenuPlugin";
    /**
     * 主界面tabbar URI
     */
    public static final String maintabbarplugin="stereo://stereo.native.maintabbarPlugin";

    /**
     * camera插件 URI
     */
    public static final String cameraplugin="stereo://stereo.native.cameraPlugin";









    String appID="";
    String appcode="";
    String appver="";
    String appname="";
    String appintroduce="";
    String apptitlepng="";
    String mainframepluginclass="";
    String httpUrl1,httpUrl2,httpUrl3,httpUrl4;
    TabBar[] tabBar=null;

    public static Map<String ,String> appmap = new HashMap<String, String>();


    public String getAppver() {
        return appver;
    }

    public void setAppver(String appver) {
        this.appver = appver;
    }


    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public String getAppintroduce() {
        return appintroduce;
    }

    public void setAppintroduce(String appintroduce) {
        this.appintroduce = appintroduce;
    }

    public String getApptitlepng() {
        return apptitlepng;
    }

    public void setApptitlepng(String apptitlepng) {
        this.apptitlepng = apptitlepng;
    }

    /**
     * APPConfig静态对象
     */
    public static AppConfig appConfig=null;

    /**
     * app code定义
     * @return
     */
    public String getAppcode() {
        return appcode;
    }

    public void setAppcode(String appcode) {
        this.appcode = appcode;
    }

    /**
     * app名称定义，该名称将会作为默认title
     * @return
     */
    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    /**
     * tabbar数组
     * @return
     */
    public TabBar[] getTabBar() {
        return tabBar;
    }

    public void setTabBar(TabBar[] tabBar) {
        this.tabBar = tabBar;
    }

    /**
     * 插件对应class
     * @return
     */
    public String getMainframepluginclass() {
        return mainframepluginclass;
    }

    public void setMainframepluginclass(String mainframepluginclass) {
        this.mainframepluginclass = mainframepluginclass;
    }


    /**
     * 序列化 Config.xml
     * @param xmlString
     * @return true 成功，false 失败
     */
    public static Boolean deserialize(String xmlString) {
        AppConfig appfg = new AppConfig();

        try {
            InputStream inputStream = new ByteArrayInputStream(xmlString.getBytes());
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "UTF-8");

            List<TabBar> tabBars = new ArrayList<TabBar>();
            TabBar tabBar = null;

            int eventType = parser.getEventType();
            appmap.clear();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String elementName = parser.getName();

                        if (elementName .equals("stereo-app-code")) {
                            appfg.setAppcode(parser.nextText());
                        }else if (elementName .equals("stereo-app-ver")) {
                            appfg.setAppver(parser.nextText());
                        }else if (elementName .equals("stereo-app-name")) {
                            appfg.setAppname(parser.nextText());
                            appmap.put(appfg.getAppcode(),appfg.getAppname());

                        }else if (elementName .equals("stereo-app-png")) {
                            appfg.setApptitlepng(parser.nextText());

                        }else if (elementName .equals("stereo-app-introduce")) {
                            appfg.setAppintroduce(parser.nextText());

                        }else if (elementName .equals("stereo-plugin-class")) {
                            appfg.setMainframepluginclass(parser.nextText());

                        }else if (elementName.equals("stereo-app-id")) {
                            appfg.setAppID(parser.nextText());
                        }/*
                        else if (elementName.equals("stereo-app-request2-http")) {
                            appfg.setHttpUrl2(parser.nextText());
                        }
                        else if (elementName.equals("stereo-app-request3-http")) {
                            appfg.setHttpUrl3(parser.nextText());
                        }
                        else if (elementName.equals("stereo-app-request4-http")) {
                            appfg.setHttpUrl4(parser.nextText());
                        }*/
                        else if (elementName.equals( "stereo-tab")) {

                            tabBar = new TabBar();
                        } else if (elementName.equals("stereo-tab-default-icon")) {
                            tabBar.setIcon_tab_normal(parser.nextText());
                        } else if (elementName.equals("stereo-tab-active-icon")) {
                            tabBar.setIcon_tab_active(parser.nextText());
                        } else if (elementName.equals("stereo-tab-uri")) {
                            tabBar.setUri_tab(parser.nextText());
                        }else if (elementName.equals("stereo-tab-url")) {
                            tabBar.setUrl(parser.nextText());
                        }else if (elementName.equals("stereo-tab-id")) {
                            tabBar.setTabid(Integer.valueOf( parser.nextText()));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String endElementName = parser.getName();
                        if (endElementName .equals( "stereo-tab")) {
                            tabBars.add(tabBar);
                            tabBar = null;
                        }
                        break;
                }
                eventType = parser.next();
            }
            appfg.setTabBar(tabBars.toArray(new TabBar[0]));
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
        appConfig=appfg;
        return true;
    }



    public static void initAppConfig()
    {
        try {
            InputStream inputStreamconfig;
            String str;
            byte[] buffers = null;
            for (int i = 0; i < GlobalConfig.globalConfig.getAppConfigs().length; i++) {


                if (!GlobalConfig.globalConfig.getIsCachefile())
                    SuyApplication.getApplication().getAbsluteAppPath = "file:///android_asset/www/apps/" + GlobalConfig.globalConfig.getAppInfos()[i].getAppName() + "/";
                else
                    SuyApplication.getApplication().getAbsluteAppPath = "file:///" +
                            SuyApplication.getApplication().getFilesDir() + "/www/apps/" +
                            GlobalConfig.globalConfig.getAppInfos()[i].getAppCode() + "/";

                if (!GlobalConfig.globalConfig.getIsCachefile())
                    SuyApplication.getApplication().getRelAppPath = "www/apps/" +
                            GlobalConfig.globalConfig.getAppInfos()[i].getAppName() + "/";
                else
                    SuyApplication.getApplication().getRelAppPath =
                            SuyApplication.getApplication().getFilesDir() + "/www/apps/" +
                                    GlobalConfig.globalConfig.getAppInfos()[i].getAppCode() + "/";


                String apppath = String.format("%1$sconfig.xml",
                        SuyApplication.getApplication().getRelAppPath);

                if (!GlobalConfig.globalConfig.getIsCachefile())
                    inputStreamconfig = SuyApplication.getApplication().getAssets().open(apppath);
                else
                    inputStreamconfig = new FileInputStream(apppath);
                buffers = new byte[inputStreamconfig.available()];
                inputStreamconfig.read(buffers);
                str = EncodingUtils.getString(buffers, "utf-8");
                AppConfig.deserialize(str);
                GlobalConfig.globalConfig.setAppConfigs(
                        AppConfig.appConfig, i);

                inputStreamconfig.close();


            }
        }
        catch (Exception e)
        {e.printStackTrace();}
    }

}
