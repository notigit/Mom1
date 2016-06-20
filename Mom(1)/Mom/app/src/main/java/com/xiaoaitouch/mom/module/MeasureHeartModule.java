package com.xiaoaitouch.mom.module;

/**
 * Desc: 心率\
 * User: huxin
 * Date: 2016/3/9
 * Time: 13:58
 * FIXME
 */
public class MeasureHeartModule {
    private long userId;
    private int number;
    private int type;
    private String date;
    private String gweek;
    private String createTime;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getGweek() {
        return gweek;
    }

    public void setGweek(String gweek) {
        this.gweek = gweek;
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
}
