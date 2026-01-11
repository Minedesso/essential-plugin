package de.minedesso.essentialPlugin.service;

import de.minedesso.essentialPlugin.dto.WarpDto;
import de.minedesso.essentialPlugin.util.Messages;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class WarpService {

    private final Set<WarpDto> warpDtos = new HashSet<>();
    private static WarpService instance;

    public static synchronized WarpService getInstance() {
        if (instance == null) {
            instance = new WarpService();
        }
        return instance;
    }

    private WarpService() {
    }

    public void teleportToWarp(Player player, String warpName) {
        WarpDto warpDto = getWarpByName(warpName);
        if (warpDto != null) {
            player.teleport(warpDto.toLocation());
            player.sendMessage(Messages.PREFIX.message + "Teleported to warp '" + warpName + "'.");
        } else {
            player.sendMessage(Messages.PREFIX.message + "Warp '" + warpName + "' does not exist.");
        }
    }

    private WarpDto getWarpByName(String warpName) {
        for (WarpDto warpDto : warpDtos) {
            if (warpDto.getName().equalsIgnoreCase(warpName)) {
                return warpDto;
            }
        }
        return null;
    }

    public Set<WarpDto> getWarps() {
        return warpDtos;
    }

    public void addWarp(WarpDto warpDto) {
        warpDtos.add(warpDto);
    }

    public void removeWarp(WarpDto warpDto) {
        warpDtos.remove(warpDto);
    }

}
