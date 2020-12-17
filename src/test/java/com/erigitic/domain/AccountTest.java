package com.erigitic.domain;

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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@Tag("Unit")
@ExtendWith(MockitoExtension.class)
public class AccountTest {
    @Mock
    TECurrency currencyMock;

    @Test
    public void getDisplayName_ShouldReturnTheCorrectDisplayName() {
        String uuid = UUID.randomUUID().toString();
        TEAccount account = new TEAccount(uuid, "MyUsername", null);

        Text result = account.getDisplayName();
        Text expectedResult = Text.of("MyUsername");

        assertEquals(expectedResult, result);
    }

    @Test
    public void getDefaultBalance_ShouldReturnZero() {
        String uuid = UUID.randomUUID().toString();
        TEAccount account = new TEAccount(uuid, "MyUsername", null);

        BigDecimal result = account.getDefaultBalance(currencyMock);
        BigDecimal expectedResult = BigDecimal.ZERO;

        assertEquals(expectedResult, result);
    }

    @Test
    public void hasBalance_WithExistingBalance_ShouldReturnTrue() {
        String uuid = UUID.randomUUID().toString();
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(
            currencyMock,
            BigDecimal.valueOf(123)
        );
        TEAccount account = new TEAccount("random-uuid", "MyUsername", balances);

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
        TEAccount account = new TEAccount("random-uuid", "MyUsername", balances);

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
        TEAccount account = new TEAccount("random-uuid", "MyUsername", balances);

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
        TEAccount account = new TEAccount("random-uuid", "MyUsername", balances);

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
        TEAccount account = new TEAccount("random-uuid", "MyUsername", balances);
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
        TEAccount account = new TEAccount("random-uuid", "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        TransactionResult result = account.setBalance(mock(TECurrency.class), BigDecimal.valueOf(10), cause);

        assertEquals(BigDecimal.valueOf(10), result.getAmount());
        assertEquals(ResultType.FAILED, result.getResult());
        assertEquals(TransactionTypes.DEPOSIT, result.getType());
        assertEquals(BigDecimal.valueOf(123), balances.get(currencyMock));
    }

    @Test
    public void setBalance_WithAmountLessThan0_ShouldReturnFailedTransactionResultAndNotSetBalance() {
        HashMap<Currency, BigDecimal> balances = new HashMap<>();
        balances.put(
            currencyMock,
            BigDecimal.valueOf(123)
        );
        TEAccount account = new TEAccount("random-uuid", "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        TransactionResult result = account.setBalance(mock(TECurrency.class), BigDecimal.valueOf(-0.01), cause);

        assertEquals(BigDecimal.valueOf(-0.01), result.getAmount());
        assertEquals(ResultType.FAILED, result.getResult());
        assertEquals(TransactionTypes.DEPOSIT, result.getType());
        assertEquals(BigDecimal.valueOf(123), balances.get(currencyMock));
    }

    @Test
    public void addBalance_ShouldAddNewBalance() {
        TEAccount account = new TEAccount("random-uuid", "MyUsername", new HashMap<>());
        TECurrency currency = mock(TECurrency.class);

        account.addBalance(currency, BigDecimal.ZERO);

        assertEquals(1, account.getBalances().size());
    }

    @Test
    public void blah() {
        System.out.println(TEAccount.class.getClassLoader());
        System.out.println(Account.class.getInterfaces()[0].getClassLoader());
    }
}
