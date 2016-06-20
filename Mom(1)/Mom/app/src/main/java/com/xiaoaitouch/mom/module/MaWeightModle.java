package com.xiaoaitouch.mom.module;

/**
 * Created by Administrator on 2016/1/22.
 */
public class MaWeightModle {
    private long id;
    private long userId;
    private String date;
    private String weight;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
