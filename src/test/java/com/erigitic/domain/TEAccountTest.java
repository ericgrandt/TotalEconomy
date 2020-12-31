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
    public void getBalance_WithValidCurrency_ShouldReturnCorrectBalance() {
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
        BigDecimal expectedResult = BigDecimal.ZERO;

        assertEquals(expectedResult, result);
    }

    @Test
    public void setBalance_WithValidCurrency_ShouldReturnCorrectTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        TransactionResult result = account.setBalance(currencyMock, BigDecimal.valueOf(10), cause);
        TransactionResult expectedResult = new TETransactionResult(
            account,
            currencyMock,
            BigDecimal.TEN,
            null,
            ResultType.SUCCESS,
            TransactionTypes.DEPOSIT
        );

        assertEquals(expectedResult, result);
    }

    @Test
    public void setBalance_WithValidCurrency_ShouldSetBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        account.setBalance(currencyMock, BigDecimal.valueOf(10), cause);
        BigDecimal expectedBalance = BigDecimal.TEN;

        assertEquals(expectedBalance, account.getBalance(currencyMock));
    }

    @Test
    public void setBalance_WithInvalidCurrency_ShouldReturnFailedTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        Currency invalidCurrency = mock(TECurrency.class);
        TransactionResult result = account.setBalance(invalidCurrency, BigDecimal.valueOf(10), cause);
        TransactionResult expectedResult = new TETransactionResult(
            account,
            invalidCurrency,
            BigDecimal.ZERO,
            null,
            ResultType.FAILED,
            TransactionTypes.DEPOSIT
        );

        assertEquals(expectedResult, result);
    }

    @Test
    public void setBalance_WithInvalidCurrency_ShouldNotSetBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        Currency invalidCurrency = mock(TECurrency.class);
        account.setBalance(invalidCurrency, BigDecimal.valueOf(10), cause);
        BigDecimal expectedBalance = BigDecimal.ZERO;

        assertEquals(expectedBalance, account.getBalance(invalidCurrency));
    }

    @Test
    public void setBalance_WithNegativeAmount_ShouldReturnFailedTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        TransactionResult result = account.setBalance(currencyMock, BigDecimal.valueOf(-0.01), cause);
        TransactionResult expectedResult = new TETransactionResult(
            account,
            currencyMock,
            BigDecimal.valueOf(123),
            null,
            ResultType.FAILED,
            TransactionTypes.DEPOSIT
        );

        assertEquals(expectedResult, result);
    }

    @Test
    public void setBalance_WithNegativeAmount_ShouldNotSetBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        account.setBalance(currencyMock, BigDecimal.valueOf(-0.01), cause);
        BigDecimal expectedBalance = BigDecimal.valueOf(123);

        assertEquals(expectedBalance, account.getBalance(currencyMock));
    }

    @Test
    public void addBalance_ShouldAddNewBalance() {
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", new HashMap<>());
        TECurrency currency = mock(TECurrency.class);

        account.addBalance(currency, BigDecimal.ZERO);

        assertEquals(1, account.getBalances().size());
    }

    @Test
    public void deposit_WithValidCurrency_ShouldReturnCorrectTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        TransactionResult result = account.deposit(currencyMock, amountToDeposit, cause);
        TransactionResult expectedResult = new TETransactionResult(
            account,
            currencyMock,
            BigDecimal.valueOf(101),
            null,
            ResultType.SUCCESS,
            TransactionTypes.DEPOSIT
        );

        assertEquals(expectedResult, result);
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

        account.deposit(currencyMock, amountToDeposit, cause);
        BigDecimal expectedBalance = BigDecimal.valueOf(101);

        assertEquals(expectedBalance, account.getBalance(currencyMock));
    }

    @Test
    public void deposit_WithInvalidCurrency_ShouldReturnFailedTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        Currency invalidCurrency = mock(TECurrency.class);
        TransactionResult result = account.deposit(invalidCurrency, amountToDeposit, cause);
        TransactionResult expectedResult = new TETransactionResult(
            account,
            invalidCurrency,
            BigDecimal.ZERO,
            null,
            ResultType.FAILED,
            TransactionTypes.DEPOSIT
        );

        assertEquals(expectedResult, result);
    }

    @Test
    public void deposit_WithInvalidCurrency_ShouldNotUpdateBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        account.deposit(mock(TECurrency.class), amountToDeposit, cause);
        BigDecimal expectedBalance = BigDecimal.valueOf(100);

        assertEquals(expectedBalance, account.getBalance(currencyMock));
    }

    @Test
    public void deposit_WithNegativeAmount_ShouldReturnFailedTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.valueOf(-1);
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        TransactionResult result = account.deposit(currencyMock, amountToDeposit, cause);
        TransactionResult expectedResult = new TETransactionResult(
            account,
            currencyMock,
            BigDecimal.valueOf(100),
            null,
            ResultType.FAILED,
            TransactionTypes.DEPOSIT
        );

        assertEquals(expectedResult, result);
    }

    @Test
    public void deposit_WithNegativeAmount_ShouldNotUpdateBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.valueOf(-1);
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        account.deposit(currencyMock, amountToDeposit, cause);
        BigDecimal expectedBalance = BigDecimal.valueOf(100);

        assertEquals(expectedBalance, account.getBalance(currencyMock));
    }

    @Test
    public void withdraw_WithValidCurrency_ShouldReturnCorrectTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        TransactionResult result = account.withdraw(currencyMock, amountToDeposit, cause);
        TransactionResult expectedResult = new TETransactionResult(
            account,
            currencyMock,
            BigDecimal.valueOf(99),
            null,
            ResultType.SUCCESS,
            TransactionTypes.DEPOSIT
        );

        assertEquals(expectedResult, result);
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

        account.withdraw(currencyMock, amountToDeposit, cause);
        BigDecimal expectedBalance = BigDecimal.valueOf(99);

        assertEquals(expectedBalance, account.getBalance(currencyMock));
    }

    @Test
    public void withdraw_WithInvalidCurrency_ShouldReturnFailedTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        Currency invalidCurrency = mock(TECurrency.class);
        TransactionResult result = account.withdraw(invalidCurrency, amountToDeposit, cause);
        TransactionResult expectedResult = new TETransactionResult(
            account,
            invalidCurrency,
            BigDecimal.ZERO,
            null,
            ResultType.FAILED,
            TransactionTypes.DEPOSIT
        );

        assertEquals(expectedResult, result);
    }

    @Test
    public void withdraw_WithInvalidCurrency_ShouldNotUpdateBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        account.withdraw(mock(TECurrency.class), amountToDeposit, cause);
        BigDecimal expectedBalance = BigDecimal.valueOf(100);

        assertEquals(expectedBalance, account.getBalance(currencyMock));
    }

    @Test
    public void withdraw_WithNegativeAmount_ShouldReturnFailedTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));
        BigDecimal amountToDeposit = BigDecimal.valueOf(-1);
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        TransactionResult result = account.withdraw(currencyMock, amountToDeposit, cause);
        TransactionResult expectedResult = new TETransactionResult(
            account,
            currencyMock,
            BigDecimal.valueOf(100),
            null,
            ResultType.FAILED,
            TransactionTypes.DEPOSIT
        );

        assertEquals(expectedResult, result);
    }

    @Test
    public void withdraw_WithNegativeAmount_ShouldNotUpdateBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));
        BigDecimal amountToDeposit = BigDecimal.valueOf(-1);
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        account.withdraw(currencyMock, amountToDeposit, cause);
        BigDecimal expectedBalance = BigDecimal.valueOf(100);

        assertEquals(expectedBalance, account.getBalance(currencyMock));
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
        TransferResult expectedResult = new TETransferResult(
            toAccount,
            fromAccount,
            currencyMock,
            BigDecimal.valueOf(90),
            null,
            ResultType.SUCCESS,
            TransactionTypes.TRANSFER
        );

        assertEquals(expectedResult, result);
    }

    @Test
    public void transfer_WithFromAccountMissingBalanceForCurrency_ShouldNotUpdateBalances() {
        HashMap<Currency, BigDecimal> fromBalances = new HashMap<>();
        HashMap<Currency, BigDecimal> toBalances = new HashMap<>();
        toBalances.put(currencyMock, BigDecimal.valueOf(150));

        BigDecimal amountToTransfer = BigDecimal.TEN;
        TEAccount fromAccount = new TEAccount(UUID.randomUUID(), "MyUsername", fromBalances);
        TEAccount toAccount = new TEAccount(UUID.randomUUID(), "MyUsername2", toBalances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        fromAccount.transfer(toAccount, currencyMock, amountToTransfer, cause);
        BigDecimal expectedFromBalance = BigDecimal.ZERO;
        BigDecimal expectedToBalance = BigDecimal.valueOf(150);

        assertEquals(expectedFromBalance, fromAccount.getBalance(currencyMock));
        assertEquals(expectedToBalance, toAccount.getBalance(currencyMock));
    }

    @Test
    public void transfer_WithFromAccountMissingBalanceForCurrency_ShouldReturnCorrectTransferResult() {
        HashMap<Currency, BigDecimal> fromBalances = new HashMap<>();
        HashMap<Currency, BigDecimal> toBalances = new HashMap<>();
        toBalances.put(currencyMock, BigDecimal.valueOf(150));

        BigDecimal amountToTransfer = BigDecimal.TEN;
        TEAccount fromAccount = new TEAccount(UUID.randomUUID(), "MyUsername", fromBalances);
        TEAccount toAccount = new TEAccount(UUID.randomUUID(), "MyUsername2", toBalances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        TransferResult result = fromAccount.transfer(toAccount, currencyMock, amountToTransfer, cause);
        TransferResult expectedResult = new TETransferResult(
            toAccount,
            fromAccount,
            currencyMock,
            BigDecimal.ZERO,
            null,
            ResultType.FAILED,
            TransactionTypes.TRANSFER
        );

        assertEquals(expectedResult, result);
    }

    @Test
    public void transfer_WithToAccountMissingBalanceForCurrency_ShouldNotUpdateBalances() {
        HashMap<Currency, BigDecimal> fromBalances = new HashMap<>();
        fromBalances.put(currencyMock, BigDecimal.valueOf(100));
        HashMap<Currency, BigDecimal> toBalances = new HashMap<>();

        BigDecimal amountToTransfer = BigDecimal.TEN;
        TEAccount fromAccount = new TEAccount(UUID.randomUUID(), "MyUsername", fromBalances);
        TEAccount toAccount = new TEAccount(UUID.randomUUID(), "MyUsername2", toBalances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        fromAccount.transfer(toAccount, currencyMock, amountToTransfer, cause);
        BigDecimal expectedFromBalance = BigDecimal.valueOf(100);
        BigDecimal expectedToBalance = BigDecimal.ZERO;

        assertEquals(expectedFromBalance, fromAccount.getBalance(currencyMock));
        assertEquals(expectedToBalance, toAccount.getBalance(currencyMock));
    }

    @Test
    public void transfer_WithToAccountMissingBalanceForCurrency_ShouldReturnCorrectTransferResult() {
        HashMap<Currency, BigDecimal> fromBalances = new HashMap<>();
        fromBalances.put(currencyMock, BigDecimal.valueOf(100));
        HashMap<Currency, BigDecimal> toBalances = new HashMap<>();

        BigDecimal amountToTransfer = BigDecimal.TEN;
        TEAccount fromAccount = new TEAccount(UUID.randomUUID(), "MyUsername", fromBalances);
        TEAccount toAccount = new TEAccount(UUID.randomUUID(), "MyUsername2", toBalances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        TransferResult result = fromAccount.transfer(toAccount, currencyMock, amountToTransfer, cause);
        TransferResult expectedResult = new TETransferResult(
            toAccount,
            fromAccount,
            currencyMock,
            BigDecimal.valueOf(100),
            null,
            ResultType.FAILED,
            TransactionTypes.TRANSFER
        );

        assertEquals(expectedResult, result);
    }

    @Test
    public void transfer_WithInsufficientFunds_ShouldNotUpdateBalances() {
        HashMap<Currency, BigDecimal> fromBalances = new HashMap<>();
        fromBalances.put(currencyMock, BigDecimal.ONE);
        HashMap<Currency, BigDecimal> toBalances = new HashMap<>();
        toBalances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToTransfer = BigDecimal.TEN;
        TEAccount fromAccount = new TEAccount(UUID.randomUUID(), "MyUsername", fromBalances);
        TEAccount toAccount = new TEAccount(UUID.randomUUID(), "MyUsername2", toBalances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        fromAccount.transfer(toAccount, currencyMock, amountToTransfer, cause);
        BigDecimal expectedFromBalance = BigDecimal.ONE;
        BigDecimal expectedToBalance = BigDecimal.valueOf(100);

        assertEquals(expectedFromBalance, fromAccount.getBalance(currencyMock));
        assertEquals(expectedToBalance, toAccount.getBalance(currencyMock));
    }

    @Test
    public void transfer_WithInsufficientFunds_ShouldReturnCorrectTransferResult() {
        HashMap<Currency, BigDecimal> fromBalances = new HashMap<>();
        fromBalances.put(currencyMock, BigDecimal.ONE);
        HashMap<Currency, BigDecimal> toBalances = new HashMap<>();
        toBalances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToTransfer = BigDecimal.TEN;
        TEAccount fromAccount = new TEAccount(UUID.randomUUID(), "MyUsername", fromBalances);
        TEAccount toAccount = new TEAccount(UUID.randomUUID(), "MyUsername2", toBalances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        TransferResult result = fromAccount.transfer(toAccount, currencyMock, amountToTransfer, cause);
        TransferResult expectedResult = new TETransferResult(
            toAccount,
            fromAccount,
            currencyMock,
            BigDecimal.ONE,
            null,
            ResultType.ACCOUNT_NO_FUNDS,
            TransactionTypes.TRANSFER
        );

        assertEquals(expectedResult, result);
    }

    @Test
    public void transfer_WithNegativeTransferAmount_ShouldNotUpdateBalances() {
        HashMap<Currency, BigDecimal> fromBalances = new HashMap<>();
        fromBalances.put(currencyMock, BigDecimal.valueOf(100));
        HashMap<Currency, BigDecimal> toBalances = new HashMap<>();
        toBalances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToTransfer = BigDecimal.valueOf(-1);
        TEAccount fromAccount = new TEAccount(UUID.randomUUID(), "MyUsername", fromBalances);
        TEAccount toAccount = new TEAccount(UUID.randomUUID(), "MyUsername2", toBalances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        fromAccount.transfer(toAccount, currencyMock, amountToTransfer, cause);
        BigDecimal expectedFromBalance = BigDecimal.valueOf(100);
        BigDecimal expectedToBalance = BigDecimal.valueOf(100);

        assertEquals(expectedFromBalance, fromAccount.getBalance(currencyMock));
        assertEquals(expectedToBalance, toAccount.getBalance(currencyMock));
    }

    @Test
    public void transfer_WithNegativeTransferAmount_ShouldReturnCorrectTransferResult() {
        HashMap<Currency, BigDecimal> fromBalances = new HashMap<>();
        fromBalances.put(currencyMock, BigDecimal.valueOf(100));
        HashMap<Currency, BigDecimal> toBalances = new HashMap<>();
        toBalances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToTransfer = BigDecimal.valueOf(-1);
        TEAccount fromAccount = new TEAccount(UUID.randomUUID(), "MyUsername", fromBalances);
        TEAccount toAccount = new TEAccount(UUID.randomUUID(), "MyUsername2", toBalances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        TransferResult result = fromAccount.transfer(toAccount, currencyMock, amountToTransfer, cause);
        TransferResult expectedResult = new TETransferResult(
            toAccount,
            fromAccount,
            currencyMock,
            BigDecimal.valueOf(100),
            null,
            ResultType.FAILED,
            TransactionTypes.TRANSFER
        );

        assertEquals(expectedResult, result);
    }
}
