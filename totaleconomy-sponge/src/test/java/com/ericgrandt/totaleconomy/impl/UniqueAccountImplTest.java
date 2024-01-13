package com.ericgrandt.totaleconomy.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

@ExtendWith(MockitoExtension.class)
public class UniqueAccountImplTest {
    @Mock
    private Logger loggerMock;

    @Mock
    private BalanceData balanceDataMock;

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
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            new HashMap<>(),
            balanceDataMock,
            currencyDto
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
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            Map.of(currency, BigDecimal.valueOf(100)),
            balanceDataMock,
            currencyDto
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
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            new HashMap<>(),
            balanceDataMock,
            currencyDto
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
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            Map.of(currency, BigDecimal.valueOf(100)),
            balanceDataMock,
            currencyDto
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
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            new HashMap<>(),
            balanceDataMock,
            currencyDto
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
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            Map.of(currency, BigDecimal.valueOf(100)),
            balanceDataMock,
            currencyDto
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
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            new HashMap<>(),
            balanceDataMock,
            currencyDto
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
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            Map.of(currency, BigDecimal.valueOf(100)),
            balanceDataMock,
            currencyDto
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
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            new HashMap<>(),
            balanceDataMock,
            currencyDto
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
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            Map.of(currency, BigDecimal.valueOf(100)),
            balanceDataMock,
            currencyDto
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
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            Map.of(currency, BigDecimal.valueOf(100)),
            balanceDataMock,
            currencyDto
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
        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
        UniqueAccountImpl sut = new UniqueAccountImpl(
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            balances,
            balanceDataMock,
            currencyDto
        );

        // Act
        sut.setBalance(currency, BigDecimal.TEN, new HashSet<>());

