package com.blackducksoftware.integration.fortify.batch.reader;

import java.util.Date;
import java.util.List;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.fortify.batch.model.VulnerableComponentView;
import com.blackducksoftware.integration.fortify.batch.util.HubServices;
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView;

@Component
public class BlackDuckReader implements ItemReader<List<VulnerableComponentView>>, StepExecutionListener {
    private Date bomUpdatedValueAt = null;

    @Autowired
    private HubServices hubServices;

    @Override
    public List<VulnerableComponentView> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        ProjectVersionView projectVersionItem = null;
        List<VulnerableComponentView> vulnerableComponentViews;
        try {
            projectVersionItem = hubServices.getProjectVersion("", "");
            vulnerableComponentViews = hubServices.getVulnerabilityComponentViews(projectVersionItem);
            bomUpdatedValueAt = hubServices.getBomLastUpdatedAt(projectVersionItem);
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
            throw new RuntimeException(e1);
        } catch (IntegrationException e1) {
            e1.printStackTrace();
            throw new RuntimeException(e1);
        }
        return vulnerableComponentViews;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        // TODO Auto-generated method stub

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }
}
