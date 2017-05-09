package com.blackducksoftware.integration.fortify.batch.processor;

import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.fortify.batch.model.Vulnerability;
import com.blackducksoftware.integration.fortify.batch.model.VulnerableComponentView;

@Component
public class BlackDuckFortifyProcessor implements ItemProcessor<List<VulnerableComponentView>, List<Vulnerability>>, StepExecutionListener {
    private StepExecution stepExecution;

    private Date bomUpdatedValueAt;

    private final Function<VulnerableComponentView, Vulnerability> transformMapping = new Function<VulnerableComponentView, Vulnerability>() {

        @Override
        public Vulnerability apply(VulnerableComponentView vulnerableComponentView) {
            Vulnerability vulnerability = new Vulnerability();
            String[] componentVersionLinkArr = vulnerableComponentView.getComponentVersionLink().split("/");
            vulnerability.setProjectId(String.valueOf(componentVersionLinkArr[5]));
            vulnerability.setVersionId(String.valueOf(componentVersionLinkArr[7]));
            vulnerability.setChannelVersionId("");
            vulnerability.setProjectName(String.valueOf(vulnerableComponentView.getComponentName()));
            vulnerability.setVersion(String.valueOf(vulnerableComponentView.getComponentVersionName()));
            vulnerability.setChannelVersionOrigin(String.valueOf(vulnerableComponentView.getComponentVersionOriginName()));
            vulnerability.setChannelVersionOriginId(String.valueOf(vulnerableComponentView.getComponentVersionOriginId()));
            vulnerability.setChannelVersionOriginName(String.valueOf(vulnerableComponentView.getComponentVersionName()));
            vulnerability.setVulnerabilityId(String.valueOf(vulnerableComponentView.getVulnerabilityWithRemediation().getVulnerabilityName()));
            vulnerability.setDescription(String.valueOf(vulnerableComponentView.getVulnerabilityWithRemediation().getDescription().replaceAll("\\r\\n", "")));
            vulnerability.setPublishedOn(vulnerableComponentView.getVulnerabilityWithRemediation().getVulnerabilityPublishedDate());
            vulnerability.setUpdatedOn(vulnerableComponentView.getVulnerabilityWithRemediation().getVulnerabilityUpdatedDate());
            vulnerability.setBaseScore(vulnerableComponentView.getVulnerabilityWithRemediation().getBaseScore());
            vulnerability.setExploitability(vulnerableComponentView.getVulnerabilityWithRemediation().getExploitabilitySubscore());
            vulnerability.setImpact(vulnerableComponentView.getVulnerabilityWithRemediation().getImpactSubscore());
            vulnerability.setVulnerabilitySource(String.valueOf(vulnerableComponentView.getVulnerabilityWithRemediation().getSource()));
            vulnerability.setSeverity(String.valueOf(vulnerableComponentView.getVulnerabilityWithRemediation().getSeverity()));
            vulnerability.setRemediationStatus(String.valueOf(vulnerableComponentView.getVulnerabilityWithRemediation().getRemediationStatus()));
            vulnerability.setRemediationComment(String.valueOf(""));
            vulnerability.setUrl("NVD".equalsIgnoreCase(vulnerableComponentView.getVulnerabilityWithRemediation().getSource())
                    ? "http://web.nvd.nist.gov/view/vuln/detail?vulnId=" + vulnerableComponentView.getVulnerabilityWithRemediation().getVulnerabilityName()
                    : "");
            vulnerability.setScanDate(bomUpdatedValueAt);
            return vulnerability;
        }
    };

    @Override
    public List<Vulnerability> process(List<VulnerableComponentView> vulnerableComponentViews) throws Exception {
        ExecutionContext stepContext = this.stepExecution.getExecutionContext();
        final String projectName = (String) stepContext.get("hubProjectName");
        final String projectVersionName = (String) stepContext.get("hubProjectVersionName");
        bomUpdatedValueAt = (Date) stepContext.get("bomUpdatedValueAt");
        System.out.println(
                "Process :: Project name::" + projectName + ", projectVersionName::" + projectVersionName + ", bomUpdatedValueAt::" + bomUpdatedValueAt);
        return vulnerableComponentViews.stream().map(transformMapping).collect(Collectors.<Vulnerability> toList());
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
