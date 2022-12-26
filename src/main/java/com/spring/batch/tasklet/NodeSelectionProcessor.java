/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.tasklet;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.spring.batch.domain.Node;
import com.spring.batch.domain.UpgradeJobParameter;

/**
 * @author gjayaraman
 * Dec 08, 2022
 */
public class NodeSelectionProcessor implements Tasklet, StepExecutionListener
{

    private MongoTemplate mongoTemplate;
    private String collectionName;

    public NodeSelectionProcessor(MongoTemplate mongoTemplate, String collectionName) {
        this.mongoTemplate = mongoTemplate;
        this.collectionName = collectionName;
    }

    @Override
    public void beforeStep(final StepExecution stepExecution) {
        System.out.println("Executing Node selection process before step : " + stepExecution);
    }

    @Override
    public ExitStatus afterStep(final StepExecution stepExecution) {
        System.out.println("Executing Node selection process after step : " + stepExecution);
        return ExitStatus.COMPLETED;
    }

    @Override
    public RepeatStatus execute(final StepContribution contribution, final ChunkContext chunkContext) {
        List<String> nodesList = new ArrayList<>();
        JobParameter jobParameter = contribution.getStepExecution().getJobParameters().getParameters().get("ips");
        if (jobParameter instanceof UpgradeJobParameter) {
            UpgradeJobParameter upgradeJobParameter = (UpgradeJobParameter) jobParameter;
            nodesList = (List<String>) upgradeJobParameter.getParameter();
        }
        List<Node> nodes =  mongoTemplate.find(new Query(Criteria.where("ip").in(nodesList)), Node.class, collectionName);
        chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("nodes", nodes);
        return RepeatStatus.FINISHED;
    }
}
