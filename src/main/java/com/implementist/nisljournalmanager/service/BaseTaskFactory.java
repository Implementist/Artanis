/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.implementist.nisljournalmanager.service;

/**
 * @author Implementist
 */
public abstract class BaseTaskFactory {

    protected Runnable runnable;

    /**
     * 根据任务信息构建Runnable
     *
     * @param task 任务信息
     */
    protected abstract void build(Object task);

    protected Runnable getRunnable() {
        return runnable;
    }
}
