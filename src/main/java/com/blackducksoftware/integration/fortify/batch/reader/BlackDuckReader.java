package com.blackducksoftware.integration.fortify.batch.reader;

import java.util.Date;
import java.util.List;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.fortify.batch.model.BlackDuckFortifyMapper;
import com.blackducksoftware.integration.fortify.batch.model.VulnerableComponentView;
import com.blackducksoftware.integration.fortify.batch.util.HubServices;
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView;

@Configuration
public class BlackDuckReader implements ItemReader<List<VulnerableComponentView>>, StepExecutionListener {
    private StepExecution stepExecution;

    private List<BlackDuckFortifyMapper> blackDuckFortifyMappers;

    private int readCount = 0;

    private BlackDuckFortifyMapper blackDuckFortifyMapper = null;

    private Date bomUpdatedValueAt;

    @Autowired
    private HubServices hubServices;

    @Override
    public synchronized List<VulnerableComponentView> read() throws IllegalArgumentException, IntegrationException {

        if (readCount < blackDuckFortifyMappers.size()) {
            blackDuckFortifyMapper = blackDuckFortifyMappers.get(readCount++);
            System.out.println("blackDuckFortifyMapper::" + blackDuckFortifyMapper.toString());
            ProjectVersionView projectVersionItem = null;
            List<VulnerableComponentView> vulnerableComponentViews;
            projectVersionItem = hubServices.getProjectVersion(blackDuckFortifyMapper.getHubProject(), blackDuckFortifyMapper.getHubProjectVersion());
            vulnerableComponentViews = hubServices.getVulnerabilityComponentViews(projectVersionItem);
            bomUpdatedValueAt = hubServices.getBomLastUpdatedAt(projectVersionItem);
            ExecutionContext stepContext = this.stepExecution.getExecutionContext();
            stepContext.put("hubProjectName", blackDuckFortifyMapper.getHubProject());
            stepContext.put("hubProjectVersionName", blackDuckFortifyMapper.getHubProjectVersion());
            stepContext.put("fortifyApplicationId", blackDuckFortifyMapper.getFortifyApplicationId());
            stepContext.put("bomUpdatedValueAt", bomUpdatedValueAt);
            return vulnerableComponentViews;
        } else {
            return null;
        }
    }

    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void beforeStep(StepExecution stepExecution) {
        blackDuckFortifyMappers = (List<BlackDuckFortifyMapper>) stepExecution.getJobExecution().getExecutionContext().get("BlackDuckFortifyMapper");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }
}
