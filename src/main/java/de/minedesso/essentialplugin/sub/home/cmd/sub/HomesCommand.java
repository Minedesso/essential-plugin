package de.minedesso.essentialplugin.sub.home.cmd.sub;

import de.minedesso.essentialplugin.sub.home.HomeService;
import de.minedesso.essentialplugin.util.Message;
import de.minedesso.essentialplugin.util.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomesCommand implements SubCommand {
    @Override
    public String name() {
        return "homes";
    }

    @Override
    public String permission() {
        return null;
    }

    @Override
    public boolean playerOnly() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 0) {
            sender.sendMessage(Message.USAGE.message + "/homes");
            return;
        }

        HomeService.getInstance().displayHomes((Player) sender);
    }
}
