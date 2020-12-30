package com.erigitic.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.UUID;

import com.erigitic.economy.TETransactionResult;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;
import org.spongepowered.api.text.Text;

@Tag("Unit")
@ExtendWith(MockitoExtension.class)
public class TEAccountTest {
    @Mock
    TECurrency currencyMock;

    @Test
    public void getDisplayName_ShouldReturnTheCorrectDisplayName() {
        UUID uuid = UUID.randomUUID();
        TEAccount account = new TEAccount(uuid, "MyUsername", null);

        Text result = account.getDisplayName();
        Text expectedResult = Text.of("MyUsername");

        assertEquals(expectedResult, result);
    }

    @Test
    public void getDefaultBalance_ShouldReturnZero() {
        UUID uuid = UUID.randomUUID();
        TEAccount account = new TEAccount(uuid, "MyUsername", null);

        BigDecimal result = account.getDefaultBalance(currencyMock);
        BigDecimal expectedResult = BigDecimal.ZERO;

        assertEquals(expectedResult, result);
    }

    @Test
    public void hasBalance_WithExistingBalance_ShouldReturnTrue() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(
            currencyMock,
            BigDecimal.valueOf(123)
        );
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        boolean result = account.hasBalance(currencyMock);

        assertTrue(result);
    }

    @Test
    public void hasBalance_WithNonExistingBalance_ShouldReturnFalse() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(
            currencyMock,
            BigDecimal.valueOf(123)
        );
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        boolean result = account.hasBalance(mock(TECurrency.class));

        assertFalse(result);
    }

    @Test
    public void getBalance_WithValidCurrency_ShouldReturnBigDecimal() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(
            currencyMock,
            BigDecimal.valueOf(123)
        );
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        BigDecimal result = account.getBalance(currencyMock);
        BigDecimal expectedResult = BigDecimal.valueOf(123);

        assertEquals(expectedResult, result);
    }

    @Test
    public void getBalance_WithInvalidCurrency_ShouldReturnZero() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(
            currencyMock,
            BigDecimal.valueOf(123)
        );
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        BigDecimal result = account.getBalance(mock(TECurrency.class));
        BigDecimal expectedResult = BigDecimal.valueOf(0);

        assertEquals(expectedResult, result);
    }

    @Test
    public void setBalance_WithValidCurrency_ShouldReturnCorrectTransactionResultAndSetBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(
            currencyMock,
            BigDecimal.valueOf(123)
        );
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        TransactionResult result = account.setBalance(currencyMock, BigDecimal.valueOf(10), cause);

        assertEquals(BigDecimal.valueOf(10), result.getAmount());
        assertEquals(ResultType.SUCCESS, result.getResult());
        assertEquals(TransactionTypes.DEPOSIT, result.getType());
        assertEquals(BigDecimal.valueOf(10), balances.get(currencyMock));
    }

    @Test
    public void setBalance_WithInvalidCurrency_ShouldReturnFailedTransactionResultAndNotSetBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(
            currencyMock,
            BigDecimal.valueOf(123)
        );
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        TransactionResult result = account.setBalance(mock(TECurrency.class), BigDecimal.valueOf(10), cause);

        assertEquals(BigDecimal.valueOf(10), result.getAmount());
        assertEquals(ResultType.FAILED, result.getResult());
        assertEquals(TransactionTypes.DEPOSIT, result.getType());
        assertEquals(BigDecimal.valueOf(123), balances.get(currencyMock));
        assertEquals(balances.size(), 1);
    }

    @Test
    public void setBalance_WithAmountLessThanZero_ShouldReturnFailedTransactionResultAndNotSetBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(
            currencyMock,
            BigDecimal.valueOf(123)
        );
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        TransactionResult result = account.setBalance(mock(TECurrency.class), BigDecimal.valueOf(-0.01), cause);

        assertEquals(BigDecimal.valueOf(-0.01), result.getAmount());
        assertEquals(ResultType.FAILED, result.getResult());
        assertEquals(TransactionTypes.DEPOSIT, result.getType());
        assertEquals(BigDecimal.valueOf(123), balances.get(currencyMock));
        assertEquals(balances.size(), 1);
    }

    @Test
    public void addBalance_ShouldAddNewBalance() {
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", new HashMap<>());
        TECurrency currency = mock(TECurrency.class);

        account.addBalance(currency, BigDecimal.ZERO);

        assertEquals(1, account.getBalances().size());
    }

    @Test
    public void deposit_WithValidCurrency_ShouldAddToBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(
            currencyMock,
            BigDecimal.valueOf(100)
        );
        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        TransactionResult result = account.deposit(currencyMock, amountToDeposit, cause);
        BigDecimal expectedBalance = BigDecimal.valueOf(101);

        assertEquals(expectedBalance, result.getAmount());
        assertEquals(ResultType.SUCCESS, result.getResult());
        assertEquals(TransactionTypes.DEPOSIT, result.getType());
        assertEquals(expectedBalance, balances.get(currencyMock));
    }

    @Test
    public void deposit_WithInvalidCurrency_ShouldReturnFailedTransactionResultAndNotUpdateBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(
            currencyMock,
            BigDecimal.valueOf(100)
        );
        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        TransactionResult result = account.deposit(mock(TECurrency.class), amountToDeposit, cause);

        assertEquals(amountToDeposit, result.getAmount());
        assertEquals(ResultType.FAILED, result.getResult());
        assertEquals(TransactionTypes.DEPOSIT, result.getType());
        assertEquals(BigDecimal.valueOf(100), balances.get(currencyMock));
        assertEquals(balances.size(), 1);
    }

    @Test
    public void deposit_WithNegativeAmount_ShouldReturnFailedTransactionResultAndNotUpdateBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(
            currencyMock,
            BigDecimal.valueOf(100)
        );
        BigDecimal amountToDeposit = BigDecimal.valueOf(-1);
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        TransactionResult result = account.deposit(currencyMock, amountToDeposit, cause);

        assertEquals(amountToDeposit, result.getAmount());
        assertEquals(ResultType.FAILED, result.getResult());
        assertEquals(TransactionTypes.DEPOSIT, result.getType());
        assertEquals(BigDecimal.valueOf(100), balances.get(currencyMock));
    }
}
