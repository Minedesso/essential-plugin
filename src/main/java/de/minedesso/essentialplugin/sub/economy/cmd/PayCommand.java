package de.minedesso.essentialplugin.sub.economy.cmd;

import de.minedesso.essentialplugin.exception.InvalidAmountException;
import de.minedesso.essentialplugin.sub.economy.EconomyService;
import de.minedesso.essentialplugin.util.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Message.ONLY_PLAYER.message);
            return true;
        }

        if(args.length != 2) {
            player.sendMessage(Message.USAGE.message + "/pay <player> <amount>");
            return true;
        }

        try {
            String receiverName = args[0];
            String amount = args[1];

            EconomyService.getInstance().pay(player, receiverName, amount);
        } catch (InvalidAmountException e) {
            player.sendMessage(Message.ERROR.message + e.getMessage());
        }
        return false;
    }
}
