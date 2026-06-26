package com.ericgrandt.totaleconomy.command;

import com.ericgrandt.totaleconomy.model.TEPlayer;

public record PlayerArg(TEPlayer value) implements CommandArgument {
}
