package de.minedesso.essentialplugin.sub.tpa.cmd.sub;

import de.minedesso.essentialplugin.sub.tpa.TpaService;
import de.minedesso.essentialplugin.util.Messages;
import de.minedesso.essentialplugin.util.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaCancelCommand implements SubCommand {
    @Override
    public String name() { return "tpcancel"; }

    @Override
    public String permission() {
        return null;
    }

    @Override
    public boolean playerOnly() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        TpaService.getInstance().cancelRequest((Player) sender);
    }
}
