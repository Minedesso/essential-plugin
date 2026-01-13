package de.minedesso.essentialplugin.sub.inv;

import de.minedesso.essentialplugin.EssentialPlugin;
import de.minedesso.essentialplugin.sub.inv.cmd.invSub.EcCommand;
import de.minedesso.essentialplugin.sub.inv.cmd.InvBaseCommand;
import de.minedesso.essentialplugin.sub.inv.cmd.invSub.InvCommand;
import de.minedesso.essentialplugin.util.Messages;
import org.bukkit.entity.Player;

import java.util.List;

public class InvService {

    private static InvService instance;

    private InvService() {
        initializeWarpCommands();
    }

    private void initializeWarpCommands() {
        InvBaseCommand baseCmd = new InvBaseCommand(List.of(
                new InvCommand(),
                new EcCommand()
        ));

        EssentialPlugin plugin = EssentialPlugin.getInstance();
        if (plugin.getCommand("inv") != null) {
            plugin.getCommand("inv").setExecutor(baseCmd);
        }
    }

    public static InvService getInstance() {
        if (instance == null) {
            instance = new InvService();
        }
        return instance;
    }

    public void openInventory(Player viewer, Player target, boolean openEc) {
        if (target == null) {
            viewer.sendMessage(Messages.PLAYER_NOT_FOUND.message);
            return;
        }

        viewer.openInventory(openEc ? target.getEnderChest() : target.getInventory());
    }

}
