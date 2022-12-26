/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.mongo.dao.repository;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.print.Doc;

import org.apache.tomcat.util.http.parser.Upgrade;
import org.bson.Document;
import org.springframework.batch.core.DefaultJobKeyGenerator;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobKeyGenerator;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.util.CollectionUtils;

import com.spring.batch.domain.UpgradeJobInstance;
import com.spring.batch.utils.RandomSequenceGeneratorUtils;

/**
 * @author gjayaraman
 * Nov 04, 2022
 */
public class MongoJobInstanceDao implements JobInstanceDao
{

    private JobKeyGenerator<JobParameters> jobKeyGenerator = new DefaultJobKeyGenerator();
    private DataFieldMaxValueIncrementer jobIncrementer;
    private MongoTemplate mongoTemplate;
    private static String COLLECTION_NAME =  "upgradeJobInstance";

    public MongoJobInstanceDao(MongoTemplate mongoTemplate, DataFieldMaxValueIncrementer jobIncrementer) {
        this.jobIncrementer = jobIncrementer;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public JobInstance createJobInstance(final String jobName, final JobParameters jobParameters) {

        UpgradeJobInstance upgradeJobInstance = UpgradeJobInstance.createUpgradeJobInstance(jobIncrementer.nextLongValue(), jobName, jobParameters, jobKeyGenerator);
        mongoTemplate.insert(upgradeJobInstance, COLLECTION_NAME);
        return UpgradeJobInstance.fromEntity(upgradeJobInstance);
    }

    @Override
    public JobInstance getJobInstance(final String jobName, final JobParameters jobParameters) {
        String jobKey = jobKeyGenerator.generateKey(jobParameters);
        Query query = Query.query(Criteria.where("jobName").is(jobName).and("jobKey").is(jobKey));
        UpgradeJobInstance upgradeJobInstance = mongoTemplate.findOne(query, UpgradeJobInstance.class, COLLECTION_NAME);
        return UpgradeJobInstance.fromEntity(upgradeJobInstance);
    }

    @Override
    public JobInstance getJobInstance(final Long instanceId) {
        Query query = Query.query(Criteria.where("id").is(instanceId));
        UpgradeJobInstance upgradeJobInstance = mongoTemplate.findOne(query, UpgradeJobInstance.class, COLLECTION_NAME);
        return UpgradeJobInstance.fromEntity(upgradeJobInstance);
    }

    @Override
    public JobInstance getJobInstance(final JobExecution jobExecution) {
        return getJobInstance(jobExecution.getId());
    }

    @Override
    public List<JobInstance> getJobInstances(final String jobName, final int start, final int count) {
        Query query = Query.query(Criteria.where("id").regex("/*" +  jobName + "/"));
        query.limit(count);
        query.skip(start);
        query.with(Sort.by(Order.desc("id")));
        List<UpgradeJobInstance> upgradeJobInstanceList = mongoTemplate.find(query, UpgradeJobInstance.class, COLLECTION_NAME);
        return upgradeJobInstanceList.stream().map(upgradeJobInstance -> UpgradeJobInstance.fromEntity(upgradeJobInstance)).collect(Collectors.toList());
    }

    @Override
    public JobInstance getLastJobInstance(final String jobName) {
        List<JobInstance> jobInstances =  getJobInstances(jobName, 0, 0);
        return CollectionUtils.isEmpty(jobInstances) ? null : jobInstances.get(0);
    }

    @Override
    public List<String> getJobNames() {
        Query query = new Query();
        query.fields().include("jobName");
        return mongoTemplate.find(query, UpgradeJobInstance.class, COLLECTION_NAME).stream().map(upgradeJobInstance -> upgradeJobInstance.getJobName()).collect(Collectors.toList());
    }

    @Override
    public List<JobInstance> findJobInstancesByName(final String jobName, final int start, final int count) {
        return getJobInstances(jobName, start, count);
    }

    @Override
    public int getJobInstanceCount(final String jobName) {
        return getJobNames().size();
    }
}
