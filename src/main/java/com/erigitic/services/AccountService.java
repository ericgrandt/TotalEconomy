package com.erigitic.services;

import com.erigitic.data.AccountData;
import com.erigitic.domain.Balance;
import com.erigitic.domain.TEAccount;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.spongepowered.api.service.economy.account.Account;

public class AccountService {
    private final AccountData accountData;

    public AccountService(AccountData accountData) {
        this.accountData = accountData;
    }

    public void addAccount(TEAccount account) {
        accountData.addAccount(account);
    }

    public Account getAccount(UUID userId) {
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

    public boolean transfer(Balance from, Balance to, BigDecimal amount) {
        if (from.getBalance().subtract(to.getBalance()).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Transfer amount is more than the user has");
        }

        if (from.getCurrencyId() != to.getCurrencyId()) {
            throw new IllegalArgumentException("Currency ids do not match");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        return accountData.setTransferBalances(from, to);
    }
}
