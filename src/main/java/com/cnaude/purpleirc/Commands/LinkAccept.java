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
 * @author cnaude
 */
public class LinkAccept implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "[bot1] [bot2]";
    private final String desc = "Accept link request from bot2.";
    private final String name = "linkaccept";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage;

    /**
     *
     * @param plugin
     */
    public LinkAccept(PurpleIRC plugin) {
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
            String bot1 = plugin.botify(args[1]);
            String bot2 = args[2];
            if (plugin.ircBots.containsKey(bot1)) {                
                PurpleBot ircBot = plugin.ircBots.get(bot1);
                if (!ircBot.botLinkingEnabled) {
                    sender.sendMessage(ChatColor.RED + "Bot linking is not enabled!");
                    return;
                }
                if (ircBot.linkRequests.containsKey(bot2)) {
                    ircBot.setBotLink(bot2, ircBot.linkRequests.get(bot2));
                    ircBot.linkRequests.remove(bot2);
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Now linked to " + ChatColor.WHITE + bot2);
                } else {
                    sender.sendMessage(ChatColor.RED + "No link requests from "
                            + ChatColor.WHITE + bot2 + ChatColor.RED + " found. Ask "
                            + ChatColor.WHITE + bot2 + ChatColor.RED + " to send a request.");
                }
            } else {
                sender.sendMessage(plugin.invalidBotName.replace("%BOT%", bot1));
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
