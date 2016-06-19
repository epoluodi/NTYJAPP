package com.suypower.pms.app.configxml;

/**
 * app-config.xml 序列化 tabbar 类
 * @author YXG
 */
public class TabBar {

    public String icon_tab_normal="";
    public String icon_tab_active="";
    public String uri_tab="";
    public String url="";
    public int tabid=-1;


    /**
     * tab id
     * @return
     */
    public int getTabid() {
        return tabid;
    }

    public void setTabid(int tabid) {
        this.tabid = tabid;
    }

    /**
     * 首页信息url
     * @return
     */
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * tab默认图标定义
     * @return
     */
    public String getIcon_tab_normal() {
        return icon_tab_normal;
    }

    public void setIcon_tab_normal(String icon_tab_normal) {
        this.icon_tab_normal = icon_tab_normal;
    }

    /**
     * tab 链接URI
     * @return
     */
    public String getUri_tab() {
        return uri_tab;
    }

    public void setUri_tab(String uri_tab) {
        this.uri_tab = uri_tab;
    }

    /**
     * tab选中图标定义
     * @return
     */
    public String getIcon_tab_active() {
        return icon_tab_active;
    }

    public void setIcon_tab_active(String icon_tab_active) {
        this.icon_tab_active = icon_tab_active;
    }
}
