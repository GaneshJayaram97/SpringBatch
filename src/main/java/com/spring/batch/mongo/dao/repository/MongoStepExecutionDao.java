/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.mongo.dao.repository;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.spring.batch.domain.UpgradeJobExecution;
import com.spring.batch.domain.UpgradeStepExecution;
import com.spring.batch.utils.UpgradeDataFieldMaxValueIncrementer;

/**
 * @author gjayaraman
 * Nov 04, 2022
 */
public class MongoStepExecutionDao implements StepExecutionDao
{

    private UpgradeDataFieldMaxValueIncrementer stepValueIncrementer;

    private MongoTemplate mongoTemplate;

    JobExecutionDao jobExecutionDao;

    private static String COLLECTION_NAME = "upgradeStepExecution";

    private static String UPGRADE_JOB_EXECUTION_COLLECTION_NAME = "upgradeJobExecution";

    public MongoStepExecutionDao(MongoTemplate mongoTemplate, UpgradeDataFieldMaxValueIncrementer stepValueIncrementer, JobExecutionDao jobExecutionDao) {
        this.stepValueIncrementer = stepValueIncrementer;
        this.mongoTemplate = mongoTemplate;
        this.jobExecutionDao = jobExecutionDao;
    }

    @Override
    public void saveStepExecution(final StepExecution stepExecution) {
        stepExecution.setId(stepValueIncrementer.nextLongValue());
        stepExecution.incrementVersion();
        UpgradeStepExecution upgradeStepExecution = UpgradeStepExecution.toEntity(stepExecution);
        upgradeStepExecution.setJobExecutionId(stepExecution.getJobExecutionId());
        mongoTemplate.insert(upgradeStepExecution, COLLECTION_NAME);
    }

    @Override
    public void saveStepExecutions(final Collection<StepExecution> stepExecutions) {
        stepExecutions.stream().forEach(this::saveStepExecution);
    }

    @Override
    public void updateStepExecution(final StepExecution stepExecution) {
        synchronized (stepExecution) {

            int version = stepExecution.getVersion();
            stepExecution.incrementVersion();
            UpgradeStepExecution upgradeStepExecution = UpgradeStepExecution.toEntity(stepExecution);
            upgradeStepExecution.setJobExecutionId(stepExecution.getJobExecutionId());
            Query query = Query.query(Criteria.where("stepExecutionId").is(upgradeStepExecution.getStepExecutionId()).and("version").is(version));

            Document doc = new Document();
            mongoTemplate.getConverter().write(upgradeStepExecution, doc);
            doc.remove("id");
            mongoTemplate.updateMulti(query, Update.fromDocument(doc), COLLECTION_NAME);
        }
    }

    @Override
    public StepExecution getStepExecution(final JobExecution jobExecution, final Long stepExecutionId) {
        Query query = Query.query(Criteria.where("stepExecutionId").is(stepExecutionId));
        UpgradeStepExecution upgradeStepExecution = mongoTemplate.findOne(query, UpgradeStepExecution.class, COLLECTION_NAME);
        return UpgradeStepExecution.fromEntity(upgradeStepExecution, jobExecution);
    }

    @Override
    public StepExecution getLastStepExecution(final JobInstance jobInstance, final String stepName) {
        Query jobExecutionQuery = Query.query(Criteria.where("jobInstanceId").is(jobInstance.getId()));

        UpgradeJobExecution upgradeJobExecution = mongoTemplate.findOne(jobExecutionQuery, UpgradeJobExecution.class, UPGRADE_JOB_EXECUTION_COLLECTION_NAME);
        if (Objects.isNull(upgradeJobExecution)) {
            return null;
        }
        Query query = Query.query(Criteria.where("stepName").is(stepName).and("jobExecutionId").is(upgradeJobExecution.getJobExecutionId()));
        query.with(Sort.by(Direction.DESC, "stepExecutionId"));
        UpgradeStepExecution upgradeStepExecution = mongoTemplate.findOne(query, UpgradeStepExecution.class, COLLECTION_NAME);
        if (Objects.isNull(upgradeStepExecution)) {
            return null;
        }
        return UpgradeStepExecution.fromEntity(upgradeStepExecution, UpgradeJobExecution.fromEntity(upgradeJobExecution));
    }

    @Override
    public void addStepExecutions(final JobExecution jobExecution) {
        Query query = Query.query(Criteria.where("jobExecutionId").is(jobExecution.getId()));
        List<StepExecution> stepExecutionList = mongoTemplate.find(query, UpgradeStepExecution.class, COLLECTION_NAME).stream().map(upgradeStepExecution -> UpgradeStepExecution.fromEntity(upgradeStepExecution, jobExecution)).collect(Collectors.toList());
        System.out.println("StepExecution list for job execution : " + jobExecution);
        stepExecutionList.forEach(System.out::println);
        jobExecution.addStepExecutions(stepExecutionList);
    }

    @Override
    public int countStepExecutions(final JobInstance jobInstance, final String stepName) {
        List<JobExecution> jobExecutionList =  jobExecutionDao.findJobExecutions(jobInstance);
        List<Long> jobExecutionIds = jobExecutionList.stream().map(jobExecution -> jobExecution.getId()).collect(Collectors.toList());
        Query query = Query.query(Criteria.where("stepName").is(stepName).and("jobExecutionId").in(jobExecutionIds));
        List<UpgradeStepExecution> stepExecutionList = mongoTemplate.find(query, UpgradeStepExecution.class, COLLECTION_NAME);
        System.out.println("Count of step executions for step name : " + stepName);
        stepExecutionList.forEach(System.out::println);
        return stepExecutionList.size();
    }
}
