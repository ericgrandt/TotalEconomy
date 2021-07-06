package com.ericgrandt.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.util.HashMap;
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

@Tag("Unit")
@ExtendWith(MockitoExtension.class)
public class TEAccountTest {
    @Mock
    TECurrency currencyMock;

    @Test
    public void getDisplayName_ShouldReturnTheCorrectDisplayName() {
        UUID uuid = UUID.randomUUID();
        TEAccount account = new TEAccount(uuid, "MyUsername", null);

        Component result = account.displayName();
        Component expectedResult = Component.text("MyUsername");

        assertEquals(expectedResult, result);
    }

    @Test
    public void getDefaultBalance_ShouldReturnZero() {
        UUID uuid = UUID.randomUUID();
        TEAccount account = new TEAccount(uuid, "MyUsername", null);

        BigDecimal result = account.defaultBalance(currencyMock);
        BigDecimal expectedResult = BigDecimal.ZERO;

        assertEquals(expectedResult, result);
    }

    @Test
    public void hasBalance_WithExistingBalance_ShouldReturnTrue() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        boolean result = account.hasBalance(currencyMock, (Cause) null);

        assertTrue(result);
    }

    @Test
    public void hasBalance_WithNonExistingBalance_ShouldReturnFalse() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        boolean result = account.hasBalance(mock(TECurrency.class), (Cause) null);

        assertFalse(result);
    }

    @Test
    public void getBalance_WithValidCurrency_ShouldReturnCorrectBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        BigDecimal result = account.balance(currencyMock, (Cause) null);
        BigDecimal expectedResult = BigDecimal.valueOf(123);

        assertEquals(expectedResult, result);
    }

    @Test
    public void getBalance_WithInvalidCurrency_ShouldReturnZero() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        BigDecimal result = account.balance(mock(TECurrency.class), (Cause) null);
        BigDecimal expectedResult = BigDecimal.ZERO;

        assertEquals(expectedResult, result);
    }

    @Test
    public void setBalance_WithValidCurrency_ShouldReturnCorrectTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        TransactionResult result = account.setBalance(currencyMock, BigDecimal.valueOf(10), (Cause) null);
        TransactionResult expectedResult = new TETransactionResult(
            account,
            currencyMock,
            BigDecimal.TEN,
            null,
            ResultType.SUCCESS,
            null
        );

        assertEquals(expectedResult, result);
    }

    @Test
    public void setBalance_WithValidCurrency_ShouldSetBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        account.setBalance(currencyMock, BigDecimal.valueOf(10), (Cause) null);
        BigDecimal expectedBalance = BigDecimal.TEN;

        assertEquals(expectedBalance, account.balance(currencyMock, (Cause) null));
    }

    @Test
    public void setBalance_WithInvalidCurrency_ShouldReturnFailedTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        Currency invalidCurrency = mock(TECurrency.class);
        TransactionResult result = account.setBalance(invalidCurrency, BigDecimal.valueOf(10), (Cause) null);
        TransactionResult expectedResult = new TETransactionResult(
            account,
            invalidCurrency,
            BigDecimal.ZERO,
            null,
            ResultType.FAILED,
            null
        );

        assertEquals(expectedResult, result);
    }

    @Test
    public void setBalance_WithInvalidCurrency_ShouldNotSetBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        Currency invalidCurrency = mock(TECurrency.class);
        account.setBalance(invalidCurrency, BigDecimal.valueOf(10), (Cause) null);
        BigDecimal expectedBalance = BigDecimal.ZERO;

        assertEquals(expectedBalance, account.balance(invalidCurrency, (Cause) null));
    }

    @Test
    public void setBalance_WithNegativeAmount_ShouldReturnFailedTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        TransactionResult result = account.setBalance(currencyMock, BigDecimal.valueOf(-0.01), (Cause) null);
        TransactionResult expectedResult = new TETransactionResult(
            account,
            currencyMock,
            BigDecimal.valueOf(123),
            null,
            ResultType.FAILED,
            null
        );

        assertEquals(expectedResult, result);
    }

    @Test
    public void setBalance_WithNegativeAmount_ShouldNotSetBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        account.setBalance(currencyMock, BigDecimal.valueOf(-0.01), (Cause) null);
        BigDecimal expectedBalance = BigDecimal.valueOf(123);

        assertEquals(expectedBalance, account.balance(currencyMock, (Cause) null));
    }

    @Test
    public void addBalance_ShouldAddNewBalance() {
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", new HashMap<>());
        TECurrency currency = mock(TECurrency.class);

        account.balances.put(currency, BigDecimal.ZERO);

        assertEquals(1, account.balances((Cause) null).size());
    }

    @Test
    public void deposit_WithValidCurrency_ShouldReturnCorrectTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        TransactionResult result = account.deposit(currencyMock, amountToDeposit, (Cause) null);
        TransactionResult expectedResult = new TETransactionResult(
            account,
            currencyMock,
            BigDecimal.valueOf(101),
            null,
            ResultType.SUCCESS,
            null
        );

        assertEquals(expectedResult, result);
    }

    @Test
    public void deposit_WithValidCurrency_ShouldAddToBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        account.deposit(currencyMock, amountToDeposit, (Cause) null);
        BigDecimal expectedBalance = BigDecimal.valueOf(101);

        assertEquals(expectedBalance, account.balance(currencyMock, (Cause) null));
    }

    @Test
    public void deposit_WithInvalidCurrency_ShouldReturnFailedTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        Currency invalidCurrency = mock(TECurrency.class);
        TransactionResult result = account.deposit(invalidCurrency, amountToDeposit, (Cause) null);
        TransactionResult expectedResult = new TETransactionResult(
            account,
            invalidCurrency,
            BigDecimal.ZERO,
            null,
            ResultType.FAILED,
            null
        );

        assertEquals(expectedResult, result);
    }

    @Test
    public void deposit_WithInvalidCurrency_ShouldNotUpdateBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        account.deposit(mock(TECurrency.class), amountToDeposit, (Cause) null);
        BigDecimal expectedBalance = BigDecimal.valueOf(100);

        assertEquals(expectedBalance, account.balance(currencyMock, (Cause) null));
    }

    @Test
    public void deposit_WithNegativeAmount_ShouldReturnFailedTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.valueOf(-1);
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        TransactionResult result = account.deposit(currencyMock, amountToDeposit, (Cause) null);
        TransactionResult expectedResult = new TETransactionResult(
            account,
            currencyMock,
            BigDecimal.valueOf(100),
            null,
            ResultType.FAILED,
            null
        );

        assertEquals(expectedResult, result);
    }

    @Test
    public void deposit_WithNegativeAmount_ShouldNotUpdateBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.valueOf(-1);
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        account.deposit(currencyMock, amountToDeposit, (Cause) null);
        BigDecimal expectedBalance = BigDecimal.valueOf(100);

        assertEquals(expectedBalance, account.balance(currencyMock, (Cause) null));
    }

    @Test
    public void withdraw_WithValidCurrency_ShouldReturnCorrectTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        TransactionResult result = account.withdraw(currencyMock, amountToDeposit, (Cause) null);
        TransactionResult expectedResult = new TETransactionResult(
            account,
            currencyMock,
            BigDecimal.valueOf(99),
            null,
            ResultType.SUCCESS,
            null
        );

        assertEquals(expectedResult, result);
    }

    @Test
    public void withdraw_WithValidCurrency_ShouldSubtractFromBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        account.withdraw(currencyMock, amountToDeposit, (Cause) null);
        BigDecimal expectedBalance = BigDecimal.valueOf(99);

        assertEquals(expectedBalance, account.balance(currencyMock, (Cause) null));
    }

    @Test
    public void withdraw_WithInvalidCurrency_ShouldReturnFailedTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        Currency invalidCurrency = mock(TECurrency.class);
        TransactionResult result = account.withdraw(invalidCurrency, amountToDeposit, (Cause) null);
        TransactionResult expectedResult = new TETransactionResult(
            account,
            invalidCurrency,
            BigDecimal.ZERO,
            null,
            ResultType.FAILED,
            null
        );

        assertEquals(expectedResult, result);
    }

    @Test
    public void withdraw_WithInvalidCurrency_ShouldNotUpdateBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        account.withdraw(mock(TECurrency.class), amountToDeposit, (Cause) null);
        BigDecimal expectedBalance = BigDecimal.valueOf(100);

        assertEquals(expectedBalance, account.balance(currencyMock, (Cause) null));
    }

    @Test
    public void withdraw_WithNegativeAmount_ShouldReturnFailedTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));
        BigDecimal amountToDeposit = BigDecimal.valueOf(-1);
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        TransactionResult result = account.withdraw(currencyMock, amountToDeposit, (Cause) null);
        TransactionResult expectedResult = new TETransactionResult(
            account,
            currencyMock,
            BigDecimal.valueOf(100),
            null,
            ResultType.FAILED,
            null
        );

        assertEquals(expectedResult, result);
    }

    @Test
    public void withdraw_WithNegativeAmount_ShouldNotUpdateBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));
        BigDecimal amountToDeposit = BigDecimal.valueOf(-1);
        TEAccount account = new TEAccount(UUID.randomUUID(), "MyUsername", balances);

        account.withdraw(currencyMock, amountToDeposit, (Cause) null);
        BigDecimal expectedBalance = BigDecimal.valueOf(100);

        assertEquals(expectedBalance, account.balance(currencyMock, (Cause) null));
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

        fromAccount.transfer(toAccount, currencyMock, amountToTransfer, (Cause) null);
        BigDecimal expectedFromBalance = BigDecimal.valueOf(90);
        BigDecimal expectedToBalance = BigDecimal.valueOf(160);

        assertEquals(expectedFromBalance, fromAccount.balance(currencyMock, (Cause) null));
        assertEquals(expectedToBalance, toAccount.balance(currencyMock, (Cause) null));
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

        TransferResult result = fromAccount.transfer(toAccount, currencyMock, amountToTransfer, (Cause) null);
        TransferResult expectedResult = new TETransferResult(
            toAccount,
            fromAccount,
            currencyMock,
            BigDecimal.valueOf(90),
            null,
            ResultType.SUCCESS,
            null
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

        fromAccount.transfer(toAccount, currencyMock, amountToTransfer, (Cause) null);
        BigDecimal expectedFromBalance = BigDecimal.ZERO;
        BigDecimal expectedToBalance = BigDecimal.valueOf(150);

        assertEquals(expectedFromBalance, fromAccount.balance(currencyMock, (Cause) null));
        assertEquals(expectedToBalance, toAccount.balance(currencyMock, (Cause) null));
    }

    @Test
    public void transfer_WithFromAccountMissingBalanceForCurrency_ShouldReturnCorrectTransferResult() {
        HashMap<Currency, BigDecimal> fromBalances = new HashMap<>();
        HashMap<Currency, BigDecimal> toBalances = new HashMap<>();
        toBalances.put(currencyMock, BigDecimal.valueOf(150));

        BigDecimal amountToTransfer = BigDecimal.TEN;
        TEAccount fromAccount = new TEAccount(UUID.randomUUID(), "MyUsername", fromBalances);
        TEAccount toAccount = new TEAccount(UUID.randomUUID(), "MyUsername2", toBalances);

        TransferResult result = fromAccount.transfer(toAccount, currencyMock, amountToTransfer, (Cause) null);
        TransferResult expectedResult = new TETransferResult(
            toAccount,
            fromAccount,
            currencyMock,
            BigDecimal.ZERO,
            null,
            ResultType.FAILED,
            null
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

        fromAccount.transfer(toAccount, currencyMock, amountToTransfer, (Cause) null);
        BigDecimal expectedFromBalance = BigDecimal.valueOf(100);
        BigDecimal expectedToBalance = BigDecimal.ZERO;

        assertEquals(expectedFromBalance, fromAccount.balance(currencyMock, (Cause) null));
        assertEquals(expectedToBalance, toAccount.balance(currencyMock, (Cause) null));
    }

    @Test
    public void transfer_WithToAccountMissingBalanceForCurrency_ShouldReturnCorrectTransferResult() {
        HashMap<Currency, BigDecimal> fromBalances = new HashMap<>();
        fromBalances.put(currencyMock, BigDecimal.valueOf(100));
        HashMap<Currency, BigDecimal> toBalances = new HashMap<>();

        BigDecimal amountToTransfer = BigDecimal.TEN;
        TEAccount fromAccount = new TEAccount(UUID.randomUUID(), "MyUsername", fromBalances);
        TEAccount toAccount = new TEAccount(UUID.randomUUID(), "MyUsername2", toBalances);

        TransferResult result = fromAccount.transfer(toAccount, currencyMock, amountToTransfer, (Cause) null);
        TransferResult expectedResult = new TETransferResult(
            toAccount,
            fromAccount,
            currencyMock,
            BigDecimal.valueOf(100),
            null,
            ResultType.FAILED,
            null
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

        fromAccount.transfer(toAccount, currencyMock, amountToTransfer, (Cause) null);
        BigDecimal expectedFromBalance = BigDecimal.ONE;
        BigDecimal expectedToBalance = BigDecimal.valueOf(100);

        assertEquals(expectedFromBalance, fromAccount.balance(currencyMock, (Cause) null));
        assertEquals(expectedToBalance, toAccount.balance(currencyMock, (Cause) null));
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

        TransferResult result = fromAccount.transfer(toAccount, currencyMock, amountToTransfer, (Cause) null);
        TransferResult expectedResult = new TETransferResult(
            toAccount,
            fromAccount,
            currencyMock,
            BigDecimal.ONE,
            null,
            ResultType.ACCOUNT_NO_FUNDS,
            null
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

        fromAccount.transfer(toAccount, currencyMock, amountToTransfer, (Cause) null);
        BigDecimal expectedFromBalance = BigDecimal.valueOf(100);
        BigDecimal expectedToBalance = BigDecimal.valueOf(100);

        assertEquals(expectedFromBalance, fromAccount.balance(currencyMock, (Cause) null));
        assertEquals(expectedToBalance, toAccount.balance(currencyMock, (Cause) null));
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

        TransferResult result = fromAccount.transfer(toAccount, currencyMock, amountToTransfer, (Cause) null);
        TransferResult expectedResult = new TETransferResult(
            toAccount,
            fromAccount,
            currencyMock,
            BigDecimal.valueOf(100),
            null,
            ResultType.FAILED,
            null
        );

        assertEquals(expectedResult, result);
    }
}
