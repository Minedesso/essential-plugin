package de.minedesso.essentialplugin.util;

import de.minedesso.essentialplugin.EssentialPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HandleCooldownUtil {

    private final List<UUID> cooldownPlayers;
    private final int defaultCooldownSeconds;

    public HandleCooldownUtil(int defaultCooldownSeconds) {
        this.cooldownPlayers = new ArrayList<>();
        this.defaultCooldownSeconds = defaultCooldownSeconds;
    }

    public void handleCooldown(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if(player == null) return;

        if(cooldownPlayers.contains(player.getUniqueId())) {
            player.sendMessage(Message.PREFIX.message + "You must wait " + defaultCooldownSeconds + " seconds to perform this command again.");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
            return;
        }

        cooldownPlayers.add(player.getUniqueId());
        Bukkit.getScheduler().scheduleSyncDelayedTask(EssentialPlugin.getInstance(), () -> {
            cooldownPlayers.remove(player.getUniqueId());
        }, defaultCooldownSeconds * 20L);
    }

}
