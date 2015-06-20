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
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author cnaude
 */
public class SMsg implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "([bot]) [(server:)player] [message]";
    private final String desc = "Send a message to a player on another server.";
    private final String name = "smsg";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage;

    /**
     *
     * @param plugin
     */
    public SMsg(PurpleIRC plugin) {
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
            plugin.logDebug("Dispatching smsg command...");
            int msgIdx = 2;
            String target;
            java.util.List<PurpleBot> myBots = new ArrayList<>();
            if (plugin.ircBots.containsKey(plugin.botify(args[1]))) {
                myBots.add(plugin.ircBots.get(plugin.botify(args[1])));
                msgIdx = 3;
                target = args[2];
            } else {
                myBots.addAll(plugin.ircBots.values());
                target = args[1];
            }

            if (msgIdx == 3 && args.length <= 3) {
                sender.sendMessage(fullUsage);
                return;
            }

            String msg = "";
            for (int i = msgIdx; i < args.length; i++) {
                msg = msg + " " + args[i];
            }
            msg = msg.trim();

            if (plugin.getServer().getPlayer(target) instanceof Player) {
                Player player = plugin.getServer().getPlayer(target);
                String template = plugin.getMsgTemplate("MAIN", "", TemplateName.GAME_PCHAT);
                String targetMsg = plugin.tokenizer.gameChatTokenizer(player, template, msg);
                String responseTemplate = plugin.getMsgTemplate("MAIN", "", TemplateName.GAME_PCHAT_RESPONSE);
                if (!responseTemplate.isEmpty()) {
                    String responseMsg = plugin.tokenizer.gameChatTokenizer(player, responseTemplate, msg);
                    sender.sendMessage(responseMsg);
                }
                plugin.logDebug("Tokenized message: " + targetMsg);
                player.sendMessage(targetMsg);
                plugin.privateMsgReply.put(player.getName(), sender.getName());
                return;
            }

            for (PurpleBot ircBot : myBots) {
                String remoteBot = "";
                String remotePlayer = "";
                if (target.contains(":")) {
                    remoteBot = target.split(":", 2)[0];
                    remotePlayer = target.split(":", 2)[1];
                } else {
                    for (String s : ircBot.remotePlayers.keySet()) {
                        plugin.logDebug("RB: " + s);
                        for (String rp : ircBot.remotePlayers.get(s)) {
                            plugin.logDebug("RP: " + rp);
                            if (target.equalsIgnoreCase(rp)) {
                                remotePlayer = target;
                                remoteBot = s;
                                break;
                            }
                        }
                    }
                }

                if (remotePlayer.isEmpty()) {
                    sender.sendMessage(ChatColor.RED + "Player "
                            + ChatColor.WHITE + target + ChatColor.RED + " not found!");
                    return;
                }

                if (ircBot.botLinkingEnabled) {
                    final String template = plugin.getMsgTemplate(ircBot.botNick, "", TemplateName.GAME_PCHAT_RESPONSE);
                    if (sender instanceof Player) {
                        ircBot.msgRemotePlayer((Player) sender, remoteBot, remotePlayer, msg.substring(1));
                    } else {
                        ircBot.msgRemotePlayer(sender, remoteBot, remotePlayer, msg.substring(1));
                    }
                    if (!template.isEmpty()) {
                        sender.sendMessage(plugin.tokenizer.msgChatResponseTokenizer(target, msg.substring(1), template));
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
