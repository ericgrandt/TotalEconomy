package com.ericgrandt.totaleconomy.command;

import net.kyori.adventure.text.Component;

public class Messages {
    public static Component balance(Component formattedBalance) {
        return Component.text("Balance: ").append(formattedBalance);
    }
}
