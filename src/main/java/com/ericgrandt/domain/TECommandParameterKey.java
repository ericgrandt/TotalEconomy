package com.ericgrandt.domain;

import org.spongepowered.api.command.parameter.Parameter;

import java.lang.reflect.Type;

public class TECommandParameterKey<T> implements Parameter.Key<T> {
    private final String key;
    private final Type type;

    public TECommandParameterKey(String key, Type type) {
        this.key = key;
        this.type = type;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public Type type() {
        return type;
    }

    @Override
    public boolean isInstance(Object value) {
        if (type instanceof Class) {
            Class<?> typeClass = (Class<?>) type;
            return typeClass.isInstance(value);
        }

        return false;
    }

    @Override
    public T cast(Object value) {
        throw new UnsupportedOperationException();
    }
}