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
            try {
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
            } catch (Exception e) {
                CommandGui.getInstance().getLogger().warning("Error loading GUI item: " + e.getMessage());
            }
        }
    }

    public static void openCommandGUI(Player player) {
        String sizeMode = CommandGui.getInstance().getConfig().getString("gui-size-mode", "dynamic");

        if (sizeMode.equalsIgnoreCase("fixed")) {
            openFixedSizeGUI(player);
        } else {
            openDynamicSizeGUI(player);
        }
    }

    private static void openDynamicSizeGUI(Player player) {
        int inventorySize = ((guiItems.size() - 1) / 9 + 1) * 9;

        Inventory gui = Bukkit.createInventory(null, inventorySize, CommandGui.getInstance().getMessage("gui_title"));

        for (Map.Entry<Integer, GUIItem> entry : guiItems.entrySet()) {
            int slot = entry.getKey();
            GUIItem guiItem = guiItems.get(slot);

            if (guiItem != null) {
                ItemStack itemStack = createItemStack(guiItem);
                gui.setItem(slot, itemStack);
            }
        }

        player.openInventory(gui);
    }

    private static void openFixedSizeGUI(Player player) {
        int itemsPerPage = 45; // 54 slots - 9 for navigation
        int totalPages = (int) Math.ceil((double) guiItems.size() / itemsPerPage);
        openFixedSizePage(player, 1, totalPages);
    }

    private static void openFixedSizePage(Player player, int page, int totalPages) {
        Inventory gui = Bukkit.createInventory(null, 54, CommandGui.getInstance().getMessage("gui_title") + " (Page " + page + "/" + totalPages + ")");

        int start = (page - 1) * 45;
        int end = Math.min(start + 45, guiItems.size());
        int index = 0;

        for (int slot = start; slot < end; slot++) {
            if (guiItems.containsKey(slot)) {
                GUIItem guiItem = guiItems.get(slot);
                if (guiItem != null) {
                    ItemStack itemStack = createItemStack(guiItem);
                    gui.setItem(index, itemStack);
                }
                index++;
            }
        }

        // Add navigation buttons
        if (page > 1) {
            ItemStack previousPage = createNavigationItem(Material.ARROW, "&aPrevious Page");
            gui.setItem(45, previousPage);
        }
        if (page < totalPages) {
            ItemStack nextPage = createNavigationItem(Material.ARROW, "&aNext Page");
            gui.setItem(53, nextPage);
        }

        player.openInventory(gui);
    }

    private static ItemStack createItemStack(GUIItem guiItem) {
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
        return itemStack;
    }

    private static ItemStack createNavigationItem(Material material, String name) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            itemStack.setItemMeta(meta);
        }
        return itemStack;
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
        player.sendMessage(CommandGui.getInstance().getMessage("book_received"));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!title.startsWith(CommandGui.getInstance().getMessage("gui_title"))) {
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }

        String sizeMode = CommandGui.getInstance().getConfig().getString("gui-size-mode", "dynamic");

        if (sizeMode.equalsIgnoreCase("fixed")) {
            String itemName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            int currentPage = extractCurrentPage(title);

            if (itemName.equals("Previous Page")) {
                openFixedSizePage(player, currentPage - 1, calculateTotalPages());
                return;
            } else if (itemName.equals("Next Page")) {
                openFixedSizePage(player, currentPage + 1, calculateTotalPages());
                return;
            }
        }

        int slot = event.getSlot();
        GUIItem guiItem = guiItems.get(slot);

        if (guiItem != null) {
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

            cooldowns.get(player).put(slot, currentTime);

            String commandToExecute = guiItem.getCommand().replace("%player%", player.getName());
            if (guiItem.isRunAsPlayer()) {
                player.performCommand(commandToExecute);
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandToExecute);
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

    private int extractCurrentPage(String title) {
        String pageInfo = title.substring(title.lastIndexOf("(Page ") + 6, title.lastIndexOf(")"));
        return Integer.parseInt(pageInfo.split("/")[0]);
    }

    private int calculateTotalPages() {
        int itemsPerPage = 45;
        return (int) Math.ceil((double) guiItems.size() / itemsPerPage);
    }
}
