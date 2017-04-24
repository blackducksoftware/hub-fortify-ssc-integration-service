package com.blackducksoftware.integration.fortify.batch;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.fortify.batch.job.BlackDuckFortifyPushData;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BlackDuckFortifyPushData.class)
public class BlackDuckFortifyPushDataTest extends TestCase {
    @Autowired
    private BlackDuckFortifyPushData pushBlackDuckScanToFortifyJob;

    @Test
    public void pushBlackDuckScanToFortifyJobTest() throws Exception {
        pushBlackDuckScanToFortifyJob.execute();
        Assert.assertNull(null);
    }
}
