package com.ericgrandt.totaleconomy.command;

import com.ericgrandt.totaleconomy.model.Player;

public record PlayerArg(Player value) implements CommandArgument {
}
