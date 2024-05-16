package com.ericgrandt.totaleconomy.common.models;

import java.util.UUID;

public record UpdateJobExperienceRequest(UUID accountId, UUID jobId, int experienceToAdd) {
}
