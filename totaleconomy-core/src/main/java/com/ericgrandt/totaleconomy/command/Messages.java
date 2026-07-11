package com.ericgrandt.totaleconomy.command;

import net.kyori.adventure.text.Component;

public class Messages {
    public static Component balance(Component formattedBalance) {
        return Component.text("Balance: ").append(formattedBalance);
    }

    public static Component payFrom(Component formattedAmount, String toPlayerName) {
        return Component.text("You paid ")
            .append(Component.text(toPlayerName))
            .append(Component.text(" "))
            .append(formattedAmount);
    }

    public static Component payTo(Component formattedAmount, String fromPlayerName) {
        return Component.text("You received ")
            .append(formattedAmount)
            .append(Component.text(" from "))
            .append(Component.text(fromPlayerName));
    }
}
