/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.domain;

import java.util.Objects;

import org.bson.types.ObjectId;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.data.annotation.Id;

/**
 * @author gjayaraman
 * Nov 07, 2022
 */
public class UpgradeExecutionContext extends ExecutionContext
{

    @Id
    private ObjectId objectId;

    private long id;

    public UpgradeExecutionContext(ExecutionContext executionContext) {
        super(executionContext);
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final UpgradeExecutionContext that = (UpgradeExecutionContext) o;
        return super.equals(that) && id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, super.hashCode());
    }

    @Override
    public String toString() {
        return new StringBuilder().append(" id " + id).append(" executionContext " +  super.toString()).toString();
    }

    public static UpgradeExecutionContext createUpgradeExecutionContext(long id, ExecutionContext executionContext) {
        UpgradeExecutionContext upgradeExecutionContext = new UpgradeExecutionContext(executionContext);
        upgradeExecutionContext.setId(id);
        return upgradeExecutionContext;
    }
}
