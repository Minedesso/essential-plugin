package de.minedesso.essentialplugin.sub.warp.cmd;

import de.minedesso.essentialplugin.util.HandleSubCommand;
import de.minedesso.essentialplugin.util.SubCommand;
import de.minedesso.essentialplugin.sub.warp.WarpService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarpsCommand implements CommandExecutor {

    private final Map<String, SubCommand> warpsSubCommands = new HashMap<>();

    public WarpsCommand(List<SubCommand> commands) {
        commands.forEach((subCmd) -> warpsSubCommands.put(subCmd.name(), subCmd));
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
