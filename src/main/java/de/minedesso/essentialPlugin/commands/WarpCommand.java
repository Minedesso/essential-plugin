package de.minedesso.essentialPlugin.commands;

import de.minedesso.essentialPlugin.service.WarpService;
import de.minedesso.essentialPlugin.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;

        if(args.length != 1) {
            player.sendMessage(Messages.PREFIX.message + "Usage: /warp <warpname> | /warps to list warps.");
            return true;
        }

        WarpService.getInstance().teleportToWarp(player, args[0]);
        return false;
    }
}
