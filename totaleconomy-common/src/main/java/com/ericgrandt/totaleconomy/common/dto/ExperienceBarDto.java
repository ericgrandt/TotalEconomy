package com.ericgrandt.totaleconomy.common.dto;

public record ExperienceBarDto(
    String jobName,
    int exp,
    int expToNext,
    int levelBaseExp
) {
}
