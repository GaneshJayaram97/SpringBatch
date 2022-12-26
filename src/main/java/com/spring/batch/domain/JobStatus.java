/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.domain;

/**
 * @author gjayaraman
 * Oct 27, 2022
 */
public class JobStatus
{
    public String jobId;
    public String jobName;
    public String jobStatus;

    public JobStatus() {}

    public String getJobId() {
        return jobId;
    }

    public void setJobId(final String jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(final String jobName) {
        this.jobName = jobName;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(final String jobStatus) {
        this.jobStatus = jobStatus;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("jobId" + jobId)
                                 .append("jobName" + jobName)
                                 .append("jobStatus" + jobStatus)
                                 .toString();
    }
}
