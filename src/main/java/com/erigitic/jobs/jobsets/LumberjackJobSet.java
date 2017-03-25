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

package com.erigitic.jobs.jobsets;

import ninja.leaping.configurate.ConfigurationNode;

public class LumberjackJobSet implements IDefaultJobSet {

    private static final String SETNAME = "lumberjackSet";

    private static final String[][] rewards = {
            //{"<event>", "<target>", "<expReward>", "<moneyReward>"}
            {"break", "minecraft:log", "10", "1.00"},
            {"break", "minecraft:log2", "10", "1.00"},
            {"break", "minecraft:leaves", "1", "0.01"},
            {"place", "minecraft:sapling", "1", "0.10"}
    };

    @Override
    public void applyOnNode(ConfigurationNode node) {
        ConfigurationNode myNode = node.getNode(SETNAME, "actions");

        for (String[] a : rewards) {
            ConfigurationNode n = myNode.getNode(a[0], a[1]);
            n.getNode("expGain").setValue(a[2]);
            n.getNode("moneyGain").setValue(a[3]);
        }
    }
}
