package de.minedesso.essentialPlugin.warp.cmd.warpsSub;

import de.minedesso.essentialPlugin.exception.WarpAlreadyExistsException;
import de.minedesso.essentialPlugin.exception.WarpsCreateException;
import de.minedesso.essentialPlugin.util.Messages;
import de.minedesso.essentialPlugin.util.SubCommand;
import de.minedesso.essentialPlugin.warp.WarpDto;
import de.minedesso.essentialPlugin.warp.WarpService;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpsCreateSubCommand implements SubCommand {
    @Override
    public String name() {
        return "create";
    }

    @Override
    public String permission() {
        return "essential.warp.create";
    }

    @Override
    public boolean playerOnly() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if(args.length != 1) {
            player.sendMessage(Messages.USAGE.message + "/warps create <warpname>");
            return;
        }

        try {
            String warpName = args[0].toLowerCase();
            Location location = player.getLocation();
            WarpDto warpDto = new WarpDto(warpName, null, location);

            WarpService.getInstance().createWarp(warpDto);
            player.sendMessage(Messages.PREFIX.message + "Warp '" + warpName + "' created successfully.");
        } catch (WarpAlreadyExistsException e) {
            player.sendMessage(Messages.PREFIX.message + e.getMessage());
        } catch (WarpsCreateException e) {
            player.sendMessage(Messages.ERROR.message + e.getMessage());
        }
    }
}
