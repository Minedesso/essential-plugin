package de.minedesso.essentialplugin.sub.fly;

import de.minedesso.essentialplugin.util.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Message.ONLY_PLAYER.message);
            return false;
        }

        if (args.length == 0) {

            FlyService.getInstance().handleFlyCommand(player, player);
            return false;

        } else if (args.length == 1) {
            Player target = player.getServer().getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(Message.PLAYER_NOT_FOUND.message);
                return false;
            }

            FlyService.getInstance().handleFlyCommand(player, target);
            return false;
        }
        return false;
    }
}
