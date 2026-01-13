package de.minedesso.essentialplugin.sub.inv.cmd;

import de.minedesso.essentialplugin.util.Messages;
import de.minedesso.essentialplugin.util.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvBaseCommand implements CommandExecutor {

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public InvBaseCommand(List<SubCommand> subCommands) {
        subCommands.forEach((subCmd) -> {this.subCommands.put(subCmd.name(), subCmd);});
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Messages.ONLY_PLAYER.message);
            return true;
        }

        SubCommand sub = subCommands.get(label);

        if(!player.hasPermission(sub.permission())) {
            player.sendMessage(Messages.NO_PERMISSION.message);
            return true;
        }

        if(args.length != 1) {
            player.sendMessage(Messages.USAGE.message + "/" + label + " <player>");
            return true;
        }

        sub.execute(sender, args);
        return false;
    }
}
