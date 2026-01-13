package de.minedesso.essentialplugin.util;

import org.bukkit.command.CommandSender;

public interface SubCommand {
    String name();

    String permission();

    boolean playerOnly();

    void execute(CommandSender sender, String[] args);
}
