package com.erigitic.jobs.actions;

import com.erigitic.jobs.TEActionReward;
import org.spongepowered.api.event.entity.DestructEntityEvent;

import java.util.Optional;

public class TEKillAction extends AbstractTEAction<DestructEntityEvent.Death> {

    public TEKillAction(TEActionReward baseReward) {
        super(baseReward);
    }

    @Override
    public Optional<TEActionReward> evaluate(DestructEntityEvent.Death triggerEvent) {
        return Optional.of(getBaseReward());
    }
}
