package com.ericgrandt.totaleconomy.services;

import com.ericgrandt.totaleconomy.common.data.BalanceData;
import com.ericgrandt.totaleconomy.models.TransferResult;
import com.ericgrandt.totaleconomy.models.TransferResult.ResultType;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;

public class BalanceService {
    private final BalanceData balanceData;

    public BalanceService(BalanceData balanceData) {
        this.balanceData = balanceData;
    }

    public TransferResult transfer(UUID fromPlayer, UUID toPlayer, double amount) throws SQLException {
        if (amount <= 0) {
            return new TransferResult(ResultType.FAILURE, "Amount must be greater than zero");
        }

        BigDecimal fromBalance = balanceData.getBalance(fromPlayer, 1);
        if (fromBalance.compareTo(BigDecimal.valueOf(amount)) < 0) {
            return new TransferResult(ResultType.FAILURE, "Insufficient funds");
        }

        balanceData.transfer(fromPlayer, toPlayer, 1, amount);

        return new TransferResult(ResultType.SUCCESS, "");
    }
}
