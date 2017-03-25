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

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TEJob {
    private List<String> sets;

    private BigDecimal salary;

    private JobBasedRequirement requirement;

    private TEJob() {
        sets = new ArrayList<>();
    }

    public static TEJob of(ConfigurationNode node) {
        TEJob job = new TEJob();

        if (job.load(node))
            return job;

        return null;
    }

    public List<String> getSets() {
        return sets;
    }

    public boolean salaryEnabled() {
        return !salary.equals(BigDecimal.ZERO);
    }

    public BigDecimal getSalary() {
        return salary;
    }

    /**
     * Read values from a {@link ConfigurationNode}
     */
    protected boolean load(ConfigurationNode node) {
        salary = new BigDecimal(node.getNode("salary").getString("0"));

        try {
            sets = node.getNode("sets").getList(TypeToken.of(String.class), new ArrayList<String>());

            if (!node.getNode("require").isVirtual()) {
                requirement = JobBasedRequirement.of(node.getNode("require"));
            }

            return true;
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Optional<JobBasedRequirement> getRequirement() {
        return Optional.ofNullable(requirement);
    }
}
