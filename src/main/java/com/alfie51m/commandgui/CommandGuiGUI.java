package com.alfie51m.commandgui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandGuiGUI implements Listener {

    private static final Map<Integer, GUIItem> guiItems = new HashMap<>();
    private static final Map<Player, Map<Integer, Long>> cooldowns = new HashMap<>();

    public static void loadGUIItems() {
        guiItems.clear();
        List<Map<?, ?>> items = CommandGui.getInstance().getConfig().getMapList("gui-items");

        for (Map<?, ?> itemConfig : items) {
            String name = (String) itemConfig.get("name");
            String command = (String) itemConfig.get("command");
            String materialName = (String) itemConfig.get("item");
            boolean runAsPlayer = itemConfig.containsKey("run-as-player") && (boolean) itemConfig.get("run-as-player");

            Material material = Material.matchMaterial(materialName);

            List<?> rawLore = (List<?>) itemConfig.get("lore");
            List<String> lore = rawLore != null
                    ? rawLore.stream().map(Object::toString).toList()
                    : List.of();

            int cooldown = itemConfig.containsKey("cooldown") ? (int) itemConfig.get("cooldown") : 0;

            if (material != null) {
                int slot = itemConfig.containsKey("slot") ? (int) itemConfig.get("slot") : guiItems.size();
                guiItems.put(slot, new GUIItem(name, command, material, runAsPlayer, lore, cooldown));
                CommandGui.getInstance().getLogger().info("Loaded GUI item: " + name + " at slot " + slot);
            } else {
                CommandGui.getInstance().getLogger().warning("Invalid material '" + materialName + "' for item: " + name);
            }
        }
    }

    public static void openCommandGUI(Player player) {
        int inventorySize = ((guiItems.size() - 1) / 9 + 1) * 9;

        Inventory gui = Bukkit.createInventory(null, inventorySize, CommandGui.getInstance().getMessage("gui_title"));

        for (Map.Entry<Integer, GUIItem> entry : guiItems.entrySet()) {
            int slot = entry.getKey();
            GUIItem guiItem = entry.getValue();

            ItemStack itemStack = new ItemStack(guiItem.getMaterial());
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', guiItem.getName()));
                List<String> lore = guiItem.getLore().stream()
                        .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                        .toList();
                meta.setLore(lore);
                itemStack.setItemMeta(meta);
            }
            gui.setItem(slot, itemStack);
        }

        player.openInventory(gui);
    }

    public static void giveCustomKnowledgeBook(Player player) {
        ItemStack knowledgeBook = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = knowledgeBook.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(CommandGui.getInstance().getMessage("book_name"));
            meta.setLore(List.of(CommandGui.getInstance().getMessage("book_description")));
            knowledgeBook.setItemMeta(meta);
        }

        player.getInventory().addItem(knowledgeBook);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(CommandGui.getInstance().getMessage("gui_title"))) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || !clickedItem.hasItemMeta()) {
                return;
            }

            int slot = event.getSlot();
            GUIItem guiItem = guiItems.get(slot);

            if (guiItem != null) {
                // Handle cooldown
                long currentTime = System.currentTimeMillis();
                cooldowns.putIfAbsent(player, new HashMap<>());
                long lastUsed = cooldowns.get(player).getOrDefault(slot, 0L);
                int cooldown = guiItem.getCooldown();

                if (currentTime - lastUsed < cooldown * 1000L) {
                    long timeLeft = (cooldown * 1000L - (currentTime - lastUsed)) / 1000L;
                    String cooldownMessage = CommandGui.getInstance()
                            .getMessage("cooldown_active")
                            .replace("%time%", String.valueOf(timeLeft));
                    player.sendMessage(ChatColor.RED + cooldownMessage);
                    return;
                }

                // Update cooldown
                cooldowns.get(player).put(slot, currentTime);

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
            if (meta != null && meta.getDisplayName().equals(CommandGui.getInstance().getMessage("book_name"))) {
                event.setCancelled(true);
                openCommandGUI(player);
            }
        }
    }
}
