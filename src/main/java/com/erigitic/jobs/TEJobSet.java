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

/**
 * Created by Life4YourGames on 15.01.17.
 */
public class TEJobSet {
    private List<TEActionReward> actions;

    private TEJobSet() {
        actions = new ArrayList();
    }

    public static TEJobSet of(ConfigurationNode node) {
        TEJobSet set = new TEJobSet();

        if (set.load(node))
            return set;

        return null;
    }

    protected boolean load(ConfigurationNode node) {
        node = node.getNode("actions");
        Map<?, ?> events = node.getChildrenMap();

        //Go through all event entries
        events.forEach((k1, v1) -> {
            if ((k1 instanceof String) && (v1 instanceof ConfigurationNode)) {
                //Go though all id entries
                Map<?, ?> ids = ((ConfigurationNode) v1).getChildrenMap();

                ids.forEach((k2, v2) -> {
                    if ((k2 instanceof String) && (v2 instanceof ConfigurationNode)) {
                        TEActionReward reward = TEActionReward.of(((String) k1), ((String) k2), ((ConfigurationNode) v2));

                        if (reward!=null) {
                            actions.add(reward);
                        } else {
                            //TODO: Might need some debug//error code here
                        }
                    }
                });

            }
        });
        return true;
    }

    public Optional<TEActionReward> getRewardFor(String event, String id) {
        return actions.stream()
                //Match same event
                .filter(r1 -> r1.getEvent().equals(event))
                //Match same id
                .filter(r2 -> r2.getTargetID().equals(id))
                //Return the first match
                .findFirst();
    }

    public Map<String, List<String>> getRewardData() {
        //TODO: You may want to switch to TEActionReward in order to show true rewards
        Map<String, List<String>> map = new HashMap();

        for (TEActionReward action : actions) {
            List<String> l = map.getOrDefault(action.getEvent(), null);

            if (l==null) {
                l = new ArrayList();
                map.put(action.getEvent(), l);
            }

            l.add(action.getTargetID());
        }

        return map;
    }
}
