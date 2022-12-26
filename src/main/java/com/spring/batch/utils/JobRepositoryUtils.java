/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;

import com.spring.batch.domain.Constants;

/**
 * @author gjayaraman
 * Nov 09, 2022
 */
public class JobRepositoryUtils
{
    public static Map<String, Object> convertToMap(JobParameters jobParameters) {
        Map<String, JobParameter> jobParams = jobParameters.getParameters();
        Map<String, Object> paramMap = new HashMap<>(jobParams.size());
        for (Entry<String, JobParameter> entry : jobParams.entrySet()) {
            paramMap.put(
                    entry.getKey().replaceAll(Constants.DOT_STRING, Constants.DOT_ESCAPE_STRING), entry.getValue().getValue());
        }
        return paramMap;
    }

    public static JobParameters convertToJobParameters(Map<String, Object> originParams) {
        if (originParams == null || originParams.isEmpty()) {
            return new JobParameters();
        }

        Map<String, JobParameter> destParams = new HashMap<>();

        for (Entry<String, Object> param : originParams.entrySet()) {
            String key = param.getKey();
            Object value = param.getValue();

            JobParameter jobParameter = null;

            if (value.getClass().isAssignableFrom(String.class)) {
                jobParameter = new JobParameter(String.valueOf(value));
            } else if (value.getClass().isAssignableFrom(Long.class)) {
                jobParameter = new JobParameter((Long) value);
            } else if (value.getClass().isAssignableFrom(Double.class)) {
                jobParameter = new JobParameter((Double) value);
            } else if (value.getClass().isAssignableFrom(Date.class)) {
                jobParameter = new JobParameter((Date) value);
            }

            // only these types are supported, so no need to assert that jobParameter is null
            destParams.put(key, jobParameter);
        }

        return new JobParameters(destParams);
    }
}
