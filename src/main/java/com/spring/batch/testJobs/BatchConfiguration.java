/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.testJobs;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.PlatformTransactionManager;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sping.batch.jobService.JobService;
import com.spring.batch.domain.Node;
import com.spring.batch.jobRestController.JobRestResource;
import com.spring.batch.tasklet.ImageFetchProcessorTasklet;
import com.spring.batch.tasklet.ImageInstallProcessorTasklet;
import com.spring.batch.tasklet.ImageRebootProcessorTasklet;
import com.spring.batch.tasklet.NodeSelectionProcessor;
import com.spring.batch.utils.UpgradeDataFieldMaxValueIncrementer;

/**
 * @author gjayaraman
 * Oct 27, 2022
 */
@Configuration
@EnableBatchProcessing
@ComponentScan(basePackageClasses = JobRestResource.class)
public class BatchConfiguration
{

    @Autowired
    JobRepository jobRepository;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired MongoSpringConfig mongoSpringConfig;

    String JOB_NAME = "firstUpgradeJob";

    @Bean
    public UpgradeDataFieldMaxValueIncrementer incrementer() {
        return new UpgradeDataFieldMaxValueIncrementer(mongoSpringConfig.mongoTemplate());
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager() {
        return new MongoTransactionManager(mongoSpringConfig.simpleMongoClientDatabaseFactory());
    }

    @Bean
    public BatchConfigurer batchConfigurer() throws Exception {
        return new UpgradeJobBatchConfigurer(mongoSpringConfig.mongoTemplate(), incrementer(), platformTransactionManager(), jobLauncher());
    }

    @Bean
    public JobLauncher jobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Bean
    public void launchFirstJob() throws Exception {
        jobLauncher().run(jobBuilderFactory.get(JOB_NAME).listener(new UpgradeJobExecutionListener())
                .start(imageFetch()).next(imageInstall()).next(imageReboot()).build(), new JobParameters());
    }

    @Bean
    public ObjectMapper jsonJacksonSerializer() {
        ObjectMapper jsonMapper = new ObjectMapper();

        // globally disable building JSON from non-explicitly-annotated elements
        jsonMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        // globally disable including null-valued elements into JSON
        jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);

        return jsonMapper;
    }

    public Step imageFetch() {
        SimpleStepBuilder stepBuilder = stepBuilderFactory.get("imageFetch")
                .chunk(2)
                .reader(mongoItemReader())
                .processor(new ImageFetchProcessor())
                .writer(mongoItemWriter());
        registerListeners(stepBuilder);
        stepBuilder.listener(new ImageFetchProcessingListener());
        return stepBuilder.build();
    }

    public Step NodeSelectionTasklet() {
        return stepBuilderFactory.get("nodeSelectionTasklet")
                                 .tasklet(new NodeSelectionProcessor(mongoSpringConfig.mongoTemplate(), mongoSpringConfig.COLLECTION_NAME))
                                 .build();
    }

    public Step imageFetchTaskLet() {
        return stepBuilderFactory.get("imageFetchTasklet").tasklet(new ImageFetchProcessorTasklet()).build();
    }

    public Step imageInstallTaskLet() {
        return stepBuilderFactory.get("imageInstallTasklet")
                                 .tasklet(new ImageInstallProcessorTasklet()).build();
    }

    public Step imageRebootTaskLet() {
        return stepBuilderFactory.get("imageRebootTasklet")
                                 .tasklet(new ImageRebootProcessorTasklet()).build();
    }

    public Step imageInstall() {
        SimpleStepBuilder stepBuilder = stepBuilderFactory.get("imageInstall")
                .chunk(2)
                .reader(mongoItemReader())
                .processor(new ImageInstallProcessor())
                .writer(mongoItemWriter());
        registerListeners(stepBuilder);
        stepBuilder.listener(new ImageInstallProcessingListener());
        return stepBuilder.build();
    }

    public Step imageReboot() {
        SimpleStepBuilder stepBuilder = stepBuilderFactory.get("imageReboot")
                .chunk(2)
                .reader(mongoItemReader())
                .processor(new ImageRebootProcessor())
                .writer(mongoItemWriter());
        registerListeners(stepBuilder);
        stepBuilder.listener(new ImageRebootProcessingListener());
        return stepBuilder.build();
    }

    private void registerListeners(SimpleStepBuilder simpleStepBuilder) {
        simpleStepBuilder.listener(new MongoItemReaderListener());
        simpleStepBuilder.listener(new UpgradeStepExecutionListener());
    }

    public UpgradeMongoItemReader mongoItemReader() {
        UpgradeMongoItemReader upgradeMongoItemReader = new UpgradeMongoItemReader();
        upgradeMongoItemReader.setCollection(mongoSpringConfig.COLLECTION_NAME);
        upgradeMongoItemReader.setTemplate(mongoSpringConfig.mongoTemplate());
        upgradeMongoItemReader.setTargetType(Node.class);
        upgradeMongoItemReader.setQuery(new Query());
        return upgradeMongoItemReader;
    }

    public UpgradeMongoItemWriter mongoItemWriter() {
        UpgradeMongoItemWriter upgradeMongoItemWriter = new UpgradeMongoItemWriter();
        upgradeMongoItemWriter.setCollection(mongoSpringConfig.COLLECTION_NAME);
        upgradeMongoItemWriter.setTemplate(mongoSpringConfig.mongoTemplate());
        return upgradeMongoItemWriter;
    }

    @Bean
    public JobService jobService() throws Exception {
        return new JobService(jobLauncher(), this);
    }
}
