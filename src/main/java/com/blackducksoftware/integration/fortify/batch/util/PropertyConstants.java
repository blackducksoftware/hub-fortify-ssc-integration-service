/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.fortify.batch.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public final class PropertyConstants {
    private static final Properties properties = new Properties();

    private static final String configFileName = "src/main/resources/application.properties";

    private static final HashMap<Object, Object> hmProps = new HashMap<>();
    static {
        InputStream isApplicationProperties = null;
        try {
            isApplicationProperties = new java.io.FileInputStream(configFileName);
            properties.load(isApplicationProperties);
            hmProps.putAll(properties);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (isApplicationProperties != null) {
                    isApplicationProperties.close();
                }
            } catch (IOException e) {

            }
        }
    }

    /**
     * Gets the property.
     *
     * @param key
     *            the key
     * @return the property
     */
    public static String getProperty(String key) {
        if (hmProps.containsKey(key)) {
            return (String) hmProps.get(key);
        } else {
            return null;
        }
    }
}
