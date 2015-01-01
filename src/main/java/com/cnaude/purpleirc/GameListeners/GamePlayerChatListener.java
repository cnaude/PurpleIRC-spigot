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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

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
        if (message.startsWith("[[townytag]]")) {
            event.setMessage(message.replace("[[townytag]]", ""));
            plugin.logDebug("Ignoring due to townytag");
            return;
        }
        event.setMessage(message.replace("[[townytag]]", ""));
        if (event.isCancelled() && !plugin.isPluginEnabled("FactionChat") && !plugin.ignoreChatCancel) {
            plugin.logDebug("Ignore chat message due to event cancellation: " + event.getMessage());
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
}
