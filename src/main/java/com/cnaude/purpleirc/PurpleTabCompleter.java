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
package com.cnaude.purpleirc;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.pircbotx.Channel;
import org.pircbotx.User;

/**
 *
 * @author cnaude
 */
public class PurpleTabCompleter implements TabCompleter {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public PurpleTabCompleter(final PurpleIRC plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmnd, String string, String[] strings) {

        List<String> list = new ArrayList<>();

        plugin.logDebug("S: " + string);
        for (String s : strings) {
            plugin.logDebug("S1: " + s);
        }
        if (strings.length == 1) {
            for (String c : plugin.commandHandlers.sortedCommands) {
                if (cs.hasPermission("irc." + c)) {
                    if (c.startsWith(strings[0])) {
                        list.add(c);
                    }
                }
            }
        }

        if (strings.length == 2) {
            for (PurpleBot ircBot : plugin.ircBots.values()) {
                for (Channel channel : ircBot.getChannels()) {
                    for (User user : channel.getUsers()) {
                        if (user.getNick().startsWith(strings[1])) {
                            list.add(user.getNick());
                        }
                    }
                }
            }
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (plugin.vanishHook != null) {
                    if (plugin.vanishHook.isVanished(player)) {
                        continue;
                    }
                }
                if (player.getDisplayName().startsWith(strings[1])) {
                    list.add(player.getDisplayName());
                }
            }
        }

        return list;
    }

}
