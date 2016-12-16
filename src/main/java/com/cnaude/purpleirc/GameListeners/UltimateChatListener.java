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

import br.net.fabiozumbi12.UltimateChat.API.SendChannelMessageEvent;
import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 *
 * @author Chris Naude
 */
public class UltimateChatListener implements Listener {

    final PurpleIRC plugin;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public UltimateChatListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onSendChannelMessageEvent(SendChannelMessageEvent event) {
        plugin.logDebug("Entering onVentureChatEvent: " + event.getMessage());
        //ventureChat(event.getPlayer(), event.getMessage(), event.getBot(), event.getIrcChannel());

        CommandSender sender = event.getSender();
        String message = event.getMessage();
        String uChannel = event.getChannel().getName();
        String uColor = event.getChannel().getColor();
        
        if (sender.hasPermission("irc.message.gamechat")) {
            for (PurpleBot ircBot : plugin.ircBots.values()) {
                ircBot.ultimateChat(sender, uChannel, uColor, event.getMessage());
            }
        }
        

    }
 

}
