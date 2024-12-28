package com.alfie51m.commandgui;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class CommandGui extends JavaPlugin {

    private static CommandGui instance;
    private FileConfiguration langConfig;

    public static CommandGui getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        loadLanguageFile();

        // Register event listeners
        Bukkit.getPluginManager().registerEvents(new CommandGuiGUI(), this);

        // Register commands and tab completers
        CommandGuiCommands commandExecutor = new CommandGuiCommands();
        getCommand("commandgui").setExecutor(commandExecutor);
        getCommand("commandgui").setTabCompleter(commandExecutor);

        // Load GUI items on startup
        CommandGuiGUI.loadGUIItems();

        getLogger().info("CommandGUI Plugin has been enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("CommandGUI Plugin has been disabled.");
    }

    private void loadLanguageFile() {
        String langFileName = getConfig().getString("language-file", "en_us.yml");
        File langFile = new File(getDataFolder(), langFileName);
        if (!langFile.exists()) {
            saveResource(langFileName, false);
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    public String getMessage(String key) {
        return langConfig.getString("messages." + key, key).replace('&', '§');
    }
}

