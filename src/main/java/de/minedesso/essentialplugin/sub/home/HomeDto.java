package de.minedesso.essentialplugin.sub.home;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;

import java.util.UUID;

@AllArgsConstructor
@Data
public class HomeDto {

    private UUID ownerUuid;
    private String name;

    private String worldName;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public HomeDto(UUID ownerUuid, String name, Location location) {
        this.ownerUuid = ownerUuid;
        this.name = name;
        this.worldName = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public Location toLocation() {
        return new Location(
                org.bukkit.Bukkit.getWorld(this.worldName),
                this.x,
                this.y,
                this.z,
                this.yaw,
                this.pitch
        );
    }

}
