package de.minedesso.essentialPlugin.warp.cmd.warpsSub;

import de.minedesso.essentialPlugin.exception.WarpDeleteException;
import de.minedesso.essentialPlugin.exception.WarpDoesNotExistException;
import de.minedesso.essentialPlugin.util.Messages;
import de.minedesso.essentialPlugin.util.Permission;
import de.minedesso.essentialPlugin.util.SubCommand;
import de.minedesso.essentialPlugin.warp.WarpService;
import org.bukkit.command.CommandSender;

public class WarpDeleteSubCommand implements SubCommand {
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
        } catch (WarpDoesNotExistException e) {
            sender.sendMessage(Messages.PREFIX.message + e.getMessage());
        } catch (WarpDeleteException e) {
            sender.sendMessage(Messages.ERROR.message + e.getMessage());
        }
    }
}
