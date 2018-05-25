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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import ninja.leaping.configurate.ConfigurationNode;

public class TEJobSet {
    private List<TEAction> actions = new ArrayList();

    public TEJobSet(ConfigurationNode node) {
        node.getChildrenMap().forEach((actionStr, targetNode) -> {
            if ((actionStr instanceof String) &&  targetNode != null) {
                targetNode.getChildrenMap().forEach((targetID, actionNode) -> {
                    if ((targetID instanceof String) && actionNode != null) {
                        TEAction action = new TEAction();
                        action.loadConfigNode((String) actionStr, actionNode);

                        if (action.isValid()) {
                            actions.add(action);
                        }
                    }
                });
            }
        });
    }

    public Optional<TEAction> getActionFor(String action, String targetID) {
        return actions.stream()
                .filter(teAction -> teAction.getAction().equals(action))
                .filter(teAction -> teAction.getTargetId().equals(targetID))
                .findFirst();
    }

    public List<TEAction> getActions() {
        return actions;
    }
}
