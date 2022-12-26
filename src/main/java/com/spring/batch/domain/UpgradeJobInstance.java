/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.bson.types.ObjectId;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobKeyGenerator;
import org.springframework.batch.core.JobParameters;
import org.springframework.data.annotation.Id;

import com.spring.batch.utils.JobRepositoryUtils;

/**
 * @author gjayaraman
 * Nov 07, 2022
 */
public class UpgradeJobInstance
{

    @Id private ObjectId _id;

    private String jobName;

    private long jobInstanceId;

    private int version;

    private String jobKey;

    private Map<String, Object> jobParameters;

    public String getJobName() {
        return jobName;
    }

    public long getJobInstanceId() {
        return jobInstanceId;
    }

    public int getVersion() {
        return version;
    }

    public String getJobKey() {
        return jobKey;
    }

    public Map<String, Object> getJobParameters() {
        return jobParameters;
    }

    public static UpgradeJobInstance toEntity(JobInstance jobInstance, final JobParameters jobParameters, JobKeyGenerator<JobParameters> jobKeyGenerator) {

        Map<String, Object> paramMap = JobRepositoryUtils.convertToMap(jobParameters);

        UpgradeJobInstance upgradeJobInstance = new UpgradeJobInstance();

        upgradeJobInstance.jobInstanceId = jobInstance.getInstanceId();
        upgradeJobInstance.jobName = jobInstance.getJobName();
        upgradeJobInstance.jobKey = jobKeyGenerator.generateKey(jobParameters);
        upgradeJobInstance.version = jobInstance.getVersion();
        upgradeJobInstance.jobParameters = paramMap;

        return upgradeJobInstance;
    }

    public static JobInstance fromEntity(UpgradeJobInstance jobInstanceEntity) {
        if (jobInstanceEntity == null) {
            return null;
        }

        JobInstance jobInstance =
                new JobInstance(jobInstanceEntity.getJobInstanceId(), jobInstanceEntity.getJobName());

        return jobInstance;
    }

    public static UpgradeJobInstance createUpgradeJobInstance(long id, String jobName, JobParameters jobParameters, JobKeyGenerator<JobParameters> jobKeyGenerator) {
        JobInstance jobInstance = new JobInstance(id, jobName);
        jobInstance.incrementVersion();
        return toEntity(jobInstance, jobParameters, jobKeyGenerator);
    }
}
