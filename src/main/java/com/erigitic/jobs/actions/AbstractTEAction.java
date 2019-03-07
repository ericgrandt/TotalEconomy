package com.erigitic.jobs.actions;

import com.erigitic.jobs.TEActionReward;
import org.spongepowered.api.event.Event;

import java.util.Optional;

public abstract class AbstractTEAction<T extends Event> {

    private TEActionReward baseReward;

    public AbstractTEAction(TEActionReward baseReward) {
        this.baseReward = baseReward;
    }

    public abstract Optional<TEActionReward> evaluate(T triggerEvent);

    public TEActionReward getBaseReward() {
        return baseReward;
    }

    protected TEActionReward makeReward(double money, int exp) {
        final TEActionReward reward = new TEActionReward();
        reward.setValues(exp, money, getBaseReward().getCurrencyId());
        return reward;
    }
}
