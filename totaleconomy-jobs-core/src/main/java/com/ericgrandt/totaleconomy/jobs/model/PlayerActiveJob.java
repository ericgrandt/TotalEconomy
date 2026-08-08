package com.ericgrandt.totaleconomy.jobs.model;

import java.util.UUID;

public record PlayerActiveJob(UUID playerId, String jobId) {
}
