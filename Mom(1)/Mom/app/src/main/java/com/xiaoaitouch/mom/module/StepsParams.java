package com.xiaoaitouch.mom.module;

import java.io.Serializable;

import com.xiaoaitouch.mom.train.BeforTraining;

/**
 * @author huxin
 * @ClassName: StepsParams
 * @Description: 运动数据传递参数
 * @date 2015-12-8 下午3:59:46
 */
public class StepsParams implements Serializable {
    private static final long serialVersionUID = -3525118175192508267L;
    public BeforTraining beforTraining;
    public String stepsMsg = "";
    public int stepsNumber = 0;
    public int days = 0;
}
