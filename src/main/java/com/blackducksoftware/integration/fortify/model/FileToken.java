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
package com.blackducksoftware.integration.fortify.model;

/**
 * This class is used to store the Fortify File Token request data
 *
 * @author smanikantan
 *
 */
public class FileToken {
    private String fileTokenType;

    public String getFileTokenType() {
        return fileTokenType;
    }

    public void setFileTokenType(String fileTokenType) {
        this.fileTokenType = fileTokenType;
    }

    @Override
    public String toString() {
        return fileTokenType;
    }
}
