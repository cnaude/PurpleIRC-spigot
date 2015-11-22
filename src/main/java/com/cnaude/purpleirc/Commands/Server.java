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

/**
 *
 * @author Chris Naude
 */
public class Server implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "[bot] [server] ([true|false])";
    private final String desc = "Set IRC server hostname. Optionally set autoconnect.";
    private final String name = "server";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage; 

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public Server(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void dispatch(CommandSender sender, String[] args) {
        if (args.length >= 3) {
            String bot = args[1];
            String server = args[2];
            if (plugin.ircBots.containsKey(bot)) {
                if (args.length == 3) {
                    plugin.ircBots.get(bot).setServer(sender, server);
                } else if (args.length == 4) {
                    plugin.ircBots.get(bot).setServer(sender, server, Boolean.parseBoolean(args[3]));
                }
            } else {
                sender.sendMessage(plugin.invalidBotName.replace("%BOT%", bot));
            }
        } else if (args.length == 2) {
            if (args[1].equals("info")) {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "-----[  " 
                            + ChatColor.WHITE + "IRC Servers" + ChatColor.LIGHT_PURPLE + "   ]-----");
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "* " + ChatColor.WHITE + ircBot.botNick);
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + " Server     : " + ChatColor.WHITE + ircBot.botServer);
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + " Port       : " + ChatColor.WHITE + ircBot.botServerPort);
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + " SSL Cipher : " + ChatColor.WHITE + ircBot.sslInfo);
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
