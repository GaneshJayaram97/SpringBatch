/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.jobRestController;

import java.io.IOException;
import java.util.Map;

import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.internal.bulk.IndexRequest;
import com.sping.batch.jobService.JobService;
import com.spring.batch.domain.JobSpec;

/**
 * @author gjayaraman
 * Oct 28, 2022
 */
@RestController
public class JobRestResource
{

    @Autowired
    JobService jobService;

    @Autowired
    ObjectMapper objectMapper;

    @PostMapping("/createJob")
    @ResponseStatus(HttpStatus.CREATED)
    public void createJob(@RequestBody String data)
            throws IOException, JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobSpec jobSpec = objectMapper.readValue(data, JobSpec.class);
        jobService.createJob(jobSpec);
    }

    @PostMapping("/createTaskletJob")
    @ResponseStatus(HttpStatus.CREATED)
    public void createTaskletJob(@RequestBody String data)
            throws IOException, JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobSpec jobSpec = objectMapper.readValue(data, JobSpec.class);
        jobService.createTaskletJob(jobSpec);
    }
}
