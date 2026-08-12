package com.ericgrandt.totaleconomy.jobs.dto;

import java.util.UUID;

public record UpsertActiveJobDto(UUID playerId, String jobId) {
}
