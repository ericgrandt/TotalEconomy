package com.ericgrandt.totaleconomy.impl;

import com.ericgrandt.totaleconomy.common.data.BalanceData;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import com.ericgrandt.totaleconomy.common.data.CurrencyData;
import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UniqueAccountImplTest {
    @Mock
    private BalanceData balanceDataMock;

    @Mock
    private CurrencyData currencyDataMock;

    private final CurrencyDto currencyDto = new CurrencyDto(
        1,
        "Dollar",
        "Dollars",
        "$",
        2,
        true
    );

    @Test
    @Tag("Unit")
    public void displayName_ShouldReturnDisplayName() {
        // Arrange
        UniqueAccountImpl sut = new UniqueAccountImpl(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            new HashMap<>(),
            balanceDataMock,
            currencyDataMock
        );

        // Act
        Component actual = sut.displayName();
        Component expected = Component.text("00000000-0000-0000-0000-000000000000");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void hasBalance_Contexts_WithBalanceForCurrency_ShouldReturnTrue() {
        // Arrange
        CurrencyImpl currency = new CurrencyImpl(currencyDto);
        UniqueAccountImpl sut = new UniqueAccountImpl(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            Map.of(currency, BigDecimal.valueOf(100)),
            balanceDataMock,
            currencyDataMock
        );

        // Act
        boolean actual = sut.hasBalance(currency, new HashSet<>());

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void hasBalance_Contexts_WithNoBalanceForCurrency_ShouldReturnFalse() {
        // Arrange
        CurrencyImpl currency = new CurrencyImpl(currencyDto);
        UniqueAccountImpl sut = new UniqueAccountImpl(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            new HashMap<>(),
            balanceDataMock,
            currencyDataMock
        );

        // Act
        boolean actual = sut.hasBalance(currency, new HashSet<>());

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void hasBalance_Cause_WithBalanceForCurrency_ShouldReturnTrue() {
        // Arrange
        CurrencyImpl currency = new CurrencyImpl(currencyDto);
        UniqueAccountImpl sut = new UniqueAccountImpl(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            Map.of(currency, BigDecimal.valueOf(100)),
            balanceDataMock,
            currencyDataMock
        );

        // Act
        boolean actual = sut.hasBalance(currency, (Cause) null);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void hasBalance_Cause_WithNoBalanceForCurrency_ShouldReturnFalse() {
        // Arrange
        CurrencyImpl currency = new CurrencyImpl(currencyDto);
        UniqueAccountImpl sut = new UniqueAccountImpl(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            new HashMap<>(),
            balanceDataMock,
            currencyDataMock
        );

        // Act
        boolean actual = sut.hasBalance(currency, (Cause) null);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void balance_Contexts_WithBalanceForCurrency_ShouldReturnBalance() {
        // Arrange
        CurrencyImpl currency = new CurrencyImpl(currencyDto);
        UniqueAccountImpl sut = new UniqueAccountImpl(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            Map.of(currency, BigDecimal.valueOf(100)),
            balanceDataMock,
            currencyDataMock
        );

        // Act
        BigDecimal actual = sut.balance(currency, new HashSet<>());
        BigDecimal expected = BigDecimal.valueOf(100);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void balance_Contexts_WithNoBalanceForCurrency_ShouldReturnZero() {
        // Arrange
        CurrencyImpl currency = new CurrencyImpl(currencyDto);
        UniqueAccountImpl sut = new UniqueAccountImpl(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            new HashMap<>(),
            balanceDataMock,
            currencyDataMock
        );

        // Act
        BigDecimal actual = sut.balance(currency, new HashSet<>());
        BigDecimal expected = BigDecimal.ZERO;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void balance_Cause_WithBalanceForCurrency_ShouldReturnBalance() {
        // Arrange
        CurrencyImpl currency = new CurrencyImpl(currencyDto);
        UniqueAccountImpl sut = new UniqueAccountImpl(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            Map.of(currency, BigDecimal.valueOf(100)),
            balanceDataMock,
            currencyDataMock
        );

        // Act
        BigDecimal actual = sut.balance(currency, (Cause) null);
        BigDecimal expected = BigDecimal.valueOf(100);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void balance_Cause_WithNoBalanceForCurrency_ShouldReturnZero() {
        // Arrange
        CurrencyImpl currency = new CurrencyImpl(currencyDto);
        UniqueAccountImpl sut = new UniqueAccountImpl(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            new HashMap<>(),
            balanceDataMock,
            currencyDataMock
        );

        // Act
        BigDecimal actual = sut.balance(currency, (Cause) null);
        BigDecimal expected = BigDecimal.ZERO;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void balance_Contexts_ShouldReturnHashMapOfBalances() {
        // Arrange
        CurrencyImpl currency = new CurrencyImpl(currencyDto);
        UniqueAccountImpl sut = new UniqueAccountImpl(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            Map.of(currency, BigDecimal.valueOf(100)),
            balanceDataMock,
            currencyDataMock
        );

        // Act
        Map<Currency, BigDecimal> actual = sut.balances(new HashSet<>());
        Map<Currency, BigDecimal> expected = Map.of(currency, BigDecimal.valueOf(100));

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void balance_Cause_ShouldReturnHashMapOfBalances() {
        // Arrange
        CurrencyImpl currency = new CurrencyImpl(currencyDto);
        UniqueAccountImpl sut = new UniqueAccountImpl(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            Map.of(currency, BigDecimal.valueOf(100)),
            balanceDataMock,
            currencyDataMock
        );

        // Act
        Map<Currency, BigDecimal> actual = sut.balances((Cause) null);
        Map<Currency, BigDecimal> expected = Map.of(currency, BigDecimal.valueOf(100));

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void setBalance_Contexts_WithBalance_ShouldSetBalance() {
        // Arrange
        CurrencyImpl currency = new CurrencyImpl(currencyDto);
        UniqueAccountImpl sut = new UniqueAccountImpl(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            Map.of(currency, BigDecimal.valueOf(100)),
            balanceDataMock,
            currencyDataMock
        );

        // Act
        TransactionResult actual = sut.setBalance(currency, BigDecimal.TEN, new HashSet<>());
        TransactionResult expected = null;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void setBalance_Contexts_WithNoBalance_ShouldSetBalance() {

    }

    @Test
    @Tag("Unit")
    public void identifier_ShouldReturnStringIdentifier() {
        // Arrange
        UniqueAccountImpl sut = new UniqueAccountImpl(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            new HashMap<>(),
            balanceDataMock,
            currencyDataMock
        );

        // Act
        String actual = sut.identifier();
        String expected = "00000000-0000-0000-0000-000000000000";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void uniqueId_ShouldReturnUuid() {
        // Arrange
        UniqueAccountImpl sut = new UniqueAccountImpl(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            new HashMap<>(),
            balanceDataMock,
            currencyDataMock
        );

        // Act
        UUID actual = sut.uniqueId();
        UUID expected = UUID.fromString("00000000-0000-0000-0000-000000000000");

        // Assert
        assertEquals(expected, actual);
    }
}
