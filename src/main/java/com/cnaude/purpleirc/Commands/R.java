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
import com.cnaude.purpleirc.TemplateName;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Chris Naude
 */
public class R implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "([message])";
    private final String desc = "Reply to a private message from an IRC user.";
    private final String name = "r";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public R(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void dispatch(CommandSender sender, String[] args) {
        if (args.length == 1) {
            for (PurpleBot ircBot : plugin.ircBots.values()) {
                if (ircBot.ircPrivateMsgMap.containsKey(sender.getName())) {
                    sender.sendMessage("Most recent private is message is from " + ircBot.ircPrivateMsgMap.get(sender.getName()));
                }
            }
        } else if (args.length >= 2) {
            plugin.logDebug("Dispatching r command...");
            for (PurpleBot ircBot : plugin.ircBots.values()) {
                String nick = "";
                String msg = "";
                if (ircBot.ircPrivateMsgMap.containsKey(sender.getName())) {
                    nick = ircBot.ircPrivateMsgMap.get(sender.getName());
                }
                if (nick.isEmpty()) {
                    sender.sendMessage(ChatColor.WHITE + "Please use " + ChatColor.GOLD
                            + "/irc msg" + ChatColor.WHITE + " first.");
                    continue;
                }
                final String template = plugin.getMessageTemplate(ircBot.botNick, "", TemplateName.GAME_PCHAT_RESPONSE);
                for (int i = 1; i < args.length; i++) {
                    msg = msg + " " + args[i];
                }
                if (sender instanceof Player) {
                    ircBot.msgPlayer((Player) sender, nick, msg.substring(1));
                } else {
                    ircBot.consoleMsgPlayer(nick, msg.substring(1));
                }
                if (!template.isEmpty()) {
                    sender.sendMessage(plugin.tokenizer.msgChatResponseTokenizer(sender, nick, msg.substring(1), template));
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
