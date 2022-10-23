package com.ericgrandt.data.dto;

import java.math.BigDecimal;

public class BalanceDto {
    private final String id;
    private final String accountId;
    private final int currencyId;
    private final BigDecimal balance;

    public BalanceDto(String id, String accountId, int currencyId, BigDecimal balance) {
        this.id = id;
        this.accountId = accountId;
        this.currencyId = currencyId;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BalanceDto that = (BalanceDto) o;

        if (currencyId != that.currencyId) {
            return false;
        }

        if (!id.equals(that.id)) {
            return false;
        }

        if (!accountId.equals(that.accountId)) {
            return false;
        }

        return balance.equals(that.balance);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + accountId.hashCode();
        result = 31 * result + currencyId;
        result = 31 * result + balance.hashCode();
        return result;
    }
}
