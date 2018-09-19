/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.implementist.nisljournalmanager;

import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 *
 * @author Implementist
 */
public class SensitivePropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    private static final DESUtil DES_UTIL = new DESUtil();

    private static final Set<String> SENSITIVE_PROPERTIES = new HashSet<String>() {
        {
            add("username");
            add("password");
        }
    };

    @Override
    protected String convertProperty(String propertyName, String propertyValue) {
        if (SENSITIVE_PROPERTIES.contains(propertyName)) {
            return DES_UTIL.decode(propertyValue);
        }
        return propertyValue;
    }
}
