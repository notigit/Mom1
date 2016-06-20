package com.xiaoaitouch.mom.module;

/**
 * Desc: 能不能列表实体类
 * User: huxin
 * Date: 2016/2/25
 * Time: 15:11
 * FIXME
 */
public class CanOrNotListBean {
    private long id;
    private int type;
    private String name;
    private String abs;
    private int can;
    private String smallImg;

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

    public String getSmallImg() {
        return smallImg;
    }

    public void setSmallImg(String smallImg) {
        this.smallImg = smallImg;
    }
}
