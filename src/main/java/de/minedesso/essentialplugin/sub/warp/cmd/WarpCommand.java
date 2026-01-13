package de.minedesso.essentialplugin.sub.warp.cmd;

import de.minedesso.essentialplugin.util.Messages;
import de.minedesso.essentialplugin.sub.warp.WarpService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if(args.length != 1) {
            player.sendMessage(Messages.USAGE.message + "/warp <warpname> | /warps to list warps.");
            return true;
        }

        WarpService.getInstance().teleportToWarp(player, args[0].toLowerCase());
        return false;
    }
}
