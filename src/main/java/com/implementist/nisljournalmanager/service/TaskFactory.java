/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.implementist.nisljournalmanager.service;

/**
 *
 * @author Implementist
 */
public abstract class TaskFactory {

    protected Runnable runnable;

    protected abstract void buildTask();

    protected Runnable getRunnable() {
        return runnable;
    }
}
