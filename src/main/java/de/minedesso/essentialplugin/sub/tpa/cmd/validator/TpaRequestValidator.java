package de.minedesso.essentialplugin.sub.tpa.cmd.validator;

import de.minedesso.essentialplugin.sub.tpa.TpaService;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@UtilityClass
public class TpaRequestValidator {

    public ValidationResult validate(ValidationContext context) {
        if (!isSenderValid(context)) {
            return ValidationResult.error("Something went wrong executing this command!");
        }

        if (context.isTargetRequired() && !isTargetSpecified(context)) {
            return ValidationResult.error("Target has to be specified.");
        }

        if (context.isPreventSelfTargeting() && isSelfTargeting(context)) {
            return ValidationResult.error("You cannot perform a teleport action on yourself.");
        }

        if (context.isRequireTargetOnline()) {
            Player onlineTarget = findOnlinePlayer(context.getTargetName());
            if (onlineTarget == null) {
                return ValidationResult.error("Player " + context.getTargetName() + " does not exist or is not online!");
            }
            context.setTarget(onlineTarget);
        }

        boolean hasOutgoingRequest = TpaService.getInstance().hasOutgoingRequest(context.getSender());

        if (context.isRequireNoOutgoingRequest() && hasOutgoingRequest) {
            return ValidationResult.error("You already have an outgoing teleport request! Cancel it first with /tpcancel");
        }

        if (context.isRequireOutgoingRequest() && !hasOutgoingRequest) {
            return ValidationResult.error("You have no outgoing teleport request!");
        }

        if (context.isRequirePendingRequest()) {
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

    private boolean isSenderValid(ValidationContext context) {
        return context.getSender() != null;
    }

    private boolean isTargetSpecified(ValidationContext context) {
        return context.getTarget() != null || context.getTargetName() != null;
    }

    private boolean isSelfTargeting(ValidationContext context) {
        if (context.getTarget() != null) {
            return context.getSender().getName().equals(context.getTarget().getName());
        }
        // If target is not resolved yet, compare sender name with target name
        return context.getTargetName() != null &&
                context.getSender().getName().equalsIgnoreCase(context.getTargetName());
    }

    private Player findOnlinePlayer(String playerName) {
        return Bukkit.getPlayer(playerName);
    }
}
