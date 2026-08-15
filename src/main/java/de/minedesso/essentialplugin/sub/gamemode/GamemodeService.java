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

    public void handleGamemodeCommand(Player sender, Player target, GameMode gamemode) {
        target.setGameMode(gamemode);

        target.sendMessage(Message.PREFIX.message + "Gamemode set to " + gamemode + ".");
        sender.sendMessage(Message.PREFIX.message + "Changed gamemode of §a" + target.getName() + " §7to §a" + gamemode);
    }

    public void handleGamemodeCommand(Player sender, GameMode gameMode) {
        sender.setGameMode(gameMode);

        sender.sendMessage(Message.PREFIX.message + "Gamemode set to " + gameMode + ".");
    }

}
