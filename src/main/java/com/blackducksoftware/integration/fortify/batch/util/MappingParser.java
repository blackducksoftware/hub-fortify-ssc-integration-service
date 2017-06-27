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
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.blackducksoftware.integration.fortify.batch.model.BlackDuckFortifyMapper;
import com.blackducksoftware.integration.fortify.batch.model.BlackDuckFortifyMapperGroup;
import com.blackducksoftware.integration.fortify.batch.model.HubProjectVersion;
import com.blackducksoftware.integration.fortify.model.CommitFortifyApplicationRequest;
import com.blackducksoftware.integration.fortify.model.CreateApplicationRequest;
import com.blackducksoftware.integration.fortify.model.CreateApplicationRequest.Project;
import com.blackducksoftware.integration.fortify.model.FortifyApplicationResponse;
import com.blackducksoftware.integration.fortify.model.UpdateFortifyApplicationAttributesRequest;
import com.blackducksoftware.integration.fortify.service.FortifyApplicationVersionApi;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;

/**
 * This class creates a mapping between the Fortify Application and Hub projects.
 *
 * @author hsathe
 *
 */
public final class MappingParser {

    private final static String FIELDS = "id";

    private final static String Q_project = "project.name:";

    private final static String Q_version = "name:";

    private final static String Q_connector = "+and+";

    private final static Logger logger = Logger.getLogger(MappingParser.class);

    /**
     * Creates a list a mappingObject read from the mapping.json file
     *
     * @param filePath
     *            - Filepath to mapping.json
     * @return List<BlackDuckForfifyMapper> Mapped objects with Fortify ID
     * @throws IOException
     */
    public static List<BlackDuckFortifyMapperGroup> createMapping(String filePath) throws JsonIOException, IOException {
        Gson gson;
        List<BlackDuckFortifyMapper> mapping;
        try {
            gson = new Gson();

            Type listType = new TypeToken<List<BlackDuckFortifyMapper>>() {
            }.getType();

            mapping = gson.fromJson(new FileReader(filePath), listType);
        } catch (JsonIOException jio) {
            logger.error("Exception occured while creating Mappings", jio);
            throw new JsonIOException("Exception occured while creating Mappings", jio);
        } catch (FileNotFoundException fe) {
            logger.error("File Not Found for creating Mappings", fe);
            throw new FileNotFoundException("Error finding the mapping.json file :: " + filePath);
        }

        return buildGroupedMappings(mapping);
    }

    /**
     *
     * This method, groups multiple Hub projects mapped to the same Fortify application.
     *
     * @param blackDuckFortifyMappers
     * @return
     * @throws IOException
     */
    private static List<BlackDuckFortifyMapperGroup> buildGroupedMappings(List<BlackDuckFortifyMapper> blackDuckFortifyMappers) throws IOException {

        Map<String, BlackDuckFortifyMapperGroup> mappings = new HashMap<>();
        try {

            for (BlackDuckFortifyMapper blackDuckFortifyMapper : blackDuckFortifyMappers) {
                int applicationId;
                List<HubProjectVersion> hubProjectVersions = new ArrayList<>();

                BlackDuckFortifyMapperGroup blackDuckFortifyMapperGroup;

                HubProjectVersion hubProjectVersion = new HubProjectVersion(blackDuckFortifyMapper.getHubProject(),
                        blackDuckFortifyMapper.getHubProjectVersion());

                String key = blackDuckFortifyMapper.getFortifyApplication() + '_' + blackDuckFortifyMapper.getFortifyApplicationVersion();

                if (mappings.containsKey(key)) {
                    blackDuckFortifyMapperGroup = mappings.get(key);
                    hubProjectVersions = blackDuckFortifyMapperGroup.getHubProjectVersion();
                    applicationId = blackDuckFortifyMapperGroup.getFortifyApplicationId();
                } else {
                    applicationId = getFortifyApplicationId(blackDuckFortifyMapper);
                }

                hubProjectVersions.add(hubProjectVersion);

                blackDuckFortifyMapperGroup = new BlackDuckFortifyMapperGroup(blackDuckFortifyMapper.getFortifyApplication(),
                        blackDuckFortifyMapper.getFortifyApplicationVersion(), hubProjectVersions, applicationId);

                mappings.put(key, blackDuckFortifyMapperGroup);

            }

        } catch (IOException ioe) {
            logger.error(ioe.getMessage(), ioe);
            throw new IOException(ioe);
        }

        return new ArrayList<>(mappings.values());
    }

