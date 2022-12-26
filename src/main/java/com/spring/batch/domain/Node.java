/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.domain;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author gjayaraman
 * Oct 27, 2022
 */
@Document(collection = "nodes")
public class Node
{
    public String ip;
    public String hostname;
    public String model;
    public String status;

    public Node() {}

    public String getIp() {
        return ip;
    }

    public void setIp(final String ip) {
        this.ip = ip;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(final String hostname) {
        this.hostname = hostname;
    }

    public String getModel() {
        return model;
    }

    public void setModel(final String model) {
        this.model = model;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new StringBuilder().append(" ip " + ip)
                                 .append(" hostname " + hostname)
                                 .append(" model " + model)
                                  .append(" status " + status)
                                 .toString();
    }
}
