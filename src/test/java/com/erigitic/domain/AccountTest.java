package com.erigitic.domain;

import com.erigitic.economy.TECurrency;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
        Account account = new Account(UUID.randomUUID(), "MyUsername", null);

        Text result = account.getDisplayName();
        Text expectedResult = Text.of("MyUsername");

        assertEquals(result, expectedResult);
    }

    @Test
    public void getDefaultBalance_ShouldReturnZero() {
        Account account = new Account(UUID.randomUUID(), "MyUsername", null);

        BigDecimal result = account.getDefaultBalance(currencyMock);
        BigDecimal expectedResult = BigDecimal.ZERO;

        assertEquals(result, expectedResult);
    }

    @Test
    public void hasBalance_WithExistingBalance_ShouldReturnTrue() {
        UUID uuid = UUID.randomUUID();
        List<Balance> balances = Arrays.asList(
            new Balance(uuid.toString(), 1, BigDecimal.valueOf(123)),
            new Balance(uuid.toString(), 2, BigDecimal.valueOf(456))
        );
        Account account = new Account(uuid, "MyUsername", balances);
        when(currencyMock.getId()).thenReturn("1");

        boolean result = account.hasBalance(currencyMock);

        assertTrue(result);
    }

    @Test
    public void hasBalance_WithNonExistingBalance_ShouldReturnFalse() {
        UUID uuid = UUID.randomUUID();
        List<Balance> balances = Collections.singletonList(
            new Balance(uuid.toString(), 1, BigDecimal.valueOf(123))
        );
        Account account = new Account(uuid, "MyUsername", balances);
        when(currencyMock.getId()).thenReturn("123");

        boolean result = account.hasBalance(currencyMock);

        assertFalse(result);
    }

    @Test
    public void getBalance_WithValidCurrency_ShouldReturnBigDecimal() {
        UUID uuid = UUID.randomUUID();
        List<Balance> balances = Collections.singletonList(
            new Balance(uuid.toString(), 1, BigDecimal.valueOf(123))
        );
        Account account = new Account(uuid, "MyUsername", balances);
        when(currencyMock.getId()).thenReturn("1");

        BigDecimal result = account.getBalance(currencyMock);
        BigDecimal expectedResult = BigDecimal.valueOf(123);

        assertEquals(result, expectedResult);
    }

    @Test
    public void getBalance_WithInvalidCurrency_ShouldReturnZero() {
        UUID uuid = UUID.randomUUID();
        List<Balance> balances = Collections.singletonList(
            new Balance(uuid.toString(), 1, BigDecimal.valueOf(123))
        );
        Account account = new Account(uuid, "MyUsername", balances);
        when(currencyMock.getId()).thenReturn("123");

        BigDecimal result = account.getBalance(currencyMock);
        BigDecimal expectedResult = BigDecimal.valueOf(0);

        assertEquals(result, expectedResult);
    }
}
