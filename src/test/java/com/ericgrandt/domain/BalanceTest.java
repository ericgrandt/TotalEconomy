package com.ericgrandt.domain;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("Unit")
public class BalanceTest {
    UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Test
    public void getUserId_ShouldReturnUserId() {
        Balance sut = new Balance(uuid, 1, BigDecimal.ONE);

        UUID result = sut.getUserId();
        UUID expectedResult = uuid;

        assertEquals(expectedResult, result);
    }

    @Test
    public void getCurrencyId_ShouldReturnCurrencyId() {
        Balance sut = new Balance(uuid, 1, BigDecimal.ONE);

        int result = sut.getCurrencyId();
        int expectedResult = 1;

        assertEquals(expectedResult, result);
    }

    @Test
    public void getBalance_ShouldReturnBalance() {
        Balance sut = new Balance(uuid, 1, BigDecimal.ONE);

        BigDecimal result = sut.getBalance();
        BigDecimal expectedResult = BigDecimal.ONE;

        assertEquals(expectedResult, result);
    }

    @Test
    public void setBalance_ShouldSetBalance() {
        Balance sut = new Balance(uuid, 1, BigDecimal.ONE);

        sut.setBalance(BigDecimal.TEN);

        BigDecimal result =  sut.getBalance();
        BigDecimal expectedResult = BigDecimal.TEN;

        assertEquals(expectedResult, result);
    }

    @Test
    public void equals_WithSameObject_ShouldReturnTrue() {
        Balance sut = new Balance(uuid, 1, BigDecimal.ONE);

        boolean result = sut.equals(sut);

        assertTrue(result);
    }

    @Test
    public void equals_WithEqualObjects_ShouldReturnTrue() {
        Balance sut = new Balance(uuid, 1, BigDecimal.ONE);
        Balance balance = new Balance(uuid, 1, BigDecimal.ONE);

        boolean result = sut.equals(balance);

        assertTrue(result);
    }

    @Test
    public void equals_WithDifferentObjectClass_ShouldReturnFalse() {
        Balance sut = new Balance(uuid, 1, BigDecimal.ONE);

        boolean result = sut.equals("123");

        assertFalse(result);
    }

    @Test
    public void equals_WithDifferentCurrencyId_ShouldReturnFalse() {
        Balance sut = new Balance(uuid, 1, BigDecimal.ONE);
        Balance balance = new Balance(uuid, 2, BigDecimal.ONE);

        boolean result = sut.equals(balance);

        assertFalse(result);
    }

    @Test
    public void equals_WithDifferentUserId_ShouldReturnFalse() {
        Balance sut = new Balance(uuid, 1, BigDecimal.ONE);
        Balance balance = new Balance(UUID.randomUUID(), 1, BigDecimal.ONE);

        boolean result = sut.equals(balance);

        assertFalse(result);
    }

    @Test
    public void equals_WithDifferentBalance_ShouldReturnFalse() {
        Balance sut = new Balance(uuid, 1, BigDecimal.ONE);
        Balance balance = new Balance(uuid, 2, BigDecimal.TEN);

        boolean result = sut.equals(balance);

        assertFalse(result);
    }

    @Test
    public void hashCode_ShouldReturnHashCode() {
        Balance sut = new Balance(uuid, 1, BigDecimal.ONE);

        int result = sut.hashCode();
        int expectedResult = 29853;

        assertEquals(expectedResult, result);
    }
}
