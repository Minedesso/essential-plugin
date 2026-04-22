package de.minedesso.essentialplugin.sub.gamemode;

import de.minedesso.essentialplugin.EssentialPlugin;
import de.minedesso.essentialplugin.util.Message;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GamemodeService {

    private static GamemodeService instance;

    public static GamemodeService getInstance() {
        if (instance == null) {
            instance = new GamemodeService();
        }
        return instance;
    }

    public GamemodeService() {
        EssentialPlugin.getInstance().getCommand("gamemode").setExecutor(new GamemodeCommand());
    }

    public void handleGamemodeCommand(Player sender, Player target, String[] args) {
        switch(args[0]) {
            case "0", "Survival" -> target.setGameMode(GameMode.SURVIVAL);
            case "1", "Creative" -> target.setGameMode(GameMode.CREATIVE);
            case "2", "Adventure" -> target.setGameMode(GameMode.ADVENTURE);
            case "3", "Spectator" -> target.setGameMode(GameMode.SPECTATOR);
        }
        target.sendMessage(Message.PREFIX.message + getGamemodeStatusMessage(args[0]));
    }

    private String getGamemodeStatusMessage(String gamemodeSet) {
        return switch(gamemodeSet) {
            case "0", "Survival" -> "Gamemode §aSurvival§7";
            case "1", "Creative" -> "Gamemode §aCreative§7";
            case "2", "Adventure" -> "Gamemode §aAdventure§7";
            case "3", "Spectator" -> "Gamemode §aSpectator§7";
        };
    }

}
