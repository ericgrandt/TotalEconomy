package com.erigitic.jobs;

import ninja.leaping.configurate.ConfigurationNode;

/**
 * Created by Erigitic on 10/29/2015.
 */
public class LumberjackJob implements Job {
    public void setupJobValues(ConfigurationNode jobsConfig) {
        String[][] breakValues = {{"log", "10", "1"}, {"leaves", "1", ".01"}};
        String[][] placeValue = {{"sapling", "1", "0.10"}};
        
        for (int i = 0; i < breakValues.length; i++) {
            jobsConfig.getNode("Lumberjack", "break", breakValues[i][0], "expreward").setValue(breakValues[i][1]);
            jobsConfig.getNode("Lumberjack", "break", breakValues[i][0], "pay").setValue(breakValues[i][2]);
        }

        for (int i = 0; i < placeValue.length; i++) {
            jobsConfig.getNode("Lumberjack", "place", placeValue[i][0], "expreward").setValue(placeValue[i][1]);
            jobsConfig.getNode("Lumberjack", "place", placeValue[i][0], "pay").setValue(placeValue[i][2]);
        }
        jobsConfig.getNode("Lumberjack", "disablesalary").setValue(false);
        jobsConfig.getNode("Lumberjack", "salary").setValue(20);
        jobsConfig.getNode("Lumberjack", "permission").setValue("totaleconomy.job.lumberjack");
    }
}