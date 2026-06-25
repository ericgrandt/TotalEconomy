package com.ericgrandt.totaleconomy.command;

import com.ericgrandt.totaleconomy.model.Sender;

import java.util.Map;

public interface Command {
    CommandResult execute(Sender sender, Map<String, CommandArgument> args);
}
