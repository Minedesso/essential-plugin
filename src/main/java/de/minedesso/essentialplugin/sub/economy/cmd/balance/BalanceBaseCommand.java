package de.minedesso.essentialplugin.sub.economy.cmd.balance;

import de.minedesso.essentialplugin.sub.economy.cmd.balance.sub.MoneyCommand;
import de.minedesso.essentialplugin.sub.economy.cmd.balance.sub.MoneySetCommand;
import de.minedesso.essentialplugin.util.Message;
import de.minedesso.essentialplugin.util.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

public class BalanceBaseCommand implements CommandExecutor {
    private final Map<String, SubCommand> subCommands = new HashMap<>();
    private final SubCommand moneyCommand = new MoneyCommand(); // fallback/base command

    public BalanceBaseCommand() {
        subCommands.put("set", new MoneySetCommand());
        // Additional Commands here...
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        SubCommand subCommand = null;

        if (args.length > 0) {
            subCommand = subCommands.get(args[0].toLowerCase());
        }

        if (subCommand != null) {
            if (subCommand.playerOnly() && !(sender instanceof Player)) {
                sender.sendMessage(Message.ONLY_PLAYER.message);
                return true;
            }
            subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
            return true;
        }

        moneyCommand.execute(sender, args);
        return true;
    }
}