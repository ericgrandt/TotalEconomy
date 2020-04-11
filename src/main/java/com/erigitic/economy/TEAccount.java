package com.erigitic.economy;

import com.erigitic.TotalEconomy;
import com.erigitic.data.AccountData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransferResult;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class TEAccount implements UniqueAccount {
    private TotalEconomy plugin;
    private AccountData accountData;
    private UUID uuid;

    public TEAccount(UUID uuid) {
        this.uuid = uuid;

        plugin = TotalEconomy.getPlugin();
        accountData = new AccountData(plugin.getDatabase());
    }

    @Override
    public Text getDisplayName() {
        Optional<Player> playerOpt = Sponge.getServer().getPlayer(uuid);

        return playerOpt.isPresent() ? Text.of(playerOpt.get().getName()) : Text.EMPTY;
    }

    @Override
    public BigDecimal getDefaultBalance(Currency currency) {
        return BigDecimal.ZERO;
    }

    @Override
    public boolean hasBalance(Currency currency, Set<Context> contexts) {
        BigDecimal balance = accountData.getBalance(currency.getId(), uuid);

        return balance != null;
    }

    @Override
    public BigDecimal getBalance(Currency currency, Set<Context> contexts) {
        BigDecimal balance = accountData.getBalance(currency.getId(), uuid);

        return balance;
    }

    @Override
    public Map<Currency, BigDecimal> getBalances(Set<Context> contexts) {
        Map<Currency, BigDecimal> balances = accountData.getBalances(uuid);

        return balances;
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        return null;
    }

    @Override
    public Map<Currency, TransactionResult> resetBalances(Cause cause, Set<Context> contexts) {
        return null;
    }

    @Override
    public TransactionResult resetBalance(Currency currency, Cause cause, Set<Context> contexts) {
        return null;
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        return null;
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        return null;
    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        return null;
    }

    @Override
    public String getIdentifier() {
        return uuid.toString();
    }

    @Override
    public Set<Context> getActiveContexts() {
        return null;
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }
}
