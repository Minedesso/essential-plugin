package de.minedesso.essentialplugin.sub.tpa.cmd.sub;

import de.minedesso.essentialplugin.exception.DoesNotExistException;
import de.minedesso.essentialplugin.sub.tpa.TpaService;
import de.minedesso.essentialplugin.util.Messages;
import de.minedesso.essentialplugin.util.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaCommand implements SubCommand {
    @Override
    public String name() { return "tpa"; }

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
            sender.sendMessage(Messages.USAGE.message + "/tpa <name>");
            return;
        }

        try {
            TpaService.getInstance().sendRequest((Player) sender, args[0]);
        } catch (DoesNotExistException e) {
            sender.sendMessage(e.getMessage());
        }
    }
}
