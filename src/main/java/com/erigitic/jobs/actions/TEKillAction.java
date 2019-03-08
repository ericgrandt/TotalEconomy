package com.erigitic.jobs.actions;

import com.erigitic.jobs.TEActionReward;

import java.util.Optional;

public class TEKillAction extends AbstractTEAction<Void> {

    public TEKillAction(TEActionReward baseReward) {
        super(baseReward);
    }

    @Override
    public Optional<TEActionReward> getRewardFor(Void v) {
        return Optional.of(getBaseReward());
    }
}
