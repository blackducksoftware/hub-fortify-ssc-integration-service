package com.blackducksoftware.integration.fortify.batch.writer;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.fortify.batch.model.Vulnerability;
import com.blackducksoftware.integration.fortify.batch.util.CSVUtils;
import com.blackducksoftware.integration.fortify.model.FileToken;
import com.blackducksoftware.integration.fortify.model.FileTokenResponse;
import com.blackducksoftware.integration.fortify.service.FortifyFileTokenApi;
import com.blackducksoftware.integration.fortify.service.FortifyUploadApi;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import okhttp3.ResponseBody;
import retrofit2.Call;

@Component
public class FortifyWriter implements ItemWriter<List<Vulnerability>>, StepExecutionListener {
    private StepExecution stepExecution;

    @Autowired
    private FortifyFileTokenApi fortifyFileTokenApi;

    @Autowired
    private FortifyUploadApi fortifyUploadApi;

    @Autowired
    private CSVUtils csvUtils;

    private final String UNDERSCORE = "_";

    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends List<Vulnerability>> vulnerabilities) throws JsonGenerationException, JsonMappingException, IOException {
        ExecutionContext stepContext = this.stepExecution.getExecutionContext();
        final String projectName = (String) stepContext.get("hubProjectName");
        final String projectVersionName = (String) stepContext.get("hubProjectVersionName");
        final int fortifyApplicationId = (int) stepContext.get("fortifyApplicationId");
        System.out.println("Writer :: Project name::" + projectName + ", projectVersionName::" + projectVersionName);

        final String fileName = projectName + UNDERSCORE + projectVersionName + UNDERSCORE
                + DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now()) + ".csv";

        csvUtils.writeToCSV((List<Vulnerability>) vulnerabilities, fileName, ',');

        String token = getFileToken();
        System.out.println("Token::" + token);
        uploadCSV(token, fileName, fortifyApplicationId);
    }

    private String getFileToken() throws IOException {
        FileToken fileToken = new FileToken();
        fileToken.setFileTokenType("UPLOAD");
        FileTokenResponse fileTokenResponse = fortifyFileTokenApi.getFileToken(fileToken);
        return fileTokenResponse.getData().getToken();
    }

    private void uploadCSV(String token, String fileName, int fortifyApplicationId) throws IOException {
        File file = new File(fileName);
        System.out.println("file::" + file);
        Call<ResponseBody> uploadVulnerabilityResponse = fortifyUploadApi.uploadVulnerabilityByProjectVersion(token, fortifyApplicationId, file);
        ResponseBody uploadVulnerabilityResponseBody = uploadVulnerabilityResponse.execute().body();
        System.out.println("uploadVulnerabilityResponse::" + uploadVulnerabilityResponseBody);
    }

    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }
}
