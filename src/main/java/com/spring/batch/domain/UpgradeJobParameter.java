/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.domain;

import java.util.Map;

import org.springframework.batch.core.JobParameter;

/**
 * @author gjayaraman
 * Dec 09, 2022
 */
public class UpgradeJobParameter extends JobParameter
{
    private Object parameter;
    public UpgradeJobParameter(final String parameter) {
        super(parameter);
    }

    public UpgradeJobParameter(Object parameter) {
        super(parameter.toString(), false);
        this.parameter = parameter;
    }

    public Object getParameter() {
        return parameter;
    }

    public void setParameter(final Object parameter) {
        this.parameter = parameter;
    }
}
