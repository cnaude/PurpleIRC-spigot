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
package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import com.cnaude.purpleirc.TemplateName;
import com.gmail.nossr50.events.chat.McMMOAdminChatEvent;
import com.gmail.nossr50.events.chat.McMMOChatEvent;
import com.gmail.nossr50.events.chat.McMMOPartyChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author Chris Naude
 */
public class McMMOChatListener implements Listener {

    final PurpleIRC plugin;

    public McMMOChatListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMcMMOChatEvent(McMMOChatEvent event) {
        String sender = event.getSender();
        Player player = plugin.getServer().getPlayer(sender);
        if (plugin.adminPrivateChatHook != null) {
            if (plugin.adminPrivateChatHook.ac.toggledPlayers.contains(player.getName())) {
                plugin.logDebug("[onMcMMOChatEvent]: Ignoring chat due to AdminPrivate chat message");
                return;
            }
        }
        plugin.logDebug("[onMcMMOChatEvent]: " + sender);
        if (player != null && !sender.isEmpty()) {
            if (player.hasPermission("irc.message.gamechat")) {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    mcMMOChat(player, event.getMessage(), ircBot);
                }
            }
        }
    }

    @EventHandler
    public void onMcMMOAdminChatEvent(McMMOAdminChatEvent event) {
        String sender = event.getSender();
        Player player = plugin.getServer().getPlayer(sender);
        if (plugin.adminPrivateChatHook != null) {
            if (plugin.adminPrivateChatHook.ac.toggledPlayers.contains(player.getName())) {
                plugin.logDebug("[onMcMMOAdminChatEvent]: Ignoring chat due to AdminPrivate chat message");
                return;
            }
        }
        plugin.logDebug("[onMcMMOAdminChatEvent]: " + sender);
        if (player != null && !sender.isEmpty()) {
            if (player.hasPermission("irc.message.gamechat")) {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    mcMMOAdminChat(player, event.getMessage(), ircBot);
                }
            }
        }
    }

    @EventHandler
    public void onMcMMOPartyChatEvent(McMMOPartyChatEvent event) {
        String sender = event.getSender();
        Player player = plugin.getServer().getPlayer(sender);
        String party = event.getParty();
        plugin.logDebug("onMcMMOPartyChatEvent caught: " + sender);
        if (plugin.adminPrivateChatHook != null) {
            if (plugin.adminPrivateChatHook.ac.toggledPlayers.contains(player.getName())) {
                plugin.logDebug("[onMcMMOPartyChatEvent]: Ignoring chat due to AdminPrivate chat message");
                return;
            }
        }
        if (player != null && !sender.isEmpty()) {
            if (player.hasPermission("irc.message.gamechat")) {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    mcMMOPartyChat(player, party, event.getMessage(), ircBot);
                }
            }
        }
    }

    public void mcMMOAdminChat(Player player, String message, PurpleBot bot) {
        if (!bot.isConnected()) {
            return;
        }
        if (bot.floodChecker.isSpam(player)) {
            bot.sendFloodWarning(player);
            return;
        }
        for (String channelName : bot.botChannels) {
            if (bot.isPlayerInValidWorld(player, channelName)) {
                if (bot.isMessageEnabled(channelName, TemplateName.MCMMO_ADMIN_CHAT)) {
                    plugin.logDebug("Sending message because " + TemplateName.MCMMO_ADMIN_CHAT + " is enabled.");
                    bot.asyncIRCMessage(channelName, plugin.tokenizer
                            .mcMMOChatToIRCTokenizer(player, plugin
                                    .getMessageTemplate(bot.botNick, channelName, TemplateName.MCMMO_ADMIN_CHAT), message));
                } else {
                    plugin.logDebug("Player " + player.getName()
                            + " is in mcMMO AdminChat but " + TemplateName.MCMMO_ADMIN_CHAT + " is disabled.");
                }
            }
        }
    }

    public void mcMMOPartyChat(Player player, String partyName, String message, PurpleBot bot) {
        if (!bot.isConnected()) {
            return;
        }
        if (bot.floodChecker.isSpam(player)) {
            bot.sendFloodWarning(player);
            return;
        }
        for (String channelName : bot.botChannels) {
            if (bot.isPlayerInValidWorld(player, channelName)) {
                if (bot.isMessageEnabled(channelName, TemplateName.MCMMO_PARTY_CHAT)) {
                    plugin.logDebug("Sending message because " + TemplateName.MCMMO_PARTY_CHAT + " is enabled.");
                    bot.asyncIRCMessage(channelName, plugin.tokenizer
                            .mcMMOPartyChatToIRCTokenizer(player, plugin
                                    .getMessageTemplate(bot.botNick, channelName, TemplateName.MCMMO_PARTY_CHAT), message, partyName));
                } else {
                    plugin.logDebug("Player " + player.getName()
                            + " is in mcMMO PartyChat but " + TemplateName.MCMMO_PARTY_CHAT + " is disabled.");
                }
            }
        }
    }

    public void mcMMOChat(Player player, String message, PurpleBot bot) {
        if (!bot.isConnected()) {
            return;
        }
        if (bot.floodChecker.isSpam(player)) {
            bot.sendFloodWarning(player);
            return;
        }
        for (String channelName : bot.botChannels) {
            if (bot.isPlayerInValidWorld(player, channelName)) {
                if (bot.isMessageEnabled(channelName, TemplateName.MCMMO_CHAT)) {
                    plugin.logDebug("Sending message because " + TemplateName.MCMMO_CHAT + " is enabled.");
                    bot.asyncIRCMessage(channelName, plugin.tokenizer
                            .mcMMOChatToIRCTokenizer(player, plugin
                                    .getMessageTemplate(bot.botNick, channelName, TemplateName.MCMMO_CHAT), message));
                } else {
                    plugin.logDebug("Player " + player.getName()
                            + " is in mcMMO Chat but " + TemplateName.MCMMO_CHAT + " is disabled.");
                }
            }
        }
    }

}
