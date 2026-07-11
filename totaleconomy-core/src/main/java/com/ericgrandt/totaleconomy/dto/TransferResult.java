package com.ericgrandt.totaleconomy.dto;

import com.ericgrandt.totaleconomy.model.TECurrency;

import java.math.BigDecimal;

public record TransferResult(TECurrency currency, BigDecimal amount) {
}
