package com.blackducksoftware.integration.fortify.batch.reader;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

@Component
public class BlackduckScanReader<BlackDuckParser> implements ItemReader<BlackDuckParser> {

    @Override
    public BlackDuckParser read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        System.out.println("Inside BlackduckScanReader");
        return null;
    }

}
