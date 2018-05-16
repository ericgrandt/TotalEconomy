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

public class TEActionReward {

    private int expReward;
    private double moneyReward;
    private String currencyId;

    public void loadConfigNode(ConfigurationNode node) {
        this.expReward = node.getNode("exp").getInt(0);
        this.moneyReward = node.getNode("money").getDouble(0.00d);
        this.currencyId = node.getNode("currency").getString(null);
    }

    public void setValues(Integer expReward, Double moneyReward, String currencyID) {
        this.expReward = expReward;
        this.moneyReward = moneyReward;
        this.currencyId = currencyID;
    }

    public Integer getExpReward() {
        return expReward;
    }

    public Double getMoneyReward() {
        return moneyReward;
    }

    public String getCurrencyId() {
        return currencyId;
    }
}
