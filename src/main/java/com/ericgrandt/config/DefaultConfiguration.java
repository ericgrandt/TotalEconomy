package com.ericgrandt.config;

import com.ericgrandt.TotalEconomy;
import com.ericgrandt.data.TempData;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.*;

@ConfigSerializable
public class DefaultConfiguration {
    @Setting("database")
    private final DatabaseSettings databaseSettings = new DatabaseSettings();
    @Setting("tempdata")
    private final Map<String, List<TempData>> tempData = new HashMap<String, List<TempData>>();

    public String getConnectionString() {
        return String.format(
            "%s?user=%s&password=%s",
            databaseSettings.getConnectionString(),
            databaseSettings.getUser(),
            databaseSettings.getPassword()
        );
    }

    public void writeTempData(String player, TempData tempData) {
        if(!this.tempData.containsKey(player)) {
            List<TempData> list = new ArrayList<TempData>();
            list.add(tempData);
            this.tempData.put(player,list);
        } else {
            if(this.tempData.get(player).contains(tempData)) {
                this.tempData.get(player).stream().filter(temp -> (temp.equals(tempData))).findFirst().ifPresent(temp -> {
                    temp.update(tempData.getAmount());
                });
            } else {
                this.tempData.get(player).add(tempData);
            }
        }
        TotalEconomy.getInstance().getDefaultConfiguration().setAndSave(this);
    }

    public Optional<List<TempData>> getTempData(String player) {
        return Optional.ofNullable(tempData.getOrDefault(player, null));
    }

    public void checkExpires() {
        if(this.tempData.isEmpty()) return;;
        Map<String, List<TempData>> toRemove = new HashMap<String, List<TempData>>();
        boolean changed = false;
        for(Map.Entry<String, List<TempData>> entry : this.tempData.entrySet()) {
            for(TempData temp : entry.getValue()) {
                if(temp.isExpire()) {
                    if(!toRemove.containsKey(entry.getKey())) {
                        toRemove.put(entry.getKey(), new ArrayList<TempData>());
                    }
                    toRemove.get(entry.getKey()).add(temp);
                    changed = true;
                }
            }
        }
        for(Map.Entry<String, List<TempData>> entry : toRemove.entrySet()) {
            tempData.get(entry.getKey()).removeAll(entry.getValue());
            if(tempData.get(entry.getKey()).isEmpty()) tempData.remove(entry.getKey());
        }
        if(changed) TotalEconomy.getInstance().getDefaultConfiguration().setAndSave(this);
    }
}