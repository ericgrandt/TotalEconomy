package com.ericgrandt.totaleconomy.jobs.dto;

import java.util.UUID;

public record UpsertPlayerActiveJobDto(UUID playerId, String jobId) {
}
