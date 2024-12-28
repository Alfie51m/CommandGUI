package com.alfie51m.commandgui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandGui extends JavaPlugin implements Listener {

    private final Map<Integer, GUIItem> guiItems = new HashMap<>();
    private FileConfiguration langConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadLanguageFile();  // Load the language file based on config
        loadGUIItems();
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("CommandGUI Plugin has been enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("CommandGUI Plugin has been disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("commandgui")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("commandgui.use")) {
                    openCommandGUI(player);
                } else {
                    sender.sendMessage(ChatColor.RED + getMessage("no_permission"));
                }
            } else {
                sender.sendMessage(ChatColor.RED + getMessage("only_players"));
            }
            return true;
        }

        if (label.equalsIgnoreCase("cgreload")) {
            if (sender.hasPermission("commandgui.reload")) {
                reloadConfig();
                loadGUIItems();
                loadLanguageFile();  // Reload the language file after config reload
                sender.sendMessage(ChatColor.GREEN + getMessage("reload_success"));
            } else {
                sender.sendMessage(ChatColor.RED + getMessage("no_permission"));
            }
            return true;
        }

        if (label.equalsIgnoreCase("cgbook")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("commandgui.book")) {
                    giveCustomKnowledgeBook(player);
                    sender.sendMessage(ChatColor.GREEN + getMessage("book_received"));
                } else {
                    sender.sendMessage(ChatColor.RED + getMessage("no_permission"));
                }
            } else {
                sender.sendMessage(ChatColor.RED + getMessage("only_players"));
            }
            return true;
        }

        if (label.equalsIgnoreCase("cggive")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("commandgui.cggive")) {
                    if (args.length == 1) {
                        Player targetPlayer = Bukkit.getPlayerExact(args[0]);

                        if (targetPlayer != null) {
                            giveCustomKnowledgeBook(targetPlayer);
                            sender.sendMessage(ChatColor.GREEN + getMessage("book_given").replace("%player%", args[0]));
                            targetPlayer.sendMessage(ChatColor.GREEN + getMessage("book_received"));
                        } else {
                            sender.sendMessage(ChatColor.RED + getMessage("player_not_found").replace("%player%", args[0]));
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + getMessage("invalid_usage"));
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + getMessage("no_permission"));
                }
            } else {
                sender.sendMessage(ChatColor.RED + getMessage("only_players"));
            }
            return true;
        }

        return false;
    }

    private void loadLanguageFile() {
        // Get the language file name from config
        String langFileName = getConfig().getString("language-file", "en_us.yml");
        File langFile = new File(getDataFolder(), langFileName);
        if (!langFile.exists()) {
            saveResource(langFileName, false);  // Save the default language file if it doesn't exist
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    private String getMessage(String key) {
        return ChatColor.translateAlternateColorCodes('&', langConfig.getString("messages." + key, key)); // Default to key if not found
    }

    private void loadGUIItems() {
        guiItems.clear();
        List<Map<?, ?>> items = getConfig().getMapList("gui-items");

        int currentSlot = 0;
        for (Map<?, ?> itemConfig : items) {
            String name = (String) itemConfig.get("name");
            String command = (String) itemConfig.get("command");
            String materialName = (String) itemConfig.get("material");
            boolean runAsPlayer = itemConfig.containsKey("run-as-player") && (boolean) itemConfig.get("run-as-player");

            Material material = Material.matchMaterial(materialName);
            if (material != null) {
                int slot = itemConfig.containsKey("slot") ? (int) itemConfig.get("slot") : currentSlot++;
                guiItems.put(slot, new GUIItem(name, command, material, runAsPlayer));
            } else {
                getLogger().warning("Invalid material '" + materialName + "' for item: " + name);
            }
        }
    }

    public void openCommandGUI(Player player) {
        int inventorySize = ((guiItems.size() - 1) / 9 + 1) * 9;

        Inventory gui = Bukkit.createInventory(null, inventorySize, getMessage("gui_title"));

        for (Map.Entry<Integer, GUIItem> entry : guiItems.entrySet()) {
            int slot = entry.getKey();
            GUIItem guiItem = entry.getValue();

            ItemStack itemStack = new ItemStack(guiItem.getMaterial());
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', guiItem.getName()));
                itemStack.setItemMeta(meta);
            }
            gui.setItem(slot, itemStack);
        }

        player.openInventory(gui);
    }

    public void giveCustomKnowledgeBook(Player player) {
        ItemStack knowledgeBook = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = knowledgeBook.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(getMessage("book_name"));
            meta.setLore(List.of(getMessage("book_description")));
            knowledgeBook.setItemMeta(meta);
        }

        player.getInventory().addItem(knowledgeBook);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(getMessage("gui_title"))) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || !clickedItem.hasItemMeta()) {
                return;
            }

            int slot = event.getSlot();
            GUIItem guiItem = guiItems.get(slot);

            if (guiItem != null) {
                player.closeInventory();

                String commandToExecute = guiItem.getCommand().replace("%player%", player.getName());
                if (guiItem.isRunAsPlayer()) {
                    player.performCommand(commandToExecute);
                } else {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandToExecute);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.KNOWLEDGE_BOOK) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.getDisplayName().equals(getMessage("book_name"))) {
                event.setCancelled(true);
                openCommandGUI(player);
            }
        }
    }

    private static class GUIItem {
        private final String name;
        private final String command;
        private final Material material;
        private final boolean runAsPlayer;

        public GUIItem(String name, String command, Material material, boolean runAsPlayer) {
            this.name = name;
            this.command = command;
            this.material = material;
            this.runAsPlayer = runAsPlayer;
        }

        public String getName() {
            return name;
        }

        public String getCommand() {
            return command;
        }

        public Material getMaterial() {
            return material;
        }

        public boolean isRunAsPlayer() {
            return runAsPlayer;
        }
    }
}