    /**
     *
     * Finds Application Id for Fortify Application
     *
     * @param mapping
     * @return
     * @throws IOException
     */
    private static int getFortifyApplicationId(BlackDuckFortifyMapper mapping) throws IOException {
        String fortifyApplicationName = mapping.getFortifyApplication();
        String fortifyApplicationVersion = mapping.getFortifyApplicationVersion();
        int applicationId;
        try {
            String Q = Q_version + fortifyApplicationVersion + Q_connector + Q_project + fortifyApplicationName;
            logger.info("Querying fortify " + Q);
            FortifyApplicationResponse response = FortifyApplicationVersionApi.getApplicationVersionByName(FIELDS, Q);
            if (response.getData().size() != 0) {
                logger.info("Fortify Application Found :" + response.getData().get(0).getId());
                applicationId = response.getData().get(0).getId();
            } else {
                logger.info("Unable to find the Application on fortify, creating a new application");
                String queryParams = Q_project + fortifyApplicationName;
                String fieldParams = "id,project";
                logger.info("Querying fortify " + queryParams);
                FortifyApplicationResponse applicationResponse = FortifyApplicationVersionApi.getApplicationVersionByName(fieldParams, queryParams);
                CreateApplicationRequest createRequest;
                if (applicationResponse.getData().size() != 0) {
                    // Create only version
                    int parentApplicationId = applicationResponse.getData().get(0).getProject().getId();
                    createRequest = createVersionRequest(parentApplicationId, fortifyApplicationVersion);
                } else {
                    // Create both new Application and Version
                    createRequest = createApplicationVersionRequest(fortifyApplicationName, fortifyApplicationVersion);
                }
                applicationId = createApplicationVersion(createRequest);
                // element.setFortifyApplicationId(applicationId);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new IOException(e);
        }
        return applicationId;
    }

    /**
     * Creates a new Application Version, updates the attributes and commits the application to mark it complete on the
     * UI
     *
     * @param createRequest
     * @return int - Application ID
     * @throws IOException
     */
    private static int createApplicationVersion(CreateApplicationRequest createRequest) throws IOException {
        // CreateApplicationRequest createRequest = buildRequestForFortifyApplication(fortifyApplicationName,
        // fortifyApplicationVersion);
        int applicationId = 0;
        int SUCCESS = 201;
        try {
            applicationId = FortifyApplicationVersionApi.createApplicationVersion(createRequest);

            String attributeValuesTemplate = "[{\"attributeDefinitionId\":5,\"values\":[{\"guid\":\"New\"}],\"value\":null},{\"attributeDefinitionId\":6,\"values\":[{\"guid\":\"Internal\"}],\"value\":null},{\"attributeDefinitionId\":7,\"values\":[{\"guid\":\"internalnetwork\"}],\"value\":null},{\"attributeDefinitionId\":10,\"values\":[],\"value\":null},{\"attributeDefinitionId\":11,\"values\":[],\"value\":null},{\"attributeDefinitionId\":12,\"values\":[],\"value\":null},{\"attributeDefinitionId\":1,\"values\":[{\"guid\":\"High\"}],\"value\":null},{\"attributeDefinitionId\":2,\"values\":[],\"value\":null},{\"attributeDefinitionId\":3,\"values\":[],\"value\":null},{\"attributeDefinitionId\":4,\"values\":[],\"value\":null}]";
            Gson gson = new Gson();
            Type listType = new TypeToken<List<UpdateFortifyApplicationAttributesRequest>>() {
            }.getType();

            List<UpdateFortifyApplicationAttributesRequest> updateAttributerequest = gson.fromJson(attributeValuesTemplate, listType);
            int responseCode = FortifyApplicationVersionApi.updateApplicationAttributes(applicationId, updateAttributerequest);
            if (responseCode == SUCCESS) {
                logger.info("Updated attributes for creating new fortify application");
            }

            CommitFortifyApplicationRequest commitRequest = new CommitFortifyApplicationRequest();
            commitRequest.setCommitted(true);
            int commitResponseCode = FortifyApplicationVersionApi.commitApplicationVersion(applicationId, commitRequest);
            if (commitResponseCode == SUCCESS) {
                logger.info("New Fortify application is now committed");
            }
        } catch (IOException e) {
            logger.error("Unable to create a new fortify application", e);
            throw new IOException(e);
        }
        return applicationId;
    }

    /**
     * Builds a request for creating new Fortify Version
     *
     * @param applicationId
     * @param fortifyApplicationVersion
     * @return Request object for
     */
    private static CreateApplicationRequest createVersionRequest(int applicationId, String fortifyApplicationVersion) {
        CreateApplicationRequest createRequest = new CreateApplicationRequest();
        String TEMPLATE = "Prioritized-HighRisk-Project-Template";
        createRequest.setActive(true);
        createRequest.setName(fortifyApplicationVersion);
        createRequest.setCommitted(false);
        createRequest.setIssueTemplateId(TEMPLATE);
        createRequest.setDescription("Built using API");

        Project proj = createRequest.new Project();
        proj.setId(String.valueOf(applicationId));
        createRequest.setProject(proj);

        return createRequest;
    }

    /**
     * Builds a request for creating a new Fortify Application Version
     *
     * @param fortifyProjectName
     * @param fortifyProjectVersion
     * @return
     */
    private static CreateApplicationRequest createApplicationVersionRequest(String fortifyProjectName, String fortifyProjectVersion) {
        CreateApplicationRequest request = new CreateApplicationRequest();
        String TEMPLATE = "Prioritized-HighRisk-Project-Template";
        request.setActive(true);
        request.setName(fortifyProjectVersion);
        request.setCommitted(false);
        request.setIssueTemplateId(TEMPLATE);
        request.setDescription("Built using API");

        Project project = request.new Project();

        project.setName(fortifyProjectName);
        project.setDescription("Built using API");
        project.setIssueTemplateId(TEMPLATE);
        project.setId("");
        request.setProject(project);

        return request;
    }

}
