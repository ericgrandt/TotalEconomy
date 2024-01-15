package com.ericgrandt.totaleconomy.config;

import java.util.HashMap;
import java.util.Map;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class FeatureSettings {
    @Setting
    private final boolean jobs = true;

    public Map<String, Boolean> getFeatures() {
        Map<String, Boolean> features = new HashMap<>();
        features.put("jobs", jobs);
        return features;
    }
}
