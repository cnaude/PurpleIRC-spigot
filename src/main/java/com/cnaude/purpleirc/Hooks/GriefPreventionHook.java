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
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.entity.Player;

/**
 *
 * @author cnaude
 */
public class GriefPreventionHook {

    private final PurpleIRC plugin;
    public final GriefPrevention gp;

    /**
     *
     * @param plugin
     */
    public GriefPreventionHook(PurpleIRC plugin) {
        this.plugin = plugin;
        this.gp = (GriefPrevention) plugin.getServer().getPluginManager().getPlugin("GriefPrevention");
    }

    public boolean isMuted(Player player) {
        plugin.logDebug("GriefPrevention: " + player.getDisplayName());
        if (gp != null) {
            return gp.dataStore.isSoftMuted(player.getUniqueId());
        }
        return false;
    }
    
}
