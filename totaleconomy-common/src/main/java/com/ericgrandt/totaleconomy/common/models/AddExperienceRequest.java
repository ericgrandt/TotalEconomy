package com.ericgrandt.totaleconomy.common.models;

public record AddExperienceRequest(
    String accountId,
    String jobId,
    String action,
    String materialName
) {
}
