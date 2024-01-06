package com.ericgrandt.totaleconomy.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.models.TransferResult;
import com.ericgrandt.totaleconomy.models.TransferResult.ResultType;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class BalanceServiceTest {
    @Test
    @Tag("Unit")
    public void transfer_WithSuccess_ShouldReturnSuccessfulTransferResult() throws SQLException {
        // Arrange
        BalanceData balanceDataMock = mock(BalanceData.class);
        when(balanceDataMock.getBalance(any(UUID.class), anyInt())).thenReturn(BigDecimal.TEN);

        BalanceService sut = new BalanceService(balanceDataMock);

        UUID fromPlayer = UUID.randomUUID();
        UUID toPlayer = UUID.randomUUID();
        double amount = 10.00;

        // Act
        TransferResult actual = sut.transfer(fromPlayer, toPlayer, amount);
        TransferResult expected = new TransferResult(ResultType.SUCCESS, "");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void transfer_WithFromPlayerHavingInsufficientFunds_ShouldReturnFailedTransferResult() throws SQLException {
        // Arrange
        BalanceData balanceDataMock = mock(BalanceData.class);
        when(balanceDataMock.getBalance(any(UUID.class), anyInt())).thenReturn(BigDecimal.ONE);

        BalanceService sut = new BalanceService(balanceDataMock);

        UUID fromPlayer = UUID.randomUUID();
        UUID toPlayer = UUID.randomUUID();
        double amount = 10.00;

        // Act
        TransferResult actual = sut.transfer(fromPlayer, toPlayer, amount);
        TransferResult expected = new TransferResult(ResultType.FAILURE, "Insufficient funds");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void transfer_WithAmountBeingLessThanZero_ShouldReturnFailedTransferResult() throws SQLException {
        // Arrange
        BalanceData balanceDataMock = mock(BalanceData.class);

        BalanceService sut = new BalanceService(balanceDataMock);

        UUID fromPlayer = UUID.randomUUID();
        UUID toPlayer = UUID.randomUUID();
        double amount = -1.00;

        // Act
        TransferResult actual = sut.transfer(fromPlayer, toPlayer, amount);
        TransferResult expected = new TransferResult(ResultType.FAILURE, "Amount must be greater than zero");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void transfer_WithAmountBeingEqualToZero_ShouldReturnFailedTransferResult() throws SQLException {
        // Arrange
        BalanceData balanceDataMock = mock(BalanceData.class);

        BalanceService sut = new BalanceService(balanceDataMock);

        UUID fromPlayer = UUID.randomUUID();
        UUID toPlayer = UUID.randomUUID();
        double amount = 0.00;

        // Act
        TransferResult actual = sut.transfer(fromPlayer, toPlayer, amount);
        TransferResult expected = new TransferResult(ResultType.FAILURE, "Amount must be greater than zero");

        // Assert
        assertEquals(expected, actual);
    }
}
