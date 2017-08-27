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

import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import org.slf4j.Logger;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.DataQuery;

import java.util.*;

public class TEAction {

    private String action;
    private String targetID;

    /* Complex stuff if we need to evaluate traits */
    private String growthTrait = null;
    private String idTrait = null;
    private Map<String, TEActionReward> rewards;

    /* Simple stuff where we don't */
    private TEActionReward reward;

    public void loadConfigNode(String action, ConfigurationNode node) {

        ConfigurationNode idTraitNode = node.getNode("id-trait");
        ConfigurationNode growthTraitNode = node.getNode("growthTrait");

        if (growthTraitNode == null) {
            growthTraitNode = node.getNode("growth-trait");
        }

        if (idTraitNode.isVirtual()) {
            this.action = action;
            this.targetID = node.getKey().toString();
            this.reward = new TEActionReward();
            this.reward.loadConfigNode(node);
            this.growthTrait = growthTraitNode.getString(null);
            return;
        }

        String traitName = idTraitNode.getString("type");

        ConfigurationNode idTraits = node.getNode(traitName);
        Map<String, TEActionReward> rewardMap = new HashMap<>();

        if (idTraits.hasMapChildren()) {
            idTraits.getChildrenMap().forEach((k, v) -> {

                if (!(k instanceof String)) {
                    return;
                }

                TEActionReward reward = new TEActionReward();
                reward.loadConfigNode(v);
                rewardMap.put(((String) k), reward);
            });
        }
        this.action = action;
        this.targetID = node.getKey().toString();
        this.rewards = rewardMap;
        this.idTrait = idTraitNode.getString("type");
        this.growthTrait = growthTraitNode.getString(null);
    }

    public boolean isGrowing() {
        return growthTrait != null;
    }

    public boolean isIDTraited() {
        return idTrait != null;
    }

    public String getIDTrait() {
        return idTrait;
    }

    public String getAction() {
        return action;
    }

    public String getTargetID() {
        return targetID;
    }

    public Optional<TEActionReward> getReward() {
        return Optional.ofNullable(reward);
    }

    public Map<String, TEActionReward> getRewards() {
        return rewards;
    }

    public boolean isValid() {
        return (action != null
                && !action.trim().isEmpty()
                && targetID != null
                && !targetID.trim().isEmpty());
    }

    public Optional<TEActionReward> evaluateBreak(Logger logger, BlockState state, UUID blockCreator) {

        // Disqualifying checks first for performance
        if (!state.getType().getId().equals(this.targetID)) {
            return Optional.empty();
        }

        if (growthTrait == null && blockCreator != null) {
            // A player placed the block and it doesn't indicate growth -> Do not pay to prevent exploits
            return Optional.empty();
        }

        // Determine base reward - Use complicated way if this block has an ID trait
        TEActionReward reward = null;

        if (idTrait != null) {
            Optional<BlockTrait<?>> trait = state.getTrait(idTrait);

            if (trait.isPresent()) {
                Optional<?> traitVal = state.getTraitValue(trait.get());

                if (traitVal.isPresent()) {
                    String key = traitVal.get().toString();
                    reward = rewards.getOrDefault(key, null);
                }

            } else {
                logger.warn("ID trait \"", idTrait, "\" not found during action: ", action, ':', targetID);
            }

        } else {
            reward = this.reward;
        }

        if (reward == null) {
            return Optional.empty();
        }

        // Base reward is determined - Now check if we need to apply modifiers
        // # GrowthTrait #
        if (growthTrait != null) {

            Optional<BlockTrait<?>> trait = state.getTrait(growthTrait);

            if (trait.isPresent()) {

                if (Integer.class.isAssignableFrom(trait.get().getValueClass())) {

                    Optional<?> traitVal = state.getTraitValue(trait.get());

                    if (traitVal.isPresent()) {

                        Integer val = ((Integer) traitVal.get());
                        Collection<Integer> coll = (Collection<Integer>) trait.get().getPossibleValues();
                        Integer max = coll.stream().max(Comparator.comparingInt(Integer::intValue)).orElse(0);
                        Integer min = coll.stream().min(Comparator.comparingInt(Integer::intValue)).orElse(0);
                        Double perc = (double) (val - min) / (double) (max - min);
                        reward = new TEActionReward() ;
                        reward.setValues((int) (reward.getExpReward() * perc),reward.getMoneyReward() * perc, reward.getCurrencyID());

                    } else {
                        logger.warn("Growth trait \"", growthTrait, "\" has missing value during action: ", action, ':', targetID);
                    }

                } else {
                    logger.warn("Growth trait \"", growthTrait, "\" is not numeric!");
                }

            } else {
                logger.warn("Growth trait \"", growthTrait, "\" not found during action: ", action, ':', targetID);
            }
        }
        return Optional.ofNullable(reward);
    }

    public Optional<TEActionReward> evaluatePlace(Logger logger, BlockState state) {

        // Disqualifying checks first for performance
        if (!state.getType().getId().equals(this.targetID)) {
            return Optional.empty();
        }

        // Determine base reward - Use complicated way if this block has an ID trait
        TEActionReward reward = null;

        if (idTrait != null) {

            Optional<BlockTrait<?>> trait = state.getTrait(idTrait);

            if (trait.isPresent()) {

                Optional<?> traitVal = state.getTraitValue(trait.get());

                if (traitVal.isPresent()) {
                    String key = traitVal.get().toString();
                    reward = rewards.getOrDefault(key, null);
                }

            } else {
                logger.warn("ID trait \"", idTrait, "\" not found during action: ", action, ':', targetID);
            }

        } else {
            reward = this.reward;
        }

        return Optional.ofNullable(reward);
    }
}
