package de.minedesso.essentialplugin.sub.tpa;

import de.minedesso.essentialplugin.util.TpaStatus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TpaEventListener implements Listener {
    private final TpaService tpaService;

    public TpaEventListener(TpaService tpaService) {
        this.tpaService = tpaService;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player disconnectedPlayer = event.getPlayer();
        UUID disconnectedPlayerId = disconnectedPlayer.getUniqueId();

        handleSenderDisconnect(disconnectedPlayer, disconnectedPlayerId);
        handleReceiverDisconnect(disconnectedPlayer, disconnectedPlayerId);
    }

    private void handleSenderDisconnect(Player disconnectedPlayer, UUID disconnectedPlayerId) {
        boolean playerSentRequest = tpaService.hasOutgoingRequest(disconnectedPlayer);
        if (playerSentRequest) {
            tpaService.voidRequest(disconnectedPlayer, TpaStatus.SENDER_DISCONNECT);
        }
    }

    private void handleReceiverDisconnect(Player disconnectedPlayer, UUID disconnectedPlayerId) {
        List<UUID> sendersWhoRequestedThisPlayer = tpaService.getIncomingRequestSenders(disconnectedPlayerId);

        if (sendersWhoRequestedThisPlayer == null || sendersWhoRequestedThisPlayer.isEmpty()) {
            return;
        }

        List<UUID> senderIdsCopy = new ArrayList<>(sendersWhoRequestedThisPlayer);
        for (UUID senderId : senderIdsCopy) {
            Player requestSender = Bukkit.getPlayer(senderId);
            if (requestSender != null) {
                tpaService.cleanupRequestByTarget(requestSender, disconnectedPlayer);
            }
        }
    }
}