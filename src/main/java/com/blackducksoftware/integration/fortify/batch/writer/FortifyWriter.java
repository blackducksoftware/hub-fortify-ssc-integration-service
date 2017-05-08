package com.blackducksoftware.integration.fortify.batch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.fortify.batch.model.Vulnerability;
import com.blackducksoftware.integration.fortify.batch.util.CSVUtils;

@Component
public class FortifyWriter implements ItemWriter<List<Vulnerability>> {

    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends List<Vulnerability>> vulnerabilities) throws Exception {
        CSVUtils csvUtils = new CSVUtils();
        csvUtils.writeToCSV((List<Vulnerability>) vulnerabilities, "security.csv", ',');
    }
}
