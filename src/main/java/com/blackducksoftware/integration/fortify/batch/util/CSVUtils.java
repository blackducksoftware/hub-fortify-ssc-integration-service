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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import com.blackducksoftware.integration.fortify.batch.model.Vulnerability;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class CSVUtils {

    public void writeToCSV(List<Vulnerability> vulnerabilities, String fileName, char delimiter)
            throws JsonGenerationException, JsonMappingException, IOException {
        // create mapper and schema
        CsvMapper mapper = new CsvMapper();
        // Create the schema with the header
        CsvSchema schema = mapper.schemaFor(Vulnerability.class).withHeader();
        schema = schema.withColumnSeparator(delimiter);

        // output writer
        ObjectWriter objectWriter = mapper.writer(schema);
        File file = new File(fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 1024);
        OutputStreamWriter writerOutputStream = new OutputStreamWriter(bufferedOutputStream, "UTF-8");
        // write to CSV file
        objectWriter.writeValue(writerOutputStream, vulnerabilities);
    }

}
