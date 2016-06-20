package com.xiaoaitouch.mom.module;

import java.util.List;

/**
 * Created by Administrator on 2016/1/22.
 */
public class MainModle {
    private List<SportModle> si;
    private List<MaWeightModle> mw;
    private WeatherModle tq;
    private List<SymptomModle> zz;
    private List<SymptomCountModle> zzCount;

    public List<SymptomCountModle> getZzCount() {
        return zzCount;
    }

    public void setZzCount(List<SymptomCountModle> zzCount) {
        this.zzCount = zzCount;
    }

    public List<SportModle> getSi() {
        return si;
    }

    public void setSi(List<SportModle> si) {
        this.si = si;
    }

    public List<MaWeightModle> getMw() {
        return mw;
    }

    public void setMw(List<MaWeightModle> mw) {
        this.mw = mw;
    }

    public WeatherModle getTq() {
        return tq;
    }

    public void setTq(WeatherModle tq) {
        this.tq = tq;
    }

    public List<SymptomModle> getZz() {
        return zz;
    }

    public void setZz(List<SymptomModle> zz) {
        this.zz = zz;
    }
}
