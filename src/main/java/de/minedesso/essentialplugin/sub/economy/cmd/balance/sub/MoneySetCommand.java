package de.minedesso.essentialplugin.sub.economy.cmd.balance.sub;

import de.minedesso.essentialplugin.sub.economy.EconomyService;
import de.minedesso.essentialplugin.util.Message;
import de.minedesso.essentialplugin.util.Permission;
import de.minedesso.essentialplugin.util.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneySetCommand implements SubCommand {
    @Override
    public String name() {
        return "set";
    }

    @Override
    public String permission() {
        return Permission.MONEY_ADMIN.perm;
    }

    @Override
    public boolean playerOnly() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (!sender.hasPermission(permission())) {
            sender.sendMessage(Message.NO_PERMISSION.message);
            return;
        }

        if (args.length != 2) {
            sender.sendMessage(Message.USAGE.message + "/money set <player> <amount>");
            return;
        }

        EconomyService.getInstance().setBalance(player, args[0], args[1]);
    }
}