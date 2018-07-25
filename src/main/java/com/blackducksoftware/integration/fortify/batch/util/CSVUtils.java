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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileSystemNotFoundException;
import java.util.List;

import com.blackducksoftware.integration.fortify.batch.model.Vulnerability;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

/**
 * This class will be used to render the content in CSV
 *
 * @author smanikantan
 *
 */
public final class CSVUtils {

    /**
     * It will be used to render the list of vulnerabilities in CSV
     *
     * @param vulnerabilities
     * @param fileName
     * @param delimiter
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    @SuppressWarnings("resource")
    public static void writeToCSV(final List<Vulnerability> vulnerabilities, final String fileName, final char delimiter)
            throws JsonGenerationException, JsonMappingException, FileNotFoundException, UnsupportedEncodingException, IOException {
        // create mapper and schema
        final CsvMapper mapper = new CsvMapper();
        // Create the schema with the header
        CsvSchema schema = mapper.schemaFor(Vulnerability.class).withHeader();
        schema = schema.withColumnSeparator(delimiter);

        // output writer
        final ObjectWriter objectWriter = mapper.writer(schema);
        final File file = new File(fileName);
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (final FileNotFoundException e) {
            throw new FileSystemNotFoundException(fileName + " CSV file is not created successfully");
        }
        final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 1024);
        OutputStreamWriter writerOutputStream;
        try {
            writerOutputStream = new OutputStreamWriter(bufferedOutputStream, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new UnsupportedEncodingException(e.getMessage());
        }
        // write to CSV file
        try {
            objectWriter.writeValue(writerOutputStream, vulnerabilities);
        } catch (final IOException e) {
            throw new IOException("Error while rendering the vulnerabilities in CSV file::" + fileName, e);
        }
    }

}
