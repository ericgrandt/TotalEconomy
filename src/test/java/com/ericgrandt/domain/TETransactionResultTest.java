package com.ericgrandt.domain;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionType;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@Tag("Unit")
@ExtendWith(MockitoExtension.class)
public class TETransactionResultTest {
    @Mock
    TEAccount accountMock;

    @Mock
    TECurrency currencyMock;

    @Mock
    ResultType resultTypeMock;

    @Mock
    TransactionType transactionTypeMock;

    @Test
    public void account_ShouldReturnAccount() {
        TETransactionResult sut = new TETransactionResult(
            accountMock,
            currencyMock,
            BigDecimal.ONE,
            new HashSet<>(),
            resultTypeMock,
            transactionTypeMock
        );

        Account result = sut.account();

        assertEquals(accountMock, result);
    }

    @Test
    public void currency_ShouldReturnCurrency() {
        TETransactionResult sut = new TETransactionResult(
            accountMock,
            currencyMock,
            BigDecimal.ONE,
            new HashSet<>(),
            resultTypeMock,
            transactionTypeMock
        );

        Currency result = sut.currency();

        assertEquals(currencyMock, result);
    }

    @Test
    public void amount_ShouldReturnAmount() {
        TETransactionResult sut = new TETransactionResult(
            accountMock,
            currencyMock,
            BigDecimal.ONE,
            new HashSet<>(),
            resultTypeMock,
            transactionTypeMock
        );

        BigDecimal result = sut.amount();
        BigDecimal expectedResult = BigDecimal.ONE;

        assertEquals(expectedResult, result);
    }

    @Test
    public void context_ShouldReturnContexts() {
        Set<Context> contexts = new HashSet<>();
        contexts.add(new Context("test-key", "test-value"));
        TETransactionResult sut = new TETransactionResult(
            accountMock,
            currencyMock,
            BigDecimal.ONE,
            contexts,
            resultTypeMock,
            transactionTypeMock
        );

        Set<Context> result = sut.contexts();

        assertEquals(contexts, result);
    }

    @Test
    public void result_ShouldReturnResult() {
        TETransactionResult sut = new TETransactionResult(
            accountMock,
            currencyMock,
            BigDecimal.ONE,
            new HashSet<>(),
            resultTypeMock,
            transactionTypeMock
        );

        ResultType result = sut.result();

        assertEquals(resultTypeMock, result);
    }

    @Test
    public void type_ShouldReturnType() {
        TETransactionResult sut = new TETransactionResult(
            accountMock,
            currencyMock,
            BigDecimal.ONE,
            new HashSet<>(),
            resultTypeMock,
            transactionTypeMock
        );

        TransactionType result = sut.type();

        assertEquals(transactionTypeMock, result);
    }

    @Test
    public void equals_WithSameObject_ShouldReturnTrue() {
        TETransactionResult sut = new TETransactionResult(
            accountMock,
            currencyMock,
            BigDecimal.ONE,
            new HashSet<>(),
            resultTypeMock,
            transactionTypeMock
        );

        boolean result = sut.equals(sut);

        assertTrue(result);
    }

    @Test
    public void equals_WithEqualObjects_ShouldReturnTrue() {
        TETransactionResult sut = new TETransactionResult(
            accountMock,
            currencyMock,
            BigDecimal.ONE,
            new HashSet<>(),
            resultTypeMock,
            transactionTypeMock
        );
        TETransactionResult transactionResult = new TETransactionResult(
            accountMock,
            currencyMock,
            BigDecimal.ONE,
            new HashSet<>(),
            resultTypeMock,
            transactionTypeMock
        );

        boolean result = sut.equals(transactionResult);

        assertTrue(result);
    }

    @Test
    public void equals_WithNullObject_ShouldReturnFalse() {
        TETransactionResult sut = new TETransactionResult(
            accountMock,
            currencyMock,
            BigDecimal.ONE,
            new HashSet<>(),
            resultTypeMock,
            transactionTypeMock
        );

        boolean result = sut.equals(null);

        assertFalse(result);
    }

