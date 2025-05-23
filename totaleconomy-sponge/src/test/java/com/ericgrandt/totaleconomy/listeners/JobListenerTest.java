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
import java.util.Optional;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.block.transaction.BlockTransaction;
import org.spongepowered.api.block.transaction.Operation;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.entity.living.monster.Creeper;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.EventContext;
import org.spongepowered.api.event.action.FishingEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

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
    @SuppressWarnings("unchecked")
    public void onPlaceOrBreakAction_WithBreakAndMaxAgeBlock_ShouldHandleAction() {
        // Arrange
        ChangeBlockEvent.All eventMock = mock(ChangeBlockEvent.All.class, RETURNS_DEEP_STUBS);
        BlockTransaction blockTransactionMock = mock(BlockTransaction.class, RETURNS_DEEP_STUBS);
        when(eventMock.transactions().stream().findFirst()).thenReturn(Optional.of(blockTransactionMock));
        when(eventMock.source()).thenReturn(playerMock);
        when(spongeWrapperMock.breakOperation()).thenReturn(operationMock);
        when(blockTransactionMock.operation()).thenReturn(operationMock);
        when(spongeWrapperMock.growthStage()).thenReturn(mock(Key.class));
        when(spongeWrapperMock.maxGrowthStage()).thenReturn(mock(Key.class));
        when(blockTransactionMock.original().state().get(spongeWrapperMock.growthStage()))
            .thenReturn(Optional.of(3));
        when(blockTransactionMock.original().state().get(spongeWrapperMock.maxGrowthStage()))
            .thenReturn(Optional.of(3));
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
    @SuppressWarnings("unchecked")
    public void onPlaceOrBreakAction_WithBreakAndNotMaxAgeBlock_ShouldReturnWithoutHandlingAction() {
        // Arrange
        ChangeBlockEvent.All eventMock = mock(ChangeBlockEvent.All.class, RETURNS_DEEP_STUBS);
        BlockTransaction blockTransactionMock = mock(BlockTransaction.class, RETURNS_DEEP_STUBS);
        when(eventMock.transactions().stream().findFirst()).thenReturn(Optional.of(blockTransactionMock));
        when(eventMock.source()).thenReturn(playerMock);
        when(spongeWrapperMock.breakOperation()).thenReturn(operationMock);
        when(blockTransactionMock.operation()).thenReturn(operationMock);
        when(spongeWrapperMock.growthStage()).thenReturn(mock(Key.class));
        when(spongeWrapperMock.maxGrowthStage()).thenReturn(mock(Key.class));
        when(blockTransactionMock.original().state().get(spongeWrapperMock.growthStage()))
            .thenReturn(Optional.of(1));
        when(blockTransactionMock.original().state().get(spongeWrapperMock.maxGrowthStage()))
            .thenReturn(Optional.of(3));

        JobListener sut = new JobListener(spongeWrapperMock, commonJobListenerMock);

        // Act
        sut.onPlaceOrBreakAction(eventMock);

        // Assert
        verify(commonJobListenerMock, times(0)).handleAction(any(JobEvent.class));
    }

    @Test
    @Tag("Unit")
    public void onPlaceOrBreakAction_WithPlaceSuccess_ShouldHandleAction() {
        // Arrange
        ChangeBlockEvent.All eventMock = mock(ChangeBlockEvent.All.class, RETURNS_DEEP_STUBS);
        BlockTransaction blockTransactionMock = mock(BlockTransaction.class);
        EventContext eventContextMock = mock(EventContext.class);
        ItemStackSnapshot itemStackSnapshotMock = mock(ItemStackSnapshot.class, RETURNS_DEEP_STUBS);
        when(eventMock.transactions().stream().findFirst()).thenReturn(Optional.of(blockTransactionMock));
        when(eventMock.source()).thenReturn(playerMock);
        when(eventMock.cause().context()).thenReturn(eventContextMock);
        when(spongeWrapperMock.placeOperation()).thenReturn(operationMock);
        when(blockTransactionMock.operation()).thenReturn(operationMock);
        when(eventContextMock.get(spongeWrapperMock.usedItem())).thenReturn(Optional.of(itemStackSnapshotMock));
        when(itemStackSnapshotMock.type().key(spongeWrapperMock.itemType())
            .formatted()).thenReturn("minecraft:carrot");

        JobListener sut = new JobListener(spongeWrapperMock, commonJobListenerMock);

        // Act
        sut.onPlaceOrBreakAction(eventMock);

        // Assert
        verify(commonJobListenerMock, times(1)).handleAction(
            new JobEvent(new SpongePlayer(playerMock), "place", "minecraft:carrot")
        );
    }

    @Test
    @Tag("Unit")
    public void onPlaceOrBreakAction_WithPlaceActionAndNoUsedItem_ShouldReturnWithoutHandlingAction() {
        // Arrange
        ChangeBlockEvent.All eventMock = mock(ChangeBlockEvent.All.class, RETURNS_DEEP_STUBS);
        BlockTransaction blockTransactionMock = mock(BlockTransaction.class);
        EventContext eventContextMock = mock(EventContext.class);
        ItemStackSnapshot itemStackSnapshotMock = mock(ItemStackSnapshot.class, RETURNS_DEEP_STUBS);
        when(eventMock.transactions().stream().findFirst()).thenReturn(Optional.of(blockTransactionMock));
        when(eventMock.source()).thenReturn(playerMock);
        when(eventMock.cause().context()).thenReturn(eventContextMock);
        when(spongeWrapperMock.placeOperation()).thenReturn(operationMock);
        when(blockTransactionMock.operation()).thenReturn(operationMock);
        when(eventContextMock.get(spongeWrapperMock.usedItem())).thenReturn(Optional.empty());

        JobListener sut = new JobListener(spongeWrapperMock, commonJobListenerMock);

        // Act
        sut.onPlaceOrBreakAction(eventMock);

        // Assert
        verify(commonJobListenerMock, times(0)).handleAction(any(JobEvent.class));
    }

    @Test
    @Tag("Unit")
    public void onPlaceOrBreakAction_WithNonPlayerSource_ShouldReturnWithoutHandlingAction() {
        // Arrange
        ChangeBlockEvent.All eventMock = mock(ChangeBlockEvent.All.class, RETURNS_DEEP_STUBS);
        BlockTransaction blockTransactionMock = mock(BlockTransaction.class, RETURNS_DEEP_STUBS);
        when(eventMock.transactions().stream().findFirst()).thenReturn(Optional.of(blockTransactionMock));
        when(eventMock.source()).thenReturn(mock(Creeper.class));

        JobListener sut = new JobListener(spongeWrapperMock, commonJobListenerMock);

        // Act
        sut.onPlaceOrBreakAction(eventMock);

        // Assert
        verify(commonJobListenerMock, times(0)).handleAction(any(JobEvent.class));
    }

    @Test
    @Tag("Unit")
    public void onPlaceOrBreakAction_WithNoBlockTransactions_ShouldReturnWithoutHandlingAction() {
        // Arrange
        ChangeBlockEvent.All eventMock = mock(ChangeBlockEvent.All.class, RETURNS_DEEP_STUBS);
        when(eventMock.transactions().stream().findFirst()).thenReturn(Optional.empty());

        JobListener sut = new JobListener(spongeWrapperMock, commonJobListenerMock);

        // Act
        sut.onPlaceOrBreakAction(eventMock);

        // Assert
        verify(commonJobListenerMock, times(0)).handleAction(any(JobEvent.class));
    }

    @Test
    @Tag("Unit")
    public void onPlaceOrBreakAction_WithNoBreakOrPlaceAction_ShouldReturnWithoutHandlingAction() {
        // Arrange
        ChangeBlockEvent.All eventMock = mock(ChangeBlockEvent.All.class, RETURNS_DEEP_STUBS);
        BlockTransaction blockTransactionMock = mock(BlockTransaction.class, RETURNS_DEEP_STUBS);
        when(eventMock.transactions().stream().findFirst()).thenReturn(Optional.of(blockTransactionMock));
        when(eventMock.source()).thenReturn(playerMock);
        when(blockTransactionMock.operation()).thenReturn(operationMock);

        JobListener sut = new JobListener(spongeWrapperMock, commonJobListenerMock);

        // Act
        sut.onPlaceOrBreakAction(eventMock);

        // Assert
        verify(commonJobListenerMock, times(0)).handleAction(any(JobEvent.class));
    }

    @Test
    @Tag("Unit")
    public void onKillAction_WithSuccess_ShouldHandleAction() {
        // Arrange
        DamageEntityEvent.Post eventMock = mock(DamageEntityEvent.Post.class, RETURNS_DEEP_STUBS);
        when(eventMock.cause().first(ServerPlayer.class)).thenReturn(Optional.of(playerMock));
        when(eventMock.willCauseDeath()).thenReturn(true);
        when(
            eventMock.entity().createSnapshot().type().key(
                spongeWrapperMock.entityType()
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
        DamageEntityEvent.Post eventMock = mock(DamageEntityEvent.Post.class, RETURNS_DEEP_STUBS);
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
        DamageEntityEvent.Post eventMock = mock(DamageEntityEvent.Post.class, RETURNS_DEEP_STUBS);
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
                spongeWrapperMock.itemType()
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
