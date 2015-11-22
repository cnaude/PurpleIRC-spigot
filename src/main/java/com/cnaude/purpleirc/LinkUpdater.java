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

import org.bukkit.scheduler.BukkitTask;

/**
 * Sends player information to remotely links bots.
 * 
 * @author Chris Naude
 */
public class LinkUpdater {

    private final PurpleIRC plugin;
    private final BukkitTask bt;

    /**
     * Asynchronously send player information every 400 ticks.
     * 
     * @param plugin the PurpleIRC plugin
     */
    public LinkUpdater(final PurpleIRC plugin) {
        this.plugin = plugin;

        bt = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new Runnable() {
            @Override
            public void run() {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    ircBot.sendRemotePlayerInfo();
                }
            }
        }, 0, 400);
    }

    /**
     * Cancel the scheduled BukkitTask. Call this when
     * shutting down.
     */
    public void cancel() {
        bt.cancel();
    }
    
}
