package com.implementist.artanis.entity;

import lombok.Data;

/**
 * @author Implementist
 */
@Data
public class SystemConfig {

    /**
     * 节假日模式是否开启
     */
    private boolean holidayModeOn;
    /**
     * 节假日起始日期
     */
    private String holidayFrom;
    /**
     * 节假日截止日期
     */
    private String holidayTo;
    /**
     * 调休模式是否开启
     */
    private boolean workdayModeOn;
    /**
     * 调休日起始日期
     */
    private String workdayFrom;
    /**
     * 调休日截止日期
     */
    private String workdayTo;
}