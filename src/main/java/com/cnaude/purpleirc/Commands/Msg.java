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
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author cnaude
 */
public class Msg implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "([bot]) [user] [message]";
    private final String desc = "Send a private message to an IRC user.";
    private final String name = "msg";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage;

    /**
     *
     * @param plugin
     */
    public Msg(PurpleIRC plugin) {
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
            plugin.logDebug("Dispatching msg command...");
            int msgIdx = 2;
            String nick;
            java.util.List<PurpleBot> myBots = new ArrayList<>();
            if (plugin.ircBots.containsKey(args[1])) {
                myBots.add(plugin.ircBots.get(args[1]));
                msgIdx = 3;
                nick = args[2];
            } else {
                myBots.addAll(plugin.ircBots.values());
                nick = args[1];
            }

            if (msgIdx == 3 && args.length <= 3) {
                sender.sendMessage(fullUsage);
                return;
            }

            for (PurpleBot ircBot : myBots) {
                String msg = "";
                final String template = plugin.getMsgTemplate(ircBot.botNick, "game-pchat-response");
                for (int i = msgIdx; i < args.length; i++) {
                    msg = msg + " " + args[i];
                }
                if (sender instanceof Player) {
                    ircBot.msgPlayer((Player) sender, nick, msg.substring(1));
                } else {
                    ircBot.consoleMsgPlayer(nick, msg.substring(1));
                }                
                if (!template.isEmpty()) {
                    sender.sendMessage(plugin.tokenizer.msgChatResponseTokenizer(nick, msg.substring(1), template));
                }
            }
        }
        else {
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
