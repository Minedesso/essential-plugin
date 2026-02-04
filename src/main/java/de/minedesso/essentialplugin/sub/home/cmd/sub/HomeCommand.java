package de.minedesso.essentialplugin.sub.home.cmd.sub;

import de.minedesso.essentialplugin.exception.DoesNotExistException;
import de.minedesso.essentialplugin.sub.home.HomeService;
import de.minedesso.essentialplugin.util.Message;
import de.minedesso.essentialplugin.util.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements SubCommand {
    @Override
    public String name() {
        return "home";
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
        if(args.length != 1) {
            sender.sendMessage(Message.USAGE.message + "/home <name>");
            return;
        }

        Player player = (Player) sender;
        try {
            String homeName = args[0];
            HomeService.getInstance().teleportToHome(player, homeName);
        } catch (DoesNotExistException e) {
            player.sendMessage(Message.PREFIX.message + e.getMessage());
        }
    }
}
