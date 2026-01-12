package de.minedesso.essentialPlugin.warp.cmd.warpsSub;

import de.minedesso.essentialPlugin.util.Messages;
import de.minedesso.essentialPlugin.util.SubCommand;
import de.minedesso.essentialPlugin.warp.WarpService;
import org.bukkit.command.CommandSender;

public class WarpsHelpSubCommand implements SubCommand {
    @Override
    public String name() {
        return "help";
    }

    @Override
    public String permission() {
        return null;
    }

    @Override
    public boolean playerOnly() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0) {
            WarpService.getInstance().displayHelp(sender);
        } else {
            sender.sendMessage(Messages.USAGE.message + "/warps help");
        }
    }
}
