package com.ericgrandt.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

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

import com.ericgrandt.data.AccountData;

@Tag("Unit")
@ExtendWith(MockitoExtension.class)
public class TEAccountTest {

    @Mock
    private AccountData accountData;

    @Mock
    TECurrency currencyMock;

    @Test
    public void uniqueId_ShouldReturnCorrectUniqueId() {
        UUID uuid = UUID.randomUUID();
        TEAccount sut = new TEAccount(accountData, uuid, "MyUsername", null);

        UUID result = sut.uniqueId();

        assertEquals(uuid, result);
    }

    @Test
    public void identifier_ShouldReturnCorrectIdentifier() {
        UUID uuid = UUID.randomUUID();
        TEAccount sut = new TEAccount(accountData, uuid, "MyUsername", null);

        String result = sut.identifier();
        String expectedResult = uuid.toString();

        assertEquals(expectedResult, result);
    }

    @Test
    public void displayName_ShouldReturnTheCorrectDisplayName() {
        UUID uuid = UUID.randomUUID();
        TEAccount account = new TEAccount(accountData, uuid, "MyUsername", null);

        Component result = account.displayName();
        Component expectedResult = Component.text("MyUsername");

        assertEquals(expectedResult, result);
    }

    @Test
    public void defaultBalance_ShouldReturnZero() {
        UUID uuid = UUID.randomUUID();
        TEAccount account = new TEAccount(accountData, uuid, "MyUsername", null);

        BigDecimal result = account.defaultBalance(currencyMock);
        BigDecimal expectedResult = BigDecimal.ZERO;

        assertEquals(expectedResult, result);
    }

    @Test
    public void hasBalanceContext_WithExistingBalance_ShouldReturnTrue() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

        boolean result = account.hasBalance(currencyMock, new HashSet<>());

