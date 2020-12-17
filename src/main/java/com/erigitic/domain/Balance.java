package com.erigitic.domain;

import com.google.common.base.Objects;

import java.math.BigDecimal;

public class Balance {
    private final String userId;
    private final int currencyId;
    private BigDecimal balance;

    public Balance(String userId, int currencyId, BigDecimal balance) {
        this.userId = userId;
        this.currencyId = currencyId;
        this.balance = balance;
    }

    public String getUserId() {
        return userId;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Balance other = (Balance) o;
        return currencyId == other.currencyId &&
            Objects.equal(userId, other.userId) &&
            Objects.equal(balance, other.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId, currencyId, balance);
    }
}
