package de.minedesso.essentialplugin.sub.warp.cmd.warpsSub;

import de.minedesso.essentialplugin.util.Message;
import de.minedesso.essentialplugin.util.SubCommand;
import de.minedesso.essentialplugin.sub.warp.WarpService;
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
            sender.sendMessage(Message.USAGE.message + "/warps help");
        }
    }
}
