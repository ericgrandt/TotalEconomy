package com.erigitic.domain;

import com.erigitic.economy.TECurrency;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Tag("Unit")
@ExtendWith(MockitoExtension.class)
public class AccountTest {
    @Mock
    TECurrency currencyMock;

    @Test
    public void getDisplayName_ShouldReturnTheCorrectDisplayName() {
        String uuid = UUID.randomUUID().toString();
        Account account = new Account(uuid, "MyUsername", null);

        Text result = account.getDisplayName();
        Text expectedResult = Text.of("MyUsername");

        assertEquals(expectedResult, result);
    }

    @Test
    public void getDefaultBalance_ShouldReturnZero() {
        String uuid = UUID.randomUUID().toString();
        Account account = new Account(uuid, "MyUsername", null);

        BigDecimal result = account.getDefaultBalance(currencyMock);
        BigDecimal expectedResult = BigDecimal.ZERO;

        assertEquals(expectedResult, result);
    }

    @Test
    public void hasBalance_WithExistingBalance_ShouldReturnTrue() {
        String uuid = UUID.randomUUID().toString();
        List<Balance> balances = Arrays.asList(
            new Balance(uuid, 1, BigDecimal.valueOf(123)),
            new Balance(uuid, 2, BigDecimal.valueOf(456))
        );
        Account account = new Account(uuid, "MyUsername", balances);
        when(currencyMock.getId()).thenReturn("1");

        boolean result = account.hasBalance(currencyMock);

        assertTrue(result);
    }

    @Test
    public void hasBalance_WithNonExistingBalance_ShouldReturnFalse() {
        String uuid = UUID.randomUUID().toString();
        List<Balance> balances = Collections.singletonList(
            new Balance(uuid, 1, BigDecimal.valueOf(123))
        );
        Account account = new Account(uuid, "MyUsername", balances);
        when(currencyMock.getId()).thenReturn("123");

        boolean result = account.hasBalance(currencyMock);

        assertFalse(result);
    }

    @Test
    public void getBalance_WithValidCurrency_ShouldReturnBigDecimal() {
        String uuid = UUID.randomUUID().toString();
        List<Balance> balances = Collections.singletonList(
            new Balance(uuid, 1, BigDecimal.valueOf(123))
        );
        Account account = new Account(uuid, "MyUsername", balances);
        when(currencyMock.getId()).thenReturn("1");

        BigDecimal result = account.getBalance(currencyMock);
        BigDecimal expectedResult = BigDecimal.valueOf(123);

        assertEquals(expectedResult, result);
    }

    @Test
    public void getBalance_WithInvalidCurrency_ShouldReturnZero() {
        String uuid = UUID.randomUUID().toString();
        List<Balance> balances = Collections.singletonList(
            new Balance(uuid, 1, BigDecimal.valueOf(123))
        );
        Account account = new Account(uuid, "MyUsername", balances);
        when(currencyMock.getId()).thenReturn("123");

        BigDecimal result = account.getBalance(currencyMock);
        BigDecimal expectedResult = BigDecimal.valueOf(0);

        assertEquals(expectedResult, result);
    }

    @Test
    public void setBalance_WithValidCurrency_ShouldReturnCorrectTransactionResultAndSetBalance() {
        String uuid = UUID.randomUUID().toString();
        List<Balance> balances = Collections.singletonList(
            new Balance(uuid, 1, BigDecimal.valueOf(123))
        );
        Account account = new Account(uuid, "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());
        when(currencyMock.getId()).thenReturn("1");

        TransactionResult result = account.setBalance(currencyMock, BigDecimal.valueOf(10), cause);

        assertEquals(BigDecimal.valueOf(10), result.getAmount());
        assertEquals(ResultType.SUCCESS, result.getResult());
        assertEquals(TransactionTypes.DEPOSIT, result.getType());
        assertEquals(BigDecimal.valueOf(10), balances.get(0).balance);
    }

    @Test
    public void setBalance_WithInvalidCurrency_ShouldReturnFailedTransactionResultAndNotSetBalance() {
        String uuid = UUID.randomUUID().toString();
        List<Balance> balances = Collections.singletonList(
            new Balance(uuid, 1, BigDecimal.valueOf(123))
        );
        Account account = new Account(uuid, "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());
        when(currencyMock.getId()).thenReturn("123");

        TransactionResult result = account.setBalance(currencyMock, BigDecimal.valueOf(10), cause);

        assertEquals(BigDecimal.valueOf(10), result.getAmount());
        assertEquals(ResultType.FAILED, result.getResult());
        assertEquals(TransactionTypes.DEPOSIT, result.getType());
        assertEquals(BigDecimal.valueOf(123), balances.get(0).balance);
    }

    @Test
    public void setBalance_WithAmountLessThan0_ShouldReturnFailedTransactionResultAndNotSetBalance() {
        String uuid = UUID.randomUUID().toString();
        List<Balance> balances = Collections.singletonList(
            new Balance(uuid, 1, BigDecimal.valueOf(123))
        );
        Account account = new Account(uuid, "MyUsername", balances);
        Cause cause = Cause.builder()
            .append(this)
            .build(EventContext.empty());

        TransactionResult result = account.setBalance(currencyMock, BigDecimal.valueOf(-1), cause);

        assertEquals(BigDecimal.valueOf(-1), result.getAmount());
        assertEquals(ResultType.FAILED, result.getResult());
        assertEquals(TransactionTypes.DEPOSIT, result.getType());
        assertEquals(BigDecimal.valueOf(123), balances.get(0).balance);
    }

    @Test
    public void addBalance_ShouldAddNewBalance() {
        UUID uuid = UUID.randomUUID();
        Account account = new Account(uuid.toString(), "MyUsername", new ArrayList<>());

        account.addBalance(new Balance(uuid.toString(), 1, BigDecimal.ZERO));

        assertEquals(1, account.balances.size());
    }
}
