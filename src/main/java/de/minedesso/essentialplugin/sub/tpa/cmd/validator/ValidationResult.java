package de.minedesso.essentialplugin.sub.tpa.cmd.validator;

import de.minedesso.essentialplugin.util.Message;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
@Builder
public class ValidationResult {
    private final boolean successful;
    private final String errorMessage;
    private final Player resolvedPlayer;

    public static ValidationResult success(Player resolvedPlayer) {
        return ValidationResult.builder()
                .successful(true)
                .resolvedPlayer(resolvedPlayer)
                .build();
    }

    public static ValidationResult error(String errorMessage) {
        return ValidationResult.builder()
                .successful(false)
                .errorMessage(errorMessage)
                .build();
    }

    public boolean failed() {
        return !successful;
    }

    public String getErrorMessage() {
        return Message.PREFIX.message + errorMessage;
    }

    public void sendErrorTo(Player player) {
        if (!successful) {
            player.sendMessage(getErrorMessage());
        }
    }
}
