/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.testJobs;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.convert.TypeInformationMapper;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * @author gjayaraman
 * Oct 27, 2022
 */
@Configuration
public class MongoSpringConfig
{
    public String DB = "upgradeJob";

    public String COLLECTION_NAME = "nodes";

    private ConnectionString CONNECTION_STRING = new ConnectionString("mongodb://" + "127.0.0.1" + ":" + 27017);

    @Bean
    public SimpleMongoClientDatabaseFactory simpleMongoClientDatabaseFactory() {
        return new SimpleMongoClientDatabaseFactory(mongoClient(), DB);
    }
    @Bean
    public MongoClient mongoClient() {
        MongoClientSettings.Builder clientSettings = MongoClientSettings.builder();
        clientSettings.applyConnectionString(CONNECTION_STRING);
        return MongoClients.create(clientSettings.build());
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient(), DB);
        MappingMongoConverter mappingMongoConverter = (MappingMongoConverter) mongoTemplate.getConverter();
        mappingMongoConverter.setMapKeyDotReplacement("&$");
        return mongoTemplate;
    }
}
