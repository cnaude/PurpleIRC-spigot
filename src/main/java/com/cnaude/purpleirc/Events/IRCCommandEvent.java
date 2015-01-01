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

import com.cnaude.purpleirc.IRCCommand;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author cnaude
 * Event listener for plugins that want to catch command events from PurpleIRC
 */
public class IRCCommandEvent extends Event {

    private static final HandlerList handlers = new HandlerList();    
    private final IRCCommand ircCommand;

    /**
     *
     * @param ircCommand
     */
    public IRCCommandEvent(IRCCommand ircCommand) {
        this.ircCommand = ircCommand;             
    }
    
    /**
     *
     * @return
     */
    public IRCCommand getIRCCommand() {
        return this.ircCommand;
    }

    /**
     *
     * @return
     */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     *
     * @return
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}