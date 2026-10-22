package com.ericgrandt.totaleconomy.jobs.dto;

import java.util.UUID;

public record UpsertJobExperienceDto(UUID playerId, String jobId, int xpToAdd) {
}
