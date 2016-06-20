package com.xiaoaitouch.mom.module;

/**
 * Desc: 数胎动
 * User: huxin
 * Date: 2016/3/9
 * Time: 10:39
 * FIXME
 */
public class FetalMovementModule {
    private long userId;
    private int number;
    private int type;
    private String date;
    private String startTime;
    private String createTime;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
