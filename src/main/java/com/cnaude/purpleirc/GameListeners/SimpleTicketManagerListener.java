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
import java.util.UUID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import uk.co.joshuawoolley.simpleticketmanager.events.SimpleTicketEvent;

/**
 *
 * @author Chris Naude
 */
public class SimpleTicketManagerListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public SimpleTicketManagerListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onSimpleTicketEvent(SimpleTicketEvent event) {
        plugin.logDebug("STM: " + event.getAction());
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.simpleTicketNotify(UUID.fromString(event.getTicket().getReportingPlayer()),
                    event.getTicket(), ircBot.botNick, "stm-" + event.getAction());
        }

    }

}
