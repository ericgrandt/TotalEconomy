package com.ericgrandt.totaleconomy.listeners;

import com.ericgrandt.totaleconomy.common.event.JobEvent;
import com.ericgrandt.totaleconomy.common.listeners.CommonJobListener;
import com.ericgrandt.totaleconomy.commonimpl.SpongePlayer;
import com.ericgrandt.totaleconomy.wrappers.SpongeWrapper;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.transaction.BlockTransaction;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.Ageable;
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
        // TODO: Check if ageable
        // TODO: Handle action
        if (!(event.source() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        SpongePlayer player = new SpongePlayer(serverPlayer);
        Optional<BlockTransaction> blockTransactionOpt = event.transactions(
                spongeWrapper.breakOperation()
        ).findFirst();

        if (blockTransactionOpt.isEmpty()) {
            return;
        }

        BlockTransaction blockTransaction = blockTransactionOpt.get();
        BlockState blockState = blockTransaction.original().state();
        if (hasGrowthStage(blockState) && !isAtMaxGrowthStage(blockState)) {
            return;
        }

        commonJobListener.handleAction(new JobEvent(player, "break", "REPLACE ME"));
    }

    private boolean hasGrowthStage(BlockState blockState) {
        var b = blockState.get(spongeWrapper.growthStage()).isPresent();
        return b;
    }

    private boolean isAtMaxGrowthStage(BlockState blockState) {
        int currentGrowthStage = blockState.get(spongeWrapper.growthStage()).orElseThrow();
        int maxGrowthStage = blockState.get(spongeWrapper.maxGrowthStage()).orElseThrow();

        return currentGrowthStage == maxGrowthStage;
    }
}
