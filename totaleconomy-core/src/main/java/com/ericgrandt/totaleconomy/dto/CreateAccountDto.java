package com.ericgrandt.totaleconomy.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateAccountDto(UUID playerId, String currencyCode, BigDecimal balance) {
}
