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

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

/**
 *
 * @author Chris Naude
 */
public class PlaceholderApiHook {

    public String setPlaceholders(Player player, String message) {
        if (player != null && message != null) {
            return PlaceholderAPI.setPlaceholders(player, message);
        }
        return "";
    }
}
