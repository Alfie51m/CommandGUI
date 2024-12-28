package com.alfie51m.commandgui;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

        // Check for updates
        checkForUpdates();

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
            if (getResource(langFileName) != null) {
                saveResource(langFileName, false);
            } else {
                getLogger().warning("Language file not found in resources: " + langFileName);
            }
        }

        langConfig = YamlConfiguration.loadConfiguration(langFile);
        getLogger().info("Loaded language file: " + langFileName);
    }

    public String getMessage(String key) {
        return langConfig.getString("messages." + key, key).replace('&', '§');
    }

    private void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                URL url = new URL("https://api.github.com/repos/Alfie51m/CommandGUI/releases/latest");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.connect();

                if (connection.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close();

                    String json = response.toString();
                    String latestVersion = json.split("\"tag_name\":\"")[1].split("\"")[0].replace("v", "");
                    String currentVersion = getDescription().getVersion().replace("v", "");

                    if (isNewerVersion(currentVersion, latestVersion)) {
                        getLogger().warning("A new version of CommandGUI is available: v" + latestVersion);
                        getLogger().warning("Download it here: https://github.com/Alfie51m/CommandGUI/releases");
                    } else {
                        getLogger().info("You are using the latest version of CommandGUI.");
                    }
                } else {
                    getLogger().warning("Unable to check for updates. HTTP Response Code: " + connection.getResponseCode());
                }
            } catch (Exception e) {
                getLogger().warning("An error occurred while checking for updates: " + e.getMessage());
            }
        });
    }

    /**
     * Compares two version strings to determine if the second version is newer.
     *
     * @param currentVersion The current version of the plugin.
     * @param latestVersion  The latest version retrieved from GitHub.
     * @return true if the latest version is newer, false otherwise.
     */
    private boolean isNewerVersion(String currentVersion, String latestVersion) {
        String[] currentParts = currentVersion.split("\\.");
        String[] latestParts = latestVersion.split("\\.");

        for (int i = 0; i < Math.max(currentParts.length, latestParts.length); i++) {
            int currentPart = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;
            int latestPart = i < latestParts.length ? Integer.parseInt(latestParts[i]) : 0;

            if (latestPart > currentPart) {
                return true;
            } else if (latestPart < currentPart) {
                return false;
            }
        }

        return false;
    }
}
