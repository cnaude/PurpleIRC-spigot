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
import com.cnaude.purpleirc.Utilities.BotsAndChannels;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Chris Naude
 */
public class ListBans implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "([bot]) ([channel])";
    private final String desc = "List IRC user mask in ban list.";
    private final String name = "listbans";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public ListBans(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void dispatch(CommandSender sender, String[] args) {
        BotsAndChannels bac;

        if (args.length >= 3) {
            bac = new BotsAndChannels(plugin, sender, args[1], args[2]);
        } else if (args.length >= 2) {
            bac = new BotsAndChannels(plugin, sender, args[1]);
        } else if (args.length == 1) {
            bac = new BotsAndChannels(plugin, sender);
        } else {
            sender.sendMessage(fullUsage);
            return;
        }
        if (bac.bot.size() > 0 && bac.channel.size() > 0) {
            for (String botName : bac.bot) {
                for (String channelName : bac.channel) {
                    if (plugin.ircBots.get(botName).banList.containsKey(channelName)) {
                        sender.sendMessage(ChatColor.LIGHT_PURPLE + "-----[  " + ChatColor.WHITE + channelName
                                + ChatColor.LIGHT_PURPLE + " - " + ChatColor.WHITE + "Ban Masks" + ChatColor.LIGHT_PURPLE + " ]-----");
                        for (String userMask : plugin.ircBots.get(botName).banList.get(channelName)) {
                            sender.sendMessage(" - " + userMask);
                        }
                    } else {
                        sender.sendMessage(plugin.invalidChannel.replace("%CHANNEL%", channelName));
                    }
                }
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
