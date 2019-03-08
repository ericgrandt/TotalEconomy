package com.erigitic.jobs.actions;

import com.erigitic.jobs.TEActionReward;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import java.util.Optional;

public class TEPlaceAction extends AbstractBlockAction<ChangeBlockEvent.Place> {

    public TEPlaceAction(TEActionReward baseReward, String blockId) {
        super(baseReward, blockId);
    }

    public TEPlaceAction(TEActionReward baseReward, String blockId, String idTrait) {
        super(baseReward, blockId, idTrait);
    }

    @Override
    protected boolean checkTransaction(Transaction<BlockSnapshot> transaction) {
        return true;
    }

    @Override
    protected BlockState getBlockState(Transaction<BlockSnapshot> transaction) {
        return transaction.getFinal().getState();
    }

    @Override
    protected Optional<TEActionReward> aggregateReward(BlockState blockState) {
        return getTraitedReward(blockState);
    }
}
