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

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Chris Naude Event listener for plugins that want to catch irc message
 * events from PurpleIRC
 */
public class IRCMessageEvent extends Event implements Cancellable{

    private static final HandlerList HANDLERS = new HandlerList();
    private String message;
    private final String channel;
    private final String permission;
    private final Player player;
    private boolean cancelled;

    /**
     *
     * @param message
     * @param channel
     * @param permission
     */
    public IRCMessageEvent(String message, String channel, String permission) {
        this.message = message;
        this.channel = channel;
        this.permission = permission;
        this.player = null;
    }

    /**
     *
     * @param message
     * @param channel
     * @param permission
     * @param player
     */
    public IRCMessageEvent(String message, String channel, String permission, Player player) {
        this.message = message;
        this.channel = channel;
        this.permission = permission;
        this.player = player;
    }

    /**
     *
     * @return
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     *
     * @return
     */
    public String getMessage() {
        return this.message;
    }

    /**
     *
     * @return
     */
    public String getPermission() {
        return this.permission;
    }

    /**
     *
     * @return
     */
    public String getChannel() {
        return this.channel;
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

    /**
     * Change the IRC message being sent to the game
     *
     * @param message the message from IRC
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
