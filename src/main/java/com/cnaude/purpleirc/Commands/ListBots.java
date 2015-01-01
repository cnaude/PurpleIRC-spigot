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

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.pircbotx.Channel;

/**
 *
 * @author cnaude
 */
public class ListBots implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "";
    private final String desc = "List IRC bots.";
    private final String name = "listbots";

    /**
     *
     * @param plugin
     */
    public ListBots(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void dispatch(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.DARK_PURPLE + "-----[  " + ChatColor.WHITE + "IRC Bots"
                + ChatColor.DARK_PURPLE + "   ]-----");
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            sender.sendMessage(ChatColor.DARK_PURPLE + "* " + ChatColor.WHITE + ircBot.getFileName()
            + ChatColor.DARK_PURPLE + " [" + ChatColor.WHITE + ircBot.botNick + ChatColor.DARK_PURPLE + "]");
            if (ircBot.isConnected()) {
                for (Channel channel : ircBot.getChannels()) {
                    sender.sendMessage(ChatColor.DARK_PURPLE + "  - " + ChatColor.WHITE + channel.getName());
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Not connected.");
            }
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
