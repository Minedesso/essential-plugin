package de.minedesso.essentialplugin.sub.tpa.cmd.validator;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

public class ValidationContext {
    @Getter
    private Player sender;
    @Setter
    @Getter
    private Player target;
    @Getter
    private String targetName;

    // is only optional in CancelCommand
    private boolean targetIsRequired = true;
    private boolean shouldPreventSelfTargeting = false;
    private boolean requireTargetOnline = false;
    private boolean requireNoOutgoingRequest = false;
    private boolean requireOutgoingRequest = false;
    private boolean requirePendingRequest = false;

    public boolean shouldPreventSelfTargeting() {
        return shouldPreventSelfTargeting;
    }

    public boolean isTargetRequired() {
        return targetIsRequired;
    }

    public boolean requiresTargetOnline() {
        return requireTargetOnline;
    }

    public boolean requiresNoOutgoingRequest() {
        return requireNoOutgoingRequest;
    }

    public boolean requiresOutgoingRequest() {
        return requireOutgoingRequest;
    }

    public boolean requiresPendingRequest() {
        return requirePendingRequest;
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
        this.shouldPreventSelfTargeting = true;
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

    public ValidationContext targetIsOptional() {
        this.targetIsRequired = false;
        return this;
    }
}