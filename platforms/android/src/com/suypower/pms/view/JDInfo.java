package com.suypower.pms.view;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cjw on 16/6/26.
 */
public class JDInfo {


    private String title;
    private String content;
    private String date;
    private List<String> imgid;
    private String audioid;

    private List<JDInfo>  jdInfos;

    static JDInfo jdInfo=null;


    public static JDInfo getIntentce()
    {
        if (jdInfo==null)
        {
            jdInfo=new JDInfo();
        }
        return jdInfo;
    }
    public JDInfo()
    {
        imgid=new ArrayList<>();
        jdInfos=new ArrayList<>();

    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getImgid() {
        return imgid;
    }

    public void setImgid(List<String> imgid) {
        this.imgid = imgid;
    }

    public String getAudioid() {
        return audioid;
    }

    public void setAudioid(String audioid) {
        this.audioid = audioid;
    }

    public List<JDInfo> getJdInfos() {
        return jdInfos;
    }

    public void setJdInfos(List<JDInfo> jdInfos) {
        this.jdInfos = jdInfos;
    }

    public static JDInfo getJdInfo() {
        return jdInfo;
    }

    public static void setJdInfo(JDInfo jdInfo) {
        JDInfo.jdInfo = jdInfo;
    }
}
