package de.minedesso.essentialplugin.sub.gamemode;

import de.minedesso.essentialplugin.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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

        GameMode gameMode = GameMode.valueOf(args[0]);

        if (gameMode == null) {
            player.sendMessage(Message.ERROR.message + "Invalid gamemode. Use 0, 1, 2, or 3.");
            return false;
        }

        switch (args.length) {
            case 1 -> {
                GamemodeService.getInstance().handleGamemodeCommand(player, gameMode);
            }
            case 2 -> {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(Message.PLAYER_NOT_FOUND.message);
                    return false;
                }
                GamemodeService.getInstance().handleGamemodeCommand(player, target, gameMode);
            }
            default -> {
                player.sendMessage(Message.USAGE.message + "/gamemode <0|1|2|3> [player]");
            }
        }

        return false;
    }
}
