package de.minedesso.essentialplugin.sub.tpa;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

// TODO: Refactor to PlayerQuitListener and inject multiple services if reused?
public class TpaEventListener implements Listener {
    private final TpaService tpaService;

    public TpaEventListener(TpaService tpaService) {
        this.tpaService = tpaService;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player disconnectedPlayer = event.getPlayer();
        UUID disconnectedPlayerId = disconnectedPlayer.getUniqueId();

        tpaService.handleSenderDisconnect(disconnectedPlayer);
        tpaService.handleReceiverDisconnect(disconnectedPlayer, disconnectedPlayerId);
    }

}