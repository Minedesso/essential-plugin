package de.minedesso.essentialPlugin.warp.cmd.warpsSub;

import de.minedesso.essentialPlugin.exception.WarpAlreadyExistsException;
import de.minedesso.essentialPlugin.exception.WarpCreateException;
import de.minedesso.essentialPlugin.util.Messages;
import de.minedesso.essentialPlugin.util.Permission;
import de.minedesso.essentialPlugin.util.SubCommand;
import de.minedesso.essentialPlugin.warp.WarpDto;
import de.minedesso.essentialPlugin.warp.WarpService;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCreateSubCommand implements SubCommand {
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
        } catch (WarpAlreadyExistsException e) {
            player.sendMessage(Messages.PREFIX.message + e.getMessage());
        } catch (WarpCreateException e) {
            player.sendMessage(Messages.ERROR.message + e.getMessage());
        }
    }
}
