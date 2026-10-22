package com.ericgrandt.totaleconomy.api.dto;

import com.ericgrandt.totaleconomy.api.model.Currency;

import java.math.BigDecimal;

public record TransferResult(Currency currency, BigDecimal amount) {
}
