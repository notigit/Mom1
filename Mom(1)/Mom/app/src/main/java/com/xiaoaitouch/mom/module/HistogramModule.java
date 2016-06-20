package com.xiaoaitouch.mom.module;

/**
 * 柱状图的实体类
 *
 * @author huxin
 * @data: 2016/1/8 16:02
 * @version: V1.0
 */
public class HistogramModule {
    private float maxValue;
    private float yValue;
    private String xValue;
    private float averageValue;


    public float getAverageValue() {
        return averageValue;
    }

    public void setAverageValue(float averageValue) {
        this.averageValue = averageValue;
    }

    /**
     * @return the maxValue
     */
    public float getMaxValue() {
        return maxValue;
    }

    /**
     * @param maxValue the maxValue to set
     */
    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * @return the yValue
     */
    public float getyValue() {
        return yValue;
    }

    /**
     * @param yValue the yValue to set
     */
    public void setyValue(float yValue) {
        this.yValue = yValue;
    }

    /**
     * @return the xValue
     */
    public String getxValue() {
        return xValue;
    }

    /**
     * @param xValue the xValue to set
     */
    public void setxValue(String xValue) {
        this.xValue = xValue;
    }


}
