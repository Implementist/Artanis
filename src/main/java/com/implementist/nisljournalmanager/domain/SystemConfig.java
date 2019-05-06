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
    private String holidayFrom;
    private String holidayTo;
    private boolean workdayModeOn;
    private String workdayFrom;
    private String workdayTo;

    public boolean isHolidayModeOn() {
        return holidayModeOn;
    }

    public void setHolidayModeOn(boolean holidayModeOn) {
        this.holidayModeOn = holidayModeOn;
    }

    public String getHolidayFrom() {
        return holidayFrom;
    }

    public void setHolidayFrom(String holidayFrom) {
        this.holidayFrom = holidayFrom;
    }

    public String getHolidayTo() {
        return holidayTo;
    }

    public void setHolidayTo(String holidayTo) {
        this.holidayTo = holidayTo;
    }

    public boolean isWorkdayModeOn() {
        return workdayModeOn;
    }

    public void setWorkdayModeOn(boolean workdayModeOn) {
        this.workdayModeOn = workdayModeOn;
    }

    public String getWorkdayFrom() {
        return workdayFrom;
    }

    public void setWorkdayFrom(String workdayFrom) {
        this.workdayFrom = workdayFrom;
    }

    public String getWorkdayTo() {
        return workdayTo;
    }

    public void setWorkdayTo(String workdayTo) {
        this.workdayTo = workdayTo;
    }
}
