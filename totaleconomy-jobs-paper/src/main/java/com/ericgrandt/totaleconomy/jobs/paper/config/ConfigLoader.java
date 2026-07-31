package com.ericgrandt.totaleconomy.jobs.paper.config;

import com.ericgrandt.totaleconomy.api.exception.ConfigLoadException;
import com.ericgrandt.totaleconomy.jobs.config.Config;
import com.ericgrandt.totaleconomy.jobs.config.ConfigParseResult;
import com.ericgrandt.totaleconomy.jobs.config.JobEnums;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

// job 'miner', field 'id': missing or invalid
// job 'miner', action 'BLOCK_BREAK': missing 'entries' section
// TODO: A lot of this should be shared. The only thing that will be unique is the validation of materials/entities
public class ConfigLoader {
    // TODO: Handle errors better. Maybe collect list of errors and display all at the end.
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

        var maxLevel = settingsSection.getInt("maxLevel");
        var xpCurveType = JobEnums.XPCurveType.fromString(settingsSection.getString("xpCurve.type"));
        if (xpCurveType.isEmpty()) {
            throw new ConfigLoadException("invalid xpCurve type");
        }

        var payoutMultiplierType = JobEnums.PayoutMultiplierType.fromString(
            settingsSection.getString("payoutMultiplier.type")
        );
        if (payoutMultiplierType.isEmpty()) {
            throw new ConfigLoadException("invalid payoutMultiplier type");
        }

        if (!errors.isEmpty()) {
            throw new ConfigLoadException(String.join("\n", errors));
        }

        return new Config(
            parsedJobResult.result(),
            new Config.Settings(
                maxLevel,
                new Config.Settings.XPCurve(
                    xpCurveType.get(),
                    settingsSection.getInt("xpCurve.baseXP")
                ),
                new Config.Settings.PayoutMultiplier(
                    payoutMultiplierType.get(),
                    settingsSection.getInt("payoutMultiplier.base"),
                    settingsSection.getDouble("payoutMultiplier.perLevel")
                )
            )
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
            parseJobActions(actionsSection, jobKey);

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

            jobList.add(new Config.Job(id, displayName, description, List.of()));
        }
        return new ConfigParseResult<>(jobList, errors);
    }

    private static ConfigParseResult<List<Config.Job.Action>> parseJobActions(
        ConfigurationSection section,
        String jobId
    ) {
        var errors = new ArrayList<String>();

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
                if (actionType.get() == JobEnums.ActionType.BLOCK_BREAK) {
                    var material = Material.matchMaterial(entryKey);
                    if (material == null) {
                        errors.add("job '%s', action '%s',: invalid material".formatted(jobId, entryKey));
                        continue;
                    }
                }

                var xp = entriesSection.getInt("%s.xp".formatted(entryKey));
                var payout = entriesSection.getDouble("%s.payout".formatted(entryKey));
            }
        }

        return new ConfigParseResult<>(new ArrayList<>(), errors);
    }

    private static ConfigParseResult<Config.Settings> parseSettings(ConfigurationSection section) {
        var errors = new ArrayList<String>();
        var maxLevel = section.getInt("maxLevel");
        if (maxLevel <= 0) {
            errors.add("invalid 'maxLevel' field");
        }

        return new ConfigParseResult<>(
            new Config.Settings(
                maxLevel,
                null,
                null
            ), errors
        );
    }
}
