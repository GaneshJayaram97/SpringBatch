/*
 * Copyright (c) 2004-2022 by Gigamon Systems, Inc. All Rights Reserved.
 */
package com.spring.batch.testJobs;

import org.springframework.batch.item.ItemProcessor;

/**
 * @author gjayaraman
 * Oct 27, 2022
 */
public class ImageRebootProcessor<Node> implements ItemProcessor<Node, Node>
{
    @Override
    public Node process(Node node) {
        System.out.println("Processing Image Reboot for the node " + node);
        System.out.println("Done processing Image Reboot for the node " + node);
        return node;
    }
}
