/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.
 *
 * The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.fortify.batch.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * This class will hold all the key value pairs that are specified in the attributes.properties file
 *
 * @author smanikantan
 *
 */
@Configuration
public class AttributeConstants {

    private final Properties properties = new Properties();

    private final HashMap<Object, Object> hmProps = new HashMap<>();

    @Autowired
    public AttributeConstants(PropertyConstants propertyConstants) {
        String attributesFileName = propertyConstants.getAttributeFilePath();
        InputStream attributeConfig = null;
        try {
            attributeConfig = new java.io.FileInputStream(attributesFileName);
            properties.load(attributeConfig);
            hmProps.putAll(properties);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Attribute file \"" + attributesFileName + "\" is not present", e);
        } catch (IOException e) {
            throw new RuntimeException("Error while loading the attributes file", e);
        } finally {
            try {
                if (attributeConfig != null) {
                    attributeConfig.close();
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
    public String getProperty(String key) {
        if (hmProps.containsKey(key)) {
            return (String) hmProps.get(key);
        } else {
            return null;
        }
    }
}
