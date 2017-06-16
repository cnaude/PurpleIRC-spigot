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
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

/**
 *
 * @author Chris Naude
 */
public class VanishHook {

    final PurpleIRC plugin;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public VanishHook(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param player
     * @return
     */
    public boolean isVanished(Player player) {
        // Try SuperVanish first
        if (plugin.superVanishHook != null) {
            return plugin.superVanishHook.isVanished(player);
        } else {
            // Fallback to other Vanish
            if (player.hasMetadata("vanished")) {
                plugin.logDebug("Player " + player.getName() + " has vanished metadata" + player.getMetadata("vanished").get(0));
                MetadataValue md = player.getMetadata("vanished").get(0);
                if (md.asBoolean()) {
                    plugin.logDebug("Player " + player.getName() + " is vanished.");
                    return true;
                } else {
                    plugin.logDebug("Player " + player.getName() + " is NOT vanished.");
                }
            } else {
                plugin.logDebug("Player " + player.getName() + " has NO vanished metadata.");
            }
        }
        return false;
    }
}
