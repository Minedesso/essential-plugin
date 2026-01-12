package de.minedesso.essentialPlugin.util;

public enum Messages {
    PREFIX("&8[&3&lMinedesso&8] &7» "),
    ERROR_PREFIX(PREFIX.message + "&c"),
    USAGE(PREFIX.message + "&eUsage: "),
    ERROR(PREFIX.message + "&cError: "),
    NO_PERMISSION("&cYou do not have permission to execute this command.");

    public final String message;

    Messages(String message) {
        this.message = message;
    }
}
