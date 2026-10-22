package com.ericgrandt.totaleconomy.jobs.paper;

import com.ericgrandt.totaleconomy.jobs.config.Config;
import com.ericgrandt.totaleconomy.jobs.config.JobEnums;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class TestUtils {
    public static Config buildTestConfig() {
        var entry = new Config.Job.Action.Entry("COAL_ORE", 10, BigDecimal.valueOf(3.0));
        var entryMap = Map.of("COAL_ORE", entry);
        var actionMap = Map.of(
            JobEnums.ActionType.BLOCK_BREAK,
            new Config.Job.Action(JobEnums.ActionType.BLOCK_BREAK, entryMap)
        );

        return new Config(
            List.of(
                new Config.Job(
                    "miner",
                    "Miner",
                    "Mine ores",
                    actionMap
                )
            ),
            new Config.Settings(
                100,
                new Config.Settings.XPCurve(
                    JobEnums.XPCurveType.QUADRATIC,
                    100
                ),
                new Config.Settings.PayoutMultiplier(
                    JobEnums.PayoutMultiplierType.LINEAR,
                    1,
                    0.01
                )
            )
        );
    }
}
