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

import be.smexhy.spigot.orebroadcast.OreBroadcastEvent;
import com.ammaraskar.adminonly.AdminChat;
import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author Chris Naude
 */
public class OreBroadcastListener implements Listener {

    private final PurpleIRC plugin;
    public final AdminChat ob;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public OreBroadcastListener(PurpleIRC plugin) {
        this.plugin = plugin;
        this.ob = (AdminChat) plugin.getServer().getPluginManager().getPlugin("OreBroadcast");
    }

    /**
     *
     * @param e
     */
    @EventHandler
    public void onOreBroadcastEvent(OreBroadcastEvent e) {

        String blockName;
        if (e.getBlockMined().getType().equals(Material.GLOWING_REDSTONE_ORE)) {
            blockName = "redstone";
        } else {
            blockName = e.getBlockMined().getType().name().toLowerCase().replace("_ore", "");
        }
        String color = ob.getConfig().getString("colors." + blockName, "white").toUpperCase();
        ChatColor oreColor = ChatColor.valueOf(color);

        plugin.logDebug("onOreBroadcastEvent caught");
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.gameOreBroadcast(e.getSource(), blockName, oreColor, e.getVein(), e.getBlockMined().getLocation());
        }
    }

}
