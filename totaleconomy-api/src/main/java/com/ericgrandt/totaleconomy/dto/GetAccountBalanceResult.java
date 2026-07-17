package com.ericgrandt.totaleconomy.dto;

import com.ericgrandt.totaleconomy.model.Currency;

import java.math.BigDecimal;

public record GetAccountBalanceResult(Currency currency, BigDecimal balance) {
}
