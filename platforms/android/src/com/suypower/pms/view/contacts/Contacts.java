package com.suypower.pms.view.contacts;

import java.io.Serializable;

/**
 * Created by Stereo on 16/3/29.
 */
public class Contacts implements Serializable {

    private String id;
    private String name="";
    private String phone="";
    private String sex="";
    private String nickimgurl;
    private String position="";
    private String departmentid;
    private String PY;
    private String departmentname="";
    private String email="";
    private String loginname="";

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartmentname() {
        return departmentname;
    }

    public void setDepartmentname(String departmentname) {
        this.departmentname = departmentname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickimgurl() {
        return nickimgurl;
    }

    public void setNickimgurl(String nickimg) {
        this.nickimgurl = nickimg;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDepartmentid() {
        return departmentid;
    }

    public void setDepartmentid(String departmentid) {
        this.departmentid = departmentid;
    }

    public String getPY() {
        return PY;
    }

    public void setPY(String PY) {
        this.PY = PY;
    }







}
