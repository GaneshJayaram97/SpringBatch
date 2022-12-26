/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.testJobs;

import org.springframework.batch.core.ItemProcessListener;
import org.springframework.lang.Nullable;

/**
 * @author gjayaraman
 * Oct 27, 2022
 */
public class ImageRebootProcessingListener<T, S> implements ItemProcessListener<T, S>
{
    @Override
    public void beforeProcess(T item) {
        System.out.println("Called before processing item : " + item);
    }

    @Override
    public void afterProcess(T item, @Nullable S result) {
        System.out.println("Called after process : " + item + " and result : " + result);
    }

    @Override
    public void onProcessError(final T item, final Exception e) {
        System.out.println("Exception occurred while processing item : " + item + " Exception : " + e);
    }
}
