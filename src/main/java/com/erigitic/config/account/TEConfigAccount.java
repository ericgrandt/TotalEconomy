package com.erigitic.config.account;

import com.erigitic.config.TEEconomyTransactionEvent;
import com.erigitic.config.TETransactionResult;
import com.erigitic.main.TotalEconomy;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionType;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Created by MarkL4YG on 08-Jan-18
 */
public class TEConfigAccount extends TEAccountBase {

    private ConfigurationNode accountNode;

    public TEConfigAccount(TotalEconomy totalEconomy, ConfigurationNode accountNode, UUID uniqueID) {
        super(totalEconomy, uniqueID);
        this.accountNode = accountNode;
    }

    /**
     * Determines if a balance exists for a {@link Currency}
     *
     * @param currency Currency type to be checked for
     * @param contexts
     * @return boolean If a balance exists for the specified currency
     */
    @Override
    public boolean hasBalance(Currency currency, Set<Context> contexts) {
        String currencyName = currency.getDisplayName().toPlain().toLowerCase();

        return !"".equals(accountNode.getNode("balance", currencyName).getString(""));
    }

    /**
     * Gets the balance of a {@link Currency}
     *
     * @param currency The currency to get the balance of
     * @param contexts
     * @return BigDecimal The balance
     */
    @Override
    public BigDecimal getBalance(Currency currency, Set<Context> contexts) {
        if (hasBalance(currency, contexts)) {
            String currencyName = currency.getDisplayName().toPlain().toLowerCase();

            ConfigurationNode balanceNode = accountNode.getNode("balance", currencyName);

            // Return the default balance for this currency when none is saved.
            if (balanceNode.isVirtual()) {
                return getDefaultBalance(currency);
            }
            // TODO: To crash or not to crash when not a string is contained here?
            return new BigDecimal(balanceNode.getString("0"));
        }

        return BigDecimal.ZERO;
    }

    /**
     * Sets the balance of a {@link Currency}
     *
     * @param currency Currency to set the balance of
     * @param amount Amount to set the balance to
     * @param cause
     * @param contexts
     * @return TransactionResult Result of the transaction
     */
    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {
        TransactionResult transactionResult;
        String currencyName = currency.getDisplayName().toPlain().toLowerCase();

        // If the amount is greater then the money cap, set the amount to the money cap
        amount = amount.min(totalEconomy.getMoneyCap());

        if (hasBalance(currency, contexts)) {
            BigDecimal delta = amount.subtract(getBalance(currency));
            TransactionType transactionType = delta.compareTo(BigDecimal.ZERO) >= 0 ? TransactionTypes.DEPOSIT : TransactionTypes.WITHDRAW;

            accountNode.getNode("balance", currencyName).setValue(amount.setScale(2, BigDecimal.ROUND_DOWN).toString());
            totalEconomy.requestAccountConfigurationSave();

            transactionResult = new TETransactionResult(this, currency, delta.abs(), contexts, ResultType.SUCCESS, transactionType);

        } else {
            transactionResult = new TETransactionResult(this, currency, BigDecimal.ZERO, contexts, ResultType.FAILED, TransactionTypes.DEPOSIT);
        }

        totalEconomy.getGame().getEventManager().post(new TEEconomyTransactionEvent(transactionResult));

        return transactionResult;
    }

    @Override
    public Text getDisplayName() {

        if (!isVirtual()) {
            UserStorageService userStore = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);

            return userStore.get(getUniqueId()).<Text>map(user -> Text.of(user.getName()))
                       .orElseGet(() -> Text.of("ERR_PLAYER_NAME"));
        }

        return TextSerializers.PLAIN.deserialize(accountNode.getNode("displayName").getString("ERR_ACCOUNT_NAME"));
    }

    @Override
    public void setDisplayName(Text displayName) {

        if (!isVirtual()) {
            throw new IllegalStateException("Setting displaynames for non-virtual accounts is not allowed!");
        }

        accountNode.getNode("displayname").setValue(TextSerializers.PLAIN.serialize(displayName));
    }
}
