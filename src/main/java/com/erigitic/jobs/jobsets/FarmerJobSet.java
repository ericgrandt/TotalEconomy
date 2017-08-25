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

public class FarmerJobSet implements JobSet {

    private final String SETNAME = "crops";

    private final Object[][] REWARDS = {
            //{"<event>", "<target>", <expReward>, <moneyReward>, "<growTrait>"}
            // Plants having an age
            {"break", "minecraft:wheat", 25, 50.00d, "age"},
            {"break", "minecraft:cocoa", 30, 60.00d, "age"},

            {"break", "minecraft:cactus", 10, 15.00d, null},
            {"break", "minecraft:reeds", 5, 7.50d, null},

            //These plants aren't hard to grow or harvest thus the low gains
            {"break", "minecraft:red_flower", 1, 0.10d, null},
            {"break", "minecraft:yellow_flower", 1, 0.10d, null},
            {"break", "minecraft:vine", 0, 0.05d, null},
            {"break", "minecraft:waterlily", 1, 0.10d, null},

            {"break", "minecraft:red_mushroom", 8, 5.00d, null},
            {"break", "minecraft:brown_mushroom", 8, 5.00d, null}
    };

    @Override
    public void populateNode(ConfigurationNode node) {
        ConfigurationNode myNode = node.getNode(SETNAME);

        for (Object[] a : REWARDS) {
            ConfigurationNode n = myNode.getNode(a[0], a[1]);
            n.getNode("exp").setValue(a[2]);
            n.getNode("money").setValue(a[3]);
            n.getNode("growth-trait").setValue(a[4]);
        }
    }
}
