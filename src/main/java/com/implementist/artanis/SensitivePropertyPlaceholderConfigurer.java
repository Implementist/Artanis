package com.implementist.artanis;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Implementist
 */
public class SensitivePropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    private static final DesUtil DES_UTIL = new DesUtil();

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
