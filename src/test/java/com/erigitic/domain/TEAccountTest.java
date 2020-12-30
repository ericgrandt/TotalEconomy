package com.erigitic.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.UUID;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;
import org.spongepowered.api.service.economy.transaction.TransferResult;
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
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        boolean result = account.hasBalance(currencyMock);

        assertTrue(result);
    }

    @Test
    public void hasBalance_WithNonExistingBalance_ShouldReturnFalse() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        boolean result = account.hasBalance(mock(TECurrency.class));

        assertFalse(result);
    }

    @Test
    public void getBalance_WithValidCurrency_ShouldReturnBigDecimal() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        BigDecimal result = account.getBalance(currencyMock);
        BigDecimal expectedResult = BigDecimal.valueOf(123);

        assertEquals(expectedResult, result);
    }

    @Test
    public void getBalance_WithInvalidCurrency_ShouldReturnZero() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        BigDecimal result = account.getBalance(mock(TECurrency.class));
        BigDecimal expectedResult = BigDecimal.valueOf(0);

        assertEquals(expectedResult, result);
    }

    @Test
    public void setBalance_WithValidCurrency_ShouldReturnCorrectTransactionResultAndSetBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        TransactionResult result = account.setBalance(currencyMock, BigDecimal.valueOf(10), cause);

        assertEquals(account, result.getAccount());
        assertEquals(currencyMock, result.getCurrency());
        assertEquals(BigDecimal.valueOf(10), result.getAmount());
        assertEquals(ResultType.SUCCESS, result.getResult());
        assertEquals(TransactionTypes.DEPOSIT, result.getType());
    }

    @Test
    public void setBalance_WithInvalidCurrency_ShouldReturnFailedTransactionResultAndNotSetBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        Currency invalidCurrency = mock(TECurrency.class);
        TransactionResult result = account.setBalance(invalidCurrency, BigDecimal.valueOf(10), cause);

        assertEquals(BigDecimal.valueOf(123), account.getBalance(currencyMock));
        assertEquals(account, result.getAccount());
        assertEquals(invalidCurrency, result.getCurrency());
        assertEquals(BigDecimal.valueOf(10), result.getAmount());
        assertEquals(ResultType.FAILED, result.getResult());
        assertEquals(TransactionTypes.DEPOSIT, result.getType());
    }

    @Test
    public void setBalance_WithAmountLessThanZero_ShouldReturnFailedTransactionResultAndNotSetBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        Currency invalidCurrency = mock(TECurrency.class);
        TransactionResult result = account.setBalance(invalidCurrency, BigDecimal.valueOf(-0.01), cause);

        assertEquals(BigDecimal.valueOf(123), account.getBalance(currencyMock));
        assertEquals(account, result.getAccount());
        assertEquals(invalidCurrency, result.getCurrency());
        assertEquals(BigDecimal.valueOf(-0.01), result.getAmount());
        assertEquals(ResultType.FAILED, result.getResult());
        assertEquals(TransactionTypes.DEPOSIT, result.getType());
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
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        TransactionResult result = account.deposit(currencyMock, amountToDeposit, cause);
        BigDecimal expectedBalance = BigDecimal.valueOf(101);

        assertEquals(expectedBalance, account.getBalance(currencyMock));
        assertEquals(account, result.getAccount());
        assertEquals(currencyMock, result.getCurrency());
        assertEquals(expectedBalance, result.getAmount());
        assertEquals(ResultType.SUCCESS, result.getResult());
        assertEquals(TransactionTypes.DEPOSIT, result.getType());
    }

    @Test
    public void deposit_WithInvalidCurrency_ShouldReturnFailedTransactionResultAndNotUpdateBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        Currency invalidCurrency = mock(TECurrency.class);
        TransactionResult result = account.deposit(invalidCurrency, amountToDeposit, cause);
        BigDecimal expectedBalance = BigDecimal.valueOf(100);

        assertEquals(expectedBalance, account.getBalance(currencyMock));
        assertEquals(account, result.getAccount());
        assertEquals(invalidCurrency, result.getCurrency());
        assertEquals(BigDecimal.ZERO, result.getAmount());
        assertEquals(ResultType.FAILED, result.getResult());
        assertEquals(TransactionTypes.DEPOSIT, result.getType());
    }

    @Test
    public void deposit_WithNegativeAmount_ShouldReturnFailedTransactionResultAndNotUpdateBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.valueOf(-1);
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        TransactionResult result = account.deposit(currencyMock, amountToDeposit, cause);
        BigDecimal expectedBalance = BigDecimal.valueOf(100);

        assertEquals(expectedBalance, account.getBalance(currencyMock));
        assertEquals(account, result.getAccount());
        assertEquals(currencyMock, result.getCurrency());
        assertEquals(expectedBalance, result.getAmount());
        assertEquals(ResultType.FAILED, result.getResult());
        assertEquals(TransactionTypes.DEPOSIT, result.getType());
    }

    @Test
    public void withdraw_WithValidCurrency_ShouldSubtractFromBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        TransactionResult result = account.withdraw(currencyMock, amountToDeposit, cause);
        BigDecimal expectedBalance = BigDecimal.valueOf(99);

        assertEquals(expectedBalance, account.getBalance(currencyMock));
        assertEquals(account, result.getAccount());
        assertEquals(currencyMock, result.getCurrency());
        assertEquals(expectedBalance, result.getAmount());
        assertEquals(ResultType.SUCCESS, result.getResult());
        assertEquals(TransactionTypes.DEPOSIT, result.getType());
    }

    @Test
    public void withdraw_WithInvalidCurrency_ShouldReturnFailedTransactionResultAndNotUpdateBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        Currency invalidCurrency = mock(TECurrency.class);
        TransactionResult result = account.withdraw(invalidCurrency, amountToDeposit, cause);
        BigDecimal expectedBalance = BigDecimal.valueOf(100);

        assertEquals(expectedBalance, account.getBalance(currencyMock));
        assertEquals(account, result.getAccount());
        assertEquals(invalidCurrency, result.getCurrency());
        assertEquals(BigDecimal.ZERO, result.getAmount());
        assertEquals(ResultType.FAILED, result.getResult());
        assertEquals(TransactionTypes.DEPOSIT, result.getType());
    }

    @Test
    public void withdraw_WithNegativeAmount_ShouldReturnFailedTransactionResultAndNotUpdateBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));
        BigDecimal amountToDeposit = BigDecimal.valueOf(-1);
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        TransactionResult result = account.withdraw(currencyMock, amountToDeposit, cause);
        BigDecimal expectedBalance = BigDecimal.valueOf(100);

        assertEquals(expectedBalance, account.getBalance(currencyMock));
        assertEquals(account, result.getAccount());
        assertEquals(currencyMock, result.getCurrency());
        assertEquals(expectedBalance, result.getAmount());
        assertEquals(ResultType.FAILED, result.getResult());
        assertEquals(TransactionTypes.DEPOSIT, result.getType());
    }

    @Test
    public void transfer_WithValidData_ShouldUpdateBothBalances() {
        HashMap<Currency, BigDecimal> fromBalances = new HashMap<>();
        fromBalances.put(currencyMock, BigDecimal.valueOf(100));
        HashMap<Currency, BigDecimal> toBalances = new HashMap<>();
        toBalances.put(currencyMock, BigDecimal.valueOf(150));

        BigDecimal amountToTransfer = BigDecimal.TEN;
        TEAccount fromAccount = new TEAccount(UUID.randomUUID(), "MyUsername", fromBalances);
        TEAccount toAccount = new TEAccount(UUID.randomUUID(), "MyUsername2", toBalances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        fromAccount.transfer(toAccount, currencyMock, amountToTransfer, cause);
        BigDecimal expectedFromBalance = BigDecimal.valueOf(90);
        BigDecimal expectedToBalance = BigDecimal.valueOf(160);

        assertEquals(expectedFromBalance, fromAccount.getBalance(currencyMock));
        assertEquals(expectedToBalance, toAccount.getBalance(currencyMock));
    }

    @Test
    public void transfer_WithValidData_ShouldReturnCorrectTransferResult() {
        HashMap<Currency, BigDecimal> fromBalances = new HashMap<>();
        fromBalances.put(currencyMock, BigDecimal.valueOf(100));
        HashMap<Currency, BigDecimal> toBalances = new HashMap<>();
        toBalances.put(currencyMock, BigDecimal.valueOf(150));

        BigDecimal amountToTransfer = BigDecimal.TEN;
        TEAccount fromAccount = new TEAccount(UUID.randomUUID(), "MyUsername", fromBalances);
        TEAccount toAccount = new TEAccount(UUID.randomUUID(), "MyUsername2", toBalances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        TransferResult result = fromAccount.transfer(toAccount, currencyMock, amountToTransfer, cause);

        assertEquals(toAccount, result.getAccountTo());
        assertEquals(fromAccount, result.getAccount());
        assertEquals(currencyMock, result.getCurrency());
        assertEquals(amountToTransfer, result.getAmount());
        assertEquals(ResultType.SUCCESS, result.getResult());
        assertEquals(TransactionTypes.TRANSFER, result.getType());
    }

    // TODO: Test not enough funds
    // TODO: Test negative (might be handled already?)
}
