package de.minedesso.essentialplugin.sub.gamemode;

import de.minedesso.essentialplugin.util.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Message.ONLY_PLAYER.message);
            return false;
        }

        Player target = null;
        switch(args.length) {
            case 0 -> {
                //error message "invalid arguments";
            }
            case 1 -> {
                target = player.getServer().getPlayer(sender.getName());
            }
            case 2 -> {
                target = player.getServer().getPlayer(args[1]);

            }
        }

        if(target != null) {
            GamemodeService.getInstance().handleGamemodeCommand(player, target, args);
        }

        return false;
    }
}
