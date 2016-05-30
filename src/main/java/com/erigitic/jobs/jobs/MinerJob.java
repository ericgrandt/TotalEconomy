package com.erigitic.jobs.jobs;

import com.erigitic.jobs.Job;
import ninja.leaping.configurate.ConfigurationNode;

/**
 * Created by Erigitic on 10/29/2015.
 */
public class MinerJob implements Job {
    public void setupJobValues(ConfigurationNode jobsConfig) {
        String[][] breakValues = {{"coal_ore", "5", "0.25"}, {"iron_ore", "10", "0.50"}, {"lapis_ore", "20", "4.00"},
                {"gold_ore", "40", "5.00"}, {"diamond_ore", "100", "25.00"}, {"redstone_ore", "25", "2.00"},
                {"emerald_ore", "50", "12.50"}, {"quartz_ore", "5", "0.15"}};

        for (int i = 0; i < breakValues.length; i++) {
            jobsConfig.getNode("Miner", "break", breakValues[i][0], "expreward").setValue(breakValues[i][1]);
            jobsConfig.getNode("Miner", "break", breakValues[i][0], "pay").setValue(breakValues[i][2]);
        }
        jobsConfig.getNode("Miner", "disablesalary").setValue(false);
        jobsConfig.getNode("Miner", "salary").setValue(20);
        jobsConfig.getNode("Miner", "permission").setValue("main.job.miner");
    }
}
