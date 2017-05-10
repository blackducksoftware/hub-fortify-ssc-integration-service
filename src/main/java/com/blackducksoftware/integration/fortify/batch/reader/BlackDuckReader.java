package com.blackducksoftware.integration.fortify.batch.reader;

import java.util.Date;
import java.util.List;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.fortify.batch.model.BlackDuckFortifyMapper;
import com.blackducksoftware.integration.fortify.batch.model.VulnerableComponentView;
import com.blackducksoftware.integration.fortify.batch.util.HubServices;
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView;

@Configuration
public class BlackDuckReader<T> extends ListItemReader<T> implements StepExecutionListener {

    private StepExecution stepExecution;

    private BlackDuckFortifyMapper blackDuckFortifyMapper = null;

    private Date bomUpdatedValueAt;

    @Autowired
    private HubServices hubServices;

    @SuppressWarnings("unchecked")
    @Override
    public synchronized T read() throws IllegalArgumentException, IntegrationException {
        blackDuckFortifyMapper = (BlackDuckFortifyMapper) super.read();
        if (blackDuckFortifyMapper != null) {
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
            return (T) vulnerableComponentViews;
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
        ExecutionContext jobContext = this.stepExecution.getJobExecution().getExecutionContext();
        list = (List<T>) jobContext.get("blackDuckFortifyMapper");
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }
}
