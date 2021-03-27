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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

/**
 *
 * @author Chris Naude
 */
public class PlaceholderApiHook {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public PlaceholderApiHook(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    public String setPlaceholders(Player player, String message) {
        String m = message;
        plugin.logDebug("[setPlaceholders: before] " + m);
        if (player != null && message != null) {
            m = PlaceholderAPI.setPlaceholders(player, message);
        }
        plugin.logDebug("[setPlaceholders: after] " + m);
        // put my tokens back toupper case
        Matcher matcher = Pattern.compile("(%\\w+%)").matcher(m);
        while (matcher.find()) {
            m = m.replace(matcher.group(), matcher.group().toUpperCase());
        }
        plugin.logDebug("[setPlaceholders: after case fix] " + m);
        return m;
    }
}
