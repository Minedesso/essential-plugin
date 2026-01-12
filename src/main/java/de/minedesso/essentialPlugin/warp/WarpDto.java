package de.minedesso.essentialPlugin.warp;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;

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

    public WarpDto(String name, String permission, Location location) {
        this.name = name;
        this.permission = permission;
        this.worldName = Objects.requireNonNull(location.getWorld()).getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public Location toLocation() {
        World world = Bukkit.getWorld(this.worldName);
        return new Location(world, x, y, z, yaw, pitch);
    }
}
