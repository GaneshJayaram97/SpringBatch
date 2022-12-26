/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.mongo.dao.repository;

import java.util.Collection;
import java.util.Objects;

import org.bson.Document;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.spring.batch.domain.UpgradeExecutionContext;

/**
 * @author gjayaraman
 * Nov 04, 2022
 */
public class MongoExecutionContextDao implements ExecutionContextDao
{

    private MongoTemplate mongoTemplate;

    private static String JOB_COLLECTION_NAME = "upgradeJobExecutionContext";

    private static String STEP_COLLECTION_NAME = "upgradeStepExecutionContext";

    public MongoExecutionContextDao(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public ExecutionContext getExecutionContext(final JobExecution jobExecution) {
        if (Objects.isNull(jobExecution)) {
            return null;
        }
        Query query = Query.query(Criteria.where("id").is(jobExecution.getId()));
        return mongoTemplate.findOne(query, ExecutionContext.class, JOB_COLLECTION_NAME);
    }

    @Override
    public ExecutionContext getExecutionContext(final StepExecution stepExecution) {
        Query query = Query.query(Criteria.where("id").is(stepExecution.getId()));
        return mongoTemplate.findOne(query, ExecutionContext.class, STEP_COLLECTION_NAME);
    }

    @Override
    public void saveExecutionContext(final JobExecution jobExecution) {
        synchronized (jobExecution) {
            UpgradeExecutionContext upgradeExecutionContext = UpgradeExecutionContext.createUpgradeExecutionContext(jobExecution.getId(), jobExecution.getExecutionContext());
            mongoTemplate.insert(upgradeExecutionContext, JOB_COLLECTION_NAME);
        }
    }

    @Override
    public void saveExecutionContext(final StepExecution stepExecution) {
        synchronized (stepExecution) {
            UpgradeExecutionContext upgradeExecutionContext = UpgradeExecutionContext.createUpgradeExecutionContext(stepExecution.getId(), stepExecution.getExecutionContext());
            mongoTemplate.insert(upgradeExecutionContext, STEP_COLLECTION_NAME);
        }
    }

    @Override
    public void saveExecutionContexts(final Collection<StepExecution> stepExecutions) {
        stepExecutions.stream().forEach(this::saveExecutionContext);
    }

    @Override
    public void updateExecutionContext(final JobExecution jobExecution) {
        Query query = Query.query(Criteria.where("id").is(jobExecution.getId()));
        UpgradeExecutionContext upgradeExecutionContext = UpgradeExecutionContext.createUpgradeExecutionContext(jobExecution.getId(), jobExecution.getExecutionContext());
        Document doc = new Document();
        mongoTemplate.getConverter().write(upgradeExecutionContext, doc);
        doc.remove("id");
        mongoTemplate.updateMulti(query, Update.fromDocument(doc), JOB_COLLECTION_NAME);
    }

    @Override
    public void updateExecutionContext(final StepExecution stepExecution) {
        Query query = Query.query(Criteria.where("id").is(stepExecution.getId()));
        UpgradeExecutionContext upgradeExecutionContext = UpgradeExecutionContext.createUpgradeExecutionContext(stepExecution.getId(), stepExecution.getExecutionContext());
        Document doc = new Document();
        mongoTemplate.getConverter().write(upgradeExecutionContext, doc);
        doc.remove("id");
        mongoTemplate.updateMulti(query, Update.fromDocument(doc), STEP_COLLECTION_NAME);
    }
}
