package com.alfie51m.commandgui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGuiCommands implements CommandExecutor {

    private final CommandGui plugin = CommandGui.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (label.toLowerCase()) {
            case "commandgui":
                if (sender instanceof Player player) {
                    if (player.hasPermission("commandgui.use")) {
                        CommandGuiGUI.openCommandGUI(player);
                    } else {
                        sender.sendMessage(ChatColor.RED + plugin.getMessage("no_permission"));
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + plugin.getMessage("only_players"));
                }
                return true;

            case "cgreload":
                if (sender.hasPermission("commandgui.reload")) {
                    plugin.reloadConfig();
                    CommandGuiGUI.loadGUIItems();
                    sender.sendMessage(ChatColor.GREEN + plugin.getMessage("reload_success"));
                } else {
                    sender.sendMessage(ChatColor.RED + plugin.getMessage("no_permission"));
                }
                return true;

            case "cgbook":
                if (sender instanceof Player player) {
                    if (player.hasPermission("commandgui.book")) {
                        CommandGuiGUI.giveCustomKnowledgeBook(player);
                        sender.sendMessage(ChatColor.GREEN + plugin.getMessage("book_received"));
                    } else {
                        sender.sendMessage(ChatColor.RED + plugin.getMessage("no_permission"));
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + plugin.getMessage("only_players"));
                }
                return true;

            case "cggive":
                if (sender.hasPermission("commandgui.give")) {
                    if (args.length == 1) {
                        Player targetPlayer = Bukkit.getPlayerExact(args[0]);
                        if (targetPlayer != null) {
                            CommandGuiGUI.giveCustomKnowledgeBook(targetPlayer);
                            sender.sendMessage(ChatColor.GREEN + plugin.getMessage("book_given").replace("%player%", args[0]));
                            targetPlayer.sendMessage(ChatColor.GREEN + plugin.getMessage("book_received"));
                        } else {
                            sender.sendMessage(ChatColor.RED + plugin.getMessage("player_not_found").replace("%player%", args[0]));
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + plugin.getMessage("invalid_usage"));
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + plugin.getMessage("no_permission"));
                }
                return true;

            default:
                return false;
        }
    }
}
