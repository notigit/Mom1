package com.xiaoaitouch.mom.module;

/**
 * Created by Administrator on 2016/1/26.
 */
public class WelfareBean {
    private long id;
    private String img;
    private String url;
    private String title;
    private long createTime;
    private long isUsed;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(long isUsed) {
        this.isUsed = isUsed;
    }
}
