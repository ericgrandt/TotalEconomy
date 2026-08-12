package com.ericgrandt.totaleconomy.jobs.data.entity;

import java.time.Instant;

public record ActiveJobEntity(int id, String playerId, String jobId, Instant createdAt) {
}
