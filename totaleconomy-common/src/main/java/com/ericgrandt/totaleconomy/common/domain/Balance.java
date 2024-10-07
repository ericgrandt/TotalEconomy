package com.ericgrandt.totaleconomy.common.domain;

import java.math.BigDecimal;

public record Balance(String id, String accountId, int currencyId, BigDecimal balance) {
}
