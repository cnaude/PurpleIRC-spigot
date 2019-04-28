/*
 * Copyright (C) 2019 cnaude
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

import com.cnaude.purpleirc.Events.IRCMessageEvent;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author cnaude
 */
public class SynchronousEventWatcher {

    private final PurpleIRC plugin;
    private int bt;
    private final Queue<IRCMessageEvent> queue = new ConcurrentLinkedQueue<>();

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public SynchronousEventWatcher(final PurpleIRC plugin) {
        this.plugin = plugin;
        startWatcher();
    }

    private void startWatcher() {
        plugin.logDebug("Starting synchronous event queue");
        bt = this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, this::queueAndSend, 0, 5);
    }

    private void queueAndSend() {
        IRCMessageEvent event = queue.poll();
        if (event != null) {
            plugin.getServer().getPluginManager().callEvent(event);
        }
    }

    public void cancel() {
        this.plugin.getServer().getScheduler().cancelTask(bt);
    }

    public String clearQueue() {
        int size = queue.size();
        if (!queue.isEmpty()) {
            queue.clear();
        }
        return "Elements removed from event queue: " + size;
    }

    /**
     * Add an IRCMessageEvent to the event queue
     *
     * @param event the IRC event to add to the queue
     */
    public void add(IRCMessageEvent event) {
        plugin.logDebug("Adding event to queue: " + event.getMessage());
        queue.offer(event);
    }
}
