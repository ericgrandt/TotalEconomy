package com.ericgrandt.totaleconomy.api.dto;

import com.ericgrandt.totaleconomy.api.model.Currency;

import java.math.BigDecimal;

public record GetAccountBalanceResult(Currency currency, BigDecimal balance) {
}
