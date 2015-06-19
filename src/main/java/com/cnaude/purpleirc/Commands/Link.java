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
import java.math.BigInteger;
import java.security.SecureRandom;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author cnaude
 */
public class Link implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "[bot1] [bot2] ([secret])";
    private final String desc = "Link bot1 with bot2. Optionally provide secret code.";
    private final String name = "link";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage;

    /**
     *
     * @param plugin
     */
    public Link(PurpleIRC plugin) {
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

                String code = generateCode(args, 130);
                String clearText = String.format("LINK_REQUEST:%s", code);
                
                ircBot.asyncCTCPMessage(bot2, plugin.encodeLinkMsg(PurpleIRC.LINK_CMD, clearText));
                ircBot.setBotLink(bot2, code);
                
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "Link request sent to " + ChatColor.WHITE + bot2);
            } else {
                sender.sendMessage(plugin.invalidBotName.replace("%BOT%", bot1));
            }
        } else {
            sender.sendMessage(fullUsage);
        }
    }

    private String generateCode(String args[], int size) {
        if (args.length == 4) {
            return args[3];
        } else {
            return new BigInteger(size, new SecureRandom()).toString(size);
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
