package com.ericgrandt.totaleconomy.command;

import com.ericgrandt.totaleconomy.model.Sender;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface Command {
    CompletableFuture<CommandResult> execute(Sender sender, Map<String, CommandArgument> args);
}
