package de.minedesso.essentialplugin.sub.home.cmd;

import de.minedesso.essentialplugin.util.Message;
import de.minedesso.essentialplugin.util.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeBaseCommand implements CommandExecutor {

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public HomeBaseCommand(List<SubCommand> cmds) {
        cmds.forEach(cmd -> {subCommands.put(cmd.name(), cmd);});
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Message.ONLY_PLAYER.message);
            return true;
        }

        SubCommand subCommand = subCommands.get(label);

        subCommand.execute(player, args);
        return false;
    }
}
