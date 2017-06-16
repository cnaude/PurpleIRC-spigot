/*
 * Copyright (C) 2017 cnaude
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
import org.kitteh.vanish.staticaccess.VanishNoPacket;
import org.kitteh.vanish.staticaccess.VanishNotLoadedException;

/**
 *
 * @author cnaude
 */
public class VanishNoPacketHook {
    
   private final PurpleIRC plugin;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public VanishNoPacketHook(PurpleIRC plugin) {
        this.plugin = plugin;      
    }

    
    public boolean isVanished(Player player) {
       try {
           if (VanishNoPacket.isVanished(player.getName())) {
               plugin.logDebug("Player " + player.getName() + " is vanished.");
               return true;
           } else {
               plugin.logDebug("Player " + player.getName() + " is NOT vanished.");
               return false;
           }
       } catch (VanishNotLoadedException ex) {
           plugin.logError("VanishNoPacketHook: " + ex.getMessage());
           return false;
       }
    }
    
}
