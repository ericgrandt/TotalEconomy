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

/**
 * @author MarkL4YG
 */
public class MinerJobSet implements IDefaultJobSet {

    private static final String SETNAME = "minerSet";

    private static final String[][] rewards = {
            //{"<event>", "<target>", "<expReward>", "<moneyReward>"}
            {"break", "minecraft:coal_ore", "5", "0.25"},
            {"break", "minecraft:iron_ore", "10", "0.50"},
            {"break", "minecraft:lapis_ore", "20", "4.00"},
            {"break", "minecraft:gold_ore", "40", "5.00"},
            {"break", "minecraft:diamond_ore", "100", "25.00"},
            {"break", "minecraft:emerald_ore", "50", "12.50"},
            {"break", "minecraft:quartz_ore", "5", "0.15"},
            {"break", "minecraft:redstone_ore", "25", "2.00"}
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
