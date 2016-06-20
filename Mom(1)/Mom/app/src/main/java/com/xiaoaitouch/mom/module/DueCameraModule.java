package com.xiaoaitouch.mom.module;
/**
 * 产检相册
 * @author  huxin
 * @data:  2016/1/14 15:38
 * @version:  V1.0
 */
public class DueCameraModule {
    private int id;
    private long userId;
    private long createTime;
    private String date;
    private String img;

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

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
