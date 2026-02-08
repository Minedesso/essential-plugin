package de.minedesso.essentialplugin.sub.tpa.cmd.sub;

import de.minedesso.essentialplugin.sub.tpa.TpaService;
import de.minedesso.essentialplugin.util.Message;
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
        if (args.length > 0) {
            sender.sendMessage(Message.USAGE.message + "/tpcancel");
            return;
        }
        if (args.length > 0) {
            sender.sendMessage(Message.USAGE.message + "/tpcancel");
            return;
        }
        TpaService.getInstance().cancelRequest((Player) sender);
    }
}
