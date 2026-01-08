package de.minedesso.essentialPlugin;

import lombok.extern.slf4j.Slf4j;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

@Slf4j
public final class EssentialPlugin extends JavaPlugin {

    private final Logger logger = getLogger();

    @Override
    public void onEnable() {
        initializeComponents();
        logger.info("Essentials plugin has been enabled.");
    }

    private void initializeComponents() {
        initializeCommands();
        initializeListener();
    }

    private void initializeCommands() {
    }

    private void initializeListener() {
        PluginManager pluginManager = getServer().getPluginManager();
    }

    @Override
    public void onDisable() {
        getLogger().info("Lobby plugin has been disabled.");
    }
}
