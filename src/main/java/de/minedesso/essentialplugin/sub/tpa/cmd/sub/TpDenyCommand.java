package de.minedesso.essentialplugin.sub.tpa.cmd.sub;

import de.minedesso.essentialplugin.sub.tpa.TpaService;
import de.minedesso.essentialplugin.util.Message;
import de.minedesso.essentialplugin.util.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpDenyCommand implements SubCommand {
    @Override
    public String name() { return "tpdeny"; }

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
        if(args.length != 1) {
            sender.sendMessage(Message.USAGE.message + "/tpdeny <name>");
            return;
        }

        String requestSenderName = args[0];
        TpaService.getInstance().denyRequest((Player) sender, requestSenderName);
    }
}
