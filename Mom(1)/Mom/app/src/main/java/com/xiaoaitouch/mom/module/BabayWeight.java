package com.xiaoaitouch.mom.module;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/3.
 */
public class BabayWeight implements Serializable {
    private int week;
    private int sex;
    private int ten;
    private int twentyFive;
    private int fifty;
    private int seventyFive;
    private int ninety;

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getTen() {
        return ten;
    }

    public void setTen(int ten) {
        this.ten = ten;
    }

    public int getTwentyFive() {
        return twentyFive;
    }

    public void setTwentyFive(int twentyFive) {
        this.twentyFive = twentyFive;
    }

    public int getFifty() {
        return fifty;
    }

    public void setFifty(int fifty) {
        this.fifty = fifty;
    }

    public int getSeventyFive() {
        return seventyFive;
    }

    public void setSeventyFive(int seventyFive) {
        this.seventyFive = seventyFive;
    }

    public int getNinety() {
        return ninety;
    }

    public void setNinety(int ninety) {
        this.ninety = ninety;
    }
}