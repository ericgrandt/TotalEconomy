package com.ericgrandt.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.data.BalanceData;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;

@ExtendWith(MockitoExtension.class)
public class UniqueAccountImplTest {
    @Mock
    private Logger loggerMock;

    @Test
    @Tag("Unit")
    public void displayName_ShouldReturnDisplayName() {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        UniqueAccount sut = new UniqueAccountImpl(playerUUID, loggerMock, null);

        // Act
        Component actual = sut.displayName();
        Component expected = Component.text(playerUUID.toString());

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void defaultBalance_WithCurrency_ShouldReturnDefaultBalanceForThatCurrency() throws SQLException {
        // Arrange
        Currency currency = new CurrencyImpl(
            1,
            "singular",
            "plural",
            "$",
            1,
            true
        );
        BalanceData balanceDataMock = mock(BalanceData.class);
        when(balanceDataMock.getDefaultBalance(1)).thenReturn(BigDecimal.TEN);

        UniqueAccount sut = new UniqueAccountImpl(UUID.randomUUID(), loggerMock, balanceDataMock);

        // Act
        BigDecimal actual = sut.defaultBalance(currency);
        BigDecimal expected = BigDecimal.TEN;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void defaultBalance_WithSqlException_ShouldReturnBigDecimalOfZero() throws SQLException {
        // Arrange
        Currency currency = new CurrencyImpl(
            1,
            "singular",
            "plural",
            "$",
            1,
            true
        );
        BalanceData balanceDataMock = mock(BalanceData.class);
        when(balanceDataMock.getDefaultBalance(1)).thenThrow(SQLException.class);

        UniqueAccount sut = new UniqueAccountImpl(UUID.randomUUID(), loggerMock, balanceDataMock);

        // Act
        BigDecimal actual = sut.defaultBalance(currency);
        BigDecimal expected = BigDecimal.ZERO;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void defaultBalance_WithSqlException_ShouldLogError() throws SQLException {
        // Arrange
        Currency currency = new CurrencyImpl(
            1,
            "singular",
            "plural",
            "$",
            1,
            true
        );
        BalanceData balanceDataMock = mock(BalanceData.class);
        when(balanceDataMock.getDefaultBalance(1)).thenThrow(SQLException.class);

        UniqueAccount sut = new UniqueAccountImpl(UUID.randomUUID(), loggerMock, balanceDataMock);

        // Act
        sut.defaultBalance(currency);

        // Assert
        verify(loggerMock, times(1)).error(
            eq("Error retrieving default balance (currencyId: 1)"),
            any(SQLException.class)
        );
    }

    @Test
    @Tag("Unit")
    public void identifier_ShouldReturnUuidAsString() {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        UniqueAccount sut = new UniqueAccountImpl(playerUUID, loggerMock, null);

        // Act
        String actual = sut.identifier();
        String expected = playerUUID.toString();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void uniqueId_ShouldReturnUniqueAccountId() {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        UniqueAccount sut = new UniqueAccountImpl(playerUUID, loggerMock, null);

        // Act
        UUID actual = sut.uniqueId();

        // Assert
        assertEquals(playerUUID, actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithEqualObjects_ShouldReturnTrue() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        UniqueAccount uniqueAccount1 = new UniqueAccountImpl(uuid, loggerMock, null);
        UniqueAccount uniqueAccount2 = new UniqueAccountImpl(uuid, loggerMock, null);

        // Act
        boolean actual = uniqueAccount1.equals(uniqueAccount2);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithSameObject_ShouldReturnTrue() {
        // Arrange
        UniqueAccount uniqueAccount = new UniqueAccountImpl(UUID.randomUUID(), loggerMock, null);

        // Act
        boolean actual = uniqueAccount.equals(uniqueAccount);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithNullObject_ShouldReturnFalse() {
        // Arrange
        UniqueAccount uniqueAccount = new UniqueAccountImpl(UUID.randomUUID(), loggerMock, null);

        // Act
        boolean actual = uniqueAccount.equals(null);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithWrongClass_ShouldReturnFalse() {
        // Arrange
        UniqueAccount uniqueAccount1 = new UniqueAccountImpl(UUID.randomUUID(), loggerMock, null);
        Object uniqueAccount2 = new Object();

        // Act
        boolean actual = uniqueAccount1.equals(uniqueAccount2);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void equals_WithDifferentPlayerUuid_ShouldReturnFalse() {
        // Arrange
        UniqueAccount uniqueAccount1 = new UniqueAccountImpl(UUID.randomUUID(), loggerMock, null);
        UniqueAccount uniqueAccount2 = new UniqueAccountImpl(UUID.randomUUID(), loggerMock, null);

        // Act
        boolean actual = uniqueAccount1.equals(uniqueAccount2);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void hashCode_ShouldReturnCorrectHashCode() {
        // Arrange
        UniqueAccount sut1 = new UniqueAccountImpl(UUID.fromString("051cfed0-9046-4e50-a7b4-6dcba5ccaa23"), loggerMock, null);
        UniqueAccount sut2 = new UniqueAccountImpl(UUID.fromString("051cfed0-9046-4e50-a7b4-6dcba5ccaa23"), loggerMock, null);

        // Act
        int actual1 = sut1.hashCode();
        int actual2 = sut2.hashCode();

        // Assert
        assertEquals(actual1, actual2);
    }
}
