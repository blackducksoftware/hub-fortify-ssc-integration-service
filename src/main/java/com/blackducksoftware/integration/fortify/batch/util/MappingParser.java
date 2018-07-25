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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.fortify.batch.model.BlackDuckFortifyMapper;
import com.blackducksoftware.integration.fortify.batch.model.BlackDuckFortifyMapperGroup;
import com.blackducksoftware.integration.fortify.batch.model.HubProjectVersion;
import com.blackducksoftware.integration.fortify.model.CommitFortifyApplicationRequest;
import com.blackducksoftware.integration.fortify.model.CreateApplicationRequest;
import com.blackducksoftware.integration.fortify.model.FortifyApplicationResponse;
import com.blackducksoftware.integration.fortify.model.FortifyAttributeDefinitionResponse;
import com.blackducksoftware.integration.fortify.model.FortifyAttributeDefinitionResponse.FortifyAttributeDefinition;
import com.blackducksoftware.integration.fortify.model.FortifyAttributeDefinitionResponse.FortifyAttributeDefinition.Option;
import com.blackducksoftware.integration.fortify.model.UpdateFortifyApplicationAttributesRequest;
import com.blackducksoftware.integration.fortify.model.UpdateFortifyApplicationAttributesRequest.Value;
import com.blackducksoftware.integration.fortify.service.FortifyApplicationVersionApi;
import com.blackducksoftware.integration.fortify.service.FortifyAttributeDefinitionApi;
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

    private final static String Q_connector = "%2Band%2B";

    private final static String DYNAMIC_SCAN_REQUEST = "DYNAMIC_SCAN_REQUEST";

    private final static Logger logger = Logger.getLogger(MappingParser.class);

    private final FortifyApplicationVersionApi fortifyApplicationVersionApi;

    private final FortifyAttributeDefinitionApi fortifyAttributeDefinitionApi;

    private final PropertyConstants propertyConstants;

    private final AttributeConstants attributeConstants;

    public MappingParser(final FortifyApplicationVersionApi fortifyApplicationVersionApi, final FortifyAttributeDefinitionApi fortifyAttributeDefinitionApi,
            final PropertyConstants propertyConstants, final AttributeConstants attributeConstants) {
        this.fortifyApplicationVersionApi = fortifyApplicationVersionApi;
        this.fortifyAttributeDefinitionApi = fortifyAttributeDefinitionApi;
        this.propertyConstants = propertyConstants;
        this.attributeConstants = attributeConstants;
    }

    /**
     * Creates a list a mappingObject read from the mapping.json file
     *
     * @param filePath
     *            - Filepath to mapping.json
     * @return List<BlackDuckForfifyMapper> Mapped objects with Fortify ID
     * @throws IOException
     * @throws IntegrationException
     */
    public List<BlackDuckFortifyMapperGroup> createMapping(final String filePath) throws JsonIOException, IOException, IntegrationException {
        Gson gson;
        List<BlackDuckFortifyMapper> mapping;
        try {
            gson = new Gson();

            final Type listType = new TypeToken<List<BlackDuckFortifyMapper>>() {
            }.getType();

            mapping = gson.fromJson(new FileReader(filePath), listType);
        } catch (final JsonIOException jio) {
            logger.error("Exception occured while creating Mappings", jio);
            throw new JsonIOException("Exception occured while creating Mappings", jio);
        } catch (final FileNotFoundException fe) {
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
     * @throws IntegrationException
     */
    private List<BlackDuckFortifyMapperGroup> buildGroupedMappings(final List<BlackDuckFortifyMapper> blackDuckFortifyMappers)
            throws IOException, IntegrationException {

        final Map<String, BlackDuckFortifyMapperGroup> mappings = new HashMap<>();
        try {

            for (final BlackDuckFortifyMapper blackDuckFortifyMapper : blackDuckFortifyMappers) {
                int applicationId;
                List<HubProjectVersion> hubProjectVersions = new ArrayList<>();

                BlackDuckFortifyMapperGroup blackDuckFortifyMapperGroup;

                final HubProjectVersion hubProjectVersion = new HubProjectVersion(blackDuckFortifyMapper.getHubProject(),
                        blackDuckFortifyMapper.getHubProjectVersion());

                final String key = blackDuckFortifyMapper.getFortifyApplication() + '_' + blackDuckFortifyMapper.getFortifyApplicationVersion();

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

        } catch (final IOException ioe) {
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
     * @throws IntegrationException
     */
    private int getFortifyApplicationId(final BlackDuckFortifyMapper mapping) throws IntegrationException, IOException {
        final String fortifyApplicationName = mapping.getFortifyApplication();
        final String fortifyApplicationVersion = mapping.getFortifyApplicationVersion();
        int applicationId;
        try {
            final String Q = Q_version + VulnerabilityUtil.encodeString(fortifyApplicationVersion) + Q_connector + Q_project + fortifyApplicationName;
            logger.info("Querying fortify " + Q);
            final FortifyApplicationResponse response = fortifyApplicationVersionApi.getApplicationVersionByName(FIELDS, Q);
            if (response.getData().size() != 0) {
                logger.info("Fortify Application Found :" + response.getData().get(0).getId());
                applicationId = response.getData().get(0).getId();
            } else {
                logger.info("Unable to find the Application on fortify application " + fortifyApplicationName + ", creating a new application");
                final String queryParams = Q_project + fortifyApplicationName;
                final String fieldParams = "id,project";
                final FortifyApplicationResponse applicationResponse = fortifyApplicationVersionApi.getApplicationVersionByName(fieldParams, queryParams);
                CreateApplicationRequest createRequest;
                if (applicationResponse.getData().size() != 0) {
                    // Create only version
                    final int parentApplicationId = applicationResponse.getData().get(0).getProject().getId();
                    createRequest = createVersionRequest(parentApplicationId, fortifyApplicationVersion);
                } else {
                    // Create both new Application and Version
                    createRequest = createApplicationVersionRequest(fortifyApplicationName, fortifyApplicationVersion);
                }
                applicationId = createApplicationVersion(createRequest);
                // element.setFortifyApplicationId(applicationId);
            }
        } catch (final IOException e) {
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
     * @throws IntegrationException
     */
    private int createApplicationVersion(final CreateApplicationRequest createRequest) throws IOException, IntegrationException {
        int applicationId = 0;
        applicationId = fortifyApplicationVersionApi.createApplicationVersion(createRequest);
        try {
            final List<UpdateFortifyApplicationAttributesRequest> updateAttributerequest = addCustomAttributes();
            logger.info("updateAttributerequest::" + updateAttributerequest);
            fortifyApplicationVersionApi.updateApplicationAttributes(applicationId, updateAttributerequest);

            final CommitFortifyApplicationRequest commitRequest = new CommitFortifyApplicationRequest(true);
            fortifyApplicationVersionApi.commitApplicationVersion(applicationId, commitRequest);
        } catch (final IOException e) {
            fortifyApplicationVersionApi.deleteApplicationVersion(applicationId);
            throw new IOException(e);
        } catch (final IntegrationException e) {
            fortifyApplicationVersionApi.deleteApplicationVersion(applicationId);
            throw new IntegrationException(e);
        }
        return applicationId;
    }

    /**
     * Add the custom required attributes to fortify update attribute definition request
     *
     * @param updateAttributerequests
     * @return
     * @throws IOException
     * @throws IntegrationException
     */
    public List<UpdateFortifyApplicationAttributesRequest> addCustomAttributes()
            throws IOException, IntegrationException {
        final FortifyAttributeDefinitionResponse fortifyAttributeDefinitionResponse = fortifyAttributeDefinitionApi.getAttributeDefinitions();
        final List<String> ignoreAttributes = Arrays.asList("Known Compliance Obligations", "Data Classification", "Application Classification", "Interfaces",
                "Development Languages", "Authentication System");
        final List<UpdateFortifyApplicationAttributesRequest> updateAttributerequests = new ArrayList<>();
        logger.debug(fortifyAttributeDefinitionResponse);
        for (final FortifyAttributeDefinition fortifyAttributeDefinition : fortifyAttributeDefinitionResponse.getApplicationAttributeDefinitions()) {
            if (DYNAMIC_SCAN_REQUEST.equalsIgnoreCase(fortifyAttributeDefinition.getCategory())) {
                continue;
            }

            /*
             * If the values are in the ignore list, then no validation on the value
             * else if the values are null for other list, throw error
             * else perform the validation and add the values to the list
             */

            if (Collections.binarySearch(ignoreAttributes, fortifyAttributeDefinition.getName(), String.CASE_INSENSITIVE_ORDER) == 0
                    && StringUtils.isEmpty(attributeConstants.getProperty(fortifyAttributeDefinition.getName()))) {
                logger.debug("Attribute name::" + fortifyAttributeDefinition.getName() + ", value::"
                        + attributeConstants.getProperty(fortifyAttributeDefinition.getName()));
                updateAttributerequests.add(new UpdateFortifyApplicationAttributesRequest(fortifyAttributeDefinition.getId(), new ArrayList<Value>(), null));
            } else if (StringUtils.isEmpty(attributeConstants.getProperty(fortifyAttributeDefinition.getName()))) {
                throw new IntegrationException(
                        "Attribute value for " + fortifyAttributeDefinition.getName() + " is missing in " + propertyConstants.getAttributeFilePath());
            } else {
                updateAttributerequests.add(addCustomAttributes(fortifyAttributeDefinition));
            }
        }
        return updateAttributerequests;
    }

    /**
     * Add the custom required attributes to fortify update attribute definition request
     *
     * @param fortifyAttributeDefinition
     * @return
     * @throws IntegrationException
     */
    private UpdateFortifyApplicationAttributesRequest addCustomAttributes(final FortifyAttributeDefinition fortifyAttributeDefinition)
            throws IntegrationException {
        List<Value> values = null;
        Object value = null;
        final String dataType = fortifyAttributeDefinition.getType();
        try {
            switch (dataType) {
            case "SINGLE":
            case "MULTIPLE":
                values = new ArrayList<>();
                values.addAll(addSingleOrMultipleDataTypeAttributes(fortifyAttributeDefinition));
                break;
            case "TEXT":
            case "LONG_TEXT":
            case "SENSITIVE_TEXT":
                value = attributeConstants.getProperty(fortifyAttributeDefinition.getName().trim());
                break;
            case "INTEGER":
                value = Integer.parseInt(attributeConstants.getProperty(fortifyAttributeDefinition.getName().trim()));
                break;
            case "BOOLEAN":
                value = Boolean.parseBoolean(attributeConstants.getProperty(fortifyAttributeDefinition.getName().trim()));
                break;
            case "DATE":
                final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate.parse(attributeConstants.getProperty(fortifyAttributeDefinition.getName().trim()), formatter);
                value = attributeConstants.getProperty(fortifyAttributeDefinition.getName().trim());
                break;
            default:
                value = attributeConstants.getProperty(fortifyAttributeDefinition.getName().trim());
            }
        } catch (final NumberFormatException e) {
            throw new IntegrationException(fortifyAttributeDefinition.getName() + "'s attribute value \""
                    + attributeConstants.getProperty(fortifyAttributeDefinition.getName().trim()) + "\" is not a valid " + dataType + "!");
        } catch (final DateTimeParseException e) {
            throw new IntegrationException(fortifyAttributeDefinition.getName() + "'s attribute value \""
                    + attributeConstants.getProperty(fortifyAttributeDefinition.getName().trim())
                    + "\" is not a valid date! Please make sure the date format is yyyy-MM-dd");
        }
        logger.debug("Attribute name::" + fortifyAttributeDefinition.getName() + ", values::" + values + ", value::" + value);
        return new UpdateFortifyApplicationAttributesRequest(fortifyAttributeDefinition.getId(), values, value);
    }

    /**
     * Return single or multiple option data type attribute values
     *
     * @param fortifyAttributeDefinition
     * @return
     * @throws IntegrationException
     */
    private List<Value> addSingleOrMultipleDataTypeAttributes(final FortifyAttributeDefinition fortifyAttributeDefinition) throws IntegrationException {
        final List<Value> values = new ArrayList<>();
        Value value;
        if ("SINGLE".equalsIgnoreCase(fortifyAttributeDefinition.getType())) {
            value = new Value(validateSingleAndMultipleDataTypeAttributeValue(fortifyAttributeDefinition,
                    attributeConstants.getProperty(fortifyAttributeDefinition.getName())));
            values.add(value);
        } else {
            final String[] valueArr = attributeConstants.getProperty(fortifyAttributeDefinition.getName()).split(",");
            for (final String strValue : valueArr) {
                value = new Value(validateSingleAndMultipleDataTypeAttributeValue(fortifyAttributeDefinition, strValue.trim()));
                values.add(value);
            }
        }
        return values;
    }

    /**
     * Validate the single and multiple option data type
     *
     * @param fortifyAttributeDefinition
     * @throws IntegrationException
     */
    private String validateSingleAndMultipleDataTypeAttributeValue(final FortifyAttributeDefinition fortifyAttributeDefinition, final String value)
            throws IntegrationException {
        final List<Option> options = fortifyAttributeDefinition.getOptions();
        for (final Option option : options) {
            if (option.getName().equalsIgnoreCase(value)) {
                return option.getGuid();
            }
        }
        throw new IntegrationException(fortifyAttributeDefinition.getName() + "'s attribute value \""
                + attributeConstants.getProperty(fortifyAttributeDefinition.getName().trim()) + "\" is not a valid option!");
    }

    /**
     * Builds a request for creating new Fortify Version
     *
     * @param applicationId
     * @param fortifyApplicationVersion
     * @return Request object for
     */
    private CreateApplicationRequest createVersionRequest(final int applicationId, final String fortifyApplicationVersion) {

        final String TEMPLATE = "Prioritized-HighRisk-Project-Template";
        return new CreateApplicationRequest(fortifyApplicationVersion, "Built using API", true, false,
                new CreateApplicationRequest.Project(String.valueOf(applicationId), null, null, null), TEMPLATE);
    }

    /**
     * Builds a request for creating a new Fortify Application Version
     *
     * @param fortifyProjectName
     * @param fortifyProjectVersion
     * @return
     */
    private CreateApplicationRequest createApplicationVersionRequest(final String fortifyProjectName, final String fortifyProjectVersion) {
        final String TEMPLATE = "Prioritized-HighRisk-Project-Template";
        return new CreateApplicationRequest(fortifyProjectVersion, "Built using API", true, false,
                new CreateApplicationRequest.Project("", fortifyProjectName, "Built using API", TEMPLATE), TEMPLATE);
    }

}
