package com.xiaoaitouch.mom.util;

import com.xiaoaitouch.mom.module.SymptomModle;

import java.util.Stack;

/**
 * 症状选择堆栈
 */
public class SymptomSelectManager {
    private static Stack<SymptomModle> symptomSelectModules;
    private static SymptomSelectManager instance;

    private SymptomSelectManager() {
    }

    /**
     * 单一实例
     */
    public static SymptomSelectManager getSymptomSelectManager() {
        if (instance == null) {
            instance = new SymptomSelectManager();
        }
        return instance;
    }

    /**
     * 添加SymptomSelectModule到堆栈
     */
    public void addSymptomSelectModule(SymptomModle symptomSelectModule) {
        if (symptomSelectModules == null) {
            symptomSelectModules = new Stack<SymptomModle>();
        }
        symptomSelectModules.add(symptomSelectModule);
    }

    /**
     * 获取当前SymptomSelectModule（堆栈中最后一个压入的）
     */
    public SymptomModle currentSymptomModle() {
        if (symptomSelectModules != null && symptomSelectModules.size() >= 1) {
            SymptomModle symptomSelectModule = symptomSelectModules.lastElement();
            return symptomSelectModule;
        } else {
            return null;
        }

    }

    /**
     * 结束指定的SymptomSelectModule
     */
    public void removeSymptomSelectModule(SymptomModle symptomSelectModule) {
        if (symptomSelectModules != null) {
            symptomSelectModules.remove(symptomSelectModule);
        }
    }

    /**
     * 结束所有SymptomModle
     */
    public void removeAllSymptomModle() {
        if (symptomSelectModules != null) {
            symptomSelectModules.clear();
            symptomSelectModules = null;
        }
    }
}
