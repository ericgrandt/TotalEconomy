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

public class WarriorJob implements Job {

    private static final String name = "warrior";
    private static final String[] sets = { name + "Set"};

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getSets() {
        return sets;
    }

    @Override
    public void populateNode(ConfigurationNode node) {
        node = node.getNode(name);

        node.getNode("salary").setValue(10);
        node.getNode("sets").setValue(Arrays.asList(sets));

        // TODO: Stop with the static when it's not necessary. Remove and replace.
        JobBasedRequirement.of(null, 0, "totaleconomy.job.warrior").addTo(node);
    }
}
