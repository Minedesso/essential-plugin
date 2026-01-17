package de.minedesso.essentialplugin;

import de.minedesso.essentialplugin.sub.inv.InvService;
import de.minedesso.essentialplugin.sub.economy.EconomyService;
import de.minedesso.essentialplugin.sub.warp.WarpService;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class EssentialPlugin extends JavaPlugin {

    @Getter
    private static EssentialPlugin instance;
    private final Logger logger = getLogger();

    @Override
    public void onEnable() {
        instance = this;
        initializeSubEssentials();
        logger.info("Essentials plugin has been enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Essentials plugin has been disabled.");
    }

    private void initializeSubEssentials() {
        // Ensure WarpService initializes without relying on commands being present in plugin.yml
        try {
            WarpService.getInstance();
            InvService.getInstance();
            EconomyService.getInstance();
        } catch (Exception e) {
            logger.warning("Failed to initialize WarpService: " + e.getMessage());
        }
    }
}
