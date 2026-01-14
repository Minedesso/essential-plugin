package de.minedesso.essentialplugin.sub.warp.cmd.warpsSub;

import de.minedesso.essentialplugin.exception.AlreadyExistsException;
import de.minedesso.essentialplugin.exception.CouldNotCreateException;
import de.minedesso.essentialplugin.util.Messages;
import de.minedesso.essentialplugin.util.Permission;
import de.minedesso.essentialplugin.util.SubCommand;
import de.minedesso.essentialplugin.sub.warp.WarpService;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpsCreateSubCommand implements SubCommand {
    @Override
    public String name() {
        return "create";
    }

    @Override
    public String permission() {
        return Permission.WARP_CREATE.perm;
    }

    @Override
    public boolean playerOnly() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if(args.length != 1 && args.length != 2) {
            player.sendMessage(Messages.USAGE.message + "/warps create <warpname> <permission>");
            return;
        }

        try {
            String warpName = args[0].toLowerCase();
            String permission = null;
            if(args.length == 2) permission = Permission.WARP_USE.perm + args[1];

            Location location = player.getLocation();

            WarpService.getInstance().createWarp(warpName, permission, location, player);
            player.sendMessage(Messages.PREFIX.message + "Warp '" + warpName + "' created successfully.");
        } catch (AlreadyExistsException e) {
            player.sendMessage(Messages.PREFIX.message + e.getMessage());
        } catch (CouldNotCreateException e) {
            player.sendMessage(Messages.ERROR.message + e.getMessage());
        }
    }
}
