package de.minedesso.essentialPlugin;

import lombok.extern.slf4j.Slf4j;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

@Slf4j
public final class EssentialPlugin extends JavaPlugin {

    private final Logger logger = getLogger();
    private static EssentialPlugin instance;

    @Override
    public void onEnable() {
        this.instance = this;
        logger.info("Essentials plugin has been enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Lobby plugin has been disabled.");
    }

    public static EssentialPlugin getInstance() {
        return instance;
    }
}
