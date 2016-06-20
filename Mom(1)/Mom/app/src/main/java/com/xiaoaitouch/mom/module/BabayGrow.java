package com.xiaoaitouch.mom.module;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/3.
 */
public class BabayGrow implements Serializable {
    public String TAG = "BabayGrow";

    private int week;
    private String sdj;
    private String fw;
    private String ggc;

    private String sdj1;
    private String fw1;
    private String ggc1;

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public String getSdj() {
        return sdj;
    }

    public void setSdj(String sdj) {
        this.sdj = sdj;
    }

    public String getFw() {
        return fw;
    }

    public void setFw(String fw) {
        this.fw = fw;
    }

    public String getGgc() {
        return ggc;
    }

    public void setGgc(String ggc) {
        this.ggc = ggc;
    }

    public String getSdj1() {
        return sdj1;
    }

    public void setSdj1(String sdj1) {
        this.sdj1 = sdj1;
    }

    public String getFw1() {
        return fw1;
    }

    public void setFw1(String fw1) {
        this.fw1 = fw1;
    }

    public String getGgc1() {
        return ggc1;
    }

    public void setGgc1(String ggc1) {
        this.ggc1 = ggc1;
    }
}