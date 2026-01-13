package de.minedesso.essentialplugin.util;

public enum Messages {
    PREFIX("&8[&3&lMinedesso&8] &7» "),
    USAGE(PREFIX.message + "&eUsage: "),
    ERROR(PREFIX.message + "&cError: "),
    ONLY_PLAYER(PREFIX.message + "&cOnly players can use this command."),
    PLAYER_NOT_FOUND(PREFIX.message + "Player not found."),
    NO_PERMISSION(PREFIX.message + "&cYou do not have permission to execute this command.");

    public final String message;

    Messages(String message) {
        this.message = message;
    }
}
