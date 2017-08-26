package com.erigitic.jobs;

import ninja.leaping.configurate.ConfigurationNode;

/**
 * Created by Life4YourGames on 25.08.17.
 */
public class TEActionReward {

    public static TEActionReward getFromNode(ConfigurationNode node) {
        return new TEActionReward(
                node.getNode("exp").getInt(0),
                node.getNode("money").getDouble(0.00d),
                node.getNode("currency").getString(null)
        );
    }

    private Integer expReward;
    private Double moneyReward;
    private String currencyID;

    public TEActionReward(Integer expReward, Double moneyReward, String currencyID) {
        this.expReward = expReward;
        this.moneyReward = moneyReward;
        this.currencyID = currencyID;
    }

    public Integer getExpReward() {
        return expReward;
    }

    public Double getMoneyReward() {
        return moneyReward;
    }

    public String getCurrencyID() {
        return currencyID;
    }
}
