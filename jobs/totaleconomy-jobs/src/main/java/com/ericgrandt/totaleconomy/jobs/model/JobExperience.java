package com.ericgrandt.totaleconomy.jobs.model;

import java.util.UUID;

public record JobExperience(UUID playerId, String jobId, int experience) {
}
