package de.minedesso.essentialplugin.sub.home;

import de.minedesso.essentialplugin.EssentialPlugin;
import de.minedesso.essentialplugin.exception.AlreadyExistsException;
import de.minedesso.essentialplugin.exception.CouldNotCreateException;
import de.minedesso.essentialplugin.exception.DoesNotExistException;
import de.minedesso.essentialplugin.sub.home.cmd.HomeBaseCommand;
import de.minedesso.essentialplugin.sub.home.cmd.sub.DelHomeCommand;
import de.minedesso.essentialplugin.sub.home.cmd.sub.HomeCommand;
import de.minedesso.essentialplugin.sub.home.cmd.sub.HomesCommand;
import de.minedesso.essentialplugin.sub.home.cmd.sub.SetHomeCommand;
import de.minedesso.essentialplugin.util.Message;
import de.minedesso.essentialplugin.util.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class HomeService {

    private static HomeService instance;
    private final HomeApi homeApi;

    private static final int DEFAULT_HOME_LIMIT = 3;

    public static HomeService getInstance() {
        if (instance == null) {
            instance = new HomeService();
        }
        return instance;
    }

    private HomeService() {
        this.homeApi = HomeApi.getInstance();

        initializeCommands();
    }

    public void setHome(UUID ownerUuid, String name, Location location) {
        validateHomeLimit(ownerUuid);
        validateHomeName(name);

        Optional<HomeDto> homeDto = getHome(ownerUuid, name);
        if (homeDto.isPresent()) throw new AlreadyExistsException("Home with name " + name + " already exists!");

        HomeDto newHomeDto = new HomeDto(ownerUuid, name, location);
        homeApi.saveHome(newHomeDto);
    }

    public void deleteHome(UUID ownerUuid, String name) {
        Optional<HomeDto> homeDto = getHome(ownerUuid, name);
        if(homeDto.isEmpty()) throw new DoesNotExistException("Home with name " + name + " does not exist!");

        homeApi.deleteHome(homeDto.get());
    }

    public Optional<HomeDto> getHome(UUID ownerUuid, String name) {
        return homeApi.fetchHomeByNameAndOwnerUuid(name, ownerUuid);
    }

    public List<HomeDto> getHomes(UUID ownerUuid) {
        return homeApi.fetchHomesByOwnerUuid(ownerUuid);
    }

    public void teleportToHome(Player player, String name) {
        Optional<HomeDto> homeDto = getHome(player.getUniqueId(), name);
        if(homeDto.isEmpty()) throw new DoesNotExistException("Home with name " + name + " does not exist!");

        HomeDto dto = homeDto.get();
        Location location = dto.toLocation();

        player.teleport(location);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_PEARL_THROW, 1f, 1f);
    }

    public void displayHomes(Player player) {
        List<HomeDto> homes = getHomes(player.getUniqueId());

        StringBuilder homeList = new StringBuilder(Message.PREFIX.message + "Homes: ");
        for (HomeDto home : homes) {
            homeList.append(home.getName()).append(", ");
        }
        if (homeList.length() > 2) {
            homeList.setLength(homeList.length() - 2); // Remove trailing comma and space
        } else {
            homeList.append("None");
        }
        player.sendMessage(homeList.toString());
    }

    private void initializeCommands() {
        HomeBaseCommand homeBaseCommand = new HomeBaseCommand(List.of(
                new HomeCommand(),
                new SetHomeCommand(),
                new DelHomeCommand(),
                new HomesCommand()
        ));

        EssentialPlugin plugin = EssentialPlugin.getInstance();
        plugin.getCommand("home").setExecutor(homeBaseCommand);
    }

    private void validateHomeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Home name cannot be null or empty.");
        }
        if (name.length() > 16) {
            throw new IllegalArgumentException("Home name cannot exceed 16 characters.");
        }
        if (!name.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Home name can only contain alphanumeric characters and underscores.");
        }
    }

    private void validateHomeLimit(UUID ownerUuid) {
        int currentHomesSize = getHomes(ownerUuid).size();

        int limit = determineHomeLimit(ownerUuid);

        if (limit != Integer.MAX_VALUE && currentHomesSize >= limit) {
            throw new CouldNotCreateException("Home limit reached: " + limit + " (current: " + currentHomesSize + ")");
        }
    }

    /**
     * Bestimmt das Home-Limit für den gegebenen Spieler (ownerUuid).
     * Regeln/Annahmen:
     * - Wenn der Spieler online ist, werden seine effektiven Permissions ausgewertet.
     *   Es werden Permissions vom Format "essential.home.count.X" ausgewertet.
     *   Bei mehreren Treffern wird der höchste Wert genommen.
     *   "essential.home.count.*" bedeutet "unlimited" (Integer.MAX_VALUE).
     * - Wenn der Spieler offline ist, wird ein Default-Limit von 3 verwendet.
     */
    private int determineHomeLimit(UUID ownerUuid) {
        Player player = Bukkit.getPlayer(ownerUuid);
        if (player == null) return DEFAULT_HOME_LIMIT;

        int max = -1;
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            if (!info.getValue()) continue; // nur explizit erlaubte Permissions
            String perm = info.getPermission();
            if (!perm.startsWith(Permission.HOME_COUNT.perm)) continue;
            String suffix = perm.substring(Permission.HOME_COUNT.perm.length());
            if (suffix.equals("*")) {
                return Integer.MAX_VALUE; // unbegrenzt
            }
            try {
                int v = Integer.parseInt(suffix);
                if (v > max) max = v;
            } catch (NumberFormatException ignored) {
                // ungültige Endung, ignorieren
            }
        }

        if(max == -1) return DEFAULT_HOME_LIMIT;
        return max;
    }

}
