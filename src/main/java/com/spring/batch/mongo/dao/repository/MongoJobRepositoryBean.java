/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.mongo.dao.repository;

import java.util.Objects;

import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.core.repository.support.AbstractJobRepositoryFactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import com.spring.batch.utils.UpgradeDataFieldMaxValueIncrementer;

/**
 * @author gjayaraman
 * Nov 04, 2022
 */
public class MongoJobRepositoryBean extends AbstractJobRepositoryFactoryBean implements InitializingBean
{

    private MongoTemplate mongoTemplate;
    private UpgradeDataFieldMaxValueIncrementer incrementer;
    private JobExecutionDao jobExecutionDao;

    public MongoJobRepositoryBean(MongoTemplate mongoTemplate, UpgradeDataFieldMaxValueIncrementer incrementer, PlatformTransactionManager transactionManager) {
        this.mongoTemplate = mongoTemplate;
        this.incrementer = incrementer;
        setTransactionManager(transactionManager);
    }

    @Override
    protected JobInstanceDao createJobInstanceDao() {
        return new MongoJobInstanceDao(mongoTemplate, incrementer);
    }

    @Override
    protected JobExecutionDao createJobExecutionDao() {
        MongoJobExecutionDao mongoJobExecutionDao = new MongoJobExecutionDao(mongoTemplate, incrementer);
        this.jobExecutionDao = mongoJobExecutionDao;
        return mongoJobExecutionDao;
    }

    @Override
    protected StepExecutionDao createStepExecutionDao() {
        if (Objects.isNull(jobExecutionDao)) {
            createJobExecutionDao();
        }
        return new MongoStepExecutionDao(mongoTemplate, incrementer, jobExecutionDao);
    }

    @Override
    protected ExecutionContextDao createExecutionContextDao() {
        return new MongoExecutionContextDao(mongoTemplate);
    }
}
