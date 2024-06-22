package com.ericgrandt.totaleconomy.listeners;

import com.ericgrandt.totaleconomy.common.listeners.CommonJobListener;
import com.ericgrandt.totaleconomy.commonimpl.SpongePlayer;
import com.ericgrandt.totaleconomy.wrappers.SpongeWrapper;
import org.spongepowered.api.block.transaction.BlockTransaction;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import java.util.Optional;

public class JobListener {
    private final SpongeWrapper spongeWrapper;
    private final CommonJobListener commonJobListener;

    public JobListener(final SpongeWrapper spongeWrapper, final CommonJobListener commonJobListener) {
        this.spongeWrapper = spongeWrapper;
        this.commonJobListener = commonJobListener;
    }

    @Listener
    public void onBreakAction(ChangeBlockEvent.All event) {
        // TODO: Check if optional blockTransaction is present
        // TODO: Check if ageable
        // TODO: Handle action
        if (!(event.source() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        SpongePlayer player = new SpongePlayer(serverPlayer);
        Optional<BlockTransaction> blockTransaction = event.transactions(
                spongeWrapper.breakOperation()
        ).findFirst();

        // var b = blockTransaction.get().original();
        // b.state().get(Keys.GROWTH_STAGE) != b.state().get(Keys.MAX_GROWTH_STAGE)
    }
}
