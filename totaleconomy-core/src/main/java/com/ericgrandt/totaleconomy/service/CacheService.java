package com.ericgrandt.totaleconomy.service;

import com.ericgrandt.totaleconomy.api.exception.DatabaseException;
import com.ericgrandt.totaleconomy.data.CurrencyData;
import com.ericgrandt.totaleconomy.data.TransactionUtil;
import com.ericgrandt.totaleconomy.model.TECurrency;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CacheService {
    private final TransactionUtil transactionUtil;
    private final CurrencyData currencyData;

    private volatile Map<String, TECurrency> currencies = Collections.emptyMap();
    private volatile TECurrency defaultCurrency;

    public CacheService(TransactionUtil transactionUtil, CurrencyData currencyData) {
        this.transactionUtil = transactionUtil;
        this.currencyData = currencyData;
    }

    public void initCache() {
        try {
            var supportedCurrencies = transactionUtil.runInTransaction(currencyData::getSupportedCurrencies);

            var newCachedCurrencies = new HashMap<String, TECurrency>();
            for (var currency : supportedCurrencies) {
                newCachedCurrencies.put(currency.code(), currency);

                if (currency.isDefault()) {
                    defaultCurrency = currency;
                }
            }

            currencies = Collections.unmodifiableMap(newCachedCurrencies);
        } catch (SQLException e) {
            throw new DatabaseException("error initializing cache", e);
        }
    }

    public Map<String, TECurrency> getCurrencies() {
        return currencies;
    }

    public TECurrency getDefaultCurrency() {
        return defaultCurrency;
    }
}
