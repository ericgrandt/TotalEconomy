package com.ericgrandt.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.service.economy.Currency;

@ExtendWith(MockitoExtension.class)
public class UniqueAccountImplTest {
    @Test
    @Tag("Unit")
    public void displayName_ShouldReturnDisplayName() {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        Map<Currency, BigDecimal> balances = new HashMap<>();
        UniqueAccountImpl sut = new UniqueAccountImpl(playerUUID, balances);

        // Act
        Component actual = sut.displayName();
        Component expected = Component.text(playerUUID.toString());

        // Assert
        assertEquals(expected, actual);
    }
}
