package com.ericgrandt.totaleconomy.api.service;

import com.ericgrandt.totaleconomy.api.dto.DepositResult;
import com.ericgrandt.totaleconomy.api.dto.GetAccountBalanceResult;
import com.ericgrandt.totaleconomy.api.dto.TransferResult;
import com.ericgrandt.totaleconomy.api.dto.WithdrawResult;
import com.ericgrandt.totaleconomy.api.exception.AccountNotFoundException;
import com.ericgrandt.totaleconomy.api.exception.CurrencyNotFoundException;
import com.ericgrandt.totaleconomy.api.exception.InsufficientFundsException;
import com.ericgrandt.totaleconomy.api.exception.MissingDefaultCurrencyException;
import com.ericgrandt.totaleconomy.api.exception.SelfTransferException;
import com.ericgrandt.totaleconomy.api.model.Account;
import com.ericgrandt.totaleconomy.api.model.Currency;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public interface EconomyService<T extends Currency> {
    /// Retrieves the default currency.
    ///
    /// @return the default [Currency]
    /// @throws MissingDefaultCurrencyException if the default currency is not found
    T getDefaultCurrency();

    /// Retrieves a list of all supported currencies.
    ///
    /// @return the map of [Currency]
    Map<String, T> getSupportedCurrencies();

    /// Creates a new account for a player with the specific currency and its starting balance.
    ///
    /// @param playerId     the unique identifier of the player
    /// @param currencyCode the currency to attach to this account
    /// @return the created [Account]
    /// @throws CurrencyNotFoundException if the currency is not found
    Account createAccount(UUID playerId, String currencyCode);

    /// Retrieves the balance for a player's account in the specified currency.
    ///
    /// @param playerId     the unique identifier of the player
    /// @param currencyCode the currency code of the account to retrieve the balance for
    /// @return the [GetAccountBalanceResult] containing the account balance
    /// @throws CurrencyNotFoundException if the currency is not found
    /// @throws AccountNotFoundException  if no account exists for the given playerId
    GetAccountBalanceResult getAccountBalance(UUID playerId, String currencyCode);

    /// Retrieves the balance for a player's account in the default currency.
    ///
    /// @param playerId the unique identifier of the player
    /// @return the [GetAccountBalanceResult] containing the account balance
    /// @throws CurrencyNotFoundException if the currency is not found
    /// @throws AccountNotFoundException  if no account exists for the given playerId
    GetAccountBalanceResult getAccountBalance(UUID playerId);

    /// Withdraws an amount from a player's account in the specified currency.
    ///
    /// @param playerId     the unique identifier of the player
    /// @param currencyCode the currency code of the accounts to transfer between
    /// @param amount       the amount to withdraw
    /// @return the [WithdrawResult] describing the outcome of the withdrawal
    /// @throws CurrencyNotFoundException  if the currency is not found
    /// @throws AccountNotFoundException   if account is not found
    /// @throws InsufficientFundsException if account has an insufficient balance to cover the withdrawal
    WithdrawResult withdraw(UUID playerId, String currencyCode, BigDecimal amount);

    /// Withdraws an amount from a player's account in the default currency.
    ///
    /// @param playerId the unique identifier of the player
    /// @param amount   the amount to withdraw
    /// @return the [WithdrawResult] describing the outcome of the withdrawal
    /// @throws CurrencyNotFoundException  if the currency is not found
    /// @throws AccountNotFoundException   if account is not found
    /// @throws InsufficientFundsException if account has an insufficient balance to cover the withdrawal
    WithdrawResult withdraw(UUID playerId, BigDecimal amount);

    /// Deposits an amount into a player's account in the specified currency.
    ///
    /// @param playerId     the unique identifier of the player
    /// @param currencyCode the currency code of the accounts to transfer between
    /// @param amount       the amount to deposit
    /// @return the [DepositResult] describing the outcome of the deposit
    /// @throws CurrencyNotFoundException if the currency is not found
    /// @throws AccountNotFoundException  if account is not found
    DepositResult deposit(UUID playerId, String currencyCode, BigDecimal amount);

    /// Deposits an amount into a player's account in the default currency.
    ///
    /// @param playerId the unique identifier of the player
    /// @param amount   the amount to withdraw
    /// @return the [DepositResult] describing the outcome of the deposit
    /// @throws CurrencyNotFoundException if the currency is not found
    /// @throws AccountNotFoundException  if account is not found
    DepositResult deposit(UUID playerId, BigDecimal amount);

    /// Transfers an amount from one player to another in the specified currency.
    ///
    /// @param fromPlayerId the unique identifier of the sending player
    /// @param toPlayerId   the unique identifier of the receiving player
    /// @param currencyCode the currency code of the accounts to transfer between
    /// @param amount       the amount to transfer
    /// @return the [TransferResult] describing the outcome of the transfer
    /// @throws SelfTransferException      if sender and receiver are the same
    /// @throws CurrencyNotFoundException  if the currency is not found
    /// @throws AccountNotFoundException   if sender or receiver are not found
    /// @throws InsufficientFundsException if sender has an insufficient balance to cover the transfer
    TransferResult transfer(UUID fromPlayerId, UUID toPlayerId, String currencyCode, BigDecimal amount);

    /// Transfers an amount from one player to another in the default currency.
    ///
    /// @param fromPlayerId the unique identifier of the sending player
    /// @param toPlayerId   the unique identifier of the receiving player
    /// @param amount       the amount to transfer
    /// @return the [TransferResult] describing the outcome of the transfer
    /// @throws SelfTransferException      if sender and receiver are the same
    /// @throws CurrencyNotFoundException  if the currency is not found
    /// @throws AccountNotFoundException   if sender or receiver are not found
    /// @throws InsufficientFundsException if sender has an insufficient balance to cover the transfer
    TransferResult transfer(UUID fromPlayerId, UUID toPlayerId, BigDecimal amount);
}
