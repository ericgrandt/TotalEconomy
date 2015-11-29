package com.erigitic.jobs.jobs;

import com.erigitic.jobs.Job;
import ninja.leaping.configurate.ConfigurationNode;

/**
 * Created by Erigitic on 10/29/2015.
 */
public class WarriorJob implements Job {
    public void setupJobValues(ConfigurationNode jobsConfig) {
        String[][] killValues = {{"skeleton", "10", "1.00"}, {"zombie", "10", "1.00"}, {"creeper", "10", "1.00"}, {"spider", "10", "1.00"}};

        for (int i = 0; i < killValues.length; i++) {
            jobsConfig.getNode("Warrior", "kill", killValues[i][0], "expreward").setValue(killValues[i][1]);
            jobsConfig.getNode("Warrior", "kill", killValues[i][0], "pay").setValue(killValues[i][2]);
        }
        jobsConfig.getNode("Warrior", "disablesalary").setValue(false);
        jobsConfig.getNode("Warrior", "salary").setValue(10);
        jobsConfig.getNode("Warrior", "permission").setValue("main.job.warrior");
    }
}
