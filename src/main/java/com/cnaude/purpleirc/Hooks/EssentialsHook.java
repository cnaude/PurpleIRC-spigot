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
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.entity.Player;

/**
 *
 * @author cnaude
 */
public class EssentialsHook {

    private final PurpleIRC plugin;
    private final Essentials essentials;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public EssentialsHook(PurpleIRC plugin) {
        this.plugin = plugin;
        this.essentials = (Essentials) plugin.getServer().getPluginManager().getPlugin(plugin.PL_ESSENTIALS);
    }

    public boolean isMuted(Player player) {
        if (essentials != null) {
            User user = essentials.getUser(player);
            if (user != null) {
                return user.isMuted();
            }
        }
        return false;
    }

}
