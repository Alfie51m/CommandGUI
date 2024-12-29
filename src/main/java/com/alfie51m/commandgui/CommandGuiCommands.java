package com.alfie51m.commandgui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandGuiCommands implements org.bukkit.command.CommandExecutor, TabCompleter {

    private final CommandGui plugin = CommandGui.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("commandgui") || label.equalsIgnoreCase("cg")) {
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

            String subCommand = args[0].toLowerCase();

            switch (subCommand) {
                case "help" -> {
                    sendHelpMessage(sender);
                }
                case "reload" -> {
                    if (sender.hasPermission("commandgui.reload")) {
                        plugin.reloadConfig();
                        plugin.loadLanguageFile(); // Reload the language file
                        CommandGuiGUI.loadGUIItems();
                        sender.sendMessage(ChatColor.GREEN + plugin.getMessage("reload_success"));
                    } else {
                        sender.sendMessage(ChatColor.RED + plugin.getMessage("no_permission"));
                    }
                }
                case "book" -> {
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
                }
                case "give" -> {
                    if (sender.hasPermission("commandgui.give")) {
                        if (args.length > 1) {
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
                }
                default -> sender.sendMessage(ChatColor.RED + plugin.getMessage("invalid_subcommand"));
            }
            return true;
        }
        return false;
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "CommandGUI Help:");
        sender.sendMessage(ChatColor.YELLOW + "/commandgui" + ChatColor.WHITE + " - Opens the Command GUI.");
        sender.sendMessage(ChatColor.YELLOW + "/commandgui book" + ChatColor.WHITE + " - Gives the player a CommandGUI Book.");
        sender.sendMessage(ChatColor.YELLOW + "/commandgui give <player>" + ChatColor.WHITE + " - Gives the specified player a CommandGUI Book.");
        sender.sendMessage(ChatColor.YELLOW + "/commandgui reload" + ChatColor.WHITE + " - Reloads the plugin configuration.");
        sender.sendMessage(ChatColor.YELLOW + "/commandgui help" + ChatColor.WHITE + " - Displays this help message.");
        sender.sendMessage(ChatColor.YELLOW + "/cg" + ChatColor.WHITE + " - /cg is an alias for /commandgui");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (alias.equalsIgnoreCase("commandgui") || alias.equalsIgnoreCase("cg")) {
            if (args.length == 1) {
                return Arrays.asList("reload", "book", "give", "help").stream()
                        .filter(sub -> sender.hasPermission("commandgui." + sub) || sub.equalsIgnoreCase("book") || sub.equalsIgnoreCase("help"))
                        .filter(sub -> sub.startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());
            } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }
}
