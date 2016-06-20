package com.xiaoaitouch.mom.module;

import java.util.List;

/**
 * Created by Administrator on 2016/3/2.
 */
public class DazzleListBean {
    private String gweek;
    private String date;
    private List<DazzleListDataBean> data;

    public String getGweek() {
        return gweek;
    }

    public void setGweek(String gweek) {
        this.gweek = gweek;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<DazzleListDataBean> getData() {
        return data;
    }

    public void setData(List<DazzleListDataBean> data) {
        this.data = data;
    }
}
