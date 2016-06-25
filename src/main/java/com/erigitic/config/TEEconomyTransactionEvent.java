package com.erigitic.config;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.economy.EconomyTransactionEvent;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

/**
 * Created by Eric on 1/16/2016.
 */
public class TEEconomyTransactionEvent implements EconomyTransactionEvent {

    private TransactionResult transactionResult;

    public TEEconomyTransactionEvent(TransactionResult transactionResult) {
        this.transactionResult = transactionResult;
    }

    @Override
    public Cause getCause() {
        return Cause.of(NamedCause.of("TotalEconomy", this));
    }

    @Override
    public TransactionResult getTransactionResult() {
        return this.transactionResult;
    }
}
