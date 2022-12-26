/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.testJobs;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

/**
 * @author gjayaraman
 * Oct 27, 2022
 */
public class UpgradeStepExecutionListener implements StepExecutionListener
{
    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("Called before step execution " + stepExecution);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println("Called after step execution " + stepExecution);
        return stepExecution.getExitStatus();
    }
}
