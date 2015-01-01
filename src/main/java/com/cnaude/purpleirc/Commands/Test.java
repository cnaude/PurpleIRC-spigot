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
 * @author cnaude
 */
public class Test implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "[player name]";
    private final String desc = "Testing various Vault methods.";
    private final String name = "test";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage;

    /**
     *
     * @param plugin
     */
    public Test(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void dispatch(CommandSender sender, String[] args) {
        //irc test <username>
        if (plugin.vaultHelpers == null) {
            sender.sendMessage(ChatColor.RED + "Vault is no enabled!");
            return;
        }
        if (plugin.debugMode()) {
            if (args.length >= 2) {
                String playername = args[1];
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "Testing " + playername);
                sender.sendMessage("getGroupPrefix  : " + plugin.getGroupPrefix(plugin.defaultPlayerWorld, playername));
                sender.sendMessage("getGroupSuffix  : " + plugin.getGroupSuffix(plugin.defaultPlayerWorld, playername));
                sender.sendMessage("getPlayerPrefix : " + plugin.getPlayerPrefix(plugin.defaultPlayerWorld, playername));
                sender.sendMessage("getPlayerSuffix : " + plugin.getPlayerSuffix(plugin.defaultPlayerWorld, playername));
                sender.sendMessage("getPlayerGroup  : " + plugin.getPlayerGroup(plugin.defaultPlayerWorld, playername));
            } else {
                sender.sendMessage(fullUsage);
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Debug mode must enabled to use this feature.");
        }
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
