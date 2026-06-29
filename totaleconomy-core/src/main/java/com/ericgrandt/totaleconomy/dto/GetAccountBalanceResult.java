package com.ericgrandt.totaleconomy.dto;

import com.ericgrandt.totaleconomy.model.TECurrency;

import java.math.BigDecimal;

public record GetAccountBalanceResult(TECurrency currency, BigDecimal balance) {
}
