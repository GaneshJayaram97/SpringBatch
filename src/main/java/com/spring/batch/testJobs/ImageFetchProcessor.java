/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.testJobs;

import org.springframework.batch.item.support.CompositeItemProcessor;

/**
 * @author gjayaraman
 * Oct 27, 2022
 */
public class ImageFetchProcessor<Node> extends CompositeItemProcessor<Node, Node>
{
    public ImageFetchProcessor() {}

    @Override
    public Node process(Node node) {
        System.out.println("Processing Image Fetch for the node " + node);
        System.out.println("Done processing Image Fetch for the node " + node);
        return node;
    }
}
