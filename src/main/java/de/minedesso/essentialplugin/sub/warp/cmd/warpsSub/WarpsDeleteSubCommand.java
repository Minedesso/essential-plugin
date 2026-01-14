package de.minedesso.essentialplugin.sub.warp.cmd.warpsSub;

import de.minedesso.essentialplugin.exception.CouldNotDeleteException;
import de.minedesso.essentialplugin.exception.DoesNotExistException;
import de.minedesso.essentialplugin.util.Messages;
import de.minedesso.essentialplugin.util.Permission;
import de.minedesso.essentialplugin.util.SubCommand;
import de.minedesso.essentialplugin.sub.warp.WarpService;
import org.bukkit.command.CommandSender;

public class WarpsDeleteSubCommand implements SubCommand {
    @Override
    public String name() {
        return "delete";
    }

    @Override
    public String permission() {
        return Permission.WARP_DELETE.perm;
    }

    @Override
    public boolean playerOnly() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length != 1) {
            sender.sendMessage(Messages.USAGE.message + "/warps delete <warpname>");
            return;
        }

        try {
            String warpName = args[0].toLowerCase();

            WarpService.getInstance().deleteWarp(warpName);
            sender.sendMessage(Messages.PREFIX.message + "Warp '" + warpName + "' deleted successfully.");
        } catch (DoesNotExistException e) {
            sender.sendMessage(Messages.PREFIX.message + e.getMessage());
        } catch (CouldNotDeleteException e) {
            sender.sendMessage(Messages.ERROR.message + e.getMessage());
        }
    }
}
