package com.ericgrandt.totaleconomy.listeners;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.event.JobEvent;
import com.ericgrandt.totaleconomy.common.listeners.CommonJobListener;
import com.ericgrandt.totaleconomy.commonimpl.SpongePlayer;
import com.ericgrandt.totaleconomy.wrappers.SpongeWrapper;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.block.transaction.BlockTransaction;
import org.spongepowered.api.block.transaction.Operation;
import org.spongepowered.api.block.transaction.Operations;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.monster.Creeper;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.action.FishingEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;

@ExtendWith(MockitoExtension.class)
public class JobListenerTest {
    @Mock
    private SpongeWrapper spongeWrapperMock;

    @Mock
    private ServerPlayer playerMock;

    @Mock
    private CommonJobListener commonJobListenerMock;

    @Mock
    private Operation operationMock;

    @Test
    @Tag("Unit")
    public void onBreakAction_WithSuccess_ShouldHandleAction() {
        // Arrange
        ChangeBlockEvent.All eventMock = mock(ChangeBlockEvent.All.class, RETURNS_DEEP_STUBS);
        BlockTransaction blockTransactionMock = mock(BlockTransaction.class, RETURNS_DEEP_STUBS);
        when(eventMock.source()).thenReturn(playerMock);
        when(eventMock.transactions(spongeWrapperMock.breakOperation()).findFirst())
            .thenReturn(Optional.of(blockTransactionMock));
        when(
            blockTransactionMock.original().state().type().key(spongeWrapperMock.blockType()
        ).formatted()).thenReturn("minecraft:coal_ore");

        JobListener sut = new JobListener(spongeWrapperMock, commonJobListenerMock);

        // Act
        sut.onBreakAction(eventMock);

        // Assert
        verify(commonJobListenerMock, times(1)).handleAction(
            new JobEvent(new SpongePlayer(playerMock), "break", "minecraft:coal_ore")
        );
    }

    @Test
    @Tag("Unit")
    public void onPlaceOrBreakAction_WithBreakSuccess_ShouldHandleAction() {
        // Arrange
        ChangeBlockEvent.All eventMock = mock(ChangeBlockEvent.All.class, RETURNS_DEEP_STUBS);
        BlockTransaction blockTransactionMock = mock(BlockTransaction.class, RETURNS_DEEP_STUBS);
        when(eventMock.transactions().stream().findFirst()).thenReturn(Optional.of(blockTransactionMock));
        when(eventMock.source()).thenReturn(playerMock);
        when(spongeWrapperMock.breakOperation()).thenReturn(operationMock);
        when(blockTransactionMock.operation()).thenReturn(operationMock);
        when(
            blockTransactionMock.original().state().type().key(
                spongeWrapperMock.blockType()
            ).formatted()
        ).thenReturn("minecraft:coal_ore");

        JobListener sut = new JobListener(spongeWrapperMock, commonJobListenerMock);

        // Act
        sut.onPlaceOrBreakAction(eventMock);

        // Assert
        verify(commonJobListenerMock, times(1)).handleAction(
            new JobEvent(new SpongePlayer(playerMock), "break", "minecraft:coal_ore")
        );
    }

    @Test
    @Tag("Unit")
    public void onBreakAction_WithNonPlayerSource_ShouldReturnWithoutHandlingAction() {
        // Arrange
        ChangeBlockEvent.All eventMock = mock(ChangeBlockEvent.All.class, RETURNS_DEEP_STUBS);
        when(eventMock.source()).thenReturn(mock(Creeper.class));

        JobListener sut = new JobListener(spongeWrapperMock, commonJobListenerMock);

        // Act
        sut.onBreakAction(eventMock);

        // Assert
        verify(commonJobListenerMock, times(0)).handleAction(any(JobEvent.class));
    }

    @Test
    @Tag("Unit")
    public void onBreakAction_WithNoBreakBlockTransaction_ShouldReturnWithoutHandlingAction() {
        // Arrange
        ChangeBlockEvent.All eventMock = mock(ChangeBlockEvent.All.class, RETURNS_DEEP_STUBS);
        when(eventMock.source()).thenReturn(playerMock);
        when(eventMock.transactions(spongeWrapperMock.breakOperation()).findFirst()).thenReturn(Optional.empty());

        JobListener sut = new JobListener(spongeWrapperMock, commonJobListenerMock);

        // Act
        sut.onBreakAction(eventMock);

        // Assert
        verify(commonJobListenerMock, times(0)).handleAction(any(JobEvent.class));
    }

