package com.alfie51m.commandgui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandGuiCommands implements CommandExecutor, TabCompleter {

    private final CommandGui plugin = CommandGui.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Default command: open the GUI
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
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "book":
                if (sender instanceof Player player) {
                    if (player.hasPermission("commandgui.book")) {
                        CommandGuiGUI.giveCustomKnowledgeBook(player);
                        player.sendMessage(ChatColor.GREEN + plugin.getMessage("book_received"));
                    } else {
                        sender.sendMessage(ChatColor.RED + plugin.getMessage("no_permission"));
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + plugin.getMessage("only_players"));
                }
                return true;

            case "give":
                if (sender.hasPermission("commandgui.give")) {
                    if (args.length == 2) {
                        Player targetPlayer = Bukkit.getPlayerExact(args[1]);
                        if (targetPlayer != null) {
                            CommandGuiGUI.giveCustomKnowledgeBook(targetPlayer);
                            sender.sendMessage(ChatColor.GREEN + plugin.getMessage("book_given").replace("%player%", args[1]));
                            targetPlayer.sendMessage(ChatColor.GREEN + plugin.getMessage("book_received"));
                        } else {
                            sender.sendMessage(ChatColor.RED + plugin.getMessage("player_not_found").replace("%player%", args[1]));
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + plugin.getMessage("invalid_usage"));
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + plugin.getMessage("no_permission"));
                }
                return true;

            case "reload":
                if (sender.hasPermission("commandgui.reload")) {
                    plugin.reloadConfig();
                    CommandGuiGUI.loadGUIItems();
                    sender.sendMessage(ChatColor.GREEN + plugin.getMessage("reload_success"));
                } else {
                    sender.sendMessage(ChatColor.RED + plugin.getMessage("no_permission"));
                }
                return true;

            default:
                sender.sendMessage(ChatColor.RED + plugin.getMessage("invalid_subcommand"));
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!command.getName().equalsIgnoreCase("commandgui") && !alias.equalsIgnoreCase("cg")) {
            return null;
        }

        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            // Suggest subcommands for the first argument
            if (sender.hasPermission("commandgui.use")) suggestions.add("book");
            if (sender.hasPermission("commandgui.give")) suggestions.add("give");
            if (sender.hasPermission("commandgui.reload")) suggestions.add("reload");
            return filterByPrefix(args[0], suggestions);
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("give") && sender.hasPermission("commandgui.give")) {
            // Suggest online player names for the second argument of "give"
            for (Player player : Bukkit.getOnlinePlayers()) {
                suggestions.add(player.getName());
            }
            return filterByPrefix(args[1], suggestions);
        }

        return suggestions;
    }

    private List<String> filterByPrefix(String prefix, List<String> options) {
        return options.stream()
                .filter(option -> option.toLowerCase().startsWith(prefix.toLowerCase()))
                .toList();
    }
}
