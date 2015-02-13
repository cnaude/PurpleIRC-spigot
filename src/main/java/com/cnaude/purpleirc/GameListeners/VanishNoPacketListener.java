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
import org.kitteh.vanish.event.VanishFakeJoinEvent;
import org.kitteh.vanish.event.VanishFakeQuitEvent;

/**
 *
 * @author cnaude
 */
public class VanishNoPacketListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public VanishNoPacketListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onVanishFakeQuitEvent(VanishFakeQuitEvent event) {
        plugin.logDebug("onVanishFakeQuitEvent: " + event.getPlayer().getName());
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.gameFakeQuit(event.getPlayer(), event.getQuitMessage());
            if (plugin.netPackets != null) {
                plugin.netPackets.updateTabList(event.getPlayer());
            }
        }
    }
    
    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onVanishFakeJoinEvent(final VanishFakeJoinEvent event) {
        plugin.logDebug("onVanishFakeJoinEvent: " + event.getPlayer().getDisplayName()
                + ": " + event.getPlayer().getCustomName());
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                plugin.clearHostCache(event.getPlayer());
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    ircBot.gameFakeJoin(event.getPlayer(), event.getJoinMessage());
                    if (plugin.netPackets != null) {
                        plugin.netPackets.updateTabList(event.getPlayer());
                    }
                }
                plugin.updateDisplayNameCache(event.getPlayer()); 
                plugin.updateUuidCache(event.getPlayer());
            }
        }, 20);
    }
}
