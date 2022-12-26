/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.testJobs;

import org.springframework.batch.core.ItemReadListener;

import com.spring.batch.domain.Job;
import com.spring.batch.domain.Node;

/**
 * @author gjayaraman
 * Oct 27, 2022
 */
public class MongoItemReaderListener implements ItemReadListener<Node>
{
    @Override
    public void beforeRead() {
        System.out.println("Called before read of item ");
    }

    @Override
    public void afterRead(Node item) {
        System.out.println("Called after read of item " + item);
    }

    @Override
    public void onReadError(final Exception ex) {
        System.out.println("Exception occurred while processing item : " + ex);
    }
}
