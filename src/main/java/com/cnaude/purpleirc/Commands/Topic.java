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
 * @author cnaude
 */
public class Topic implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "([bot]) ([channel]) ([topic])";
    private final String desc = "Set, or get, IRC channel top";
    private final String name = "topic";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage;

    /**
     *
     * @param plugin
     */
    public Topic(PurpleIRC plugin) {
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
        int idx;

        if (args.length >= 4) {
            bac = new BotsAndChannels(plugin, sender, args[1], args[2]);
            idx = 3;
        } else if (args.length == 2) {
            bac = new BotsAndChannels(plugin, sender);
            idx = 1;
        } else {
            bac = new BotsAndChannels(plugin, sender);
            for (String botName : bac.bot) {
                for (String channelName : bac.channel) {
                    sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.DARK_PURPLE 
                            + botName + ChatColor.WHITE 
                            + "/" + ChatColor.DARK_PURPLE + channelName 
                            + ChatColor.WHITE + "]"
                            + " Topic: " + plugin.ircBots.get(botName)
                                    .channelTopic.get(channelName));
                }
            }
            sender.sendMessage(fullUsage);
            return;
        }
        if (bac.bot.size() > 0 && bac.channel.size() > 0) {
            for (String botName : bac.bot) {
                for (String channelName : bac.channel) {
                    String topic = "";
                    for (int i = idx; i < args.length; i++) {
                        topic = topic + " " + args[i];                        
                    }
                    plugin.ircBots.get(botName).changeTopic(channelName,
                            topic.substring(1), sender);
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
