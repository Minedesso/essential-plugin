package de.minedesso.essentialPlugin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

@AllArgsConstructor
@Data
public class WarpDto {
    private String name;
    private String permission;

    private String worldName;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public Location toLocation() {
        World world = Bukkit.getWorld(this.worldName);
        return new Location(world, x, y, z, yaw, pitch);
    }
}
