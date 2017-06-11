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

import java.util.*;

public class TEJobSet {
    private List<TEActionReward> actionRewards = new ArrayList();

    public TEJobSet(ConfigurationNode node) {
        node.getChildrenMap().forEach((action, targetNode) -> {
            if ((action instanceof String) &&  targetNode != null) {
                targetNode.getChildrenMap().forEach((targetID, rewardsNode) -> {
                    if ((targetID instanceof String) && rewardsNode != null) {
                        TEActionReward actionReward = new TEActionReward(
                                (String) action,
                                (String) targetID,
                                rewardsNode.getNode("exp").getString("0"),
                                rewardsNode.getNode("money").getString("0"),
                                rewardsNode.getNode("growthTrait").getString(null)
                        );

                        if (actionReward.isValid()) {
                            actionRewards.add(actionReward);
                        }
                    }
                });
            }
        });
    }

    public Optional<TEActionReward> getRewardFor(String action, String targetID) {
        return actionRewards.stream()
                .filter(teActionReward -> teActionReward.getAction().equals(action))
                .filter(teActionReward -> teActionReward.getTargetID().equals(targetID))
                .findFirst();
    }

    public List<TEActionReward> getActionRewards() {
        return actionRewards;
    }
}
