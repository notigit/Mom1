package com.xiaoaitouch.mom.module;

/**
 * Desc: 记宫缩
 * User: huxin
 * Date: 2016/3/6
 * Time: 11:43
 * FIXME
 */
public class RecordContractionsModule {
    private long userId;
    private String startTime;
    private String createTime;
    private int type;
    private int cxTime;
    private int jgTime;
    private String date;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getCxTime() {
        return cxTime;
    }

    public void setCxTime(int cxTime) {
        this.cxTime = cxTime;
    }

    public int getJgTime() {
        return jgTime;
    }

    public void setJgTime(int jgTime) {
        this.jgTime = jgTime;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
