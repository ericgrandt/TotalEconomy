package com.ericgrandt.totaleconomy.common.models;

public record GetJobExperienceResponse(String jobName, int level, int experience, int experienceToNext) {
}
