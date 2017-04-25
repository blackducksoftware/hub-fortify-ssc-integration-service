package com.blackducksoftware.integration.fortify.batch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class FortifyWriter<FortifyParser> implements ItemWriter<FortifyParser> {

    @Override
    public void write(List<? extends FortifyParser> arg0) throws Exception {
        System.out.println("Inside FortifyPushWriter");
    }

}
