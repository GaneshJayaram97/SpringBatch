/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.tasklet;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * @author gjayaraman
 * Dec 08, 2022
 */
public class ImageFetchProcessorTasklet implements Tasklet, StepExecutionListener
{
    @Override
    public RepeatStatus execute(final StepContribution contribution, final ChunkContext chunkContext) throws Exception {
        System.out.println("Processing Image Fetch for the node " + chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext());
        System.out.println("Done processing Image Fetch for the node " + chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext());
        return RepeatStatus.FINISHED;
    }

    @Override
    public void beforeStep(final StepExecution stepExecution) {
        System.out.println("Executing image fetch before step : " + stepExecution);
    }

    @Override
    public ExitStatus afterStep(final StepExecution stepExecution) {
        System.out.println("Executing image fetch after step : " + stepExecution);
        return ExitStatus.COMPLETED;
    }
}
