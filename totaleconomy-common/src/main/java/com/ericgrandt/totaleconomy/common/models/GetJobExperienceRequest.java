package com.ericgrandt.totaleconomy.common.models;

import java.util.UUID;

public record GetJobExperienceRequest(UUID accountId, UUID jobId) {
}
