/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.utils;

/**
 * @author gjayaraman
 * Nov 04, 2022
 */
public class RandomSequenceGeneratorUtils
{
    private RandomSequenceGeneratorUtils() {}

    public synchronized static long getNextSequence() {
        return 1L;
    }
}
