/*
 * Copyright (C) 2015 cnaude
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
package com.cnaude.purpleirc.Hooks;

import com.cnaude.purpleirc.PurpleIRC;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author cnaude
 */
public class VentureChatHook {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public VentureChatHook(PurpleIRC plugin) {
        this.plugin = plugin;

    }

    public void sendMessage(String channel, String message) {
        if (channel.isEmpty() || message.isEmpty()) {
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer(player);
            if (mcp != null) {
                for (String listen : mcp.getListening()) {
                    if (listen.equalsIgnoreCase(channel)) {
                        plugin.broadcastToPlayer(player, message, "irc.message.chat");
                    }
                }
            }
        }
    }

}
