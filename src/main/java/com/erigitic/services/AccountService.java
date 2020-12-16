package com.erigitic.services;

import com.erigitic.data.AccountData;
import com.erigitic.domain.Account;
import com.erigitic.domain.Balance;

import java.math.BigDecimal;
import java.util.List;

public class AccountService {
    private final AccountData accountData;

    public AccountService(AccountData accountData) {
        this.accountData = accountData;
    }

    public void addAccount(Account account) {
        accountData.addAccount(account);
    }

    public Account getAccount(String userId) {
        return accountData.getAccount(userId);
    }

    public Balance getBalance(String userId, int currencyId) {
        return accountData.getBalance(userId, currencyId);
    }

    public List<Balance> getBalances(String userId) {
        return accountData.getBalances(userId);
    }

    public Balance setBalance(Balance balance) {
        if (balance.balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance must be greater than or equal to zero");
        }

        return accountData.setBalance(balance);
    }

    // TODO: transfer() should check that the "from" balance has enough money
    // TODO: transfer() should check that the currency exists for both users
}
