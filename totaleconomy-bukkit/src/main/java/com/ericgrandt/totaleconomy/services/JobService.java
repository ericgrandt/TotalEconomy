package com.ericgrandt.totaleconomy.services;

import com.ericgrandt.totaleconomy.common.data.JobData;
import com.ericgrandt.totaleconomy.common.data.dto.JobActionDto;
import com.ericgrandt.totaleconomy.common.data.dto.JobDto;
import com.ericgrandt.totaleconomy.common.data.dto.JobExperienceDto;
import com.ericgrandt.totaleconomy.common.data.dto.JobRewardDto;
import com.ericgrandt.totaleconomy.impl.JobExperienceBar;
import com.ericgrandt.totaleconomy.models.AddExperienceResult;
import com.ericgrandt.totaleconomy.models.JobExperience;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobService {
    private final Logger logger;
    private final JobData jobData;
    private final HashMap<UUID, JobExperienceBar> playerJobExperienceBars = new HashMap<>();

    public JobService(Logger logger, JobData jobData) {
        this.logger = logger;
        this.jobData = jobData;
    }

    public JobRewardDto getJobReward(String actionName, String materialName) {
        try {
            JobActionDto jobActionDto = jobData.getJobActionByName(actionName);
            if (jobActionDto == null) {
                return null;
            }

            return jobData.getJobReward(jobActionDto.id(), materialName);
        } catch (SQLException e) {
            logger.log(
                Level.SEVERE,
                String.format(
                    "[Total Economy] Error calling getJobReward (actionName: %s, materialName: %s)",
                    actionName,
                    materialName
                ),
                e
            );
            return null;
        }
    }

    public JobExperience getExperienceForJob(UUID accountId, UUID jobId) throws SQLException {
        JobExperienceDto jobExperienceDto = jobData.getExperienceForJob(accountId, jobId);
        JobDto jobDto = jobData.getJob(jobId);
        int curLevel = calculateLevelFromExperience(jobExperienceDto.experience());

        return new JobExperience(
            jobDto.jobName(),
            jobExperienceDto.experience(),
            calculateCurrentLevelBaseExperience(curLevel),
            calculateExperienceForNextLevel(curLevel),
            curLevel
        );
    }

    public List<JobExperience> getExperienceForAllJobs(UUID accountId) throws SQLException {
        List<JobExperienceDto> jobExperienceDtos = jobData.getExperienceForAllJobs(accountId);
        List<JobExperience> jobExperienceList = new ArrayList<>();

        for (JobExperienceDto jobExperienceDto : jobExperienceDtos) {
            JobDto jobDto = jobData.getJob(UUID.fromString(jobExperienceDto.jobId()));
            int curLevel = calculateLevelFromExperience(jobExperienceDto.experience());

            jobExperienceList.add(
                new JobExperience(
                    jobDto.jobName(),
                    jobExperienceDto.experience(),
                    calculateCurrentLevelBaseExperience(curLevel),
                    calculateExperienceForNextLevel(curLevel),
                    curLevel
                )
            );
        }

        return jobExperienceList;
    }

    public void createJobExperienceForAccount(UUID accountId) {
        try {
            jobData.createJobExperienceRows(accountId);
        } catch (SQLException e) {
            logger.log(
                Level.SEVERE,
                String.format(
                    "[Total Economy] Error calling createJobExperienceForAccount (accountId: %s)",
                    accountId
                ),
                e
            );
        }
    }

    public AddExperienceResult addExperience(UUID accountId, UUID jobId, int experienceToAdd) {
        try {
            Optional<JobExperienceDto> jobExperienceDtoOptional = getJobExperienceDto(accountId, jobId);
            if (jobExperienceDtoOptional.isEmpty()) {
                return new AddExperienceResult(null, false);
            }

            JobExperienceDto jobExperienceDto = jobExperienceDtoOptional.get();
            JobDto jobDto = jobData.getJob(jobId);
            int currentExperience = jobExperienceDto.experience();
            int newExperience = jobExperienceDto.experience() + experienceToAdd;
            int currentLevel = calculateLevelFromExperience(currentExperience);
            int newLevel = calculateLevelFromExperience(newExperience);

            jobData.updateExperienceForJob(accountId, jobId, newExperience);

            JobExperience jobExperience = new JobExperience(
                jobDto.jobName(),
                newExperience,
                calculateCurrentLevelBaseExperience(newLevel),
                calculateExperienceForNextLevel(newLevel),
                newLevel
            );

            return new AddExperienceResult(jobExperience, newLevel > currentLevel);
        } catch (SQLException e) {
            logger.log(
                Level.SEVERE,
                String.format(
                    "[Total Economy] Error calling addExperience (accountId: %s, jobId: %s, experienceToAdd: %s)",
                    accountId,
                    jobId,
                    experienceToAdd
                ),
                e
            );
            return new AddExperienceResult(null, false);
        }
    }

    public int calculateLevelFromExperience(int experience) {
        // Inverse of: 49 * (cur_level ^ 2)
        int level = (int) Math.ceil(Math.sqrt(experience) / 7);
        return Math.max(level, 1);
    }

    public JobExperienceBar getPlayerJobExperienceBar(UUID playerUUID) {
        return playerJobExperienceBars.get(playerUUID);
    }

    public void addPlayerJobExperienceBar(UUID playerUUID, JobExperienceBar experienceBar) {
        playerJobExperienceBars.put(playerUUID, experienceBar);
    }

    private int calculateExperienceForNextLevel(int curLevel) {
        return (int) Math.ceil(49 * Math.pow(curLevel, 2)) + 1;
    }

    private int calculateCurrentLevelBaseExperience(int curLevel) {
        int experience = (int) Math.ceil(49 * Math.pow(curLevel - 1, 2));
        return experience > 0 ? experience + 1 : experience;
    }

    private Optional<JobExperienceDto> getJobExperienceDto(UUID accountId, UUID jobId) throws SQLException {
        return Optional.ofNullable(jobData.getExperienceForJob(accountId, jobId));
    }
}
