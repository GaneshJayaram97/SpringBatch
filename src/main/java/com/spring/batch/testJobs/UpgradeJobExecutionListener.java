/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.testJobs;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

/**
 * @author gjayaraman
 * Oct 27, 2022
 */
public class UpgradeJobExecutionListener implements JobExecutionListener
{
    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("Called before job execution : " + jobExecution);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println("Called after job execution : " + jobExecution);
    }
}
