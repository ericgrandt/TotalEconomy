package com.erigitic.config;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.economy.EconomyTransactionEvent;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

/**
 * Created by Erigitic on 1/16/2016.
 */
public class TEEconomyTransactionEvent implements EconomyTransactionEvent {

    private TransactionResult transactionResult;

    public TEEconomyTransactionEvent(TransactionResult transactionResult) {
        this.transactionResult = transactionResult;
    }

    @Override
    public Cause getCause() {
        return Cause.of("TotalEconomy");
    }

    @Override
    public TransactionResult getTransactionResult() {
        return this.transactionResult;
    }
}
