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
package com.blackducksoftware.integration.fortify.batch.model;

import java.util.Date;

import com.blackducksoftware.integration.hub.model.HubView;

/**
 * This class is to store the Last BOM Updated value for the given project version
 * 
 * @author smanikantan
 *
 */
public class RiskProfile extends HubView {
    private Date bomLastUpdatedAt;

    public Date getBomLastUpdatedAt() {
        return bomLastUpdatedAt;
    }

    public void setBomLastUpdatedAt(Date bomLastUpdatedAt) {
        this.bomLastUpdatedAt = bomLastUpdatedAt;
    }
}
