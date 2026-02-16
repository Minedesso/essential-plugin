package de.minedesso.essentialplugin.sub.tpa.cmd.validator;

import de.minedesso.essentialplugin.util.Message;
import lombok.Getter;
import org.bukkit.entity.Player;

public class ValidationResult {
    private final boolean isSuccessful;
    private final String errorMessage;
    @Getter
    private final Player resolvedPlayer;

    private ValidationResult(boolean isSuccessful, String errorMessage, Player resolvedPlayer) {
        this.isSuccessful = isSuccessful;
        this.errorMessage = errorMessage;
        this.resolvedPlayer = resolvedPlayer;
    }

    public static ValidationResult success(Player resolvedPlayer) {
        return new ValidationResult(true, null, resolvedPlayer);
    }

    public static ValidationResult error(String errorMessage) {
        return new ValidationResult(false, errorMessage, null);
    }

    public boolean failed() {
        return !isSuccessful;
    }

    public String getErrorMessage() {
        return Message.PREFIX.message + errorMessage;
    }

    public void sendErrorTo(Player player) {
        if (!isSuccessful) {
            player.sendMessage(getErrorMessage());
        }
    }
}