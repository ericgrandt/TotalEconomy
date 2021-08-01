package com.ericgrandt.services;

import com.ericgrandt.data.AccountData;
import com.ericgrandt.domain.Balance;
import com.ericgrandt.domain.TEAccount;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class AccountService {
    private final AccountData accountData;

    public AccountService(AccountData accountData) {
        this.accountData = accountData;
    }

    public void addAccount(TEAccount account) {
        accountData.addAccount(account);
    }

    public TEAccount getAccount(UUID userId) {
        return accountData.getAccount(userId);
    }

    public Balance getBalance(UUID userId, int currencyId) {
        return accountData.getBalance(userId, currencyId);
    }

    public List<Balance> getBalances(UUID userId) {
        return accountData.getBalances(userId);
    }

    public Balance setBalance(Balance balance) {
        if (balance.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance must be greater than or equal to zero");
        }

        return accountData.setBalance(balance);
    }

    public boolean setTransferBalances(Balance from, Balance to) {
        if (from.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("From balance cannot be negative");
        } else if (to.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("To balance cannot be negative");
        }

        if (from.getCurrencyId() != to.getCurrencyId()) {
            throw new IllegalArgumentException("Currency ids do not match");
        }

        return accountData.setTransferBalances(from, to);
    }
}
