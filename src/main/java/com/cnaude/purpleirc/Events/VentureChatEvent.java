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
package com.cnaude.purpleirc.Events;

import com.cnaude.purpleirc.PurpleBot;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 *
 * @author Chris Naude Event listener for plugins that want to catch command
 * events from PurpleIRC
 */
public class VentureChatEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final AsyncPlayerChatEvent event;
    private final PurpleBot bot;

    /**
     *
     * @param event
     * @param bot
     */
    public VentureChatEvent(AsyncPlayerChatEvent event, PurpleBot bot) {
        this.event = event;
        this.bot = bot;
    }

    /**
     *
     * @return
     */
    public Player getPlayer() {
        return event.getPlayer();
    }

    /**
     *
     * @return
     */
    public String getMessage() {
        return event.getMessage();
    }

    /**
     *
     * @return
     */
    public PurpleBot getBot() {
        return bot;
    }

    /**
     *
     * @return
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     *
     * @return
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
