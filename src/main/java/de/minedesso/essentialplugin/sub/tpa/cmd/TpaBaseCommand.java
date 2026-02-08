package de.minedesso.essentialplugin.sub.tpa.cmd;

import de.minedesso.essentialplugin.util.Messages;
import de.minedesso.essentialplugin.util.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TpaBaseCommand implements CommandExecutor {

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public TpaBaseCommand(List<SubCommand> cmds) {
        cmds.forEach(cmd -> {subCommands.put(cmd.name(), cmd);});
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        SubCommand subCommand = subCommands.get(label);

        if(subCommand.playerOnly() && !(sender instanceof Player)) {
            sender.sendMessage(Messages.ONLY_PLAYER.message);
            return true;
        }

        subCommand.execute(sender, args);
        return true;
    }
}
