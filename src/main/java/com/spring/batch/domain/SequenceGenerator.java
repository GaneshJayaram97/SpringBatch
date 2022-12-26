/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.domain;

import java.util.Objects;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

/**
 * @author gjayaraman
 * Nov 07, 2022
 */
public class SequenceGenerator
{
    @Id
    private ObjectId id;
    private long seqId;
    private String type;

    public SequenceGenerator() {}

    public long getSeqId() {
        return seqId;
    }

    public void setSeqId(final long seqId) {
        this.seqId = seqId;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("id" + seqId).append("type" + type).toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final SequenceGenerator that = (SequenceGenerator) o;
        return seqId == that.seqId && type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seqId, type);
    }

    public static SequenceGenerator create(long id, String type) {
        SequenceGenerator sequenceGenerator = new SequenceGenerator();
        sequenceGenerator.setSeqId(id);
        sequenceGenerator.setType(type);
        return sequenceGenerator;
    }
}