    @Test
    public void equals_WithDifferentObjectClass_ShouldReturnFalse() {
        TETransactionResult sut = new TETransactionResult(
            accountMock,
            currencyMock,
            BigDecimal.ONE,
            new HashSet<>(),
            resultTypeMock,
            transactionTypeMock
        );

        boolean result = sut.equals("123");

        assertFalse(result);
    }

    @Test
    public void equals_WithDifferentAccount_ShouldReturnFalse() {
        TETransactionResult sut = new TETransactionResult(
            accountMock,
            currencyMock,
            BigDecimal.ONE,
            new HashSet<>(),
            resultTypeMock,
            transactionTypeMock
        );
        TETransactionResult transactionResult = new TETransactionResult(
            mock(TEAccount.class),
            currencyMock,
            BigDecimal.ONE,
            new HashSet<>(),
            resultTypeMock,
            transactionTypeMock
        );

        boolean result = sut.equals(transactionResult);

        assertFalse(result);
    }

    @Test
    public void equals_WithDifferentCurrency_ShouldReturnFalse() {
        TETransactionResult sut = new TETransactionResult(
            accountMock,
            currencyMock,
            BigDecimal.ONE,
            new HashSet<>(),
            resultTypeMock,
            transactionTypeMock
        );
        TETransactionResult transactionResult = new TETransactionResult(
            accountMock,
            mock(TECurrency.class),
            BigDecimal.ONE,
            new HashSet<>(),
            resultTypeMock,
            transactionTypeMock
        );

        boolean result = sut.equals(transactionResult);

        assertFalse(result);
    }

    @Test
    public void equals_WithDifferentAmount_ShouldReturnFalse() {
        TETransactionResult sut = new TETransactionResult(
            accountMock,
            currencyMock,
            BigDecimal.ONE,
            new HashSet<>(),
            resultTypeMock,
            transactionTypeMock
        );
        TETransactionResult transactionResult = new TETransactionResult(
            accountMock,
            currencyMock,
            BigDecimal.TEN,
            new HashSet<>(),
            resultTypeMock,
            transactionTypeMock
        );

        boolean result = sut.equals(transactionResult);

        assertFalse(result);
    }

    @Test
    public void equals_WithDifferentContexts_ShouldReturnFalse() {
        TETransactionResult sut = new TETransactionResult(
            accountMock,
            currencyMock,
            BigDecimal.ONE,
            new HashSet<>(),
            resultTypeMock,
            transactionTypeMock
        );
        Set<Context> contexts = new HashSet<>();
        contexts.add(new Context("test-key", "test-value"));
        TETransactionResult transactionResult = new TETransactionResult(
            accountMock,
            currencyMock,
            BigDecimal.ONE,
            contexts,
            resultTypeMock,
            transactionTypeMock
        );

        boolean result = sut.equals(transactionResult);

        assertFalse(result);
    }

    @Test
    public void equals_WithDifferentResultType_ShouldReturnFalse() {
        TETransactionResult sut = new TETransactionResult(
            accountMock,
            currencyMock,
            BigDecimal.ONE,
            new HashSet<>(),
            resultTypeMock,
            transactionTypeMock
        );
        TETransactionResult transactionResult = new TETransactionResult(
            accountMock,
            currencyMock,
            BigDecimal.TEN,
            new HashSet<>(),
            mock(ResultType.class),
            transactionTypeMock
        );

        boolean result = sut.equals(transactionResult);

        assertFalse(result);
    }

    @Test
    public void equals_WithDifferentTransactionType_ShouldReturnFalse() {
        TETransactionResult sut = new TETransactionResult(
            accountMock,
            currencyMock,
            BigDecimal.ONE,
            new HashSet<>(),
            resultTypeMock,
            transactionTypeMock
        );
        TETransactionResult transactionResult = new TETransactionResult(
            accountMock,
            currencyMock,
            BigDecimal.TEN,
            new HashSet<>(),
            resultTypeMock,
            mock(TransactionType.class)
        );

        boolean result = sut.equals(transactionResult);

        assertFalse(result);
    }
}
