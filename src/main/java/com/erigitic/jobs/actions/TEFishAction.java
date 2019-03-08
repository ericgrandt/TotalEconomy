package com.erigitic.jobs.actions;

import com.erigitic.jobs.TEActionReward;
import org.spongepowered.api.event.action.FishingEvent;

import java.util.Optional;

public class TEFishAction extends AbstractTEAction<FishingEvent.Stop> {

    public TEFishAction(TEActionReward baseReward) {
        super(baseReward);
    }

    @Override
    public Optional<TEActionReward> evaluate(FishingEvent.Stop triggerEvent) {
        return Optional.of(getBaseReward());
    }
}
