package com.ericgrandt.totaleconomy.config;

import java.util.HashMap;
import java.util.Map;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class FeatureSettings {
    private boolean jobs = true;

    public Map<String, Boolean> getFeatures() {
        Map<String, Boolean> features = new HashMap<>();
        features.put("jobs", jobs);
        return features;
    }
}
