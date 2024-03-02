package com.ericgrandt.totaleconomy.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.common.econ.CommonEconomy;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import com.ericgrandt.totaleconomy.wrappers.SpongeWrapper;
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
import org.spongepowered.api.service.economy.transaction.TransactionTypes;

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

        when(economyMock.deposit(uuid, 1, BigDecimal.TEN)).thenReturn(
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

        when(economyMock.deposit(uuid, 1, BigDecimal.TEN)).thenReturn(
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

        when(economyMock.deposit(uuid, 1, BigDecimal.TEN)).thenReturn(
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

        when(economyMock.withdraw(uuid, 1, BigDecimal.TEN)).thenReturn(
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

        when(economyMock.withdraw(uuid, 1, BigDecimal.TEN)).thenReturn(
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

        when(economyMock.withdraw(uuid, 1, BigDecimal.TEN)).thenReturn(
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

//    @Test
//    @Tag("Unit")
//    public void setBalance_Contexts_WithBalance_ShouldSetBalance() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sut = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            balances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        sut.setBalance(currency, BigDecimal.TEN, new HashSet<>());
//
//        BigDecimal actual = balances.get(currency);
//        BigDecimal expected = BigDecimal.TEN;
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void setBalance_Contexts_WithBalance_ShouldReturnSuccessfulTransactionResult() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sut = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            balances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransactionResult actual = sut.setBalance(currency, BigDecimal.TEN, new HashSet<>());
//        TransactionResult expected = new TransactionResultImpl(
//            sut,
//            currency,
//            BigDecimal.TEN,
//            ResultType.SUCCESS,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void setBalance_Contexts_WithNoBalance_ShouldReturnFailedTransactionResult() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        UniqueAccountImpl sut = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            new HashMap<>(),
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransactionResult actual = sut.setBalance(currency, BigDecimal.TEN, new HashSet<>());
//        TransactionResult expected = new TransactionResultImpl(
//            sut,
//            currency,
//            BigDecimal.TEN,
//            ResultType.FAILED,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void setBalance_Contexts_WithSqlException_ShouldReturnFailedTransactionResult() throws SQLException {
//        // Arrange
//        when(balanceDataMock.updateBalance(any(UUID.class), any(Integer.class), any(Double.class))).thenThrow(
//            SQLException.class
//        );
//
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sut = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            balances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransactionResult actual = sut.setBalance(currency, BigDecimal.TEN, new HashSet<>());
//        TransactionResult expected = new TransactionResultImpl(
//            sut,
//            currency,
//            BigDecimal.TEN,
//            ResultType.FAILED,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void setBalance_Cause_WithBalance_ShouldSetBalance() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sut = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            balances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        sut.setBalance(currency, BigDecimal.TEN, (Cause) null);
//
//        BigDecimal actual = balances.get(currency);
//        BigDecimal expected = BigDecimal.TEN;
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void setBalance_Cause_WithBalance_ShouldReturnSuccessfulTransactionResult() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sut = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            balances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransactionResult actual = sut.setBalance(currency, BigDecimal.TEN, (Cause) null);
//        TransactionResult expected = new TransactionResultImpl(
//            sut,
//            currency,
//            BigDecimal.TEN,
//            ResultType.SUCCESS,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void setBalance_Cause_WithNoBalance_ShouldReturnFailedTransactionResult() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        UniqueAccountImpl sut = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            new HashMap<>(),
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransactionResult actual = sut.setBalance(currency, BigDecimal.TEN, (Cause) null);
//        TransactionResult expected = new TransactionResultImpl(
//            sut,
//            currency,
//            BigDecimal.TEN,
//            ResultType.FAILED,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void setBalance_Cause_WithSqlException_ShouldReturnFailedTransactionResult() throws SQLException {
//        // Arrange
//        when(balanceDataMock.updateBalance(any(UUID.class), any(Integer.class), any(Double.class))).thenThrow(
//            SQLException.class
//        );
//
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sut = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            balances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransactionResult actual = sut.setBalance(currency, BigDecimal.TEN, (Cause) null);
//        TransactionResult expected = new TransactionResultImpl(
//            sut,
//            currency,
//            BigDecimal.TEN,
//            ResultType.FAILED,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void deposit_Contexts_WithBalance_ShouldDepositAmountIntoBalance() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sut = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            balances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        sut.deposit(currency, BigDecimal.TEN, new HashSet<>());
//
//        BigDecimal actual = balances.get(currency);
//        BigDecimal expected = BigDecimal.valueOf(110);
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void deposit_Contexts_WithBalance_ShouldReturnSuccessfulTransactionResult() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sut = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            balances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransactionResult actual = sut.deposit(currency, BigDecimal.TEN, new HashSet<>());
//        TransactionResult expected = new TransactionResultImpl(
//            sut,
//            currency,
//            BigDecimal.TEN,
//            ResultType.SUCCESS,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void deposit_Contexts_WithNoBalance_ShouldReturnFailedTransactionResult() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        UniqueAccountImpl sut = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            new HashMap<>(),
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransactionResult actual = sut.deposit(currency, BigDecimal.TEN, new HashSet<>());
//        TransactionResult expected = new TransactionResultImpl(
//            sut,
//            currency,
//            BigDecimal.TEN,
//            ResultType.FAILED,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void deposit_Contexts_WithSqlException_ShouldReturnFailedTransactionResult() throws SQLException {
//        // Arrange
//        when(balanceDataMock.updateBalance(any(UUID.class), any(Integer.class), any(Double.class))).thenThrow(
//            SQLException.class
//        );
//
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sut = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            balances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransactionResult actual = sut.deposit(currency, BigDecimal.TEN, new HashSet<>());
//        TransactionResult expected = new TransactionResultImpl(
//            sut,
//            currency,
//            BigDecimal.TEN,
//            ResultType.FAILED,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void deposit_Cause_WithBalance_ShouldDepositAmountIntoBalance() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sut = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            balances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        sut.deposit(currency, BigDecimal.TEN, (Cause) null);
//
//        BigDecimal actual = balances.get(currency);
//        BigDecimal expected = BigDecimal.valueOf(110);
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void deposit_Cause_WithBalance_ShouldReturnSuccessfulTransactionResult() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sut = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            balances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransactionResult actual = sut.deposit(currency, BigDecimal.TEN, (Cause) null);
//        TransactionResult expected = new TransactionResultImpl(
//            sut,
//            currency,
//            BigDecimal.TEN,
//            ResultType.SUCCESS,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void deposit_Cause_WithNoBalance_ShouldReturnFailedTransactionResult() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        UniqueAccountImpl sut = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            new HashMap<>(),
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransactionResult actual = sut.deposit(currency, BigDecimal.TEN, (Cause) null);
//        TransactionResult expected = new TransactionResultImpl(
//            sut,
//            currency,
//            BigDecimal.TEN,
//            ResultType.FAILED,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void deposit_Cause_WithSqlException_ShouldReturnFailedTransactionResult() throws SQLException {
//        // Arrange
//        when(balanceDataMock.updateBalance(any(UUID.class), any(Integer.class), any(Double.class))).thenThrow(
//            SQLException.class
//        );
//
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sut = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            balances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransactionResult actual = sut.deposit(currency, BigDecimal.TEN, (Cause) null);
//        TransactionResult expected = new TransactionResultImpl(
//            sut,
//            currency,
//            BigDecimal.TEN,
//            ResultType.FAILED,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void withdraw_Contexts_WithBalance_ShouldWithdrawAmountFromBalance() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sut = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            balances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        sut.withdraw(currency, BigDecimal.TEN, new HashSet<>());
//
//        BigDecimal actual = balances.get(currency);
//        BigDecimal expected = BigDecimal.valueOf(90);
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void withdraw_Contexts_WithBalance_ShouldReturnSuccessfulTransactionResult() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sut = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            balances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransactionResult actual = sut.withdraw(currency, BigDecimal.TEN, new HashSet<>());
//        TransactionResult expected = new TransactionResultImpl(
//            sut,
//            currency,
//            BigDecimal.TEN,
//            ResultType.SUCCESS,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void withdraw_Contexts_WithNoBalance_ShouldReturnFailedTransactionResult() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        UniqueAccountImpl sut = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            new HashMap<>(),
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransactionResult actual = sut.withdraw(currency, BigDecimal.TEN, new HashSet<>());
//        TransactionResult expected = new TransactionResultImpl(
//            sut,
//            currency,
//            BigDecimal.TEN,
//            ResultType.FAILED,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void withdraw_Contexts_WithSqlException_ShouldReturnFailedTransactionResult() throws SQLException {
//        // Arrange
//        when(balanceDataMock.updateBalance(any(UUID.class), any(Integer.class), any(Double.class))).thenThrow(
//            SQLException.class
//        );
//
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sut = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            balances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransactionResult actual = sut.withdraw(currency, BigDecimal.TEN, new HashSet<>());
//        TransactionResult expected = new TransactionResultImpl(
//            sut,
//            currency,
//            BigDecimal.TEN,
//            ResultType.FAILED,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void withdraw_Cause_WithBalance_ShouldWithdrawAmountFromBalance() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sut = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            balances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        sut.withdraw(currency, BigDecimal.TEN, (Cause) null);
//
//        BigDecimal actual = balances.get(currency);
//        BigDecimal expected = BigDecimal.valueOf(90);
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void withdraw_Cause_WithBalance_ShouldReturnSuccessfulTransactionResult() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sut = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            balances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransactionResult actual = sut.withdraw(currency, BigDecimal.TEN, (Cause) null);
//        TransactionResult expected = new TransactionResultImpl(
//            sut,
//            currency,
//            BigDecimal.TEN,
//            ResultType.SUCCESS,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void withdraw_Cause_WithNoBalance_ShouldReturnFailedTransactionResult() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        UniqueAccountImpl sut = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            new HashMap<>(),
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransactionResult actual = sut.withdraw(currency, BigDecimal.TEN, (Cause) null);
//        TransactionResult expected = new TransactionResultImpl(
//            sut,
//            currency,
//            BigDecimal.TEN,
//            ResultType.FAILED,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void withdraw_Cause_WithSqlException_ShouldReturnFailedTransactionResult() throws SQLException {
//        // Arrange
//        when(balanceDataMock.updateBalance(any(UUID.class), any(Integer.class), any(Double.class))).thenThrow(
//            SQLException.class
//        );
//
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> balances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sut = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            balances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransactionResult actual = sut.withdraw(currency, BigDecimal.TEN, (Cause) null);
//        TransactionResult expected = new TransactionResultImpl(
//            sut,
//            currency,
//            BigDecimal.TEN,
//            ResultType.FAILED,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void transfer_Contexts_WithBalances_ShouldTransferBetweenBalances() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> fromBalances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(10)));
//        Map<Currency, BigDecimal> toBalances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sutFrom = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            fromBalances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//        UniqueAccountImpl sutTo = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000001"),
//            toBalances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        sutFrom.transfer(sutTo, currency, BigDecimal.TEN, new HashSet<>());
//
//        BigDecimal actualFrom = fromBalances.get(currency);
//        BigDecimal actualTo = toBalances.get(currency);
//        BigDecimal expectedFrom = BigDecimal.valueOf(0);
//        BigDecimal expectedTo = BigDecimal.valueOf(110);
//
//        // Assert
//        assertEquals(expectedFrom, actualFrom);
//        assertEquals(expectedTo, actualTo);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void transfer_Contexts_WithBalances_ShouldReturnSuccessfulTransferResult() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> fromBalances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        Map<Currency, BigDecimal> toBalances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sutFrom = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            fromBalances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//        UniqueAccountImpl sutTo = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000001"),
//            toBalances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransferResult actual = sutFrom.transfer(sutTo, currency, BigDecimal.TEN, new HashSet<>());
//        TransferResult expected = new TransferResultImpl(
//            sutFrom,
//            sutTo,
//            currency,
//            BigDecimal.TEN,
//            ResultType.SUCCESS,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void transfer_Contexts_WithInsufficientFromBalance_ShouldReturnAccountNoFundsTransferResult() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> fromBalances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(9)));
//        Map<Currency, BigDecimal> toBalances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sutFrom = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            fromBalances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//        UniqueAccountImpl sutTo = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000001"),
//            toBalances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransferResult actual = sutFrom.transfer(sutTo, currency, BigDecimal.TEN, new HashSet<>());
//        TransferResult expected = new TransferResultImpl(
//            sutFrom,
//            sutTo,
//            currency,
//            BigDecimal.TEN,
//            ResultType.ACCOUNT_NO_FUNDS,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void transfer_Contexts_WithAmountEqualToZero_ShouldReturnFailedTransferResult() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> fromBalances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(9)));
//        Map<Currency, BigDecimal> toBalances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sutFrom = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            fromBalances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//        UniqueAccountImpl sutTo = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000001"),
//            toBalances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransferResult actual = sutFrom.transfer(sutTo, currency, BigDecimal.ZERO, new HashSet<>());
//        TransferResult expected = new TransferResultImpl(
//            sutFrom,
//            sutTo,
//            currency,
//            BigDecimal.ZERO,
//            ResultType.FAILED,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void transfer_Contexts_WithAmountLessThanZero_ShouldReturnFailedTransferResult() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> fromBalances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(9)));
//        Map<Currency, BigDecimal> toBalances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sutFrom = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            fromBalances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//        UniqueAccountImpl sutTo = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000001"),
//            toBalances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransferResult actual = sutFrom.transfer(sutTo, currency, BigDecimal.valueOf(-1), new HashSet<>());
//        TransferResult expected = new TransferResultImpl(
//            sutFrom,
//            sutTo,
//            currency,
//            BigDecimal.valueOf(-1),
//            ResultType.FAILED,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void transfer_Contexts_WithNoToBalance_ShouldReturnFailedTransferResult() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> fromBalances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sutFrom = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            fromBalances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//        UniqueAccountImpl sutTo = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000001"),
//            new HashMap<>(),
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransferResult actual = sutFrom.transfer(sutTo, currency, BigDecimal.TEN, new HashSet<>());
//        TransferResult expected = new TransferResultImpl(
//            sutFrom,
//            sutTo,
//            currency,
//            BigDecimal.TEN,
//            ResultType.FAILED,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void transfer_Contexts_WithSqlException_ShouldReturnFailedTransferResult() throws SQLException {
//        // Arrange
//        doThrow(SQLException.class).when(balanceDataMock).transfer(
//            any(UUID.class),
//            any(UUID.class),
//            any(Integer.class),
//            any(BigDecimal.class)
//        );
//
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> fromBalances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        Map<Currency, BigDecimal> toBalances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sutFrom = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            fromBalances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//        UniqueAccountImpl sutTo = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000001"),
//            toBalances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransferResult actual = sutFrom.transfer(sutTo, currency, BigDecimal.TEN, new HashSet<>());
//        TransferResult expected = new TransferResultImpl(
//            sutFrom,
//            sutTo,
//            currency,
//            BigDecimal.TEN,
//            ResultType.FAILED,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void transfer_Cause_WithBalances_ShouldTransferBetweenBalances() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> fromBalances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(10)));
//        Map<Currency, BigDecimal> toBalances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sutFrom = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            fromBalances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//        UniqueAccountImpl sutTo = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000001"),
//            toBalances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        sutFrom.transfer(sutTo, currency, BigDecimal.TEN, (Cause) null);
//
//        BigDecimal actualFrom = fromBalances.get(currency);
//        BigDecimal actualTo = toBalances.get(currency);
//        BigDecimal expectedFrom = BigDecimal.valueOf(0);
//        BigDecimal expectedTo = BigDecimal.valueOf(110);
//
//        // Assert
//        assertEquals(expectedFrom, actualFrom);
//        assertEquals(expectedTo, actualTo);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void transfer_Cause_WithBalances_ShouldReturnSuccessfulTransferResult() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> fromBalances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        Map<Currency, BigDecimal> toBalances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sutFrom = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            fromBalances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//        UniqueAccountImpl sutTo = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000001"),
//            toBalances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransferResult actual = sutFrom.transfer(sutTo, currency, BigDecimal.TEN, (Cause) null);
//        TransferResult expected = new TransferResultImpl(
//            sutFrom,
//            sutTo,
//            currency,
//            BigDecimal.TEN,
//            ResultType.SUCCESS,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void transfer_Cause_WithInsufficientFromBalance_ShouldReturnAccountNoFundsTransferResult() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> fromBalances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(9)));
//        Map<Currency, BigDecimal> toBalances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sutFrom = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            fromBalances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//        UniqueAccountImpl sutTo = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000001"),
//            toBalances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransferResult actual = sutFrom.transfer(sutTo, currency, BigDecimal.TEN, (Cause) null);
//        TransferResult expected = new TransferResultImpl(
//            sutFrom,
//            sutTo,
//            currency,
//            BigDecimal.TEN,
//            ResultType.ACCOUNT_NO_FUNDS,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void transfer_Cause_WithAmountEqualToZero_ShouldReturnFailedTransferResult() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> fromBalances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(9)));
//        Map<Currency, BigDecimal> toBalances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sutFrom = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            fromBalances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//        UniqueAccountImpl sutTo = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000001"),
//            toBalances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransferResult actual = sutFrom.transfer(sutTo, currency, BigDecimal.ZERO, (Cause) null);
//        TransferResult expected = new TransferResultImpl(
//            sutFrom,
//            sutTo,
//            currency,
//            BigDecimal.ZERO,
//            ResultType.FAILED,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void transfer_Cause_WithAmountLessThanZero_ShouldReturnFailedTransferResult() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> fromBalances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(9)));
//        Map<Currency, BigDecimal> toBalances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sutFrom = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            fromBalances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//        UniqueAccountImpl sutTo = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000001"),
//            toBalances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransferResult actual = sutFrom.transfer(sutTo, currency, BigDecimal.valueOf(-1), (Cause) null);
//        TransferResult expected = new TransferResultImpl(
//            sutFrom,
//            sutTo,
//            currency,
//            BigDecimal.valueOf(-1),
//            ResultType.FAILED,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void transfer_Cause_WithNoToBalance_ShouldReturnFailedTransferResult() {
//        // Arrange
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> fromBalances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sutFrom = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            fromBalances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//        UniqueAccountImpl sutTo = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000001"),
//            new HashMap<>(),
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransferResult actual = sutFrom.transfer(sutTo, currency, BigDecimal.TEN, (Cause) null);
//        TransferResult expected = new TransferResultImpl(
//            sutFrom,
//            sutTo,
//            currency,
//            BigDecimal.TEN,
//            ResultType.FAILED,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void transfer_Cause_WithSqlException_ShouldReturnFailedTransferResult() throws SQLException {
//        // Arrange
//        doThrow(SQLException.class).when(balanceDataMock).transfer(
//            any(UUID.class),
//            any(UUID.class),
//            any(Integer.class),
//            any(BigDecimal.class)
//        );
//
//        CurrencyImpl currency = new CurrencyImpl(currencyDto);
//        Map<Currency, BigDecimal> fromBalances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        Map<Currency, BigDecimal> toBalances = new HashMap<>(Map.of(currency, BigDecimal.valueOf(100)));
//        UniqueAccountImpl sutFrom = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            fromBalances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//        UniqueAccountImpl sutTo = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000001"),
//            toBalances,
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        TransferResult actual = sutFrom.transfer(sutTo, currency, BigDecimal.TEN, (Cause) null);
//        TransferResult expected = new TransferResultImpl(
//            sutFrom,
//            sutTo,
//            currency,
//            BigDecimal.TEN,
//            ResultType.FAILED,
//            null
//        );
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void identifier_ShouldReturnStringIdentifier() {
//        // Arrange
//        UniqueAccountImpl sut = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            new HashMap<>(),
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        String actual = sut.identifier();
//        String expected = "00000000-0000-0000-0000-000000000000";
//
//        // Assert
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    @Tag("Unit")
//    public void uniqueId_ShouldReturnUuid() {
//        // Arrange
//        UniqueAccountImpl sut = new UniqueAccountImpl(
//            loggerMock,
//            UUID.fromString("00000000-0000-0000-0000-000000000000"),
//            new HashMap<>(),
//            balanceDataMock,
//            currencyDto,
//            economyMock
//        );
//
//        // Act
//        UUID actual = sut.uniqueId();
//        UUID expected = UUID.fromString("00000000-0000-0000-0000-000000000000");
//
//        // Assert
//        assertEquals(expected, actual);
//    }
}
