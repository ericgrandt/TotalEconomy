package com.ericgrandt.totaleconomy.dto;

import com.ericgrandt.totaleconomy.model.Currency;

import java.math.BigDecimal;

public record TransferResult(Currency currency, BigDecimal amount) {
}
