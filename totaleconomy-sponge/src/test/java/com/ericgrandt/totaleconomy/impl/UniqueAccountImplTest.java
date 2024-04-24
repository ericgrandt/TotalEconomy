package com.ericgrandt.totaleconomy.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import com.ericgrandt.totaleconomy.wrappers.SpongeWrapper;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransferResult;

@ExtendWith(MockitoExtension.class)
public class UniqueAccountImplTest {
    @Mock
    private SpongeWrapper spongeWrapperMock;

    @Mock
    private CommonEconomy economyMock;

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
        UUID uuid = UUID.randomUUID();

        UniqueAccountImpl sut = new UniqueAccountImpl(
            spongeWrapperMock,
            uuid,
            economyMock,
            currencyDto.id(),
            new HashMap<>()
        );

        // Act
        Component actual = sut.displayName();
        Component expected = Component.text(uuid.toString());

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void identifier_ShouldReturnUuidAsString() {
        // Arrange
        UUID uuid = UUID.randomUUID();

        UniqueAccountImpl sut = new UniqueAccountImpl(
            spongeWrapperMock,
            uuid,
            economyMock,
            currencyDto.id(),
            new HashMap<>()
        );

        // Act
        String actual = sut.identifier();
        String expected = uuid.toString();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void uniqueId_ShouldReturnUuid() {
        // Arrange
        UUID uuid = UUID.randomUUID();

        UniqueAccountImpl sut = new UniqueAccountImpl(
            spongeWrapperMock,
            uuid,
            economyMock,
            currencyDto.id(),
            new HashMap<>()
        );

        // Act
        UUID actual = sut.uniqueId();
        UUID expected = uuid;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void hasBalance_WithBalance_ShouldReturnTrue() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Currency currency = new CurrencyImpl(currencyDto);

        UniqueAccountImpl sut = new UniqueAccountImpl(
            spongeWrapperMock,
            uuid,
            economyMock,
            currencyDto.id(),
            Map.of(currency, BigDecimal.TEN)
        );

        // Act
        boolean actual = sut.hasBalance(currency, new HashSet<>());

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void hasBalance_WithNoBalance_ShouldReturnFalse() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Currency currency = new CurrencyImpl(currencyDto);

        UniqueAccountImpl sut = new UniqueAccountImpl(
            spongeWrapperMock,
            uuid,
            economyMock,
            currencyDto.id(),
            Map.of()
        );

        // Act
        boolean actual = sut.hasBalance(currency, new HashSet<>());

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void hasBalance_Cause_WithBalance_ShouldReturnTrue() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Currency currency = new CurrencyImpl(currencyDto);

        UniqueAccountImpl sut = new UniqueAccountImpl(
            spongeWrapperMock,
            uuid,
            economyMock,
            currencyDto.id(),
            Map.of(currency, BigDecimal.TEN)
        );

        // Act
        boolean actual = sut.hasBalance(currency, (Cause) null);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void balance_WithBalanceForCurrency_ShouldReturnBalance() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Currency currency = new CurrencyImpl(currencyDto);

        UniqueAccountImpl sut = new UniqueAccountImpl(
            spongeWrapperMock,
            uuid,
            economyMock,
            currencyDto.id(),
            Map.of(currency, BigDecimal.TEN)
        );

        // Act
        BigDecimal actual = sut.balance(currency, new HashSet<>());
        BigDecimal expected = BigDecimal.TEN;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void balance_WithNoBalanceForCurrency_ShouldReturnABalanceOfZero() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Currency currency = new CurrencyImpl(currencyDto);

        UniqueAccountImpl sut = new UniqueAccountImpl(
            spongeWrapperMock,
            uuid,
            economyMock,
            currencyDto.id(),
            Map.of()
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
        UUID uuid = UUID.randomUUID();
        Currency currency = new CurrencyImpl(currencyDto);

        UniqueAccountImpl sut = new UniqueAccountImpl(
            spongeWrapperMock,
            uuid,
            economyMock,
            currencyDto.id(),
            Map.of(currency, BigDecimal.TEN)
        );

        // Act
        BigDecimal actual = sut.balance(currency, (Cause) null);
        BigDecimal expected = BigDecimal.TEN;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void balances_ShouldReturnBalances() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Currency currency = new CurrencyImpl(currencyDto);
        Map<Currency, BigDecimal> balances = Map.of(currency, BigDecimal.TEN);

        UniqueAccountImpl sut = new UniqueAccountImpl(
            spongeWrapperMock,
            uuid,
            economyMock,
            currencyDto.id(),
            balances
        );

        // Act
        Map<Currency, BigDecimal> actual = sut.balances(new HashSet<>());
        Map<Currency, BigDecimal> expected = balances;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void balances_Cause_ShouldReturnBalances() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Currency currency = new CurrencyImpl(currencyDto);
        Map<Currency, BigDecimal> balances = Map.of(currency, BigDecimal.TEN);

        UniqueAccountImpl sut = new UniqueAccountImpl(
            spongeWrapperMock,
            uuid,
            economyMock,
            currencyDto.id(),
            balances
        );

        // Act
        Map<Currency, BigDecimal> actual = sut.balances((Cause) null);
        Map<Currency, BigDecimal> expected = balances;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void deposit_WithSuccess_ShouldReturnTransactionResult() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Currency currency = new CurrencyImpl(currencyDto);

        when(economyMock.deposit(uuid, 1, BigDecimal.TEN, false)).thenReturn(
            new com.ericgrandt.totaleconomy.common.econ.TransactionResult(
                com.ericgrandt.totaleconomy.common.econ.TransactionResult.ResultType.SUCCESS,
                ""
            )
        );

        UniqueAccountImpl sut = new UniqueAccountImpl(
            spongeWrapperMock,
            uuid,
            economyMock,
            currencyDto.id(),
            Map.of(currency, BigDecimal.TEN)
        );

        // Act
        TransactionResult actual = sut.deposit(currency, BigDecimal.TEN, new HashSet<>());
        TransactionResult expected = new TransactionResultImpl(
            sut,
            currency,
            BigDecimal.TEN,
            ResultType.SUCCESS,
            spongeWrapperMock.deposit()
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void deposit_WithFailure_ShouldReturnTransactionResult() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Currency currency = new CurrencyImpl(currencyDto);

        when(economyMock.deposit(uuid, 1, BigDecimal.TEN, false)).thenReturn(
            new com.ericgrandt.totaleconomy.common.econ.TransactionResult(
                com.ericgrandt.totaleconomy.common.econ.TransactionResult.ResultType.FAILURE,
                ""
            )
        );

        UniqueAccountImpl sut = new UniqueAccountImpl(
            spongeWrapperMock,
            uuid,
            economyMock,
            currencyDto.id(),
            Map.of(currency, BigDecimal.TEN)
        );

        // Act
        TransactionResult actual = sut.deposit(currency, BigDecimal.TEN, new HashSet<>());
        TransactionResult expected = new TransactionResultImpl(
            sut,
            currency,
            BigDecimal.TEN,
            ResultType.FAILED,
            spongeWrapperMock.deposit()
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void deposit_Cause_WithSuccess_ShouldReturnTransactionResult() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Currency currency = new CurrencyImpl(currencyDto);

        when(economyMock.deposit(uuid, 1, BigDecimal.TEN, false)).thenReturn(
            new com.ericgrandt.totaleconomy.common.econ.TransactionResult(
                com.ericgrandt.totaleconomy.common.econ.TransactionResult.ResultType.SUCCESS,
                ""
            )
        );

        UniqueAccountImpl sut = new UniqueAccountImpl(
            spongeWrapperMock,
            uuid,
            economyMock,
            currencyDto.id(),
            Map.of(currency, BigDecimal.TEN)
        );

        // Act
        TransactionResult actual = sut.deposit(currency, BigDecimal.TEN, (Cause) null);
        TransactionResult expected = new TransactionResultImpl(
            sut,
            currency,
            BigDecimal.TEN,
            ResultType.SUCCESS,
            spongeWrapperMock.deposit()
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void withdraw_WithSuccess_ShouldReturnTransactionResult() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Currency currency = new CurrencyImpl(currencyDto);

        when(economyMock.withdraw(uuid, 1, BigDecimal.TEN, false)).thenReturn(
            new com.ericgrandt.totaleconomy.common.econ.TransactionResult(
                com.ericgrandt.totaleconomy.common.econ.TransactionResult.ResultType.SUCCESS,
                ""
            )
        );

        UniqueAccountImpl sut = new UniqueAccountImpl(
            spongeWrapperMock,
            uuid,
            economyMock,
            currencyDto.id(),
            Map.of(currency, BigDecimal.TEN)
        );

        // Act
        TransactionResult actual = sut.withdraw(currency, BigDecimal.TEN, new HashSet<>());
        TransactionResult expected = new TransactionResultImpl(
            sut,
            currency,
            BigDecimal.TEN,
            ResultType.SUCCESS,
            spongeWrapperMock.withdraw()
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void withdraw_WithFailure_ShouldReturnTransactionResult() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Currency currency = new CurrencyImpl(currencyDto);

        when(economyMock.withdraw(uuid, 1, BigDecimal.TEN, false)).thenReturn(
            new com.ericgrandt.totaleconomy.common.econ.TransactionResult(
                com.ericgrandt.totaleconomy.common.econ.TransactionResult.ResultType.FAILURE,
                ""
            )
        );

        UniqueAccountImpl sut = new UniqueAccountImpl(
            spongeWrapperMock,
            uuid,
            economyMock,
            currencyDto.id(),
            Map.of(currency, BigDecimal.TEN)
        );

        // Act
        TransactionResult actual = sut.withdraw(currency, BigDecimal.TEN, new HashSet<>());
        TransactionResult expected = new TransactionResultImpl(
            sut,
            currency,
            BigDecimal.TEN,
            ResultType.FAILED,
            spongeWrapperMock.withdraw()
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void withdraw_Cause_WithSuccess_ShouldReturnTransactionResult() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Currency currency = new CurrencyImpl(currencyDto);

        when(economyMock.withdraw(uuid, 1, BigDecimal.TEN, false)).thenReturn(
            new com.ericgrandt.totaleconomy.common.econ.TransactionResult(
                com.ericgrandt.totaleconomy.common.econ.TransactionResult.ResultType.SUCCESS,
                ""
            )
        );

        UniqueAccountImpl sut = new UniqueAccountImpl(
            spongeWrapperMock,
            uuid,
            economyMock,
            currencyDto.id(),
            Map.of(currency, BigDecimal.TEN)
        );

        // Act
        TransactionResult actual = sut.withdraw(currency, BigDecimal.TEN, (Cause) null);
        TransactionResult expected = new TransactionResultImpl(
            sut,
            currency,
            BigDecimal.TEN,
            ResultType.SUCCESS,
            spongeWrapperMock.withdraw()
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void transfer_WithSuccess_ShouldReturnTransferResult() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        UUID toUuid = UUID.randomUUID();
        Currency currency = new CurrencyImpl(currencyDto);

        when(economyMock.transfer(uuid, toUuid, 1, BigDecimal.TEN)).thenReturn(
            new com.ericgrandt.totaleconomy.common.econ.TransactionResult(
                com.ericgrandt.totaleconomy.common.econ.TransactionResult.ResultType.SUCCESS,
                ""
            )
        );

        UniqueAccountImpl toAccount = new UniqueAccountImpl(
            spongeWrapperMock,
            toUuid,
            economyMock,
            currencyDto.id(),
            Map.of(currency, BigDecimal.TEN)
        );

        UniqueAccountImpl sut = new UniqueAccountImpl(
            spongeWrapperMock,
            uuid,
            economyMock,
            currencyDto.id(),
            Map.of(currency, BigDecimal.TEN)
        );

        // Act
        TransferResult actual = sut.transfer(toAccount, currency, BigDecimal.TEN, new HashSet<>());
        TransferResult expected = new TransferResultImpl(
            sut,
            toAccount,
            currency,
            BigDecimal.TEN,
            ResultType.SUCCESS,
            spongeWrapperMock.withdraw()
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void transfer_WithFailure_ShouldReturnTransferResult() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        UUID toUuid = UUID.randomUUID();
        Currency currency = new CurrencyImpl(currencyDto);

        when(economyMock.transfer(uuid, toUuid, 1, BigDecimal.TEN)).thenReturn(
            new com.ericgrandt.totaleconomy.common.econ.TransactionResult(
                com.ericgrandt.totaleconomy.common.econ.TransactionResult.ResultType.FAILURE,
                ""
            )
        );

        UniqueAccountImpl toAccount = new UniqueAccountImpl(
            spongeWrapperMock,
            toUuid,
            economyMock,
            currencyDto.id(),
            Map.of(currency, BigDecimal.TEN)
        );

        UniqueAccountImpl sut = new UniqueAccountImpl(
            spongeWrapperMock,
            uuid,
            economyMock,
            currencyDto.id(),
            Map.of(currency, BigDecimal.TEN)
        );

        // Act
        TransferResult actual = sut.transfer(toAccount, currency, BigDecimal.TEN, new HashSet<>());
        TransferResult expected = new TransferResultImpl(
            sut,
            toAccount,
            currency,
            BigDecimal.TEN,
            ResultType.FAILED,
            spongeWrapperMock.withdraw()
        );

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void transfer_Cause_WithSuccess_ShouldReturnTransferResult() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        UUID toUuid = UUID.randomUUID();
        Currency currency = new CurrencyImpl(currencyDto);

        when(economyMock.transfer(uuid, toUuid, 1, BigDecimal.TEN)).thenReturn(
            new com.ericgrandt.totaleconomy.common.econ.TransactionResult(
                com.ericgrandt.totaleconomy.common.econ.TransactionResult.ResultType.SUCCESS,
                ""
            )
        );

        UniqueAccountImpl toAccount = new UniqueAccountImpl(
            spongeWrapperMock,
            toUuid,
            economyMock,
            currencyDto.id(),
            Map.of(currency, BigDecimal.TEN)
        );

        UniqueAccountImpl sut = new UniqueAccountImpl(
            spongeWrapperMock,
            uuid,
            economyMock,
            currencyDto.id(),
            Map.of(currency, BigDecimal.TEN)
        );

        // Act
        TransferResult actual = sut.transfer(toAccount, currency, BigDecimal.TEN, (Cause) null);
        TransferResult expected = new TransferResultImpl(
            sut,
            toAccount,
            currency,
            BigDecimal.TEN,
            ResultType.SUCCESS,
            spongeWrapperMock.withdraw()
        );

        // Assert
        assertEquals(expected, actual);
    }
}
