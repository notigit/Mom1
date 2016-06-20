package com.xiaoaitouch.mom.module;

/**
 * Created by Administrator on 2016/1/22.
 */
public class SymptomModle {
    private long symptomId;
    private String symptom;
    private int pregrenttime;
    private int isOk;
    private long userId;
    private String date;
    private String abs;

    public String getAbs() {
        return abs;
    }

    public void setAbs(String abs) {
        this.abs = abs;
    }

    public long getSymptomId() {
        return symptomId;
    }

    public void setSymptomId(long symptomId) {
        this.symptomId = symptomId;
    }

    public String getSymptom() {
        return symptom;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
    }

    public int getPregrenttime() {
        return pregrenttime;
    }

    public void setPregrenttime(int pregrenttime) {
        this.pregrenttime = pregrenttime;
    }

    public int getIsOk() {
        return isOk;
    }

    public void setIsOk(int isOk) {
        this.isOk = isOk;
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
}
