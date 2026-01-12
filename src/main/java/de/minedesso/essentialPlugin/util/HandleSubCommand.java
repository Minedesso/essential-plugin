package de.minedesso.essentialPlugin.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public abstract class HandleSubCommand {

    private HandleSubCommand() {
    }

    public static boolean handleSubCommand(SubCommand sub, CommandSender sender, String label, String[] args) {
        if (sub == null) {
            sender.sendMessage("Unknown subcommand. Type '/" + label + " help' for help.");
            return true;
        }

        if (sub.playerOnly() && !(sender instanceof Player)) {
            sender.sendMessage("Only players may use this command.");
            return true;
        }

        if (sub.permission() != null && !sender.hasPermission(sub.permission())) {
            sender.sendMessage(Messages.NO_PERMISSION.message);
            return true;
        }

        sub.execute(sender, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }

}
