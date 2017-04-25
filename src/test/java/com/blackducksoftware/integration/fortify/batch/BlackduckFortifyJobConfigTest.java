package com.blackducksoftware.integration.fortify.batch;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.fortify.batch.job.BlackduckFortifyJobConfig;
import com.blackducksoftware.integration.fortify.batch.util.RestConnectionHelper;
import com.blackducksoftware.integration.hub.dataservice.vulnerability.VulnerabilityDataService;
import com.blackducksoftware.integration.hub.model.view.VulnerabilityView;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class })
public class BlackduckFortifyJobConfigTest extends TestCase {
    @Autowired
    private BlackduckFortifyJobConfig pushBlackDuckScanToFortifyJob;

    @Autowired
    private RestConnectionHelper restConnectionHelper;

    @Test
    public void pushBlackDuckScanToFortifyJobTest() throws Exception {
        pushBlackDuckScanToFortifyJob.execute();

        final HubServicesFactory hubServicesFactory = restConnectionHelper.createHubServicesFactory();
        final VulnerabilityDataService vulnerabilityService = hubServicesFactory
                .createVulnerabilityDataService(hubServicesFactory.getRestConnection().logger);

        List<VulnerabilityView> vulnerabilities = vulnerabilityService.getVulnsFromComponentVersion("maven", "commons-fileupload",
                "commons-fileupload",
                "1.2.2");
        System.out.println("Vulnerabilities::" + vulnerabilities);
        assertNotNull(vulnerabilities);
        assertFalse(vulnerabilities.isEmpty());

        vulnerabilities = vulnerabilityService.getVulnsFromComponentVersion("maven", "commons-fileupload",
                "commons-fileupload",
                "1.3.2");
        System.out.println("Vulnerabilities::" + vulnerabilities);
        assertNotNull(vulnerabilities);
        assertTrue(vulnerabilities.isEmpty());
        Assert.assertNull(null);
    }
}
