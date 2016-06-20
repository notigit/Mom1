package com.xiaoaitouch.mom.module;

/**
 * Desc: 能不能详情实体类
 * User: huxin
 * Date: 2016/2/26
 * Time: 10:39
 * FIXME
 */
public class CanOrNotDetailsBean {
    private long id;
    private int type;
    private String name;
    private String abs;
    private int can;
    private String content;
    private String buyTips;
    private String bigImg;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbs() {
        return abs;
    }

    public void setAbs(String abs) {
        this.abs = abs;
    }

    public int getCan() {
        return can;
    }

    public void setCan(int can) {
        this.can = can;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBuyTips() {
        return buyTips;
    }

    public void setBuyTips(String buyTips) {
        this.buyTips = buyTips;
    }

    public String getBigImg() {
        return bigImg;
    }

    public void setBigImg(String bigImg) {
        this.bigImg = bigImg;
    }
}
