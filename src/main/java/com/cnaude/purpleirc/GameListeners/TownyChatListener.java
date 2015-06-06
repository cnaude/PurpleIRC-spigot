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
import com.palmergames.bukkit.TownyChat.channels.Channel;
import com.palmergames.bukkit.TownyChat.events.AsyncChatHookEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author cnaude
 */
public class TownyChatListener implements Listener {

    final PurpleIRC plugin;

    public TownyChatListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAsyncChatHookEvent(AsyncChatHookEvent event) {
        if (event.getAsyncPlayerChatEvent().isCancelled()) {
            plugin.logDebug("Ignore TC chat due to event is cancelled.");
            return;
        }
        Channel townyChannel = event.getChannel();

        plugin.logDebug("TC Format[1]: " + event.getFormat());

        Player player = event.getPlayer();
        if (player.hasPermission("irc.message.gamechat")) {
            for (PurpleBot ircBot : plugin.ircBots.values()) {
                plugin.tcHook.sendToIrc(ircBot, player, townyChannel, event.getMessage());
            }
        }
        event.getAsyncPlayerChatEvent().setMessage(PurpleIRC.TOWNYTAG + event.getMessage());
    }
}
