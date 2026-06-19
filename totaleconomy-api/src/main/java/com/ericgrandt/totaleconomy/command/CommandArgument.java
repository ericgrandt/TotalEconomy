package com.ericgrandt.totaleconomy.command;

/**
 * Represents a parsed command argument.
 * <p>
 * This sealed hierarchy ensures only defined argument types ({@link DoubleArg}, {@link StringArg}, {@link PlayerArg},
 * etc.) can be used.
 * </p>
 */
public sealed interface CommandArgument permits DoubleArg, StringArg, PlayerArg {
}
