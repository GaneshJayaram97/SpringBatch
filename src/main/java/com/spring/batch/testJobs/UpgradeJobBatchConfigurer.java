/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.testJobs;

import javax.annotation.PostConstruct;

import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import com.spring.batch.mongo.dao.repository.MongoJobExplorerBean;
import com.spring.batch.mongo.dao.repository.MongoJobRepositoryBean;
import com.spring.batch.utils.UpgradeDataFieldMaxValueIncrementer;

/**
 * @author gjayaraman
 * Oct 28, 2022
 */
public class UpgradeJobBatchConfigurer extends DefaultBatchConfigurer implements BatchConfigurer
{

    private JobLauncher jobLauncher;
    private MongoTemplate mongoTemplate;
    private UpgradeDataFieldMaxValueIncrementer incrementer;
    private PlatformTransactionManager transactionManager;
    private JobExplorer jobExplorer;
    private JobRepository jobRepository;

    public UpgradeJobBatchConfigurer(MongoTemplate mongoTemplate, UpgradeDataFieldMaxValueIncrementer incrementer, PlatformTransactionManager transactionManager, JobLauncher jobLauncher)
            throws Exception {
        super();
        this.mongoTemplate = mongoTemplate;
        this.transactionManager = transactionManager;
        this.incrementer = incrementer;
        this.jobLauncher = jobLauncher;
        this.jobRepository = createJobRepository();
        this.jobExplorer = createJobExplorer();
    }

    @Override
    public PlatformTransactionManager getTransactionManager() {
        return transactionManager;
    }

    @Override
    protected JobRepository createJobRepository() throws Exception {
        MongoJobRepositoryBean factory = new MongoJobRepositoryBean(mongoTemplate, incrementer, transactionManager);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Override
    protected JobExplorer createJobExplorer() throws Exception {
        MongoJobExplorerBean jobExplorerFactoryBean = new MongoJobExplorerBean(mongoTemplate, incrementer);
        return jobExplorerFactoryBean.getObject();
    }

    @Override
    public JobRepository getJobRepository() {
        return jobRepository;
    }

    @Override
    public JobExplorer getJobExplorer() {
        return jobExplorer;
    }

    @Override
    protected JobLauncher createJobLauncher() {
        return jobLauncher;
    }
}
