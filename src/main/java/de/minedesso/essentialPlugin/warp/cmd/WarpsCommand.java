package de.minedesso.essentialPlugin.warp.cmd;

import de.minedesso.essentialPlugin.util.HandleSubCommand;
import de.minedesso.essentialPlugin.util.SubCommand;
import de.minedesso.essentialPlugin.warp.WarpService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarpsCommand implements CommandExecutor {

    private final Map<String, SubCommand> warpsSubCommands = new HashMap<>();

    public WarpsCommand(List<SubCommand> commands) {
        commands.forEach((subCmd) -> {warpsSubCommands.put(subCmd.name(), subCmd);});
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 0) {
            WarpService.getInstance().displayAllWarps(sender);
            return true;
        }

        SubCommand sub = warpsSubCommands.get(args[0].toLowerCase());
        return HandleSubCommand.handleSubCommand(sub, sender, label, args);
    }
}
