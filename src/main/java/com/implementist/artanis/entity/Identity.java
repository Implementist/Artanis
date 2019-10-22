package com.implementist.artanis.entity;

import lombok.Data;

/**
 * @author Implementist
 */
@Data
public class Identity {

    /**
     * 邮箱账户
     */
    private String from;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 账户授权码
     */
    private String authCode;
}