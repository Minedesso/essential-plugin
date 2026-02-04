package de.minedesso.essentialplugin.sub.home.cmd.sub;

import de.minedesso.essentialplugin.exception.AlreadyExistsException;
import de.minedesso.essentialplugin.exception.CouldNotCreateException;
import de.minedesso.essentialplugin.sub.home.HomeService;
import de.minedesso.essentialplugin.util.Message;
import de.minedesso.essentialplugin.util.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand implements SubCommand {
    @Override
    public String name() {
        return "sethome";
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
            sender.sendMessage(Message.USAGE.message + "/sethome <name>");
            return;
        }

        Player player = (Player) sender;

        try {
            String homeName = args[0];
            HomeService.getInstance().setHome(player.getUniqueId(), homeName, player.getLocation());
            player.sendMessage(Message.PREFIX.message + "Home '" + homeName + "' set successfully.");
        } catch (AlreadyExistsException | CouldNotCreateException e) {
            player.sendMessage(Message.PREFIX.message + e.getMessage());
        } catch (IllegalArgumentException e) {
            player.sendMessage(Message.ERROR.message + e.getMessage());
        }
    }
}
