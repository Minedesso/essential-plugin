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

        if (args.length == 0) {
            //GamemodeService.getInstance().handleGamemodeCommand(player, player, args);
            //return false;

        } else if (args.length == 1) {
            Player target = player.getServer().getPlayer(args[0]);

        } else if (args.length == 2) {
            Player target = player.getServer().getPlayer(args[1]);

            if (target == null) {
                player.sendMessage(Message.PLAYER_NOT_FOUND.message);
                return false;
            }

            GamemodeService.getInstance().handleGamemodeCommand(player, target, args);
            return false;
        }
        return false;
    }
}
