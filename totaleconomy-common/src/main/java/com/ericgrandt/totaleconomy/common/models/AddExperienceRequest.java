package com.ericgrandt.totaleconomy.common.models;

import java.util.UUID;

public record AddExperienceRequest(
    UUID accountId,
    UUID jobId,
    int experience
) {
}
