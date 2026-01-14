package de.minedesso.essentialplugin.sub.home;

import de.minedesso.essentialplugin.EssentialPlugin;
import de.minedesso.essentialplugin.exception.AlreadyExistsException;
import de.minedesso.essentialplugin.exception.DoesNotExistException;
import de.minedesso.essentialplugin.sub.home.cmd.HomeBaseCommand;
import de.minedesso.essentialplugin.sub.home.cmd.sub.DelHomeCommand;
import de.minedesso.essentialplugin.sub.home.cmd.sub.HomeCommand;
import de.minedesso.essentialplugin.sub.home.cmd.sub.SetHomeCommand;
import de.minedesso.essentialplugin.util.Message;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class HomeService {

    private static HomeService instance;
    private final HomeApi homeApi;

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
        Optional<HomeDto> homeDto = getHome(ownerUuid, name);
        if(homeDto.isPresent()) throw new AlreadyExistsException("Home with name " + name + " already exists!");

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
                new DelHomeCommand()
        ));

        EssentialPlugin plugin = EssentialPlugin.getInstance();
        plugin.getCommand("home").setExecutor(homeBaseCommand);
    }
}
