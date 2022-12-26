/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.utils;

import java.util.Objects;

import org.bson.Document;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.spring.batch.domain.SequenceGenerator;

/**
 * @author gjayaraman
 * Nov 07, 2022
 */
public class UpgradeDataFieldMaxValueIncrementer implements DataFieldMaxValueIncrementer
{

    private MongoTemplate mongoTemplate;

    private static String COLLECTION_NAME = "sequenceGenerator";
    public UpgradeDataFieldMaxValueIncrementer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public int nextIntValue() throws DataAccessException {
        return (int) nextLongValue();
    }

    @Override
    public long nextLongValue() throws DataAccessException {
        return getNextLongValue();
    }

    @Override
    public String nextStringValue() throws DataAccessException {
        return String.valueOf(nextLongValue());
    }

    private synchronized long getNextLongValue() {
        SequenceGenerator sequenceGenerator = mongoTemplate.findOne(Query.query(Criteria.where("type").is("upgrade")), SequenceGenerator.class, COLLECTION_NAME);
        if (Objects.isNull(sequenceGenerator)) {
            sequenceGenerator = SequenceGenerator.create(1L, "upgrade");
            mongoTemplate.insert(sequenceGenerator, COLLECTION_NAME);
        }
        else {
            sequenceGenerator.setSeqId(sequenceGenerator.getSeqId()+1);
            Document doc = new Document();
            mongoTemplate.getConverter().write(sequenceGenerator, doc);
            mongoTemplate.updateMulti(Query.query(Criteria.where("type").is("upgrade")), Update.fromDocument(doc), COLLECTION_NAME);
        }
        return sequenceGenerator.getSeqId();
    }


}
