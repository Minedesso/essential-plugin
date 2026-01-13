package de.minedesso.essentialplugin.sub.inv.cmd.invSub;

import de.minedesso.essentialplugin.sub.inv.InvService;
import de.minedesso.essentialplugin.util.Permission;
import de.minedesso.essentialplugin.util.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EcCommand  implements SubCommand {
    @Override
    public String name() {
        return "ec";
    }

    @Override
    public String permission() {
        return Permission.INV_EC.perm;
    }

    @Override
    public boolean playerOnly() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        InvService.getInstance().openInventory((Player) sender, Bukkit.getPlayer(args[0]), true);
    }
}