        BigDecimal actual = balances.get(currency);
        BigDecimal expected = BigDecimal.TEN;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void setBalance_Contexts_WithBalance_ShouldReturnSuccessfulTransactionResult() {
        // Arrange
        CurrencyImpl currency = new CurrencyImpl(currencyDto);
        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
        UniqueAccountImpl sut = new UniqueAccountImpl(
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            balances,
            balanceDataMock,
            currencyDto
        );

        // Act
        TransactionResult actual = sut.setBalance(currency, BigDecimal.TEN, new HashSet<>());
        TransactionResult expected = new TransactionResultImpl(
            sut,
            currency,
            BigDecimal.TEN,
            ResultType.SUCCESS,
            null
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void setBalance_Contexts_WithNoBalance_ShouldReturnFailedTransactionResult() {
        // Arrange
        CurrencyImpl currency = new CurrencyImpl(currencyDto);
        UniqueAccountImpl sut = new UniqueAccountImpl(
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            new HashMap<>(),
            balanceDataMock,
            currencyDto
        );

        // Act
        TransactionResult actual = sut.setBalance(currency, BigDecimal.TEN, new HashSet<>());
        TransactionResult expected = new TransactionResultImpl(
            sut,
            currency,
            BigDecimal.TEN,
            ResultType.FAILED,
            null
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void setBalance_Contexts_WithSqlException_ShouldReturnFailedTransactionResult() throws SQLException {
        // Arrange
        when(balanceDataMock.updateBalance(any(UUID.class), any(Integer.class), any(Double.class))).thenThrow(
            SQLException.class
        );

        CurrencyImpl currency = new CurrencyImpl(currencyDto);
        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
        UniqueAccountImpl sut = new UniqueAccountImpl(
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            balances,
            balanceDataMock,
            currencyDto
        );

        // Act
        TransactionResult actual = sut.setBalance(currency, BigDecimal.TEN, new HashSet<>());
        TransactionResult expected = new TransactionResultImpl(
            sut,
            currency,
            BigDecimal.TEN,
            ResultType.FAILED,
            null
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void setBalance_Cause_WithBalance_ShouldSetBalance() {
        // Arrange
        CurrencyImpl currency = new CurrencyImpl(currencyDto);
        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
        UniqueAccountImpl sut = new UniqueAccountImpl(
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            balances,
            balanceDataMock,
            currencyDto
        );

        // Act
        sut.setBalance(currency, BigDecimal.TEN, (Cause) null);

        BigDecimal actual = balances.get(currency);
        BigDecimal expected = BigDecimal.TEN;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void setBalance_Cause_WithBalance_ShouldReturnSuccessfulTransactionResult() {
        // Arrange
        CurrencyImpl currency = new CurrencyImpl(currencyDto);
        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
        UniqueAccountImpl sut = new UniqueAccountImpl(
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            balances,
            balanceDataMock,
            currencyDto
        );

        // Act
        TransactionResult actual = sut.setBalance(currency, BigDecimal.TEN, (Cause) null);
        TransactionResult expected = new TransactionResultImpl(
            sut,
            currency,
            BigDecimal.TEN,
            ResultType.SUCCESS,
            null
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void setBalance_Cause_WithNoBalance_ShouldReturnFailedTransactionResult() {
        // Arrange
        CurrencyImpl currency = new CurrencyImpl(currencyDto);
        UniqueAccountImpl sut = new UniqueAccountImpl(
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            new HashMap<>(),
            balanceDataMock,
            currencyDto
        );

        // Act
        TransactionResult actual = sut.setBalance(currency, BigDecimal.TEN, (Cause) null);
        TransactionResult expected = new TransactionResultImpl(
            sut,
            currency,
            BigDecimal.TEN,
            ResultType.FAILED,
            null
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void setBalance_Cause_WithSqlException_ShouldReturnFailedTransactionResult() throws SQLException {
        // Arrange
        when(balanceDataMock.updateBalance(any(UUID.class), any(Integer.class), any(Double.class))).thenThrow(
            SQLException.class
        );

        CurrencyImpl currency = new CurrencyImpl(currencyDto);
        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
        UniqueAccountImpl sut = new UniqueAccountImpl(
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            balances,
            balanceDataMock,
            currencyDto
        );

        // Act
        TransactionResult actual = sut.setBalance(currency, BigDecimal.TEN, (Cause) null);
        TransactionResult expected = new TransactionResultImpl(
            sut,
            currency,
            BigDecimal.TEN,
            ResultType.FAILED,
            null
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void deposit_Contexts_WithBalance_ShouldDepositAmountIntoBalance() {
        // Arrange
        CurrencyImpl currency = new CurrencyImpl(currencyDto);
        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
        UniqueAccountImpl sut = new UniqueAccountImpl(
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            balances,
            balanceDataMock,
            currencyDto
        );

        // Act
        sut.deposit(currency, BigDecimal.TEN, new HashSet<>());

        BigDecimal actual = balances.get(currency);
        BigDecimal expected = BigDecimal.valueOf(110);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void deposit_Contexts_WithBalance_ShouldReturnSuccessfulTransactionResult() {
        // Arrange
        CurrencyImpl currency = new CurrencyImpl(currencyDto);
        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
        UniqueAccountImpl sut = new UniqueAccountImpl(
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            balances,
            balanceDataMock,
            currencyDto
        );

        // Act
        TransactionResult actual = sut.deposit(currency, BigDecimal.TEN, new HashSet<>());
        TransactionResult expected = new TransactionResultImpl(
            sut,
            currency,
            BigDecimal.TEN,
            ResultType.SUCCESS,
            null
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void deposit_Contexts_WithNoBalance_ShouldReturnFailedTransactionResult() {
        // Arrange
        CurrencyImpl currency = new CurrencyImpl(currencyDto);
        UniqueAccountImpl sut = new UniqueAccountImpl(
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            new HashMap<>(),
            balanceDataMock,
            currencyDto
        );

        // Act
        TransactionResult actual = sut.deposit(currency, BigDecimal.TEN, new HashSet<>());
        TransactionResult expected = new TransactionResultImpl(
            sut,
            currency,
            BigDecimal.TEN,
            ResultType.FAILED,
            null
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void deposit_Contexts_WithSqlException_ShouldReturnFailedTransactionResult() throws SQLException {
        // Arrange
        when(balanceDataMock.updateBalance(any(UUID.class), any(Integer.class), any(Double.class))).thenThrow(
            SQLException.class
        );

        CurrencyImpl currency = new CurrencyImpl(currencyDto);
        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
        UniqueAccountImpl sut = new UniqueAccountImpl(
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            balances,
            balanceDataMock,
            currencyDto
        );

        // Act
        TransactionResult actual = sut.deposit(currency, BigDecimal.TEN, new HashSet<>());
        TransactionResult expected = new TransactionResultImpl(
            sut,
            currency,
            BigDecimal.TEN,
            ResultType.FAILED,
            null
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void deposit_Cause_WithBalance_ShouldDepositAmountIntoBalance() {
        // Arrange
        CurrencyImpl currency = new CurrencyImpl(currencyDto);
        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
        UniqueAccountImpl sut = new UniqueAccountImpl(
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            balances,
            balanceDataMock,
            currencyDto
        );

        // Act
        sut.deposit(currency, BigDecimal.TEN, (Cause) null);

        BigDecimal actual = balances.get(currency);
        BigDecimal expected = BigDecimal.valueOf(110);

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void deposit_Cause_WithBalance_ShouldReturnSuccessfulTransactionResult() {
        // Arrange
        CurrencyImpl currency = new CurrencyImpl(currencyDto);
        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
        UniqueAccountImpl sut = new UniqueAccountImpl(
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            balances,
            balanceDataMock,
            currencyDto
        );

        // Act
        TransactionResult actual = sut.deposit(currency, BigDecimal.TEN, (Cause) null);
        TransactionResult expected = new TransactionResultImpl(
            sut,
            currency,
            BigDecimal.TEN,
            ResultType.SUCCESS,
            null
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void deposit_Cause_WithNoBalance_ShouldReturnFailedTransactionResult() {
        // Arrange
        CurrencyImpl currency = new CurrencyImpl(currencyDto);
        UniqueAccountImpl sut = new UniqueAccountImpl(
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            new HashMap<>(),
            balanceDataMock,
            currencyDto
        );

        // Act
        TransactionResult actual = sut.deposit(currency, BigDecimal.TEN, (Cause) null);
        TransactionResult expected = new TransactionResultImpl(
            sut,
            currency,
            BigDecimal.TEN,
            ResultType.FAILED,
            null
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void deposit_Cause_WithSqlException_ShouldReturnFailedTransactionResult() throws SQLException {
        // Arrange
        when(balanceDataMock.updateBalance(any(UUID.class), any(Integer.class), any(Double.class))).thenThrow(
            SQLException.class
        );

        CurrencyImpl currency = new CurrencyImpl(currencyDto);
        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
        UniqueAccountImpl sut = new UniqueAccountImpl(
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            balances,
            balanceDataMock,
            currencyDto
        );

        // Act
        TransactionResult actual = sut.deposit(currency, BigDecimal.TEN, (Cause) null);
        TransactionResult expected = new TransactionResultImpl(
            sut,
            currency,
            BigDecimal.TEN,
            ResultType.FAILED,
            null
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void identifier_ShouldReturnStringIdentifier() {
        // Arrange
        UniqueAccountImpl sut = new UniqueAccountImpl(
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            new HashMap<>(),
            balanceDataMock,
            currencyDto
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
            loggerMock,
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            new HashMap<>(),
            balanceDataMock,
            currencyDto
        );

        // Act
        UUID actual = sut.uniqueId();
        UUID expected = UUID.fromString("00000000-0000-0000-0000-000000000000");

        // Assert
        assertEquals(expected, actual);
    }
}
