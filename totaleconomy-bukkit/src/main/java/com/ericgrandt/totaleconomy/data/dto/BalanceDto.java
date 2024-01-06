package com.ericgrandt.totaleconomy.data.dto;

import java.math.BigDecimal;

public record BalanceDto(String id, String accountId, int currencyId, BigDecimal balance) {
}