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

import com.erigitic.jobs.Job;
import ninja.leaping.configurate.ConfigurationNode;

public class LumberjackJob implements Job {
    public void setupJobValues(ConfigurationNode jobsConfig) {
        String[][] breakValues = {{"minecraft:log", "10", "1.00"}, {"minecraft:leaves", "1", "0.01"}};
        String[][] placeValue = {{"minecraft:sapling", "1", "0.10"}};
        
        for (int i = 0; i < breakValues.length; i++) {
            jobsConfig.getNode("Lumberjack", "break", breakValues[i][0], "expreward").setValue(breakValues[i][1]);
            jobsConfig.getNode("Lumberjack", "break", breakValues[i][0], "pay").setValue(breakValues[i][2]);
        }

        for (int i = 0; i < placeValue.length; i++) {
            jobsConfig.getNode("Lumberjack", "place", placeValue[i][0], "expreward").setValue(placeValue[i][1]);
            jobsConfig.getNode("Lumberjack", "place", placeValue[i][0], "pay").setValue(placeValue[i][2]);
        }
        jobsConfig.getNode("Lumberjack", "disablesalary").setValue(false);
        jobsConfig.getNode("Lumberjack", "salary").setValue(20);
        jobsConfig.getNode("Lumberjack", "permission").setValue("totaleconomy.job.lumberjack");
    }
}