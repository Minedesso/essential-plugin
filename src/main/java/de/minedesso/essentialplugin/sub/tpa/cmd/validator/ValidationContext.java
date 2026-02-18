package de.minedesso.essentialplugin.sub.tpa.cmd.validator;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
@Setter
@Builder(toBuilder = true)
public class ValidationContext {
    private Player sender;
    private Player target;
    private String targetName;

    private boolean targetRequired;
    private boolean preventSelfTargeting;
    private boolean requireTargetOnline;
    private boolean requireNoOutgoingRequest;
    private boolean requireOutgoingRequest;
    private boolean requirePendingRequest;
}
