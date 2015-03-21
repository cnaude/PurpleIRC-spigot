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
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author cnaude
 */
public class Send implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "([bot]) ([channel]) [message]";
    private final String desc = "Send a message to an IRC channel.";
    private final String name = "send";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage; 

    /**
     *
     * @param plugin
     */
    public Send(PurpleIRC plugin) {
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
            int msgIdx = 1;
            String channelName = null;
            List<PurpleBot> myBots = new ArrayList<>();
            if (plugin.ircBots.containsKey(args[1])) {
                myBots.add(plugin.ircBots.get(args[1]));
                msgIdx = 2;
                if (args.length >= 3) {
                    if (plugin.ircBots.get(args[1]).isValidChannel(args[2])) {
                        channelName = args[2];
                    }
                }
            } else {
                myBots.addAll(plugin.ircBots.values());
            }
            for (PurpleBot ircBot : myBots) {
                String msg = "";
                for (int i = msgIdx; i < args.length; i++) {
                    msg = msg + " " + args[i];
                }
                if (channelName == null) {
                    for (String c : ircBot.botChannels) {
                        if (sender instanceof Player) {
                            ircBot.gameChat((Player) sender, c, msg.substring(1));
                        } else {
                            ircBot.consoleChat(c, msg.substring(1));
                        }
                    }
                } else {
                    if (sender instanceof Player) {
                        ircBot.gameChat((Player) sender, channelName, msg.substring(1));
                    } else {
                        ircBot.consoleChat(channelName, msg.substring(1));
                    }
                }

            }
        } else {
            sender.sendMessage(fullUsage);
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
