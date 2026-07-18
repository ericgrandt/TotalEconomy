package com.ericgrandt.totaleconomy.service;

import com.ericgrandt.totaleconomy.dto.GetAccountBalanceResult;
import com.ericgrandt.totaleconomy.dto.TransferResult;
import com.ericgrandt.totaleconomy.exception.AccountNotFoundException;
import com.ericgrandt.totaleconomy.exception.MissingDefaultCurrencyException;
import com.ericgrandt.totaleconomy.model.Account;
import com.ericgrandt.totaleconomy.model.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public interface EconomyService {
    /**
     * Retrieves the default currency.
     *
     * @return the default {@link Currency}
     * @throws MissingDefaultCurrencyException if the default currency is not found
     */
    Currency getDefaultCurrency();

    /**
     * Creates a new account for a player with the specific currency and its starting balance.
     *
     * @param playerId     the unique identifier of the player
     * @param currencyCode the currency to attach to this account
     * @return the created {@link Account}
     * @throws AccountNotFoundException if the account is not found after successful creation
     */
    Account createAccount(UUID playerId, String currencyCode);

    /**
     * Retrieves the balance for a player's account in the specified currency.
     *
     * @param playerId     the unique identifier of the player
     * @param currencyCode the currency code of the account to retrieve the balance for
     * @return the {@link GetAccountBalanceResult} containing the account balance
     * @throws AccountNotFoundException if no account exists for the given playerId
     */
    GetAccountBalanceResult getAccountBalance(UUID playerId, String currencyCode);

    /**
     * Retrieves the balance for a player's account in the default currency.
     *
     * @param playerId the unique identifier of the player
     * @return the {@link GetAccountBalanceResult} containing the account balance
     * @throws AccountNotFoundException if no account exists for the given playerId
     */
    GetAccountBalanceResult getAccountBalance(UUID playerId);

    /**
     * Transfers an amount from one player to another in the specified currency.
     *
     * @param fromPlayerId the unique identifier of the sending player
     * @param toPlayerId   the unique identifier of the receiving player
     * @param currencyCode the currency code of the accounts to transfer between
     * @param amount       the amount to transfer
     * @return the {@link TransferResult} describing the outcome of the transfer
     */
    TransferResult transfer(UUID fromPlayerId, UUID toPlayerId, String currencyCode, BigDecimal amount);

    /**
     * Transfers an amount from one player to another in the default currency.
     *
     * @param fromPlayerId the unique identifier of the sending player
     * @param toPlayerId   the unique identifier of the receiving player
     * @param amount       the amount to transfer
     * @return the {@link TransferResult} describing the outcome of the transfer
     */
    TransferResult transfer(UUID fromPlayerId, UUID toPlayerId, BigDecimal amount);
}
