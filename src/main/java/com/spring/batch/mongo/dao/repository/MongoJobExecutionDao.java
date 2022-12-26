/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.mongo.dao.repository;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.CollectionUtils;

import com.spring.batch.domain.UpgradeJobExecution;
import com.spring.batch.domain.UpgradeJobInstance;
import com.spring.batch.utils.RandomSequenceGeneratorUtils;
import com.spring.batch.utils.UpgradeDataFieldMaxValueIncrementer;

/**
 * @author gjayaraman
 * Nov 04, 2022
 */
public class MongoJobExecutionDao implements JobExecutionDao
{

    private MongoTemplate mongoTemplate;

    private UpgradeDataFieldMaxValueIncrementer jobIncrementer;

    private static String JOB_INSTANCE_COLLECTION_NAME = "upgradeJobInstance";

    private static String COLLECTION_NAME = "upgradeJobExecution";

    public MongoJobExecutionDao(MongoTemplate mongoTemplate, UpgradeDataFieldMaxValueIncrementer jobIncrementer) {
        this.mongoTemplate = mongoTemplate;
        this.jobIncrementer = jobIncrementer;
    }

    @Override
    public void saveJobExecution(final JobExecution jobExecution) {
        jobExecution.incrementVersion();
        synchronized (this) {
            jobExecution.setId(jobIncrementer.nextLongValue());
        }
        UpgradeJobExecution upgradeJobExecution = UpgradeJobExecution.createUpgradeJobExecution(jobExecution);
        mongoTemplate.insert(upgradeJobExecution, COLLECTION_NAME);
    }

    @Override
    public void updateJobExecution(final JobExecution jobExecution) {
        UpgradeJobExecution upgradeJobExecution = UpgradeJobExecution.createUpgradeJobExecution(jobExecution);
        jobExecution.incrementVersion();
        Query query = new Query(Criteria.where("jobExecutionId").is(jobExecution.getId()));
        Document dbDoc = new Document();
        mongoTemplate.getConverter().write(upgradeJobExecution, dbDoc);
        dbDoc.remove("_id");
        mongoTemplate.updateFirst(query, Update.fromDocument(dbDoc), COLLECTION_NAME);
    }

    @Override
    public List<JobExecution> findJobExecutions(final JobInstance jobInstance) {
        Criteria criteria = Criteria.where("jobInstanceId").is(jobInstance.getId());
        Query query = new Query(criteria);
        return mongoTemplate.find(query, UpgradeJobExecution.class, COLLECTION_NAME).stream().map(upgradeJobExecution -> UpgradeJobExecution.fromEntity(upgradeJobExecution)).collect(Collectors.toList());
    }

    private List<JobExecution> findJobExecutions(String jobName) {
        Criteria criteria = Criteria.where("jobName").is(jobName);
        Query query = new Query(criteria);
        List<Long> jobExecutionIds = mongoTemplate.find(query, UpgradeJobInstance.class, JOB_INSTANCE_COLLECTION_NAME).stream().map(upgradeJobInstance -> upgradeJobInstance.getJobInstanceId()).collect(Collectors.toList());
        query = new Query(Criteria.where("jobInstanceId").in(jobExecutionIds));
        return mongoTemplate.find(query, UpgradeJobExecution.class, COLLECTION_NAME).stream().map(upgradeJobExecution -> UpgradeJobExecution.fromEntity(upgradeJobExecution)).collect(Collectors.toList());
    }

    private JobExecution findJobExecutions(Long jobExecutionId) {
        Criteria criteria = Criteria.where("jobExecutionId").is(jobExecutionId);
        Query query = new Query(criteria);
        UpgradeJobExecution upgradeJobExecution =  mongoTemplate.findOne(query, UpgradeJobExecution.class, COLLECTION_NAME);
        return Objects.isNull(upgradeJobExecution) ? null : UpgradeJobExecution.fromEntity(upgradeJobExecution);
    }

    @Override
    public JobExecution getLastJobExecution(final JobInstance jobInstance) {
        List<JobExecution> jobExecutionList = findJobExecutions(jobInstance.getJobName());
        return CollectionUtils.isEmpty(jobExecutionList) ? null : jobExecutionList.get(0);
    }

    @Override
    public Set<JobExecution> findRunningJobExecutions(final String jobName) {
        return findJobExecutions(jobName).stream().collect(Collectors.toSet());
    }

    @Override
    public JobExecution getJobExecution(final Long executionId) {
        return findJobExecutions(executionId);
    }

    @Override
    public void synchronizeStatus(final JobExecution jobExecution) {
        updateJobExecution(jobExecution);
    }
}
