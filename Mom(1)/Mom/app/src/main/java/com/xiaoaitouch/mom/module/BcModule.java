package com.xiaoaitouch.mom.module;

/**
 * Created by Administrator on 2016/3/3.
 */
public class BcModule {
    private int id;
    private int week;
    private int scanNo;
    private String value;
    private String name;
    private String detail;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getScanNo() {
        return scanNo;
    }

    public void setScanNo(int scanNo) {
        this.scanNo = scanNo;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
