/*
 * Copyright (C) 2014 cnaude
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.cnaude.purpleirc.Commands;

import com.cnaude.purpleirc.PurpleIRC;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Chris Naude
 */
public class Help implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "([command])";
    private final String desc = "Display help on a specific command or list all commands.";
    private final String name = "help";

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public Help(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void dispatch(CommandSender sender, String[] args) {
        if (args.length >= 2) {
            String s = args[1];
            if (plugin.commandHandlers.commands.containsKey(s)) {
                sender.sendMessage(helpStringBuilder(
                        plugin.commandHandlers.commands.get(s).name(),
                        plugin.commandHandlers.commands.get(s).desc(),
                        plugin.commandHandlers.commands.get(s).usage()));
                return;
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid sub command: " 
                        + ChatColor.WHITE + s);
                return;
            }
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&5-----[  &fPurpleIRC&5 - &f" + plugin.getServer().getPluginManager()
                .getPlugin("PurpleIRC").getDescription().getVersion() + "&5 ]-----"));
        for (String s : plugin.commandHandlers.sortedCommands) {
            if (plugin.commandHandlers.commands.containsKey(s)) {
                sender.sendMessage(helpStringBuilder(
                        plugin.commandHandlers.commands.get(s).name(),
                        plugin.commandHandlers.commands.get(s).desc(),
                        plugin.commandHandlers.commands.get(s).usage()));
            }
        }

    }

    private String helpStringBuilder(String n, String d, String u) {
        return ChatColor.translateAlternateColorCodes('&', "&5/irc "
                + n + " &6" + u + " &f- " + d);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String desc() {
        return desc;
    }

    @Override
    public String usage() {
        return usage;
    }
}
