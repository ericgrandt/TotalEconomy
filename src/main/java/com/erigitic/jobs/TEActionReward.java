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

import java.math.BigDecimal;

/**
 * @author MarkL4YG
 */
public class TEActionReward {

    //@Nullable
    public static TEActionReward of(String event, String targetID, ConfigurationNode node) {
        TEActionReward reward = new TEActionReward();
        if (reward.load(event, targetID, node))
            return reward;
        return null;
    }

    private String event;
    private String targetID;
    private int expReward;
    private BigDecimal moneyReward;

    private TEActionReward() {
    }

    protected boolean load(String event, String targetID, ConfigurationNode node) {
        this.event = event;
        this.targetID = targetID;
        expReward = node.getNode("expGain").getInt(0);
        moneyReward = new BigDecimal(node.getNode("moneyGain").getString("0"));
        return event!=null && !event.isEmpty() && targetID!=null && !targetID.isEmpty();
    }

    public String getEvent() {
        return event;
    }

    public String getTargetID() {
        return targetID;
    }

    public int getExpReward() {
        return expReward;
    }

    public BigDecimal getMoneyReward() {
        return moneyReward;
    }
}
