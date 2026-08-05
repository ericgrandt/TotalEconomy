package com.ericgrandt.totaleconomy.jobs.paper.config;

import com.ericgrandt.totaleconomy.api.exception.ConfigLoadException;
import com.ericgrandt.totaleconomy.jobs.config.Config;
import com.ericgrandt.totaleconomy.jobs.config.ConfigParseResult;
import com.ericgrandt.totaleconomy.jobs.config.JobEnums;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// TODO: A lot of this should be shared. The only thing that will be unique is the validation of materials/entities
// NOTE: This is a mess, I'm sorry. It works though.
public class ConfigLoader {
    public static Config from(FileConfiguration fileConfig) {
        var errors = new ArrayList<String>();

        var jobsSection = fileConfig.getConfigurationSection("jobs");
        var settingsSection = fileConfig.getConfigurationSection("settings");

        if (jobsSection == null) {
            errors.add("missing 'jobs' section");
        }
        if (settingsSection == null) {
            errors.add("missing 'settings' section");
        }
        if (!errors.isEmpty()) {
            throw new ConfigLoadException(String.join("\n", errors));
        }

        var parsedJobResult = parseJobs(jobsSection);
        errors.addAll(parsedJobResult.errors());

        var parsedSettingsResult = parseSettings(settingsSection);
        errors.addAll(parsedSettingsResult.errors());

        if (!errors.isEmpty()) {
            throw new ConfigLoadException(String.join("\n", errors));
        }

        return new Config(
            parsedJobResult.result(),
            parsedSettingsResult.result()
        );
    }

    private static ConfigParseResult<List<Config.Job>> parseJobs(ConfigurationSection section) {
        var errors = new ArrayList<String>();
        var jobList = new ArrayList<Config.Job>();
        for (String jobKey : section.getKeys(false)) {
            var jobSection = section.getConfigurationSection(jobKey);
            if (jobSection == null) {
                errors.add("job '%s': invalid configuration".formatted(jobKey));
                continue;
            }

            var validJob = true;
            var id = jobSection.getString("id");
            var displayName = jobSection.getString("displayName");
            var description = jobSection.getString("description");
            var actionsSection = jobSection.getConfigurationSection("actions");
            if (actionsSection == null) {
                errors.add("job '%s': invalid/missing 'actions' section".formatted(jobKey));
                continue;
            }
            var parsedActionsResult = parseJobActions(actionsSection, jobKey);
            errors.addAll(parsedActionsResult.errors());

            if (id == null || id.isBlank()) {
                errors.add("job '%s': missing 'id' field".formatted(jobKey));
                validJob = false;
            }
            if (displayName == null || displayName.isBlank()) {
                errors.add("job '%s': missing 'displayName' field".formatted(jobKey));
                validJob = false;
            }
            if (description == null || description.isBlank()) {
                errors.add("job '%s': missing 'description' field".formatted(jobKey));
                validJob = false;
            }
            if (!validJob) {
                continue;
            }

            jobList.add(new Config.Job(id, displayName, description, parsedActionsResult.result()));
        }
        return new ConfigParseResult<>(jobList, errors);
    }

    private static ConfigParseResult<List<Config.Job.Action>> parseJobActions(
        ConfigurationSection section,
        String jobId
    ) {
        var errors = new ArrayList<String>();

        var actions = new ArrayList<Config.Job.Action>();
        for (String actionKey : section.getKeys(false)) {
            var actionType = JobEnums.ActionType.fromString(actionKey);
            if (actionType.isEmpty()) {
                errors.add("job '%s', action '%s': invalid action type".formatted(jobId, actionKey));
                continue;
            }

            var entriesSection = section.getConfigurationSection("%s.entries".formatted(actionType.get()));
            if (entriesSection == null) {
                errors.add("job '%s', action '%s': invalid/missing 'entries' section".formatted(jobId, actionKey));
                continue;
            }

            var entries = new ArrayList<Config.Job.Action.Entry>();
            for (String entryKey : entriesSection.getKeys(false)) {
                var validEntry = true;
                if (actionType.get() == JobEnums.ActionType.BLOCK_BREAK) {

                    var material = Material.matchMaterial(entryKey);
                    if (material == null) {
                        errors.add("job '%s', action '%s', entry '%s': invalid entry".formatted(
                            jobId,
                            actionKey,
                            entryKey
                        ));
                        validEntry = false;
                    }
                }

                var xp = entriesSection.getInt("%s.xp".formatted(entryKey), -1);
                if (xp < 0) {
                    errors.add("job '%s', action '%s', entry '%s': invalid xp".formatted(jobId, actionKey, entryKey));
                    validEntry = false;
                }

                var payout = entriesSection.getString("%s.payout".formatted(entryKey));
                if (payout == null || payout.isBlank()) {
                    errors.add("job '%s', action '%s', entry '%s': invalid payout".formatted(
                        jobId,
                        actionKey,
                        entryKey
                    ));
                    validEntry = false;
                }

                if (!validEntry) {
                    continue;
                }

                BigDecimal payoutBigDecimal;
                try {
                    payoutBigDecimal = new BigDecimal(payout);
                } catch (NumberFormatException e) {
                    errors.add("job '%s', action '%s', entry '%s': invalid payout".formatted(
                        jobId,
                        actionKey,
                        entryKey
                    ));
                    continue;
                }

                entries.add(
                    new Config.Job.Action.Entry(
                        entryKey,
                        xp,
                        payoutBigDecimal
                    )
                );
            }

            actions.add(
                new Config.Job.Action(actionType.get(), entries)
            );
        }

        return new ConfigParseResult<>(actions, errors);
    }

    private static ConfigParseResult<Config.Settings> parseSettings(ConfigurationSection section) {
        var errors = new ArrayList<String>();
        var maxLevel = section.getInt("maxLevel", 0);
        if (maxLevel <= 0) {
            errors.add("settings: invalid max level, must be greater than 0");
        }
        var xpCurveType = JobEnums.XPCurveType.fromString(section.getString("xpCurve.type"));
        if (xpCurveType.isEmpty()) {
            errors.add("settings: invalid xpCurve type");
        }

        var payoutMultiplierType = JobEnums.PayoutMultiplierType.fromString(
            section.getString("payoutMultiplier.type")
        );
        if (payoutMultiplierType.isEmpty()) {
            errors.add("settings: invalid payoutMultiplier type");
        }

        return new ConfigParseResult<>(
            new Config.Settings(
                maxLevel,
                new Config.Settings.XPCurve(
                    xpCurveType.orElse(JobEnums.XPCurveType.QUADRATIC),
                    section.getInt("xpCurve.baseXP")
                ),
                new Config.Settings.PayoutMultiplier(
                    payoutMultiplierType.orElse(JobEnums.PayoutMultiplierType.LINEAR),
                    section.getInt("payoutMultiplier.base"),
                    section.getDouble("payoutMultiplier.perLevel")
                )
            ), errors
        );
    }
}
