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
        GameMode gamemodeSet = target.getGameMode();
        if(args[0] == "survival") {
            target.setGameMode(GameMode.SURVIVAL);
            target.sendMessage(Message.PREFIX.message + getGamemodeStatusMessage(gamemodeSet));

        } else if (args[0] == "creative") {
            target.setGameMode(GameMode.CREATIVE);

        } else if (args[0] == "adventure") {
            target.setGameMode(GameMode.ADVENTURE);

        } else if (args[0] == "spectator") {
            target.setGameMode(GameMode.SPECTATOR);

        }
    }

    private String getGamemodeStatusMessage(GameMode gamemodeSet) {
        if (gamemodeSet == GameMode.SURVIVAL) {
            return "Gamemode &aSurvival&7";

        } else if (gamemodeSet == GameMode.CREATIVE){
            return "Gamemode &aCreative&7";

        } else if (gamemodeSet == GameMode.ADVENTURE) {
            return "Gamemode &aAdventure&7";

        } else {

            return "Gamemode &aSpectator&7";

        }
    }

}
