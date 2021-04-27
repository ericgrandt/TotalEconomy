package com.erigitic.commands;

import org.spongepowered.api.command.parameter.Parameter;

import java.lang.reflect.Type;

public class TECommandParameterKey<T> implements Parameter.Key<T> {
    private final String key;

    public TECommandParameterKey(String key) {
        this.key = key;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public Type type() {
        return null;
    }

    @Override
    public boolean isInstance(Object value) {
        return false;
    }

    @Override
    public T cast(Object value) {
        return null;
    }
}