package de.minedesso.essentialplugin.sub.tpa;

import de.minedesso.essentialplugin.EssentialPlugin;
import de.minedesso.essentialplugin.exception.DoesNotExistException;
import de.minedesso.essentialplugin.sub.tpa.cmd.TpaBaseCommand;
import de.minedesso.essentialplugin.sub.tpa.cmd.sub.*;
import de.minedesso.essentialplugin.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class TpaService {
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
    }

    private void initializeCommands() {
        TpaBaseCommand tpaBaseCommand = new TpaBaseCommand(List.of(
                new TpaCommand(),
                new TpaCancelCommand(),
                new TpAcceptCommand(),
                new TpDenyCommand(),
                new TpHelpCommand()
        ));

        EssentialPlugin.getInstance().getCommand("tpa").setExecutor(tpaBaseCommand);
    }

    public void displayHelp(CommandSender sender) {
        String border = Messages.PREFIX.message + "TP-Help";

        StringBuilder help = new StringBuilder(border).append("\n");
        help.append("/tpa <player> - Send a teleportation request to the specified player.\n");
        help.append("/tpcancel - Cancel your outgoing teleportation request.\n");
        help.append("/tpaccept [player] - Accept a teleportation request. Specify a player if you have multiple requests.\n");
        help.append("/tpdeny [player] - Deny a teleportation request. Denies all requests if no player is specified.\n");
        help.append("/tphelp - Shows this help message.\n");
        help.append(border);

        sender.sendMessage(help.toString());
    }

    public void sendRequest(Player sender, String targetName) {
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            throw new DoesNotExistException(Messages.PREFIX.message + "Player " + targetName + " does not exist or is not online!");
        }

        if (sender.getUniqueId().equals(target.getUniqueId())) {
            sender.sendMessage(Messages.ERROR.message + "You cannot send a teleport request to yourself!");
            return;
        }

        if (tpRequests.containsKey(sender.getUniqueId())) {
            sender.sendMessage(Messages.PREFIX.message + "You already have a pending teleport request! Cancel it first with /tpcancel");
            return;
        }

        tpRequests.put(sender.getUniqueId(), target.getUniqueId());
        incomingRequests.computeIfAbsent(target.getUniqueId(), k -> new ArrayList<>()).add(sender.getUniqueId());

        sender.sendMessage(Messages.PREFIX.message + "Teleport request sent to " + target.getName() + ". Request expires in " + REQUEST_TIMEOUT_SECONDS + " seconds.");
        target.sendMessage(Messages.PREFIX.message + sender.getName() + " wants to teleport to you. Use /tpaccept " + sender.getName());

        Bukkit.getScheduler().scheduleSyncDelayedTask(EssentialPlugin.getInstance(), () -> {
            if (tpRequests.containsKey(sender.getUniqueId())) {
                voidRequest(sender, VoidType.EXPIRATION);
            }
        }, REQUEST_TIMEOUT_SECONDS * 20L);
    }

    public void cancelRequest(Player sender) {
        if (!tpRequests.containsKey(sender.getUniqueId())) {
            sender.sendMessage(Messages.PREFIX.message + "You have no pending teleportation request!");
            return;
        }

        voidRequest(sender, VoidType.CANCELLATION);
    }

    public void acceptRequest(Player target, String acceptedPlayerName) {
        Player tpaSender = Bukkit.getPlayer(acceptedPlayerName);

        if (tpaSender == null) {
            throw new DoesNotExistException(Messages.PREFIX.message + "Player " + acceptedPlayerName + " does not exist or is not online!");
        }

        UUID senderId = tpaSender.getUniqueId();
        if (!tpRequests.containsKey(senderId) || !tpRequests.get(senderId).equals(target.getUniqueId())) {
            target.sendMessage(Messages.PREFIX.message + "You have no pending request from " + acceptedPlayerName + "!");
            return;
        }

        tpaSender.teleport(target.getLocation());
        tpaSender.playSound(tpaSender.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);

        voidRequest(tpaSender, VoidType.ACCEPTANCE);
    }

    public void denyRequest(Player target, String deniedPlayerName) {
        Player tpaSender = Bukkit.getPlayer(deniedPlayerName);

        if (tpaSender == null) {
            throw new DoesNotExistException(Messages.PREFIX.message + "Player " + deniedPlayerName + " does not exist or is not online!");
        }

        UUID senderId = tpaSender.getUniqueId();
        if (!tpRequests.containsKey(senderId) || !tpRequests.get(senderId).equals(target.getUniqueId())) {
            target.sendMessage(Messages.PREFIX.message + "You have no pending request from " + deniedPlayerName + "!");
            return;
        }

        voidRequest(tpaSender, VoidType.REFUSAL);
    }

    private void voidRequest(Player sender, VoidType voidType) {
        UUID senderId = sender.getUniqueId();
        UUID targetId = tpRequests.get(senderId);

        if (targetId == null) return;

        Player target = Bukkit.getPlayer(targetId);

        tpRequests.remove(senderId);
        List<UUID> incoming = incomingRequests.get(targetId);
        if (incoming != null) {
            incoming.remove(senderId);
            if (incoming.isEmpty()) {
                incomingRequests.remove(targetId);
            }
        }

        String senderMessage = switch (voidType) {
            case EXPIRATION -> "Your teleport request has expired.";
            case REFUSAL -> "Your teleport request has been denied.";
            case CANCELLATION -> "Your teleport request has been cancelled successfully.";
            case ACCEPTANCE -> "Teleporting to " + (target != null ? target.getName() : "player") + "...";
        };

        String targetMessageSuffix = switch (voidType) {
            case EXPIRATION -> "'s teleport request has expired.";
            case REFUSAL -> "'s teleport request has been denied.";
            case CANCELLATION -> " has cancelled their teleport request.";
            case ACCEPTANCE -> " has been teleported to you.";
        };

        sender.sendMessage(Messages.PREFIX.message + senderMessage);

        if (target != null) {
            int remainingCount = incoming != null ? incoming.size() : 0;
            target.sendMessage(Messages.PREFIX.message + sender.getName() + targetMessageSuffix
                    + " You now have " + remainingCount + " pending request(s).");
        }
    }

    private enum VoidType {
        EXPIRATION,
        REFUSAL,
        CANCELLATION,
        ACCEPTANCE
    }
}