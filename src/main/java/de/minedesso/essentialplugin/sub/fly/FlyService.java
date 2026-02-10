package de.minedesso.essentialplugin.sub.fly;

import de.minedesso.essentialplugin.util.Message;
import org.bukkit.entity.Player;

public class FlyService {

    private static FlyService instance;

    public static FlyService getInstance() {
        if (instance == null) {
            instance = new FlyService();
        }
        return instance;
    }

    public void handleFlyCommand(Player target) {
        boolean canFly = target.getAllowFlight();

        target.setAllowFlight(!canFly);
        target.setFlying(!canFly);

        target.sendMessage(Message.PREFIX.message + getFlyStatusMessage(canFly));
    }

    private String getFlyStatusMessage(boolean canFly) {
        if (canFly) {
            return "§aFlying enabled.";
        } else {
            return "§cFlying disabled.";
        }
    }

}
