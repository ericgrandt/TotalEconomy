package com.ericgrandt.totaleconomy.jobs.config;

import java.util.Arrays;
import java.util.Optional;

public final class JobEnums {
    public enum ActionType {
        BLOCK_BREAK;

        public static Optional<ActionType> fromString(String name) {
            return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(name))
                .findFirst();
        }
    }

    // TODO: May be able to combine both of these into a single Equation enum
    public enum XPCurveType {
        QUADRATIC;

        public static Optional<XPCurveType> fromString(String name) {
            return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(name))
                .findFirst();
        }
    }

    public enum PayoutMultiplierType {
        LINEAR;

        public static Optional<PayoutMultiplierType> fromString(String name) {
            return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(name))
                .findFirst();
        }
    }
}
