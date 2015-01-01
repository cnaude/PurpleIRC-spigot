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
import com.gmail.nossr50.events.chat.McMMOAdminChatEvent;
import com.gmail.nossr50.events.chat.McMMOChatEvent;
import com.gmail.nossr50.events.chat.McMMOPartyChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author cnaude
 */
public class McMMOChatListener implements Listener {

    final PurpleIRC plugin;

    public McMMOChatListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMcMMOChatEvent(McMMOChatEvent event) {
        event.setMessage(event.getMessage().replace("[[townytag]]", ""));
        String sender = event.getSender();
        Player player = plugin.getServer().getPlayer(sender);
        plugin.logDebug("McMMOChatEvent caught: " + sender);
        if (player != null && !sender.isEmpty()) {
            if (player.hasPermission("irc.message.gamechat")) {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    ircBot.mcMMOChat(player, event.getMessage());
                }
            }
        }
    }

    @EventHandler
    public void onMcMMOAdminChatEvent(McMMOAdminChatEvent event) {
        event.setMessage(event.getMessage().replace("[[townytag]]", ""));
        String sender = event.getSender();
        Player player = plugin.getServer().getPlayer(sender);
        plugin.logDebug("McMMOAdminChatEvent caught: " + sender);
        if (player != null && !sender.isEmpty()) {
            if (player.hasPermission("irc.message.gamechat")) {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    ircBot.mcMMOAdminChat(player, event.getMessage());
                }
            }
        }
    }

    @EventHandler
    public void onMcMMOPartyChatEvent(McMMOPartyChatEvent event) {
        event.setMessage(event.getMessage().replace("[[townytag]]", ""));
        String sender = event.getSender();
        Player player = plugin.getServer().getPlayer(sender);
        String party = event.getParty();
        plugin.logDebug("onMcMMOPartyChatEvent caught: " + sender);
        if (player != null && !sender.isEmpty()) {
            if (player.hasPermission("irc.message.gamechat")) {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    ircBot.mcMMOPartyChat(player, party, event.getMessage());
                }
            }
        }
    }
}
