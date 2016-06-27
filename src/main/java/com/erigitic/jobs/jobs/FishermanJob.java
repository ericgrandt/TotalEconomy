package com.erigitic.jobs.jobs;

import com.erigitic.jobs.Job;
import ninja.leaping.configurate.ConfigurationNode;

/**
 * Created by Eric on 11/3/2015.
 */
public class FishermanJob implements Job {
    public void setupJobValues(ConfigurationNode jobsConfig) {
        String[][] catchValues = {{"cod", "25", "50"}, {"salmon", "100", "150"}, {"pufferfish", "250", "300"}};

        for (int i = 0; i < catchValues.length; i++) {
            jobsConfig.getNode("Fisherman", "catch", catchValues[i][0], "expreward").setValue(catchValues[i][1]);
            jobsConfig.getNode("Fisherman", "catch", catchValues[i][0], "pay").setValue(catchValues[i][2]);
        }

        jobsConfig.getNode("Fisherman", "disablesalary").setValue(false);
        jobsConfig.getNode("Fisherman", "salary").setValue(20);
        jobsConfig.getNode("Fisherman", "permission").setValue("main.job.fisherman");
    }
}
