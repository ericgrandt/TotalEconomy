package com.erigitic.jobs.actions;

import com.erigitic.jobs.TEActionReward;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import java.util.Optional;

public class TEPlaceAction extends AbstractBlockAction<ChangeBlockEvent.Place> {

    public TEPlaceAction(TEActionReward baseReward) {
        super(baseReward);
    }

    public TEPlaceAction(TEActionReward baseReward, String idTrait, String idTraitValue) {
        super(baseReward, idTrait, idTraitValue);
    }

    @Override
    public Optional<TEActionReward> evaluate(ChangeBlockEvent.Place triggerEvent) {
        double[] moneyReward = new double[]{0};
        int[] expReward = new int[]{0};

        for (Transaction<BlockSnapshot> transaction : triggerEvent.getTransactions()) {
            final BlockState state = transaction.getFinal().getState();
            aggregateReward(state).ifPresent(reward -> {
                moneyReward[0] += reward.getMoneyReward();
                expReward[0] += reward.getExpReward();
            });
        }

        if (moneyReward[0] == 0 && expReward[0] == 0) {
            return Optional.empty();
        } else {
            return Optional.of(makeReward(moneyReward[0], expReward[0]));
        }
    }

    private Optional<TEActionReward> aggregateReward(BlockState blockState) {
        if (!checkIdTrait(blockState)) {
            return Optional.empty();
        } else {
            return Optional.of(getBaseReward());
        }
    }
}
