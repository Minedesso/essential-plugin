package de.minedesso.essentialplugin.sub.tpa;

import de.minedesso.essentialplugin.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaRequestValidator {
    public static ValidationResult validate(ValidationContext context) {
        boolean senderHasOutgoingRequest = TpaService.getInstance().hasOutgoingRequest(context.sender);

        // target cannot be null
        if(context.target == null && context.targetName == null) {
            return new ValidationResult(false, "Target has to be specified.");
        }

        // Sender cannot be target
        if (context.checkSelfTarget) {
            if (context.sender.getUniqueId().equals(context.target.getUniqueId())) {
                return new ValidationResult(false,"You as the sender can not be a target on this command.");
            }
        }

        // Check if target exists/is online
        if (context.requireTargetOnline) {
            Player target = Bukkit.getPlayer(context.targetName);
            if (target == null) {
                return new ValidationResult(false, "Player " + context.targetName + " does not exist or is not online!");
            }
            context.target = target;
        }

        // Check if sender has outgoing request sender can't have any
        if (context.requireNoOutgoingRequest) {
            if (senderHasOutgoingRequest) {
                return new ValidationResult(false, "You already have an outgoing teleport request! Cancel it first with /tpcancel");
            }
        }

        // Check if sender has outgoing request sender has to have one
        if (context.requireOutgoingRequest) {
            if (!senderHasOutgoingRequest) {
                return new ValidationResult(false, "You have no outgoing teleport request!");
            }
        }

        // Check if pending request exists
        if (context.requirePendingRequest) {
            if (!TpaService.getInstance().hasRequestFromSenderToReceiver(context.sender, context.target)) {
                return new ValidationResult(false, "You have no pending request from " + context.target.getName() + "!");
            }
        }

        ValidationResult result = new ValidationResult(true, "");
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

        public boolean failed() {
            return !success;
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
