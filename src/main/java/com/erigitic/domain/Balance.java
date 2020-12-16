package com.erigitic.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class Balance {
    public String userId;
    public int currencyId;
    public BigDecimal balance;

    public Balance(String userId, int currencyId, BigDecimal balance) {
        this.userId = userId;
        this.currencyId = currencyId;
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
