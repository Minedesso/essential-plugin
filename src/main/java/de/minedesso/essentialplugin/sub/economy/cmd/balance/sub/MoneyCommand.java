package de.minedesso.essentialplugin.sub.economy.cmd.balance.sub;

import de.minedesso.essentialplugin.exception.InvalidAmountException;
import de.minedesso.essentialplugin.sub.economy.EconomyService;
import de.minedesso.essentialplugin.util.Message;
import de.minedesso.essentialplugin.util.Permission;
import de.minedesso.essentialplugin.util.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements SubCommand {
    @Override
    public String name() {
        return "";
    }

    @Override
    public String permission() {
        return ""; // check whilst using
    }

    @Override
    public boolean playerOnly() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        try {
            if (args.length == 0) {
                // View own
                if (!player.hasPermission(Permission.MONEY_VIEW.perm)) {
                    player.sendMessage(Message.NO_PERMISSION.message);
                    return;
                }
                EconomyService.getInstance().showBalance(player);
                return;
            }

            if (args.length == 1) {
                // View other
                if (!player.hasPermission(Permission.MONEY_VIEW_OTHERS.perm)) {
                    player.sendMessage(Message.NO_PERMISSION.message);
                    return;
                }
                EconomyService.getInstance().showBalance(player, args[0]);
                return;
            }

            String usage = Message.USAGE.message + "/money [player]";
            if (player.hasPermission(Permission.MONEY_ADMIN.perm)) {
                usage += " | /money set <player> <amount>";
            }
            player.sendMessage(usage);
        } catch (InvalidAmountException e) {
            player.sendMessage(Message.ERROR.message + e.getMessage());
        } catch (Exception e) {
            player.sendMessage(Message.ERROR.message + "An internal error occurred. Please try again.");
        }
    }
}