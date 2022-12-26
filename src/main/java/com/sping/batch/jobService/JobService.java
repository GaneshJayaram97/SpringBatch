/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.sping.batch.jobService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import com.spring.batch.domain.JobSpec;
import com.spring.batch.domain.UpgradeJobParameter;
import com.spring.batch.testJobs.BatchConfiguration;
import com.spring.batch.testJobs.UpgradeJobExecutionListener;

/**
 * @author gjayaraman
 * Oct 28, 2022
 */
public class JobService
{
    JobLauncher jobLauncher;

    BatchConfiguration batchConfiguration;

    public JobService(JobLauncher jobLauncher, BatchConfiguration batchConfiguration) {
        this.jobLauncher = jobLauncher;
        this.batchConfiguration = batchConfiguration;
    }

    public Job newJob(JobSpec jobSpec) {
        return batchConfiguration.jobBuilderFactory
                .get(jobSpec.getJobName())
                .listener(new UpgradeJobExecutionListener())
                .start(batchConfiguration.imageFetch()).next(batchConfiguration.imageInstall()).next(batchConfiguration.imageReboot()).build();
    }

    public Job newTaskletJob(JobSpec jobSpec) {
        return batchConfiguration.jobBuilderFactory
                .get(jobSpec.getJobName())
                .start(batchConfiguration.NodeSelectionTasklet())
                .next(batchConfiguration.imageFetchTaskLet()).next(batchConfiguration.imageInstallTaskLet()).next(batchConfiguration.imageRebootTaskLet()).build();
    }

    public JobParameters newJobParameters(JobSpec jobSpec) {
        JobParameters jobParameters;
        if (Objects.nonNull(jobSpec.getJobParams())) {
            Map<String, JobParameter> map = new HashMap<>();
            jobSpec.getJobParams().forEach((key, value) -> {
                map.put(key, new UpgradeJobParameter(value));
            });
            jobParameters = new JobParameters(map);
        }
        else {
            jobParameters = new JobParameters();
        }
        return jobParameters;
    }

    public JobExecution createJob(JobSpec jobSpec)
            throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        Job job = newJob(jobSpec);
        JobParameters jobParameters = newJobParameters(jobSpec);
        return jobLauncher.run(job, jobParameters);
    }

    public JobExecution createTaskletJob(JobSpec jobSpec)
            throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        Job job = newTaskletJob(jobSpec);
        JobParameters jobParameters = newJobParameters(jobSpec);
        return jobLauncher.run(job, jobParameters);
    }
}
