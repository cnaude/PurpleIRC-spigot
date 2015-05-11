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
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 *
 * @author cnaude
 */
public class GamePlayerCommandPreprocessingListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public GamePlayerCommandPreprocessingListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) {
            return;
        }
        String msg = event.getMessage();
        if (event.getPlayer().hasPermission("irc.message.gamechat")) {
            if (msg.toLowerCase().startsWith("/me ")) {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    ircBot.gameAction(event.getPlayer(), msg.replace("/me", ""));
                }
            } else if (msg.toLowerCase().startsWith("/broadcast ")) {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    ircBot.gameBroadcast(event.getPlayer(), msg.replace("/broadcast", ""));
                }
            }
        }
        if (plugin.isPluginEnabled("Essentials")) {
            if (msg.toLowerCase().startsWith("/helpop ") || msg.toLowerCase().startsWith("/amsg ") || msg.toLowerCase().startsWith("/ac ")) {
                if (msg.contains(" ")) {
                    String message = msg.split(" ", 2)[1];
                    for (PurpleBot ircBot : plugin.ircBots.values()) {
                        ircBot.essHelpOp(event.getPlayer(), message);
                    }
                }
            }
        }
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            if (!ircBot.channelCmdNotifyEnabled) {
                continue;
            }
            if (msg.toLowerCase().startsWith("/")) {
                String cmd;
                String params = "";
                if (msg.contains(" ")) {
                    cmd = msg.split(" ", 2)[0];
                    params = msg.split(" ", 2)[1];
                } else {
                    cmd = msg;
                }
                cmd = cmd.substring(0);
                boolean ignoreMe = false;
                for (String s : ircBot.channelCmdNotifyIgnore) {
                    if (s.equalsIgnoreCase(cmd)) {
                        ignoreMe = true;
                    }
                }
                if (!ignoreMe) {
                    ircBot.commandNotify(event.getPlayer(), cmd, params);
                }
            }
        }
    }
}
