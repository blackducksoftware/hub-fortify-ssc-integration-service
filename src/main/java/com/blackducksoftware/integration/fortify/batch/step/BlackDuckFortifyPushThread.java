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
package com.blackducksoftware.integration.fortify.batch.step;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeParseException;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.fortify.batch.util.PropertyConstants;
import com.blackducksoftware.integration.fortify.model.FileToken;
import com.blackducksoftware.integration.fortify.service.FortifyFileTokenApi;
import com.blackducksoftware.integration.fortify.service.FortifyUploadApi;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * This class will be used as Thread and it will perform the following tasks in
 * parallel for each Hub-Fortify mapper 1) Get the Hub project version
 * information 2) Get the Maximum BOM updated date and Last successful runtime
 * of the job 3) Compare the dates, if the last BOM updated date is lesser than
 * last successful runtime of the job, do nothing else perform the following the
 * task i) Get the Vulnerabilities and merged it to single list ii) Write it to
 * CSV iii) Upload the CSV to Fortify
 *
 * @author smanikantan
 *
 */
public class BlackDuckFortifyPushThread implements Callable<Boolean> {

	private final static Logger logger = Logger.getLogger(BlackDuckFortifyPushThread.class);

	private final FortifyFileTokenApi fortifyFileTokenApi;

	private final FortifyUploadApi fortifyUploadApi;

	private final PropertyConstants propertyConstants;

	public BlackDuckFortifyPushThread(final FortifyFileTokenApi fortifyFileTokenApi,
			final FortifyUploadApi fortifyUploadApi, final PropertyConstants propertyConstants) {
		this.fortifyFileTokenApi = fortifyFileTokenApi;
		this.fortifyUploadApi = fortifyUploadApi;
		this.propertyConstants = propertyConstants;
	}

	@Override
	public Boolean call()
			throws DateTimeParseException, IntegrationException, IllegalArgumentException, JsonGenerationException,
			JsonMappingException, FileNotFoundException, UnsupportedEncodingException, IOException {

		final String fileName = propertyConstants.getReportDir() + "/" + propertyConstants.getVulnerabilityFileName();

		// Get the file token for upload
		final String token = getFileToken();

		// Upload the vulnerabilities CSV to Fortify
		uploadCSV(token, fileName, propertyConstants.getFortifyApplicationId());

		// Delete the file token that is created for upload
		fortifyFileTokenApi.deleteFileToken();
		// }

		return true;
	}

	/**
	 * Get the new file token from Fortify to upload the vulnerabilities
	 *
	 * @return
	 * @throws IOException
	 * @throws IntegrationException
	 */
	private String getFileToken() throws IOException, IntegrationException {
		final FileToken fileToken = new FileToken("UPLOAD");
		return fortifyFileTokenApi.getFileToken(fileToken);
	}

	/**
	 * Upload the CSV to Fortify
	 *
	 * @param token
	 * @param fileName
	 * @param fortifyApplicationId
	 * @throws IOException
	 * @throws IntegrationException
	 */
	private void uploadCSV(final String token, final String fileName, final int fortifyApplicationId)
			throws IOException, IntegrationException {
		final File file = new File(fileName);
		logger.debug("Uploading " + file.getName() + " to fortify");
		// Call Fortify upload
		final boolean response = fortifyUploadApi.uploadVulnerabilityByProjectVersion(token, fortifyApplicationId,
				file);

		// Check if the upload is submitted successfully, if not don't delete the CSV
		// files. It can be used for
		// debugging
		if (response) {
			if (file.exists()) {
				file.delete();
			}
			logger.info(file.getName() + " File uploaded successfully");
		}
	}

}
