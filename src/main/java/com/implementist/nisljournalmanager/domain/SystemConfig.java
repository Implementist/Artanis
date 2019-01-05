/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.implementist.nisljournalmanager.domain;

/**
 *
 * @author Implementist
 */
public class SystemConfig {

    private boolean holidayModeOn;
    private String from;
    private String to;

    public boolean isHolidayModeOn() {
        return holidayModeOn;
    }

    public void setHolidayModeOn(boolean holidayModeOn) {
        this.holidayModeOn = holidayModeOn;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
