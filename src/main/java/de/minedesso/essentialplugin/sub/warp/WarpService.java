package de.minedesso.essentialplugin.sub.warp;

import de.minedesso.essentialplugin.EssentialPlugin;
import de.minedesso.essentialplugin.exception.warp.WarpCreateException;
import de.minedesso.essentialplugin.exception.warp.WarpDeleteException;
import de.minedesso.essentialplugin.exception.warp.WarpDoesNotExistException;
import de.minedesso.essentialplugin.sub.warp.cmd.WarpCommand;
import de.minedesso.essentialplugin.sub.warp.cmd.WarpsCommand;
import de.minedesso.essentialplugin.sub.warp.cmd.warpsSub.WarpsCreateSubCommand;
import de.minedesso.essentialplugin.sub.warp.cmd.warpsSub.WarpsDeleteSubCommand;
import de.minedesso.essentialplugin.sub.warp.cmd.warpsSub.WarpsHelpSubCommand;
import de.minedesso.essentialplugin.util.Messages;
import de.minedesso.essentialplugin.util.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WarpService {

    private static WarpService instance;
    private final WarpApi warpApi;
    private final Set<Player> cooldownPlayers = new HashSet<>();

    private static final int COOLDOWN_SECONDS = 5;

    public static synchronized WarpService getInstance() {
        if (instance == null) {
            instance = new WarpService();
        }
        return instance;
    }

    private WarpService() {
        this.warpApi = WarpApi.getInstance();

        initializeWarpCommands();
    }

    private void initializeWarpCommands() {
        WarpsCommand warpsCommand = new WarpsCommand(List.of(
                new WarpsCreateSubCommand(),
                new WarpsDeleteSubCommand(),
                new WarpsHelpSubCommand()
                // Add other sub-commands here
        ));

        EssentialPlugin plugin = EssentialPlugin.getInstance();
        if (plugin != null && plugin.getCommand("warps") != null) {
            plugin.getCommand("warps").setExecutor(warpsCommand);
        }
        if (plugin != null && plugin.getCommand("warp") != null) {
            plugin.getCommand("warp").setExecutor(new WarpCommand());
        }
    }

    public void teleportToWarp(Player player, String warpName) {
        handleCooldown(player);

        WarpDto warpDto = warpApi.fetchWarpByName(warpName);
        if (warpDto != null && player.hasPermission(warpDto.getPermission())) {
            player.teleport(warpDto.toLocation());
            player.sendMessage(Messages.PREFIX.message + "Teleported to warp '" + warpName + "'.");
        } else {
            player.sendMessage(Messages.PREFIX.message + "Warp '" + warpName + "' does not exist.");
        }
    }

    private void handleCooldown(Player player) {
        if(cooldownPlayers.contains(player)) {
            player.sendMessage(Messages.PREFIX.message + "You must wait " + COOLDOWN_SECONDS + " seconds before using another warp.");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
            return;
        }

        cooldownPlayers.add(player);
        Bukkit.getScheduler().scheduleSyncDelayedTask(EssentialPlugin.getInstance(), () -> {
            cooldownPlayers.remove(player);
        }, COOLDOWN_SECONDS * 20L);
    }

    public void displayAllWarps(CommandSender sender) {
        List<WarpDto> warps = warpApi.fetchWarps();

        StringBuilder warpList = new StringBuilder(Messages.PREFIX.message + "Available Warps: ");
        for (WarpDto warp : warps) {
            if (sender.hasPermission(warp.getPermission())) {
                warpList.append(warp.getName()).append(", ");
            }
        }
        if (warpList.length() > 2) {
            warpList.setLength(warpList.length() - 2); // Remove trailing comma and space
        } else {
            warpList.append("None");
        }
        sender.sendMessage(warpList.toString());
    }

    public void displayHelp(CommandSender sender) {
        String border = Messages.PREFIX.message + "Warp-Help";

        StringBuilder warpList = new StringBuilder(border + "\n");
        warpList.append("/warp <warpname> - Teleport to the specified warp.\n");
        warpList.append("/warps - List all available warps.\n");
        warpList.append("/warps help - Show this help message.\n");

        // Permission-based commands
        if(sender.hasPermission(Permission.WARP_CREATE.perm))
            warpList.append("/warps create <warpname> <permission>\n");
        if(sender.hasPermission(Permission.WARP_DELETE.perm))
            warpList.append("/warps delete <warpname>\n");

        sender.sendMessage(warpList.append(border).toString());
    }

    public void createWarp(String warpName, String permission, Location location, Player player) {
        WarpDto warpDto = warpApi.fetchWarpByName(warpName);
        if (warpDto != null) player.sendMessage(Messages.PREFIX.message + "Warp '" + warpName + "' already exists. Updating warp instead.");
        WarpDto newWarpDto = new WarpDto(warpName, permission, location);

        boolean success = warpApi.saveWarp(newWarpDto);
        if (!success) throw new WarpCreateException("Failed to create warp '" + warpName + "'.");
    }

    public void deleteWarp(String warpName) {
        WarpDto warpDto = warpApi.fetchWarpByName(warpName);
        if (warpDto == null) throw new WarpDoesNotExistException(warpName);

        boolean success = warpApi.deleteWarp(warpName);
        if (!success) throw new WarpDeleteException("Failed to delete warp '" + warpName + "'.");
    }
}
