/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.domain;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author gjayaraman
 * Oct 27, 2022
 */
@Document(collection = "job")
public class Job
{
    public String id;

    public String name;

    public List<String> deviceIps;

    public Integer priority;

    public String status;

    public Job() {}

    public Job createJob(String name, List<String> deviceIps, Integer priority) {
        Job job = new Job();
        job.id = UUID.randomUUID().toString();
        job.name = name;
        job.deviceIps = deviceIps;
        job.priority = priority;
        return job;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<String> getDeviceIps() {
        return deviceIps;
    }

    public void setDeviceIps(final List<String> deviceIps) {
        this.deviceIps = deviceIps;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(final Integer priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("id" + id)
                                 .append("name" + name)
                                 .append("deviceIps " + deviceIps)
                                 .append("priority" + priority)
                                  .append("status" + status)
                                 .toString();
    }
}
