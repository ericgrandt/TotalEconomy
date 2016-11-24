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

public class MinerJob implements Job {
    public void setupJobValues(ConfigurationNode jobsConfig) {
        String[][] breakValues = {{"minecraft:coal_ore", "5", "0.25"}, {"minecraft:iron_ore", "10", "0.50"}, {"minecraft:lapis_ore", "20", "4.00"},
                {"minecraft:gold_ore", "40", "5.00"}, {"minecraft:diamond_ore", "100", "25.00"}, {"minecraft:redstone_ore", "25", "2.00"},
                {"minecraft:emerald_ore", "50", "12.50"}, {"minecraft:quartz_ore", "5", "0.15"}};

        for (int i = 0; i < breakValues.length; i++) {
            jobsConfig.getNode("Miner", "break", breakValues[i][0], "expreward").setValue(breakValues[i][1]);
            jobsConfig.getNode("Miner", "break", breakValues[i][0], "pay").setValue(breakValues[i][2]);
        }
        jobsConfig.getNode("Miner", "disablesalary").setValue(false);
        jobsConfig.getNode("Miner", "salary").setValue(20);
        jobsConfig.getNode("Miner", "permission").setValue("totaleconomy.job.miner");
    }
}
