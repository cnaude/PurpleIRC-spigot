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
import com.nyancraft.reportrts.event.TicketAssignEvent;
import com.nyancraft.reportrts.event.TicketClaimEvent;
import com.nyancraft.reportrts.event.TicketCompleteEvent;
import com.nyancraft.reportrts.event.TicketCreateEvent;
import com.nyancraft.reportrts.event.TicketHoldEvent;
import com.nyancraft.reportrts.event.TicketBroadcastEvent;
import com.nyancraft.reportrts.event.TicketReopenEvent;
import com.nyancraft.reportrts.event.TicketUnclaimEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author Chris Naude
 */
public class ReportRTSListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public ReportRTSListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onTicketCreateEvent(TicketCreateEvent event) {
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.reportRTSNotify(event.getTicket().getName(),
                    event.getTicket(), ircBot.botNick, "rts-notify");
        }
    }

    @EventHandler
    public void onTicketCompleteEvent(TicketCompleteEvent event) {
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.reportRTSNotify(event.getTicket().getName(),
                    event.getTicket(), ircBot.botNick, "rts-complete");
        }
    }

    @EventHandler
    public void onTicketClaimEvent(TicketClaimEvent event) {
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.reportRTSNotify(event.getTicket().getName(),
                    event.getTicket(), ircBot.botNick, "rts-claim");
        }
    }

    @EventHandler
    public void onTicketUnclaimEvent(TicketUnclaimEvent event) {
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.reportRTSNotify(event.getTicket().getName(),
                    event.getTicket(), ircBot.botNick, "rts-unclaim");
        }
    }

    @EventHandler
    public void onTicketHoldEvent(TicketHoldEvent event) {
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.reportRTSNotify(event.getTicket().getName(),
                    event.getTicket(), ircBot.botNick, "rts-held");
        }
    }

    @EventHandler
    public void onTicketAssignEvent(TicketAssignEvent event) {
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.reportRTSNotify(event.getTicket().getName(),
                    event.getTicket(), ircBot.botNick, "rts-assign");
        }
    }

    @EventHandler
    public void onTicketReopenEvent(TicketReopenEvent event) {
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.reportRTSNotify(event.getTicket().getName(),
                    event.getTicket(), ircBot.botNick, "rts-reopen");
        }
    }

    @EventHandler
    public void onTicketBroadcastEvent(TicketBroadcastEvent event) {
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.reportRTSNotify(event.getSender(),
                    event.getMessage(), ircBot.botNick, "rts-modbroadcast");
        }
    }
}
