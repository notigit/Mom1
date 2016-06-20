package com.xiaoaitouch.mom.module;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/2/24.
 */
public class AddToolsModule implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private long userId;
    private String name;
    private String inform;
    private int onOff;
    private int indexs;

    public int getOnOff() {
        return onOff;
    }

    public void setOnOff(int onOff) {
        this.onOff = onOff;
    }

    public int getIndexs() {
        return indexs;
    }

    public void setIndexs(int indexs) {
        this.indexs = indexs;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInform() {
        return inform;
    }

    public void setInform(String inform) {
        this.inform = inform;
    }

}
