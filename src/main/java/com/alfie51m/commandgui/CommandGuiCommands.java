package com.alfie51m.commandgui;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandGuiCommands implements CommandExecutor, TabCompleter {

    private final CommandGui plugin = CommandGui.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
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

        switch (args[0].toLowerCase()) {
            case "reload":
                if (sender.hasPermission("commandgui.reload")) {
                    plugin.reloadConfig();
                    CommandGuiGUI.loadGUIItems();
                    sender.sendMessage(ChatColor.GREEN + plugin.getMessage("reload_success"));
                } else {
                    sender.sendMessage(ChatColor.RED + plugin.getMessage("no_permission"));
                }
                return true;

            case "book":
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

            case "give":
                if (args.length == 2) {
                    Player target = plugin.getServer().getPlayer(args[1]);
                    if (target != null) {
                        CommandGuiGUI.giveCustomKnowledgeBook(target);
                        sender.sendMessage(ChatColor.GREEN + plugin.getMessage("book_given").replace("%player%", args[1]));
                        target.sendMessage(ChatColor.GREEN + plugin.getMessage("book_received"));
                    } else {
                        sender.sendMessage(ChatColor.RED + plugin.getMessage("player_not_found").replace("%player%", args[1]));
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + plugin.getMessage("invalid_usage"));
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
            if (sender.hasPermission("commandgui.reload")) suggestions.add("reload");
            if (sender.hasPermission("commandgui.book")) suggestions.add("book");
            if (sender.hasPermission("commandgui.give")) suggestions.add("give");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                suggestions.add(player.getName());
            }
        }

        return suggestions.stream()
                .filter(option -> option.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .toList();
    }
}
