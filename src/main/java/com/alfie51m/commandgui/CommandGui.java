package com.alfie51m.commandgui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandGui extends JavaPlugin implements Listener {

    private final Map<Integer, GUIItem> guiItems = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();

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
                openCommandGUI(player);
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Only players can use this command!");
                return true;
            }
        }

        if (label.equalsIgnoreCase("cgreload")) {
            if (sender.hasPermission("commandgui.reload")) {
                reloadConfig();
                loadGUIItems();
                sender.sendMessage(ChatColor.GREEN + "CommandGUI configuration reloaded!");
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
            return true;
        }

        if (label.equalsIgnoreCase("cgbook")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                giveCustomKnowledgeBook(player);
                sender.sendMessage(ChatColor.GREEN + "Knowledge Book given!");
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Only players can use this command!");
                return true;
            }
        }

        return false;
    }

    private void loadGUIItems() {
        guiItems.clear();
        List<Map<?, ?>> items = getConfig().getMapList("gui-items");

        int currentSlot = 0;
        for (Map<?, ?> itemConfig : items) {
            String name = (String) itemConfig.get("name");
            String command = (String) itemConfig.get("command");
            String materialName = (String) itemConfig.get("material");

            Material material = Material.matchMaterial(materialName);
            if (material != null) {
                int slot = itemConfig.containsKey("slot") ? (int) itemConfig.get("slot") : currentSlot++;
                guiItems.put(slot, new GUIItem(name, command, material));
            } else {
                getLogger().warning("Invalid material '" + materialName + "' for item: " + name);
            }
        }
    }

    public void openCommandGUI(Player player) {
        int inventorySize = ((guiItems.size() - 1) / 9 + 1) * 9;

        Inventory gui = Bukkit.createInventory(null, inventorySize, ChatColor.BLUE + "Command GUI");

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
            meta.setDisplayName(ChatColor.AQUA + "Command GUI Book");
            meta.setLore(List.of(ChatColor.GRAY + "Right-click to open the Command GUI."));
            knowledgeBook.setItemMeta(meta);
        }

        player.getInventory().addItem(knowledgeBook);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.BLUE + "Command GUI")) {
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
                player.performCommand(guiItem.getCommand());
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.KNOWLEDGE_BOOK) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.getDisplayName().equals(ChatColor.AQUA + "Command GUI Book")) {
                event.setCancelled(true);
                openCommandGUI(player);
            }
        }
    }

    private static class GUIItem {
        private final String name;
        private final String command;
        private final Material material;

        public GUIItem(String name, String command, Material material) {
            this.name = name;
            this.command = command;
            this.material = material;
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
    }
}
