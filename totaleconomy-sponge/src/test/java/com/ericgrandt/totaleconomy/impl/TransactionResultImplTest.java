package com.ericgrandt.totaleconomy.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ericgrandt.totaleconomy.helpers.TransactionTypeMock;
import java.math.BigDecimal;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionType;

@ExtendWith(MockitoExtension.class)
public class TransactionResultImplTest {
    private final UniqueAccountImpl account = new UniqueAccountImpl(null, null, null, null, null);
    private final CurrencyImpl currency = new CurrencyImpl(null);
    private final TransactionType type = new TransactionTypeMock();

    private final TransactionResultImpl sut = new TransactionResultImpl(
        account,
        currency,
        BigDecimal.TEN,
        ResultType.SUCCESS,
        type
    );

    @Test
    @Tag("Unit")
    public void account_ShouldReturnAccount() {
        // Act
        Account actual = sut.account();
        Account expected = account;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void currency_ShouldReturnCurrency() {
        // Act
        Currency actual = sut.currency();
        Currency expected = currency;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void amount_ShouldReturnAmount() {
        // Act
        BigDecimal actual = sut.amount();
        BigDecimal expected = BigDecimal.TEN;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void result_ShouldReturnType() {
        // Act
        ResultType actual = sut.result();
        ResultType expected = ResultType.SUCCESS;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void type_ShouldReturnType() {
        // Act
        TransactionType actual = sut.type();
        TransactionType expected = type;

        // Assert
        assertEquals(expected, actual);
    }
}
