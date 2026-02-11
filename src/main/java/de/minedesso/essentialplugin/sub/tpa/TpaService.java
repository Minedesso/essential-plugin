package de.minedesso.essentialplugin.sub.tpa;

import de.minedesso.essentialplugin.EssentialPlugin;
import de.minedesso.essentialplugin.sub.tpa.cmd.TpaBaseCommand;
import de.minedesso.essentialplugin.sub.tpa.cmd.sub.*;
import de.minedesso.essentialplugin.util.Message;
import de.minedesso.essentialplugin.util.TpaStatus;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import de.minedesso.essentialplugin.sub.tpa.TpaRequestValidator.*;

import java.util.*;

public class TpaService implements Listener {
    private static TpaService instance;
    private static final int REQUEST_TIMEOUT_SECONDS = 90;

    private final Map<UUID, UUID> tpRequests = new HashMap<>();
    private final Map<UUID, List<UUID>> incomingRequests = new HashMap<>();

    public static synchronized TpaService getInstance() {
        if (instance == null) {
            instance = new TpaService();
        }
        return instance;
    }

    private TpaService() {
        initializeCommands();
        registerListener();
    }

    private void initializeCommands() {
        TpaBaseCommand tpaBaseCommand = new TpaBaseCommand(List.of(
                new TpaCommand(),
                new TpCancelCommand(),
                new TpAcceptCommand(),
                new TpDenyCommand(),
                new TpHelpCommand()
        ));

        EssentialPlugin.getInstance().getCommand("tpa").setExecutor(tpaBaseCommand);
    }

    private void registerListener() {
        Bukkit.getPluginManager().registerEvents(this, EssentialPlugin.getInstance());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // requestSender is leaving
        if (tpRequests.containsKey(playerId)) {
            voidRequest(player, TpaStatus.SENDER_DISCONNECT);
        }

        // requestReciever is leaving
        List<UUID> incomingRequestList = incomingRequests.get(playerId);
        if (incomingRequestList != null) {
            List<UUID> requestsCopy = new ArrayList<>(incomingRequestList);
            for (UUID senderId : requestsCopy) {
                Player sender = Bukkit.getPlayer(senderId);
                if (sender != null) {
                    cleanupRequestByTarget(sender, player);
                }
            }
        }
    }

    public void displayHelp(CommandSender sender) {
        String border = Message.PREFIX.message + "TP-Help";

        String help = border + "\n" +
                "/tpa <player> - Send a teleportation request to the specified player.\n" +
                "/tpaccept [player] - Accept a teleportation request. Specify a player if you have multiple requests.\n" +
                "/tpdeny [player] - Deny a teleportation request. Denies all requests if no player is specified.\n" +
                "/tpcancel - Cancel your outgoing teleportation request.\n" +
                "/tphelp - Shows this help message.\n" +
                border;

        sender.sendMessage(help);
    }

    public void sendRequest(Player sender, String targetName) {
        ValidationResult result = TpaRequestValidator.validate(
                new ValidationContext()
                        .sender(sender)
                        .targetName(targetName)
                        .checkSelfTarget()
                        .requireTargetOnline()
                        .requireNoOutgoingRequest()
        );

        if (result.failed()) {
            result.sendErrorTo(sender);
            return;
        }

        Player target = result.resolvedTarget;

        tpRequests.put(sender.getUniqueId(), target.getUniqueId());
        incomingRequests.computeIfAbsent(target.getUniqueId(), k -> new ArrayList<>()).add(sender.getUniqueId());

        sender.sendMessage(Message.PREFIX.message + "Teleport request sent to " + target.getName() + ".\n" + Message.PREFIX.message + "Request expires in " + REQUEST_TIMEOUT_SECONDS + " seconds.");
        target.sendMessage(Message.PREFIX.message + sender.getName() + " have sent a teleportation request to you."
                + "\n" + Message.PREFIX.message + "Use /tpaccept " + sender.getName() + " - to accept the request");

        scheduleRequestTimeout(sender);
    }

    public void cancelOutgoingRequest(Player sender) {
        ValidationResult result = TpaRequestValidator.validate(
                new ValidationContext()
                        .sender(sender)
                        .requireOutgoingRequest()
        );

        if (result.failed()) {
            result.sendErrorTo(sender);
            return;
        }

        voidRequest(sender, TpaStatus.CANCELLATION);
    }

