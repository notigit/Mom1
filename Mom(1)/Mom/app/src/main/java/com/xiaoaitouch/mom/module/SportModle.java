package com.xiaoaitouch.mom.module;

/**
 * 运动信息对象
 */
public class SportModle {
    private String date;
    private String sportMessage;
    private String sportMessage2;
    private String sportMessage3;
    private long time;
    private double calorie;
    private double km;
    private int stepNumber;
    private long id;
    private long userId;
    private int dueDay;
    private String weatherCode;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSportMessage() {
        return sportMessage;
    }

    public void setSportMessage(String sportMessage) {
        this.sportMessage = sportMessage;
    }

    public String getSportMessage2() {
        return sportMessage2;
    }

    public void setSportMessage2(String sportMessage2) {
        this.sportMessage2 = sportMessage2;
    }

    public String getSportMessage3() {
        return sportMessage3;
    }

    public void setSportMessage3(String sportMessage3) {
        this.sportMessage3 = sportMessage3;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getCalorie() {
        return calorie;
    }

    public void setCalorie(double calorie) {
        this.calorie = calorie;
    }

    public double getKm() {
        return km;
    }

    public void setKm(double km) {
        this.km = km;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

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

    public int getDueDay() {
        return dueDay;
    }

    public void setDueDay(int dueDay) {
        this.dueDay = dueDay;
    }

    public String getWeatherCode() {
        return weatherCode;
    }

    public void setWeatherCode(String weatherCode) {
        this.weatherCode = weatherCode;
    }
}
