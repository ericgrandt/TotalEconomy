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

package com.erigitic.jobs.jobs;

import com.erigitic.jobs.JobBasedRequirement;
import ninja.leaping.configurate.ConfigurationNode;

import java.util.Arrays;

public class FishermanJob implements Job {

    private final String NAME = "fisherman";
    private final String[] SETS = { "fish" };

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String[] getSets() {
        return SETS;
    }

    @Override
    public void populateNode(ConfigurationNode node) {
        node = node.getNode(NAME);

        node.getNode("salary").setValue(20);
        node.getNode("sets").setValue(Arrays.asList(SETS));

        new JobBasedRequirement("", 0, "totaleconomy.job.fisherman").addTo(node);
    }
}
