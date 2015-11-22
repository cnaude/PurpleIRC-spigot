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
package com.cnaude.purpleirc.Hooks;

import com.cnaude.purpleirc.PurpleIRC;
import nz.co.lolnet.james137137.FactionChat.FactionChatAPI;
import org.bukkit.entity.Player;

/**
 *
 * @author Chris Naude
 */
public class FactionChatHook {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public FactionChatHook(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param player
     * @return
     */
    public String getChatMode(Player player) {
        String playerChatMode = null;

        try {
            playerChatMode = FactionChatAPI.getChatMode(player);
            plugin.logDebug("fcHook => [CM: " + playerChatMode + "] [" + player.getName() + "]");
        } catch (Exception ex) {
            plugin.logError("fcHook ERROR: " + ex.getMessage());
        }
        if (playerChatMode == null) {
            playerChatMode = "unknown";
        }
        return playerChatMode.toLowerCase();
    }

    /**
     *
     * @param player
     * @return
     */
    public String getFactionName(Player player) {
        String factionName = null;
        try {
            factionName = FactionChatAPI.getFactionName(player);
            plugin.logDebug("fcHook => [FN: " + factionName + "] [" + player.getName() + "]");
        } catch (Exception ex) {
            plugin.logError("fcHook ERROR: " + ex.getMessage());
        }
        if (factionName == null) {
            factionName = "unknown";
        }
        return factionName;
    }
}
