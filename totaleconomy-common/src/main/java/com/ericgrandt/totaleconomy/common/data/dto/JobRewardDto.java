package com.ericgrandt.totaleconomy.common.data.dto;

import java.math.BigDecimal;

public record JobRewardDto(
    String id,
    String jobId,
    String jobActionId,
    int currencyId,
    String material,
    BigDecimal money,
    int experience
) {

}
