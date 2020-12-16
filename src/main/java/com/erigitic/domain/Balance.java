package com.erigitic.domain;

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

    public boolean equals(Object obj) {
        if (!(obj instanceof Balance)) {
            return false;
        }

        Balance other = (Balance) obj;
        return this.userId.equals(other.userId)
            && this.currencyId == other.currencyId
            && this.balance.equals(other.balance);
    }
}