        assertTrue(result);
    }

    @Test
    public void hasBalanceCause_WithExistingBalance_ShouldReturnTrue() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

        boolean result = account.hasBalance(currencyMock, (Cause) null);

        assertTrue(result);
    }

    @Test
    public void hasBalanceCause_WithNonExistingBalance_ShouldReturnFalse() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

        boolean result = account.hasBalance(mock(TECurrency.class), (Cause) null);

        assertFalse(result);
    }

    @Test
    public void balanceContext_WithValidCurrency_ShouldReturnCorrectBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

        BigDecimal result = account.balance(currencyMock, new HashSet<>());
        BigDecimal expectedResult = BigDecimal.valueOf(123);

        assertEquals(expectedResult, result);
    }

    @Test
    public void balanceCause_WithValidCurrency_ShouldReturnCorrectBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

        BigDecimal result = account.balance(currencyMock, (Cause) null);
        BigDecimal expectedResult = BigDecimal.valueOf(123);

        assertEquals(expectedResult, result);
    }

    @Test
    public void balanceCause_WithInvalidCurrency_ShouldReturnZero() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

        BigDecimal result = account.balance(mock(TECurrency.class), (Cause) null);
        BigDecimal expectedResult = BigDecimal.ZERO;

        assertEquals(expectedResult, result);
    }

    @Test
    public void balancesContext_ShouldReturnMapOfBalances() {
        Map<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

        Map<Currency, BigDecimal> result = account.balances(new HashSet<>());

        assertEquals(balances, result);
    }

    @Test
    public void balancesCause_ShouldReturnMapOfBalances() {
        Map<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

        Map<Currency, BigDecimal> result = account.balances((Cause) null);

        assertEquals(balances, result);
    }

    @Test
    public void setBalanceContext_WithValidCurrency_ShouldReturnCorrectTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

        TransactionResult result = account.setBalance(currencyMock, BigDecimal.valueOf(10), new HashSet<>());
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
    public void setBalanceCause_WithValidCurrency_ShouldReturnCorrectTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

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
    public void setBalanceCause_WithValidCurrency_ShouldSetBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

        account.setBalance(currencyMock, BigDecimal.valueOf(10), (Cause) null);
        BigDecimal expectedBalance = BigDecimal.TEN;

        assertEquals(expectedBalance, account.balance(currencyMock, (Cause) null));
    }

    @Test
    public void setBalanceCause_WithInvalidCurrency_ShouldReturnFailedTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

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
    public void setBalanceCause_WithInvalidCurrency_ShouldNotSetBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

        Currency invalidCurrency = mock(TECurrency.class);
        account.setBalance(invalidCurrency, BigDecimal.valueOf(10), (Cause) null);
        BigDecimal expectedBalance = BigDecimal.ZERO;

        assertEquals(expectedBalance, account.balance(invalidCurrency, (Cause) null));
    }

    @Test
    public void setBalanceCause_WithNegativeAmount_ShouldReturnFailedTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

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
    public void setBalanceCause_WithNegativeAmount_ShouldNotSetBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(123));

        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

        account.setBalance(currencyMock, BigDecimal.valueOf(-0.01), (Cause) null);
        BigDecimal expectedBalance = BigDecimal.valueOf(123);

        assertEquals(expectedBalance, account.balance(currencyMock, (Cause) null));
    }

    @Test
    public void depositContext_WithValidCurrency_ShouldReturnCorrectTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

        TransactionResult result = account.deposit(currencyMock, amountToDeposit, new HashSet<>());
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
    public void depositCause_WithValidCurrency_ShouldReturnCorrectTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

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
    public void depositCause_WithValidCurrency_ShouldAddToBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

        account.deposit(currencyMock, amountToDeposit, (Cause) null);
        BigDecimal expectedBalance = BigDecimal.valueOf(101);

        assertEquals(expectedBalance, account.balance(currencyMock, (Cause) null));
    }

    @Test
    public void depositCause_WithInvalidCurrency_ShouldReturnFailedTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

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
    public void depositCause_WithInvalidCurrency_ShouldNotUpdateBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

        account.deposit(mock(TECurrency.class), amountToDeposit, (Cause) null);
        BigDecimal expectedBalance = BigDecimal.valueOf(100);

        assertEquals(expectedBalance, account.balance(currencyMock, (Cause) null));
    }

    @Test
    public void depositCause_WithNegativeAmount_ShouldReturnFailedTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.valueOf(-1);
        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

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
    public void depositCause_WithNegativeAmount_ShouldNotUpdateBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.valueOf(-1);
        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

        account.deposit(currencyMock, amountToDeposit, (Cause) null);
        BigDecimal expectedBalance = BigDecimal.valueOf(100);

        assertEquals(expectedBalance, account.balance(currencyMock, (Cause) null));
    }

    @Test
    public void withdrawContext_WithValidCurrency_ShouldReturnCorrectTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

        TransactionResult result = account.withdraw(currencyMock, amountToDeposit, new HashSet<>());
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
    public void withdrawCause_WithValidCurrency_ShouldReturnCorrectTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

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
    public void withdrawCause_WithValidCurrency_ShouldSubtractFromBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

        account.withdraw(currencyMock, amountToDeposit, (Cause) null);
        BigDecimal expectedBalance = BigDecimal.valueOf(99);

        assertEquals(expectedBalance, account.balance(currencyMock, (Cause) null));
    }

    @Test
    public void withdrawCause_WithInvalidCurrency_ShouldReturnFailedTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

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
    public void withdrawCause_WithInvalidCurrency_ShouldNotUpdateBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToDeposit = BigDecimal.ONE;
        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

        account.withdraw(mock(TECurrency.class), amountToDeposit, (Cause) null);
        BigDecimal expectedBalance = BigDecimal.valueOf(100);

        assertEquals(expectedBalance, account.balance(currencyMock, (Cause) null));
    }

    @Test
    public void withdrawCause_WithNegativeAmount_ShouldReturnFailedTransactionResult() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));
        BigDecimal amountToDeposit = BigDecimal.valueOf(-1);
        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

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
    public void withdrawCause_WithNegativeAmount_ShouldNotUpdateBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));
        BigDecimal amountToDeposit = BigDecimal.valueOf(-1);
        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", balances);

        account.withdraw(currencyMock, amountToDeposit, (Cause) null);
        BigDecimal expectedBalance = BigDecimal.valueOf(100);

        assertEquals(expectedBalance, account.balance(currencyMock, (Cause) null));
    }

    @Test
    public void transferContext_WithValidData_ShouldUpdateBothBalances() {
        HashMap<Currency, BigDecimal> fromBalances = new HashMap<>();
        fromBalances.put(currencyMock, BigDecimal.valueOf(100));
        HashMap<Currency, BigDecimal> toBalances = new HashMap<>();
        toBalances.put(currencyMock, BigDecimal.valueOf(150));

        BigDecimal amountToTransfer = BigDecimal.TEN;
        TEAccount fromAccount = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", fromBalances);
        TEAccount toAccount = new TEAccount(accountData, UUID.randomUUID(), "MyUsername2", toBalances);

        fromAccount.transfer(toAccount, currencyMock, amountToTransfer, new HashSet<>());
        BigDecimal expectedFromBalance = BigDecimal.valueOf(90);
        BigDecimal expectedToBalance = BigDecimal.valueOf(160);

        assertEquals(expectedFromBalance, fromAccount.balance(currencyMock, (Cause) null));
        assertEquals(expectedToBalance, toAccount.balance(currencyMock, (Cause) null));
    }

    @Test
    public void transferCause_WithValidData_ShouldUpdateBothBalances() {
        HashMap<Currency, BigDecimal> fromBalances = new HashMap<>();
        fromBalances.put(currencyMock, BigDecimal.valueOf(100));
        HashMap<Currency, BigDecimal> toBalances = new HashMap<>();
        toBalances.put(currencyMock, BigDecimal.valueOf(150));

        BigDecimal amountToTransfer = BigDecimal.TEN;
        TEAccount fromAccount = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", fromBalances);
        TEAccount toAccount = new TEAccount(accountData, UUID.randomUUID(), "MyUsername2", toBalances);

        fromAccount.transfer(toAccount, currencyMock, amountToTransfer, (Cause) null);
        BigDecimal expectedFromBalance = BigDecimal.valueOf(90);
        BigDecimal expectedToBalance = BigDecimal.valueOf(160);

        assertEquals(expectedFromBalance, fromAccount.balance(currencyMock, (Cause) null));
        assertEquals(expectedToBalance, toAccount.balance(currencyMock, (Cause) null));
    }

    @Test
    public void transferCause_WithValidData_ShouldReturnCorrectTransferResult() {
        HashMap<Currency, BigDecimal> fromBalances = new HashMap<>();
        fromBalances.put(currencyMock, BigDecimal.valueOf(100));
        HashMap<Currency, BigDecimal> toBalances = new HashMap<>();
        toBalances.put(currencyMock, BigDecimal.valueOf(150));

        BigDecimal amountToTransfer = BigDecimal.TEN;
        TEAccount fromAccount = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", fromBalances);
        TEAccount toAccount = new TEAccount(accountData, UUID.randomUUID(), "MyUsername2", toBalances);

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
    public void transferCause_WithFromAccountMissingBalanceForCurrency_ShouldNotUpdateBalances() {
        HashMap<Currency, BigDecimal> fromBalances = new HashMap<>();
        HashMap<Currency, BigDecimal> toBalances = new HashMap<>();
        toBalances.put(currencyMock, BigDecimal.valueOf(150));

        BigDecimal amountToTransfer = BigDecimal.TEN;
        TEAccount fromAccount = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", fromBalances);
        TEAccount toAccount = new TEAccount(accountData, UUID.randomUUID(), "MyUsername2", toBalances);

        fromAccount.transfer(toAccount, currencyMock, amountToTransfer, (Cause) null);
        BigDecimal expectedFromBalance = BigDecimal.ZERO;
        BigDecimal expectedToBalance = BigDecimal.valueOf(150);

        assertEquals(expectedFromBalance, fromAccount.balance(currencyMock, (Cause) null));
        assertEquals(expectedToBalance, toAccount.balance(currencyMock, (Cause) null));
    }

    @Test
    public void transferCause_WithFromAccountMissingBalanceForCurrency_ShouldReturnCorrectTransferResult() {
        HashMap<Currency, BigDecimal> fromBalances = new HashMap<>();
        HashMap<Currency, BigDecimal> toBalances = new HashMap<>();
        toBalances.put(currencyMock, BigDecimal.valueOf(150));

        BigDecimal amountToTransfer = BigDecimal.TEN;
        TEAccount fromAccount = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", fromBalances);
        TEAccount toAccount = new TEAccount(accountData, UUID.randomUUID(), "MyUsername2", toBalances);

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
    public void transferCause_WithToAccountMissingBalanceForCurrency_ShouldNotUpdateBalances() {
        HashMap<Currency, BigDecimal> fromBalances = new HashMap<>();
        fromBalances.put(currencyMock, BigDecimal.valueOf(100));
        HashMap<Currency, BigDecimal> toBalances = new HashMap<>();

        BigDecimal amountToTransfer = BigDecimal.TEN;
        TEAccount fromAccount = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", fromBalances);
        TEAccount toAccount = new TEAccount(accountData, UUID.randomUUID(), "MyUsername2", toBalances);

        fromAccount.transfer(toAccount, currencyMock, amountToTransfer, (Cause) null);
        BigDecimal expectedFromBalance = BigDecimal.valueOf(100);
        BigDecimal expectedToBalance = BigDecimal.ZERO;

        assertEquals(expectedFromBalance, fromAccount.balance(currencyMock, (Cause) null));
        assertEquals(expectedToBalance, toAccount.balance(currencyMock, (Cause) null));
    }

    @Test
    public void transferCause_WithToAccountMissingBalanceForCurrency_ShouldReturnCorrectTransferResult() {
        HashMap<Currency, BigDecimal> fromBalances = new HashMap<>();
        fromBalances.put(currencyMock, BigDecimal.valueOf(100));
        HashMap<Currency, BigDecimal> toBalances = new HashMap<>();

        BigDecimal amountToTransfer = BigDecimal.TEN;
        TEAccount fromAccount = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", fromBalances);
        TEAccount toAccount = new TEAccount(accountData, UUID.randomUUID(), "MyUsername2", toBalances);

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
    public void transferCause_WithInsufficientFunds_ShouldNotUpdateBalances() {
        HashMap<Currency, BigDecimal> fromBalances = new HashMap<>();
        fromBalances.put(currencyMock, BigDecimal.ONE);
        HashMap<Currency, BigDecimal> toBalances = new HashMap<>();
        toBalances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToTransfer = BigDecimal.TEN;
        TEAccount fromAccount = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", fromBalances);
        TEAccount toAccount = new TEAccount(accountData, UUID.randomUUID(), "MyUsername2", toBalances);

        fromAccount.transfer(toAccount, currencyMock, amountToTransfer, (Cause) null);
        BigDecimal expectedFromBalance = BigDecimal.ONE;
        BigDecimal expectedToBalance = BigDecimal.valueOf(100);

        assertEquals(expectedFromBalance, fromAccount.balance(currencyMock, (Cause) null));
        assertEquals(expectedToBalance, toAccount.balance(currencyMock, (Cause) null));
    }

    @Test
    public void transferCause_WithInsufficientFunds_ShouldReturnCorrectTransferResult() {
        HashMap<Currency, BigDecimal> fromBalances = new HashMap<>();
        fromBalances.put(currencyMock, BigDecimal.ONE);
        HashMap<Currency, BigDecimal> toBalances = new HashMap<>();
        toBalances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToTransfer = BigDecimal.TEN;
        TEAccount fromAccount = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", fromBalances);
        TEAccount toAccount = new TEAccount(accountData, UUID.randomUUID(), "MyUsername2", toBalances);

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
    public void transferCause_WithNegativeTransferAmount_ShouldNotUpdateBalances() {
        HashMap<Currency, BigDecimal> fromBalances = new HashMap<>();
        fromBalances.put(currencyMock, BigDecimal.valueOf(100));
        HashMap<Currency, BigDecimal> toBalances = new HashMap<>();
        toBalances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToTransfer = BigDecimal.valueOf(-1);
        TEAccount fromAccount = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", fromBalances);
        TEAccount toAccount = new TEAccount(accountData, UUID.randomUUID(), "MyUsername2", toBalances);

        fromAccount.transfer(toAccount, currencyMock, amountToTransfer, (Cause) null);
        BigDecimal expectedFromBalance = BigDecimal.valueOf(100);
        BigDecimal expectedToBalance = BigDecimal.valueOf(100);

        assertEquals(expectedFromBalance, fromAccount.balance(currencyMock, (Cause) null));
        assertEquals(expectedToBalance, toAccount.balance(currencyMock, (Cause) null));
    }

    @Test
    public void transferCause_WithNegativeTransferAmount_ShouldReturnCorrectTransferResult() {
        HashMap<Currency, BigDecimal> fromBalances = new HashMap<>();
        fromBalances.put(currencyMock, BigDecimal.valueOf(100));
        HashMap<Currency, BigDecimal> toBalances = new HashMap<>();
        toBalances.put(currencyMock, BigDecimal.valueOf(100));

        BigDecimal amountToTransfer = BigDecimal.valueOf(-1);
        TEAccount fromAccount = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", fromBalances);
        TEAccount toAccount = new TEAccount(accountData, UUID.randomUUID(), "MyUsername2", toBalances);

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
    public void resetBalancesContext_ShouldThrowUnsupportedException() {
        TEAccount sut = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", null);

        assertThrows(
            UnsupportedOperationException.class,
            () -> sut.resetBalances(new HashSet<>())
        );
    }

    @Test
    public void resetBalancesCause_ShouldThrowUnsupportedException() {
        TEAccount sut = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", null);

        assertThrows(
            UnsupportedOperationException.class,
            () -> sut.resetBalances((Cause) null)
        );
    }

    @Test
    public void resetBalanceContext_ShouldThrowUnsupportedException() {
        TEAccount sut = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", null);

        assertThrows(
            UnsupportedOperationException.class,
            () -> sut.resetBalance(currencyMock, new HashSet<>())
        );
    }

    @Test
    public void resetBalanceCause_ShouldThrowUnsupportedException() {
        TEAccount sut = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", null);

        assertThrows(
            UnsupportedOperationException.class,
            () -> sut.resetBalance(currencyMock, (Cause) null)
        );
    }

    @Test
    public void equals_WithSameObject_ShouldReturnTrue() {
        TEAccount sut = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", null);

        boolean result = sut.equals(sut);

        assertTrue(result);
    }

    @Test
    public void equals_WithEqualObjects_ShouldReturnTrue() {
        UUID uuid = UUID.randomUUID();
        TEAccount sut = new TEAccount(accountData, uuid, "MyUsername", null);
        TEAccount account = new TEAccount(accountData, uuid, "MyUsername", null);

        boolean result = sut.equals(account);

        assertTrue(result);
    }

    @Test
    public void equals_WithNullObject_ShouldReturnFalse() {
        TEAccount sut = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", null);

        boolean result = sut.equals(null);

        assertFalse(result);
    }

    @Test
    public void equals_WithDifferentObjectClass_ShouldReturnFalse() {
        TEAccount sut = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", null);

        boolean result = sut.equals("123");

        assertFalse(result);
    }

    @Test
    public void equals_WithDifferentUserId_ShouldReturnFalse() {
        TEAccount sut = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", null);
        TEAccount account = new TEAccount(accountData, UUID.randomUUID(), "MyUsername", null);

        boolean result = sut.equals(account);

        assertFalse(result);
    }

    @Test
    public void equals_WithDifferentDisplayName_ShouldReturnFalse() {
        UUID uuid = UUID.randomUUID();
        TEAccount sut = new TEAccount(accountData, uuid, "MyUsername", null);
        TEAccount account = new TEAccount(accountData, uuid, "AnotherUsername", null);

        boolean result = sut.equals(account);

        assertFalse(result);
    }

    @Test
    public void equals_WithDifferentBalances_ShouldReturnFalse() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(currencyMock, BigDecimal.valueOf(100));

        UUID uuid = UUID.randomUUID();
        TEAccount sut = new TEAccount(accountData, uuid, "MyUsername", balances);
        TEAccount account = new TEAccount(accountData, uuid, "MyUsername", null);

        boolean result = sut.equals(account);

        assertFalse(result);
    }

    @Test
    public void hashCode_ShouldReturnHashCode() {
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        TEAccount sut = new TEAccount(accountData, uuid, "MyUsername", null);

        int result = sut.hashCode();
        int expectedResult = -872783395;

        assertEquals(expectedResult, result);
    }
}
