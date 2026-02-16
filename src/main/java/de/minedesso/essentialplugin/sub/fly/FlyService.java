package de.minedesso.essentialplugin.sub.fly;

import de.minedesso.essentialplugin.EssentialPlugin;
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

    public FlyService() {
        EssentialPlugin.getInstance().getCommand("fly").setExecutor(new FlyCommand());
    }

    public void handleFlyCommand(Player sender, Player target) {
        boolean canFly = target.getAllowFlight();

        target.setAllowFlight(!canFly);
        target.setFlying(!canFly);

        target.sendMessage(Message.PREFIX.message + getFlyStatusMessage(canFly)
                + ".");

        if (!sender.getUniqueId().equals(target.getUniqueId())) {
            sender.sendMessage(Message.PREFIX.message
                    + getFlyStatusMessage(canFly) + " for " + target.getName() + ".");
        }
    }

    private String getFlyStatusMessage(boolean canFly) {
        if (canFly) {
            return "Flying &aenabled&7";
        } else {
            return "Flying &cdisabled&7";
        }
    }

}
