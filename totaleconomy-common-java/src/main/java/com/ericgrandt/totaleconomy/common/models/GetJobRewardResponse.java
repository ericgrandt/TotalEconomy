package com.ericgrandt.totaleconomy.common.models;

import java.math.BigDecimal;

public record GetJobRewardResponse(String jobId, BigDecimal money, int experience) {
}
