package com.blackducksoftware.integration.fortify.batch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.fortify.batch.model.FortifyParser;

@Component
public class BlackduckFortifyProcessor<BlackDuckParser> implements ItemProcessor<BlackDuckParser, FortifyParser> {

    @Override
    public FortifyParser process(BlackDuckParser arg0) throws Exception {
        System.out.println("Inside BlackduckFortifyProcessor");
        return null;
    }

}
