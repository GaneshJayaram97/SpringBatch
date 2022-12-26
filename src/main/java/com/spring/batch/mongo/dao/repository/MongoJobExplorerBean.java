/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.mongo.dao.repository;

import java.util.Objects;

import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.AbstractJobExplorerFactoryBean;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.spring.batch.utils.UpgradeDataFieldMaxValueIncrementer;

/**
 * @author gjayaraman
 * Nov 07, 2022
 */
public class MongoJobExplorerBean extends AbstractJobExplorerFactoryBean
{
    private MongoTemplate mongoTemplate;
    private UpgradeDataFieldMaxValueIncrementer incrementer;
    private JobExecutionDao jobExecutionDao;

    public MongoJobExplorerBean(MongoTemplate mongoTemplate, UpgradeDataFieldMaxValueIncrementer incrementer) {
        this.mongoTemplate = mongoTemplate;
        this.incrementer = incrementer;
    }

    @Override
    protected JobInstanceDao createJobInstanceDao() throws Exception {
        return new MongoJobInstanceDao(mongoTemplate, incrementer);
    }

    @Override
    protected JobExecutionDao createJobExecutionDao() throws Exception {
        MongoJobExecutionDao mongoJobExecutionDao = new MongoJobExecutionDao(mongoTemplate, incrementer);
        this.jobExecutionDao = mongoJobExecutionDao;
        return mongoJobExecutionDao;
    }

    @Override
    protected StepExecutionDao createStepExecutionDao() throws Exception {
        if (Objects.isNull(jobExecutionDao)) {
            createJobExecutionDao();
        }
        return new MongoStepExecutionDao(mongoTemplate, incrementer, jobExecutionDao);
    }

    @Override
    protected ExecutionContextDao createExecutionContextDao() throws Exception {
        return new MongoExecutionContextDao(mongoTemplate);
    }

    @Override
    public JobExplorer getObject() throws Exception {
        return getTarget();
    }

    private JobExplorer getTarget() throws Exception {
        return new SimpleJobExplorer(createJobInstanceDao(),
                createJobExecutionDao(), createStepExecutionDao(),
                createExecutionContextDao());
    }
}
