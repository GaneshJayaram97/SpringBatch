/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.domain;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author gjayaraman
 * Oct 28, 2022
 */
public class JobSpec
{
    String jobName;
    List<String> deviceIps;
    Integer priority;
    Map<String, Object> jobParams;

    public JobSpec() {}

    @JsonProperty("jobName")
    public String getJobName() {
        return jobName;
    }

    public void setJobName(final String jobName) {
        this.jobName = jobName;
    }

    @JsonProperty("deviceIps")
    public List<String> getDeviceIps() {
        return deviceIps;
    }

    public void setDeviceIps(final List<String> deviceIps) {
        this.deviceIps = deviceIps;
    }

    @JsonProperty("priority")
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(final Integer priority) {
        this.priority = priority;
    }

    @JsonProperty("jobParams")
    public Map<String, Object> getJobParams() {
        return jobParams;
    }

    public void setJobParams(final Map<String, Object> jobParams) {
        this.jobParams = jobParams;
    }

    @Override
    public String toString() {
        return new StringBuilder().append(" jobName " + jobName)
                                  .append(" deviceIps " + deviceIps)
                                  .append(" priority " + priority)
                                  .append(" params " + jobParams)
                                  .toString();
    }
}
