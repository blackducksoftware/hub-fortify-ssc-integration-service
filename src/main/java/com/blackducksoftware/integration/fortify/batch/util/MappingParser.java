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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.blackducksoftware.integration.fortify.batch.model.BlackDuckFortifyMapper;
import com.blackducksoftware.integration.fortify.model.FortifyApplicationResponse;
import com.blackducksoftware.integration.fortify.service.FortifyApplicationVersionApi;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;

public final class MappingParser {

    private final static String FIELDS = "id";

    private final static String Q_project = "project.name:";

    private final static String Q_version = "name:";

    private final static String Q_connector = "+and+";

    /**
     * @param filePath
     * @return List<BlackDuckForfifyMapper>
     */
    public static List<BlackDuckFortifyMapper> createMapping(String filePath) {
        Gson gson;
        List<BlackDuckFortifyMapper> mappingObj = null;
        try {
            gson = new Gson();

            Type listType = new TypeToken<List<BlackDuckFortifyMapper>>() {
            }.getType();

            List<BlackDuckFortifyMapper> mapping = gson.fromJson(new FileReader(filePath), listType);
            mappingObj = addApplicationIdToResponse(mapping);

        } catch (JsonIOException jio) {
            // To Do: Log information
            jio.printStackTrace();
        } catch (FileNotFoundException fe) {
            // To Do: Log information
            fe.printStackTrace();
        }

        return mappingObj;
    }

    public static List<BlackDuckFortifyMapper> addApplicationIdToResponse(List<BlackDuckFortifyMapper> mapping) {
        for (BlackDuckFortifyMapper element : mapping) {
            String fortify_applicationName = element.getFortifyApplication();
            String fortify_applicationVersion = element.getFortifyApplicationVersion();

            try {
                String Q = Q_version + fortify_applicationVersion + Q_connector + Q_project + fortify_applicationName;
                FortifyApplicationResponse response = FortifyApplicationVersionApi.getApplicationByName(FIELDS, Q);
                System.out.println("Fortify Application Id::" + response.getData().get(0).getId());
                if (response.getData() != null) {
                    element.setFortifyApplicationId(response.getData().get(0).getId());
                } else {
                    // To Do Create application
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                throw new RuntimeException(e);
            }
        }

        return mapping;
    }

}
