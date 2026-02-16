package de.minedesso.essentialplugin.sub.tpa.cmd.validator;

import de.minedesso.essentialplugin.sub.tpa.TpaService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TpaRequestValidator {

    public static ValidationResult validate(ValidationContext context) {
        if (!isSenderValid(context)) {
            return ValidationResult.error("Something went wrong executing this command!");
        }

        if (context.isTargetRequired() && !isTargetSpecified(context)) {
            return ValidationResult.error("Target has to be specified.");
        }

        if (context.shouldPreventSelfTargeting() && isSelfTargeting(context)) {
            return ValidationResult.error("You cannot send a teleport request to yourself.");
        }

        if (context.requiresTargetOnline()) {
            Player onlineTarget = findOnlinePlayer(context.getTargetName());
            if (onlineTarget == null) {
                return ValidationResult.error("Player " + context.getTargetName() + " does not exist or is not online!");
            }
            context.setTarget(onlineTarget);
        }

        boolean hasOutgoingRequest = TpaService.getInstance().hasOutgoingRequest(context.getSender());

        if (context.requiresNoOutgoingRequest() && hasOutgoingRequest) {
            return ValidationResult.error("You already have an outgoing teleport request! Cancel it first with /tpcancel");
        }

        if (context.requiresOutgoingRequest() && !hasOutgoingRequest) {
            return ValidationResult.error("You have no outgoing teleport request!");
        }

        if (context.requiresPendingRequest()) {
            boolean hasPendingRequest = TpaService.getInstance().hasRequestFromSenderToReceiver(
                    context.getTarget(),
                    context.getSender()
            );
            if (!hasPendingRequest) {
                return ValidationResult.error("You have no pending request from " + context.getTarget().getName() + "!");
            }
        }

        return ValidationResult.success(context.getTarget());
    }

    private static boolean isSenderValid(ValidationContext context) {
        return context.getSender() != null;
    }

    private static boolean isTargetSpecified(ValidationContext context) {
        return context.getTarget() != null || context.getTargetName() != null;
    }

    private static boolean isSelfTargeting(ValidationContext context) {
        return context.getTarget() != null &&
                context.getSender().getName().equals(context.getTargetName());
    }

    private static Player findOnlinePlayer(String playerName) {
        return Bukkit.getPlayer(playerName);
    }
}