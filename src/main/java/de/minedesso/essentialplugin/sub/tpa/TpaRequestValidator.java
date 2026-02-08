package de.minedesso.essentialplugin.sub.tpa;

import de.minedesso.essentialplugin.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaRequestValidator {
    public static ValidationResult validate(ValidationContext context) {
        // Sender cannot be target
        if (context.checkSelfTarget && context.sender != null && context.target != null) {
            if (context.sender.getUniqueId().equals(context.target.getUniqueId())) {
                return ValidationResult.error("You as the sender can not be a target on this command.");
            }
        }

        // Check if target exists/is online
        if (context.requireTargetOnline && context.targetName != null) {
            Player target = Bukkit.getPlayer(context.targetName);
            if (target == null) {
                return ValidationResult.error("Player " + context.targetName + " does not exist or is not online!");
            }
            context.target = target;
        }

        // Check if sender has outgoing request sender can't have any
        if (context.requireNoOutgoingRequest && context.sender != null) {
            if (TpaService.getInstance().hasOutgoingRequest(context.sender)) {
                return ValidationResult.error("You already have an outgoing teleport request! Cancel it first with /tpcancel");
            }
        }

        // Check if sender has outgoing request sender has to have one
        if (context.requireOutgoingRequest && context.sender != null) {
            if (!TpaService.getInstance().hasOutgoingRequest(context.sender)) {
                return ValidationResult.error("You have no outgoing teleport request!");
            }
        }

        // Check if pending request exists
        if (context.requirePendingRequest && context.sender != null && context.target != null) {
            if (!TpaService.getInstance().hasPendingRequest(context.sender, context.target)) {
                return ValidationResult.error("You have no pending request from " + context.target.getName() + "!");
            }
        }

        ValidationResult result = ValidationResult.success();
        result.resolvedTarget = context.target;
        return result;
    }

    public static class ValidationContext {
        Player sender;
        Player target;
        String targetName;
        boolean checkSelfTarget = false;
        boolean requireTargetOnline = false;
        boolean requireNoOutgoingRequest = false;
        boolean requireOutgoingRequest = false;
        boolean requirePendingRequest = false;

        public static ValidationContext builder() {
            return new ValidationContext();
        }

        public ValidationContext sender(Player sender) {
            this.sender = sender;
            return this;
        }

        public ValidationContext target(Player target) {
            this.target = target;
            return this;
        }

        public ValidationContext targetName(String name) {
            this.targetName = name;
            return this;
        }

        public ValidationContext checkSelfTarget() {
            this.checkSelfTarget = true;
            return this;
        }

        public ValidationContext requireTargetOnline() {
            this.requireTargetOnline = true;
            return this;
        }

        public ValidationContext requireNoOutgoingRequest() {
            this.requireNoOutgoingRequest = true;
            return this;
        }

        public ValidationContext requireOutgoingRequest() {
            this.requireOutgoingRequest = true;
            return this;
        }

        public ValidationContext requirePendingRequest() {
            this.requirePendingRequest = true;
            return this;
        }
    }

    public static class ValidationResult {
        private final boolean success;
        private final String errorMessage;
        public Player resolvedTarget;

        private ValidationResult(boolean success, String errorMessage) {
            this.success = success;
            this.errorMessage = errorMessage;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }

        public boolean isValid() {
            return success;
        }

        public String getErrorMessage() {
            return Message.PREFIX.message + errorMessage;
        }

        public void sendErrorTo(CommandSender sender) {
            if (!success) {
                sender.sendMessage(getErrorMessage());
            }
        }
    }
}
