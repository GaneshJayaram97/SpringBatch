/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.domain;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

/**
 * @author gjayaraman
 * Nov 07, 2022
 */
public class UpgradeStepExecution
{

    @Id private ObjectId _id;

    private long stepExecutionId;

    private long jobExecutionId;

    private String stepName;

    private Date startTime;

    private Date endTime;
    private String status;
    private int commitCount;
    private int readCount;
    private int filterCount;
    private int writeCount;
    private String exitCode;
    private String exitMessage;
    private int readSkipCount;
    private int writeSkipCount;
    private int processSkipCount;
    private int rollbackCount;
    private Date lastUpdated;
    private int version;

    public long getStepExecutionId() {
        return stepExecutionId;
    }

    public String getStepName() {
        return stepName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }

    public int getCommitCount() {
        return commitCount;
    }

    public int getReadCount() {
        return readCount;
    }

    public int getFilterCount() {
        return filterCount;
    }

    public int getWriteCount() {
        return writeCount;
    }

    public String getExitCode() {
        return exitCode;
    }

    public String getExitMessage() {
        return exitMessage;
    }

    public int getReadSkipCount() {
        return readSkipCount;
    }

    public int getWriteSkipCount() {
        return writeSkipCount;
    }

    public int getProcessSkipCount() {
        return processSkipCount;
    }

    public int getRollbackCount() {
        return rollbackCount;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public int getVersion() {
        return version;
    }

    public long getJobExecutionId() {
        return jobExecutionId;
    }

    public static UpgradeStepExecution toEntity(final StepExecution stepExecution) {
        UpgradeStepExecution upgradeStepExecution = new UpgradeStepExecution();

        upgradeStepExecution.stepExecutionId = stepExecution.getId();
        upgradeStepExecution.stepName = stepExecution.getStepName();
        upgradeStepExecution.jobExecutionId = stepExecution.getJobExecutionId();
        upgradeStepExecution.startTime = stepExecution.getStartTime();
        upgradeStepExecution.endTime = stepExecution.getEndTime();
        upgradeStepExecution.status = stepExecution.getStatus().toString();
        upgradeStepExecution.commitCount = stepExecution.getCommitCount();
        upgradeStepExecution.readCount = stepExecution.getReadCount();
        upgradeStepExecution.filterCount = stepExecution.getFilterCount();
        upgradeStepExecution.writeCount = stepExecution.getWriteCount();
        upgradeStepExecution.exitCode = stepExecution.getExitStatus().getExitCode();
        upgradeStepExecution.exitMessage = stepExecution.getExitStatus().getExitDescription();
        upgradeStepExecution.readSkipCount = stepExecution.getReadSkipCount();
        upgradeStepExecution.writeSkipCount = stepExecution.getWriteSkipCount();
        upgradeStepExecution.processSkipCount = stepExecution.getProcessSkipCount();
        upgradeStepExecution.rollbackCount = stepExecution.getRollbackCount();
        upgradeStepExecution.lastUpdated = stepExecution.getLastUpdated();
        upgradeStepExecution.version = stepExecution.getVersion();

        return upgradeStepExecution;
    }

    public static StepExecution fromEntity(UpgradeStepExecution entity, JobExecution jobExecution) {
        if (entity == null) {
            return null;
        }

        // stepExecution is added to jobExecution in this constructor!
        StepExecution stepExecution =
                new StepExecution(entity.getStepName(), jobExecution, entity.getStepExecutionId());

        stepExecution.setStartTime(entity.getStartTime());
        stepExecution.setEndTime(entity.getEndTime());
        stepExecution.setStatus(BatchStatus.valueOf(entity.getStatus()));
        stepExecution.setCommitCount(entity.getCommitCount());
        stepExecution.setReadCount(entity.getReadCount());
        stepExecution.setFilterCount(entity.getFilterCount());
        stepExecution.setWriteCount(entity.getWriteCount());
        stepExecution.setExitStatus(new ExitStatus(entity.getExitCode(), entity.getExitMessage()));
        stepExecution.setReadSkipCount(entity.getReadSkipCount());
        stepExecution.setWriteSkipCount(entity.getWriteSkipCount());
        stepExecution.setProcessSkipCount(entity.getProcessSkipCount());
        stepExecution.setRollbackCount(entity.getRollbackCount());
        stepExecution.setLastUpdated(entity.getLastUpdated());
        stepExecution.setVersion(entity.getVersion());
        return stepExecution;
    }

    public void setJobExecutionId(long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }
}
