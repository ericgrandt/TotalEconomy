package com.ericgrandt.commands.models;

import com.ericgrandt.domain.TEAccount;

public class PayCommandAccounts {
    private final TEAccount fromAccount;
    private final TEAccount toAccount;

    public PayCommandAccounts(TEAccount fromAccount, TEAccount toAccount) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
    }

    public TEAccount getFromAccount() {
        return fromAccount;
    }

    public TEAccount getToAccount() {
        return toAccount;
    }
}