    public void acceptRequest(Player requestReceiver, String requestSenderName) {
        ValidationResult result = TpaRequestValidator.validate(
                new ValidationContext()
                        .sender(requestReceiver)
                        .targetName(requestSenderName)
                        .requireTargetOnline()
                        .checkSelfTarget()
                        .requirePendingRequest()
        );

        if (result.failed()) {
            result.sendErrorTo(requestReceiver);
            return;
        }

        Player requestSender = result.resolvedTarget;

        requestSender.teleport(requestReceiver.getLocation());
        requestSender.playSound(requestReceiver.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);

        voidRequest(requestSender, TpaStatus.ACCEPTANCE);
    }

    public void denyRequest(Player requestReceiver, String requestSenderName) {
        ValidationResult result = TpaRequestValidator.validate(
                new ValidationContext()
                        .sender(requestReceiver)
                        .targetName(requestSenderName)
                        .requireTargetOnline()
                        .checkSelfTarget()
                        .requirePendingRequest()
        );

        if (result.failed()) {
            result.sendErrorTo(requestReceiver);
            return;
        }

        Player tpaSender = result.resolvedTarget;

        voidRequest(tpaSender, TpaStatus.REFUSAL);
    }

    private void scheduleRequestTimeout(Player sender) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(EssentialPlugin.getInstance(), () -> {
            if (tpRequests.containsKey(sender.getUniqueId())) {
                voidRequest(sender, TpaStatus.EXPIRATION);
            }
        }, REQUEST_TIMEOUT_SECONDS * 20L);
    }

    private void voidRequest(Player sender, TpaStatus tpaStatus) {
        UUID senderId = sender.getUniqueId();
        UUID targetId = tpRequests.get(senderId);

        if (targetId == null) return;

        Player target = Bukkit.getPlayer(targetId);

        cleanupRequest(senderId, targetId);
        notifySender(sender, tpaStatus, target);

        notifyTarget(sender, target, tpaStatus);
    }

    private void cleanupRequestByTarget(Player sender, Player target) {
        UUID senderId = sender.getUniqueId();
        UUID targetId = target.getUniqueId();

        cleanupRequest(senderId, targetId);
        notifySender(sender, TpaStatus.TARGET_DISCONNECT, target);
    }

    private void cleanupRequest(UUID senderId, UUID targetId) {
        tpRequests.remove(senderId);
        List<UUID> incoming = incomingRequests.get(targetId);
        if (incoming != null) {
            incoming.remove(senderId);
            if (incoming.isEmpty()) {
                incomingRequests.remove(targetId);
            }
        }
    }

    private void notifySender(Player sender, TpaStatus reason, Player target) {
        String message = switch (reason) {
            case EXPIRATION -> "Teleport request expired.";
            case REFUSAL -> target.getName() + " denied your teleportation request.";
            case CANCELLATION -> "Teleport request cancelled.";
            case TARGET_DISCONNECT -> "Your teleport request to " + (target != null ? target.getName() : "player") + " was cancelled (they disconnected).";
            case ACCEPTANCE, SENDER_DISCONNECT -> null;
        };

        if (message != null) {
            sender.sendMessage(Message.PREFIX.message + message);
        }
    }

    private void notifyTarget(Player sender, Player target, TpaStatus reason) {
        if (target == null) return;

        String message = switch (reason) {
            case EXPIRATION -> "'s teleport request has expired.";
            case REFUSAL -> "'s teleport request has been denied.";
            case CANCELLATION, SENDER_DISCONNECT -> " has cancelled their teleport request.";
            case TARGET_DISCONNECT, ACCEPTANCE -> null;
        };

        if (message != null) {
            target.sendMessage(Message.PREFIX.message + sender.getName() + message);
        }
    }

    // access for request validator
    public boolean hasOutgoingRequest(Player sender) {
        return tpRequests.containsKey(sender.getUniqueId());
    }

    public boolean hasRequestFromSenderToReceiver(Player requestSender, Player requestReceiver) {
        UUID senderId = requestSender.getUniqueId();
        return tpRequests.containsKey(senderId) &&
                tpRequests.get(senderId).equals(requestReceiver.getUniqueId());
    }
}