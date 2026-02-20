package com.alfie51m.commandgui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;


public final class GUITool {

    private GUITool() {
    }

    public static Material parseMaterial(String name) {
        return parseMaterial(name, Material.KNOWLEDGE_BOOK);
    }

    public static Material parseMaterial(String name, Material fallback) {
        if (name == null || name.isEmpty()) {
            log("Material was null/empty. Using fallback: " + fallback);
            return fallback;
        }

        Material mat = Material.matchMaterial(name);

        if (mat == null) {
            log("Invalid material '" + name + "'. Using fallback: " + fallback);
            return fallback;
        }

        return mat;
    }

    private static void log(String message) {
        Bukkit.getLogger().warning("[CommandGUI] " + message);
    }


    public static void giveCommandGUITool(Player player) {
        Material mat = parseMaterial(CommandGui.getInstance().getConfig().getString("gui-tool"));
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(CommandGui.getInstance().getMessage("tool_name"));
            meta.setLore(List.of(CommandGui.getInstance().getMessage("tool_description")));
            item.setItemMeta(meta);
        }

        player.getInventory().addItem(item);
    }
}