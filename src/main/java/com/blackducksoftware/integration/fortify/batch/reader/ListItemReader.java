/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.fortify.batch.reader;

import java.util.List;

import org.springframework.batch.item.ItemReader;

import com.blackducksoftware.integration.exception.IntegrationException;

public class ListItemReader<T> implements ItemReader<T> {
    public List<T> list;

    @Override
    public T read() throws IllegalArgumentException, IntegrationException {
        if (!(this.list.isEmpty())) {
            return this.list.remove(0);
        }
        return null;
    }
}
