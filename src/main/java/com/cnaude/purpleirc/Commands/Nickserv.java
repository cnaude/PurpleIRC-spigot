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

/**
 *
 * @author Chris Naude
 */
public class Nickserv implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "([bot]) [message]";
    private final String desc = "Send nickserv commands to the IRC server.";
    private final String name = "nickserv";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage; 

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public Nickserv(PurpleIRC plugin) {
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
            List<PurpleBot> myBots = new ArrayList<>();
            if (plugin.ircBots.containsKey(args[1])) {
                myBots.add(plugin.ircBots.get(args[1]));
                msgIdx = 2;
            } else {
                myBots.addAll(plugin.ircBots.values());
            }
            for (PurpleBot ircBot : myBots) {
                String msg = "";
                for (int i = msgIdx; i < args.length; i++) {
                    msg = msg + " " + args[i];
                }
                plugin.logDebug("Sending nickserv message to the server: " + msg.substring(1));
                ircBot.asyncRawlineNow("nickserv " + msg.substring(1));                  
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
