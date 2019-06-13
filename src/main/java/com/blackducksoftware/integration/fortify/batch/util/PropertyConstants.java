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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * This class is to read the application properties key-value pairs
 *
 * @author smanikantan
 *
 */
@Configuration
public class PropertyConstants {

	private String fortifyUserName;

	@Value("${fortify.username}")
	public void setFortifyUserName(final String fortifyUserName) {
		this.fortifyUserName = fortifyUserName;
	}

	private String fortifyPassword;

	@Value("${fortify.password}")
	public void setFortifyPassword(final String fortifyPassword) {
		this.fortifyPassword = fortifyPassword;
	}

	private String fortifyServerUrl;

	@Value("${fortify.server.url}")
	public void setFortifyServerUrl(final String fortifyServerUrl) {
		this.fortifyServerUrl = fortifyServerUrl;
	}

	private int fortifyApplicationId;

	@Value("${fortify.application.id}")
	public void setFortifyApplicationId(final int fortifyApplicationId) {
		this.fortifyApplicationId = fortifyApplicationId;
	}

	private String reportDir;

	@Value("${fortify.report.dir}")
	public void setReportDir(final String reportDir) {
		this.reportDir = reportDir;
	}

	private String vulnerabilityFileName;

	@Value("${vulnerability.file.name}")
	public void setVulnerabilityFileName(final String vulnerabilityFileName) {
		this.vulnerabilityFileName = vulnerabilityFileName;
	}

	private String logLevel;

	@Value("${logging.level.com.blackducksoftware}")
	public void setLogLevel(final String logLevel) {
		this.logLevel = logLevel;
	}

	public String getFortifyUserName() {
		return fortifyUserName;
	}

	public String getFortifyPassword() {
		return fortifyPassword;
	}

	public String getFortifyServerUrl() {
		return fortifyServerUrl;
	}

	public int getFortifyApplicationId() {
		return fortifyApplicationId;
	}

	public String getVulnerabilityFileName() {
		return vulnerabilityFileName;
	}

	public String getReportDir() {
		return reportDir;
	}

	public String getLogLevel() {
		return logLevel;
	}
}
