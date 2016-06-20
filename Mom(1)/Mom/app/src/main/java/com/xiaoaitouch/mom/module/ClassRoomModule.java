package com.xiaoaitouch.mom.module;

import java.util.List;

public class ClassRoomModule {
    private int stageCode;
    private String zmc;
    private String tsMore;
    private String zzMore;
    private List<ClassRoomImageListModule> imgList;

    public int getStageCode() {
        return stageCode;
    }

    public void setStageCode(int stageCode) {
        this.stageCode = stageCode;
    }

    public String getZmc() {
        return zmc;
    }

    public void setZmc(String zmc) {
        this.zmc = zmc;
    }

    public String getTsMore() {
        return tsMore;
    }

    public void setTsMore(String tsMore) {
        this.tsMore = tsMore;
    }

    public String getZzMore() {
        return zzMore;
    }

    public void setZzMore(String zzMore) {
        this.zzMore = zzMore;
    }

    public List<ClassRoomImageListModule> getImgList() {
        return imgList;
    }

    public void setImgList(List<ClassRoomImageListModule> imgList) {
        this.imgList = imgList;
    }
}
