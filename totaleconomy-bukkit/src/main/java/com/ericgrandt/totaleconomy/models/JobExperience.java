package com.ericgrandt.totaleconomy.models;

public record JobExperience(
    String jobName,
    int experience,
    int levelBaseExperience,
    int experienceToNext,
    int level
) {
}