    @Test
    @Tag("Unit")
    @SuppressWarnings("unchecked")
    public void onBreakAction_WithAgeableBlockAndNotMaxAge_ShouldReturnWithoutHandlingAction() {
        // Arrange
        ChangeBlockEvent.All eventMock = mock(ChangeBlockEvent.All.class, RETURNS_DEEP_STUBS);
        BlockTransaction blockTransactionMock = mock(BlockTransaction.class, RETURNS_DEEP_STUBS);
        when(eventMock.source()).thenReturn(playerMock);
        when(eventMock.transactions(spongeWrapperMock.breakOperation()).findFirst())
            .thenReturn(Optional.of(blockTransactionMock));

        when(spongeWrapperMock.growthStage()).thenReturn(mock(Key.class));
        when(spongeWrapperMock.maxGrowthStage()).thenReturn(mock(Key.class));

        when(blockTransactionMock.original().state().get(spongeWrapperMock.growthStage()))
            .thenReturn(Optional.of(1));
        when(blockTransactionMock.original().state().get(spongeWrapperMock.maxGrowthStage()))
            .thenReturn(Optional.of(3));

        JobListener sut = new JobListener(spongeWrapperMock, commonJobListenerMock);

        // Act
        sut.onBreakAction(eventMock);

        // Assert
        verify(commonJobListenerMock, times(0)).handleAction(any(JobEvent.class));
    }

    @Test
    @Tag("Unit")
    public void onKillAction_WithSuccess_ShouldHandleAction() {
        // Arrange
        DamageEntityEvent eventMock = mock(DamageEntityEvent.class, RETURNS_DEEP_STUBS);
        when(eventMock.cause().first(ServerPlayer.class)).thenReturn(Optional.of(playerMock));
        when(eventMock.willCauseDeath()).thenReturn(true);
        when(
            eventMock.entity().createSnapshot().type().key(
                spongeWrapperMock.entityType
            ).formatted()
        ).thenReturn("minecraft:bat");

        JobListener sut = new JobListener(spongeWrapperMock, commonJobListenerMock);

        // Act
        sut.onKillAction(eventMock);

        // Assert
        verify(commonJobListenerMock, times(1)).handleAction(
            new JobEvent(new SpongePlayer(playerMock), "kill", "minecraft:bat")
        );
    }

    @Test
    @Tag("Unit")
    public void onKillAction_WithNonPlayerCause_ShouldReturnWithoutHandlingAction() {
        // Arrange
        DamageEntityEvent eventMock = mock(DamageEntityEvent.class, RETURNS_DEEP_STUBS);
        when(eventMock.cause().first(ServerPlayer.class)).thenReturn(Optional.empty());

        JobListener sut = new JobListener(spongeWrapperMock, commonJobListenerMock);

        // Act
        sut.onKillAction(eventMock);

        // Assert
        verify(commonJobListenerMock, times(0)).handleAction(any(JobEvent.class));
    }

    @Test
    @Tag("Unit")
    public void onKillAction_WithEventNotCausingDeath_ShouldReturnWithoutHandlingAction() {
        // Arrange
        DamageEntityEvent eventMock = mock(DamageEntityEvent.class, RETURNS_DEEP_STUBS);
        when(eventMock.cause().first(ServerPlayer.class)).thenReturn(Optional.of(playerMock));
        when(eventMock.willCauseDeath()).thenReturn(false);

        JobListener sut = new JobListener(spongeWrapperMock, commonJobListenerMock);

        // Act
        sut.onKillAction(eventMock);

        // Assert
        verify(commonJobListenerMock, times(0)).handleAction(any(JobEvent.class));
    }

    @Test
    @Tag("Unit")
    public void onFishAction_WithSuccess_ShouldHandleAction() {
        // Arrange
        FishingEvent.Stop eventMock = mock(FishingEvent.Stop.class, RETURNS_DEEP_STUBS);
        when(eventMock.source()).thenReturn(playerMock);
        when(eventMock.transactions().isEmpty()).thenReturn(false);
        when(
            eventMock.transactions().getFirst().original().type().key(
                spongeWrapperMock.itemType
            ).formatted()
        ).thenReturn("minecraft:cod");

        JobListener sut = new JobListener(spongeWrapperMock, commonJobListenerMock);

        // Act
        sut.onFishAction(eventMock);

        // Assert
        verify(commonJobListenerMock, times(1)).handleAction(
            new JobEvent(new SpongePlayer(playerMock), "fish", "minecraft:cod")
        );
    }

    @Test
    @Tag("Unit")
    public void onFishAction_WithNonPlayerSource_ShouldReturnWithoutHandlingAction() {
        // Arrange
        FishingEvent.Stop eventMock = mock(FishingEvent.Stop.class, RETURNS_DEEP_STUBS);
        when(eventMock.source()).thenReturn(mock(Creeper.class));

        JobListener sut = new JobListener(spongeWrapperMock, commonJobListenerMock);

        // Act
        sut.onFishAction(eventMock);

        // Assert
        verify(commonJobListenerMock, times(0)).handleAction(any(JobEvent.class));
    }

    @Test
    @Tag("Unit")
    public void onFishAction_WithNoItemCaught_ShouldReturnWithoutHandlingAction() {
        // Arrange
        FishingEvent.Stop eventMock = mock(FishingEvent.Stop.class, RETURNS_DEEP_STUBS);
        when(eventMock.source()).thenReturn(playerMock);
        when(eventMock.transactions().isEmpty()).thenReturn(true);

        JobListener sut = new JobListener(spongeWrapperMock, commonJobListenerMock);

        // Act
        sut.onFishAction(eventMock);

        // Assert
        verify(commonJobListenerMock, times(0)).handleAction(any(JobEvent.class));
    }
}
