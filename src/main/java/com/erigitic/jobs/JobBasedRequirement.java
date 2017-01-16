/*
 * This file is part of Total Economy, licensed under the MIT License (MIT).
 *
 * Copyright (c) Eric Grandt <https://www.ericgrandt.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.erigitic.jobs;

import ninja.leaping.configurate.ConfigurationNode;

/**
 * @author MarkL4YG
 *
 * Requirement notation that is usable in various places such as higher job tiers
 */
public class JobBasedRequirement {

    public static JobBasedRequirement of(ConfigurationNode node) {
        return new JobBasedRequirement(node);
    }
    public static JobBasedRequirement of(String needsJob, int needJobLevel, String needsPermission) {
        return new JobBasedRequirement(needsJob, needJobLevel, needsPermission);
    }

    private int needJobLevel;
    private String needsJob;
    private String needsPermission;

    private JobBasedRequirement(ConfigurationNode node) {
        this(node.getNode("job").getString(null),
                node.getNode("level").getInt(0),
                node.getNode("permission").getString(null));
    }

    private JobBasedRequirement(String needsJob, int needJobLevel, String needsPermission) {
        this.needsJob = needsJob;
        this.needJobLevel = needJobLevel;
        this.needsPermission = needsPermission;
    }

    public int jobLevelNeeded() {
        return needJobLevel;
    }

    //@Nullable
    public String jobNeeded() {
        return needsJob;
    }

    //@Nullable
    public String permissionNeeded() {
        return needsPermission;
    }

    public void addTo(ConfigurationNode node) {
        node = node.getNode("require");
        node.getNode("job").setValue(needsJob);
        node.getNode("level").setValue(needJobLevel);
        node.getNode("permission").setValue(needsPermission);
    }
}
