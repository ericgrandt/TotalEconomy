package com.ericgrandt.totaleconomy.listeners;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.event.JobEvent;
import com.ericgrandt.totaleconomy.common.listeners.CommonJobListener;
import com.ericgrandt.totaleconomy.wrappers.SpongeWrapper;
import java.util.Optional;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.block.transaction.BlockTransaction;
import org.spongepowered.api.block.transaction.Operations;
import org.spongepowered.api.entity.living.monster.Creeper;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.block.ChangeBlockEvent;

@ExtendWith(MockitoExtension.class)
public class JobListenerTest {
    @Mock
    private SpongeWrapper spongeWrapperMock;

    @Mock
    private ServerPlayer playerMock;

    @Mock
    private CommonJobListener commonJobListenerMock;

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
    public void onBreakAction_WithAgeableBlockAndNotMaxAge_ShouldReturnWithoutHandlingAction() {
        // Arrange
        ChangeBlockEvent.All eventMock = mock(ChangeBlockEvent.All.class, RETURNS_DEEP_STUBS);
//        Ageable blockDataMock = mock(Ageable.class);
        BlockTransaction blockTransaction = mock(BlockTransaction.class);
        when(eventMock.source()).thenReturn(playerMock);
        when(eventMock.transactions(spongeWrapperMock.breakOperation()).findFirst()).thenReturn(Optional.of(blockTransaction));
//        when(eventMock.getBlock().getType().name()).thenReturn("blockName");
//        when(eventMock.getBlock().getBlockData()).thenReturn(blockDataMock);
//        when(blockDataMock.getAge()).thenReturn(1);
//        when(blockDataMock.getMaximumAge()).thenReturn(2);

        JobListener sut = new JobListener(spongeWrapperMock, commonJobListenerMock);

        // Act
        sut.onBreakAction(eventMock);

        // Assert
        verify(commonJobListenerMock, times(0)).handleAction(any(JobEvent.class));
    }
}
