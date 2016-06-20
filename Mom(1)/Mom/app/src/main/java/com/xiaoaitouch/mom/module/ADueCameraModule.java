package com.xiaoaitouch.mom.module;

import java.util.List;

/**
 * 产检相册网络接受实体类
 *
 * @author huxin
 * @data: 2016/1/14 15:40
 * @version: V1.0
 */
public class ADueCameraModule {
    private long createTime;
    private int group;
    private List<DueCameraModule> dueCameras;

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public List<DueCameraModule> getDueCameras() {
        return dueCameras;
    }

    public void setDueCameras(List<DueCameraModule> dueCameras) {
        this.dueCameras = dueCameras;
    }
}
