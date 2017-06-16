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

import com.cnaude.purpleirc.Events.IRCMessageEvent;
import com.cnaude.purpleirc.Message;
import com.cnaude.purpleirc.PurpleIRC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author Chris Naude
 */
public class IRCMessageListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public IRCMessageListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onIRCMessageEvent(IRCMessageEvent event) {
        if (event.isCancelled()) {
            plugin.logDebug("onIRCMessageEvent cancelled");
            return;
        }
        String permission = event.getPermission();
        String message = event.getMessage();        
        Player player = event.getPlayer();
        String fixedMessage = message.replace("\u200B", "");

        if (player != null) {            
            if (player.hasPermission(permission))  {
                plugin.logDebug("Broadcast player [" + player.getName() + "] [" + permission + "]: " + fixedMessage);
                player.sendMessage(fixedMessage);
            }
            return;
        }
        
        if (plugin.broadcastChatToConsole) {
            plugin.logDebug("Broadcast All [" + permission + "]: " + fixedMessage);
            plugin.messageQueue.add(new Message(fixedMessage, permission));
        } else {
            plugin.logDebug("Broadcast Players [" + permission + "]: " + fixedMessage);
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                if (p.hasPermission(permission)) {
                    p.sendMessage(fixedMessage);
                }
            }
        }
        
    }
}
