package com.suypower.pms.app.configxml;

/**
 * app-config.xml 序列化 APPINFO 类
 * @author YXG
 */
public class AppInfo {

    String appName="";
    String appCode="";
    int appVerCode=0;
    String appVer;



    /**
     * app 路径
     * @return
     */
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appPath) {
        this.appName = appPath;

    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public int getAppVerCode() {
        return appVerCode;
    }

    public void setAppVerCode(int appVerCode) {
        this.appVerCode = appVerCode;
    }

    public String getAppVer() {
        return appVer;
    }

    public void setAppVer(String appVer) {
        this.appVer = appVer;
    }
}
