/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.testJobs;

import java.util.List;

import org.springframework.batch.core.step.item.Chunk;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.spring.batch.domain.Job;
import com.spring.batch.domain.Node;

/**
 * @author gjayaraman
 * Oct 27, 2022
 */
public class UpgradeMongoItemWriter<Node> extends MongoItemWriter<com.spring.batch.domain.Node>
{
    public UpgradeMongoItemWriter() {
        super();
    }

    @Override
    protected void doWrite(List<? extends com.spring.batch.domain.Node> items) {
       items.stream().forEach(this::writeToDB);
    }

    private void writeToDB(com.spring.batch.domain.Node node) {
        System.out.println("Updating DB on write : " + node);
        Update update = Update.update("status", "completed");
        Query query = new Query(Criteria.where("ip").is(node.getIp()));
        getTemplate().updateFirst(query, update, com.spring.batch.domain.Node.class, getTemplate().getCollectionName(com.spring.batch.domain.Node.class));
    }
}
