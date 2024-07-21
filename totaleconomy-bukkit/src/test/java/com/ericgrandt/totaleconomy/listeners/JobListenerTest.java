package com.ericgrandt.totaleconomy.listeners;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.event.JobEvent;
import com.ericgrandt.totaleconomy.common.listeners.CommonJobListener;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JobListenerTest {
    @Mock
    private Player playerMock;

    @Mock
    private CommonJobListener commonJobListenerMock;

    @Test
    @Tag("Unit")
    public void onBreakAction_WithAgeableBlockAndNotMaxAge_ShouldReturnWithoutHandlingAction() {
        // Arrange
        BlockBreakEvent eventMock = mock(BlockBreakEvent.class, RETURNS_DEEP_STUBS);
        Ageable blockDataMock = mock(Ageable.class);
        when(eventMock.getPlayer()).thenReturn(playerMock);
        when(eventMock.getBlock().getBlockData()).thenReturn(blockDataMock);
        when(blockDataMock.getAge()).thenReturn(1);
        when(blockDataMock.getMaximumAge()).thenReturn(2);

        JobListener sut = new JobListener(commonJobListenerMock);

        // Act
        sut.onBreakAction(eventMock);

        // Assert
        verify(commonJobListenerMock, times(0)).handleAction(any(JobEvent.class));
    }

    @Test
    @Tag("Unit")
    public void onBreakAction_WithAgeableBlockAndMaxAge_ShouldHandleAction() {
        // Arrange
        BlockBreakEvent eventMock = mock(BlockBreakEvent.class, RETURNS_DEEP_STUBS);
        Ageable blockDataMock = mock(Ageable.class, RETURNS_DEEP_STUBS);
        when(eventMock.getPlayer()).thenReturn(playerMock);
        when(eventMock.getBlock().getBlockData()).thenReturn(blockDataMock);
        when(blockDataMock.getAge()).thenReturn(2);
        when(blockDataMock.getMaximumAge()).thenReturn(2);
        when(
            blockDataMock.getMaterial().key().asString()
        ).thenReturn("blockName");

        JobListener sut = new JobListener(commonJobListenerMock);

        // Act
        sut.onBreakAction(eventMock);

        // Assert
        verify(commonJobListenerMock, times(1)).handleAction(any(JobEvent.class));
    }

    @Test
    @Tag("Unit")
    public void onKillAction_WithNullPlayer_ShouldReturnWithoutHandlingAction() {
        // Arrange
        EntityDeathEvent eventMock = mock(EntityDeathEvent.class);
        LivingEntity livingEntityMock = mock(LivingEntity.class);

        when(eventMock.getEntity()).thenReturn(livingEntityMock);
        when(livingEntityMock.getKiller()).thenReturn(null);

        JobListener sut = new JobListener(commonJobListenerMock);

        // Act
        sut.onKillAction(eventMock);

        // Assert
        verify(commonJobListenerMock, times(0)).handleAction(any(JobEvent.class));
    }

    @Test
    @Tag("Unit")
    public void onKillAction_WithSuccess_ShouldHandleAction() {
        // Arrange
        EntityDeathEvent eventMock = mock(EntityDeathEvent.class);
        LivingEntity livingEntityMock = mock(LivingEntity.class, RETURNS_DEEP_STUBS);

        when(eventMock.getEntity()).thenReturn(livingEntityMock);
        when(livingEntityMock.getKiller()).thenReturn(playerMock);
        when(livingEntityMock.getType().key().asString()).thenReturn("entityName");

        JobListener sut = new JobListener(commonJobListenerMock);

        // Act
        sut.onKillAction(eventMock);

        // Assert
        verify(commonJobListenerMock, times(1)).handleAction(any(JobEvent.class));
    }

    @Test
    @Tag("Unit")
    public void onPlaceAction_WithSuccess_ShouldHandleAction() {
        // Arrange
        BlockPlaceEvent eventMock = mock(BlockPlaceEvent.class, RETURNS_DEEP_STUBS);
        when(eventMock.getPlayer()).thenReturn(playerMock);
        when(eventMock.getItemInHand().getType().key().asString()).thenReturn("blockName");

        JobListener sut = new JobListener(commonJobListenerMock);

        // Act
        sut.onPlaceAction(eventMock);

        // Assert
        verify(commonJobListenerMock, times(1)).handleAction(any(JobEvent.class));
    }
}
