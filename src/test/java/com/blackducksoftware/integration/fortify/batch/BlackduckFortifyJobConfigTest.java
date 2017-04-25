package com.blackducksoftware.integration.fortify.batch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.fortify.batch.job.BlackduckFortifyJobConfig;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class })
public class BlackduckFortifyJobConfigTest extends TestCase {

    private BlackduckFortifyJobConfig blackduckFortifyJobConfig;

    @Test
    public void executeBatchJob() throws Exception {
        blackduckFortifyJobConfig.execute();
    }
}
