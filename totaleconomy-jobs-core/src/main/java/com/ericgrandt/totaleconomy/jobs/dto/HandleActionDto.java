package com.ericgrandt.totaleconomy.jobs.dto;

import java.math.BigDecimal;

public record HandleActionDto(Status status, int xpAwarded, BigDecimal payoutAwarded, boolean levelUp) {
}
