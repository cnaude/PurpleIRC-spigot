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

import com.ammaraskar.adminonly.AdminChat;
import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.pircbotx.Channel;
import org.pircbotx.User;

/**
 *
 * @author cnaude
 */
public class GamePlayerChatListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public GamePlayerChatListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        plugin.logDebug("ChatFormat [" + event.isCancelled() + "]: " + event.getFormat());
        if (message.startsWith(PurpleIRC.TOWNYTAG)) {
            event.setMessage(message.replace(PurpleIRC.TOWNYTAG, ""));
            plugin.logDebug("Ignoring due to TownyChat tag");
            return;
        }
        event.setMessage(message.replace(PurpleIRC.TOWNYTAG, ""));
        if (event.isCancelled() && !plugin.isPluginEnabled("FactionChat") && !plugin.ignoreChatCancel) {
            plugin.logDebug("Ignore chat message due to event cancellation: " + event.getMessage());
            return;
        }
        if (event.isCancelled() && plugin.adminPrivateChatHook.ac.toggledPlayers.contains(event.getPlayer().getName())) {
            plugin.logDebug("Ignore AdminChat message due to event cancellation: " + event.getMessage());
            return;
        }
        if (event.getPlayer().hasPermission("irc.message.gamechat")) {
            plugin.logDebug("Player " + event.getPlayer().getName() + " has permission irc.message.gamechat");
            for (PurpleBot ircBot : plugin.ircBots.values()) {
                ircBot.gameChat(event.getPlayer(), event.getMessage());
            }
        } else {
            plugin.logDebug("Player " + event.getPlayer().getName() + " does not have irc.message.gamechat permission.");
        }
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onPlayerChatTabCompleteEvent(PlayerChatTabCompleteEvent event) {
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            for (Channel channel : ircBot.getChannels()) {
                String channelName = channel.getName();
                for (User user : channel.getUsers()) {
                    String nick = user.getNick();
                    if (event.getTabCompletions().contains(nick)) {
                        continue;
                    }
                    if (ircBot.tabIgnoreNicks.containsKey(channelName)) {
                        if (ircBot.tabIgnoreNicks.get(channelName).contains(nick)) {
                            continue;
                        }
                    }
                    if (nick.toLowerCase().startsWith(event.getLastToken().toLowerCase())) {
                        event.getTabCompletions().add(nick);
                    }
                }
            }
        }
    }
}
