package com.alfie51m.commandgui;

import org.bukkit.Material;

public class GUIItem {
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
