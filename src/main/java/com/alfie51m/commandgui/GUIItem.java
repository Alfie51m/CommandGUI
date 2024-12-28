package com.alfie51m.commandgui;

import org.bukkit.Material;

import java.util.List;

public class GUIItem {
    private final String name;
    private final String command;
    private final Material material;
    private final boolean runAsPlayer;
    private final List<String> lore;
    private final int cooldown;
    private final Boolean verbose; // Nullable, can override global verbose-mode

    public GUIItem(String name, String command, Material material, boolean runAsPlayer, List<String> lore, int cooldown, Boolean verbose) {
        this.name = name;
        this.command = command;
        this.material = material;
        this.runAsPlayer = runAsPlayer;
        this.lore = lore;
        this.cooldown = cooldown;
        this.verbose = verbose;
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

    public List<String> getLore() {
        return lore;
    }

    public int getCooldown() {
        return cooldown;
    }

    public Boolean getVerbose() {
        return verbose;
    }
}
