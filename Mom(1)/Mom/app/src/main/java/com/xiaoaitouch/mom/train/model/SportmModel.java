package com.xiaoaitouch.mom.train.model;

/**
 * Created by Administrator on 2016/1/24.
 */
public class SportmModel {
    private int id;
    private long userId;
    private String sportDate;
    private int stepNumber;
    private float km;
    private float calorie;
    private float time;

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

    public String getSportDate() {
        return sportDate;
    }

    public void setSportDate(String sportDate) {
        this.sportDate = sportDate;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public float getKm() {
        return km;
    }

    public void setKm(float km) {
        this.km = km;
    }

    public float getCalorie() {
        return calorie;
    }

    public void setCalorie(float calorie) {
        this.calorie = calorie;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }
}
