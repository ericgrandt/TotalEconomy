package com.ericgrandt.totaleconomy.listeners;

import com.ericgrandt.totaleconomy.common.event.JobEvent;
import com.ericgrandt.totaleconomy.common.listeners.CommonJobListener;
import com.ericgrandt.totaleconomy.commonimpl.SpongePlayer;
import com.ericgrandt.totaleconomy.wrappers.SpongeWrapper;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.FishingEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;

public class JobListener {
    private final SpongeWrapper spongeWrapper;
    private final CommonJobListener commonJobListener;

    public JobListener(final SpongeWrapper spongeWrapper, final CommonJobListener commonJobListener) {
        this.spongeWrapper = spongeWrapper;
        this.commonJobListener = commonJobListener;
    }

    @Listener
    public void onBreakAction(ChangeBlockEvent.All event) {
        if (!(event.source() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        SpongePlayer player = new SpongePlayer(serverPlayer);
        var blockTransaction = event.transactions(
            spongeWrapper.breakOperation()
        ).findFirst().orElse(null);

        if (blockTransaction == null) {
            return;
        }

        BlockState blockState = blockTransaction.original().state();
        if (hasGrowthStage(blockState) && !isAtMaxGrowthStage(blockState)) {
            return;
        }

        commonJobListener.handleAction(
            new JobEvent(
                player,
                "break",
                blockState.type().key(spongeWrapper.blockType()).formatted()
            )
        );
    }

    @Listener
    public void onPlaceOrBreakAction(ChangeBlockEvent.All event) {
        var blockTransactionOpt = event.transactions().stream().findFirst();
        if (!(event.source() instanceof ServerPlayer serverPlayer) || blockTransactionOpt.isEmpty()) {
            return;
        }

        var player = new SpongePlayer(serverPlayer);
        var blockTransaction = blockTransactionOpt.get();
        var blockState = blockTransaction.original().state();

        var operation = blockTransaction.operation();
        if (operation == spongeWrapper.breakOperation()) {
            handleBreakAction(player, blockState);
        } else if (operation == spongeWrapper.placeOperation()) {
            handlePlaceAction(player, blockState);
        }
    }

    @Listener
    public void onKillAction(DamageEntityEvent event) {
        if (event.cause().first(ServerPlayer.class).isEmpty() || !event.willCauseDeath()) {
            return;
        }

        SpongePlayer player = new SpongePlayer(event.cause().first(ServerPlayer.class).get());
        var entityName = event.entity().createSnapshot().type().key(spongeWrapper.entityType).formatted();

        commonJobListener.handleAction(
            new JobEvent(
                player,
                "kill",
                entityName
            )
        );
    }

    @Listener
    public void onFishAction(FishingEvent.Stop event) {
        if (!(event.source() instanceof ServerPlayer player) || event.transactions().isEmpty()) {
            return;
        }

        var itemName = event.transactions().getFirst().original().type().key(spongeWrapper.itemType).formatted();

        commonJobListener.handleAction(
            new JobEvent(
                new SpongePlayer(player),
                "fish",
                itemName
            )
        );
    }

    private void handleBreakAction(SpongePlayer player, BlockState blockState) {
        if (hasGrowthStage(blockState) && !isAtMaxGrowthStage(blockState)) {
            return;
        }

        commonJobListener.handleAction(
            new JobEvent(
                player,
                "break",
                blockState.type().key(spongeWrapper.blockType()).formatted()
            )
        );
    }

    private void handlePlaceAction(SpongePlayer player, BlockState blockState) {
        commonJobListener.handleAction(
            new JobEvent(
                player,
                "place",
                blockState.type().key(spongeWrapper.blockType()).formatted()
            )
        );
    }

    private boolean hasGrowthStage(BlockState blockState) {
        return blockState.get(spongeWrapper.growthStage()).isPresent();
    }

    private boolean isAtMaxGrowthStage(BlockState blockState) {
        int currentGrowthStage = blockState.get(spongeWrapper.growthStage()).orElse(0);
        int maxGrowthStage = blockState.get(spongeWrapper.maxGrowthStage()).orElse(0);

        return currentGrowthStage == maxGrowthStage;
    }
}
