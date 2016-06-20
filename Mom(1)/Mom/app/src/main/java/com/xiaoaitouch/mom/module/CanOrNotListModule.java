package com.xiaoaitouch.mom.module;

import java.util.List;

/**
 * Desc: 能不能列表实体类
 * User: huxin
 * Date: 2016/2/25
 * Time: 15:11
 * FIXME
 */
public class CanOrNotListModule {
    private List<CanOrNotListBean> list;
    private int index;

    public List<CanOrNotListBean> getList() {
        return list;
    }

    public void setList(List<CanOrNotListBean> list) {
        this.list = list;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
