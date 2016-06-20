package com.xiaoaitouch.mom.module;

/**
 * Created by Administrator on 2016/1/23.
 */
public class SymptomCountModle {
    private long symptomId;
    private String symptom;
    private int pregrenttime;
    private long userId;
    private int counts;

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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getCounts() {
        return counts;
    }

    public void setCounts(int counts) {
        this.counts = counts;
    }
}
