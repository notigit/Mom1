package com.xiaoaitouch.mom.module;

import java.io.Serializable;

public class UserModule implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private long userId;
    private String age;
    private String height;
    private String neckname;
    private String dueTime;
    private String lastMensesTime;
    private String createTime;
    private String uniqueness;
    private String userName;
    private String pwd;
    private String headPic;
    private double weight;
    private int mensesCircle;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getNeckname() {
        return neckname;
    }

    public void setNeckname(String neckname) {
        this.neckname = neckname;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public String getLastMensesTime() {
        return lastMensesTime;
    }

    public void setLastMensesTime(String lastMensesTime) {
        this.lastMensesTime = lastMensesTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUniqueness() {
        return uniqueness;
    }

    public void setUniqueness(String uniqueness) {
        this.uniqueness = uniqueness;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getMensesCircle() {
        return mensesCircle;
    }

    public void setMensesCircle(int mensesCircle) {
        this.mensesCircle = mensesCircle;
    }

}
