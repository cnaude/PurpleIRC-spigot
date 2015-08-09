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
import me.ryanhamshire.GriefPrevention.ChatMode;
import me.ryanhamshire.GriefPrevention.events.ChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author cnaude
 */
public class GriefPreventionListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public GriefPreventionListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onChatEvent(ChatEvent event) {
        ChatMode chatMode = event.getChatMode();
        Player player = event.getPlayer();
        String message = event.getMessage();
        plugin.logDebug("GP (" + chatMode
                + "): " + player.getName()
                + ": " + message);
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            if (chatMode.equals(ChatMode.NORMAL)) {
                ircBot.gameChat(player, message);
            } 
        }
    }
}
