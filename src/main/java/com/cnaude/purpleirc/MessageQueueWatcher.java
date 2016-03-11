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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author Chris Naude Poll the command queue and dispatch to Bukkit
 */
public class MessageQueueWatcher {

    private final PurpleIRC plugin;
    private int bt;
    private final Queue<Message> queue = new ConcurrentLinkedQueue<>();

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public MessageQueueWatcher(final PurpleIRC plugin) {
        this.plugin = plugin;
        startWatcher();
    }

    private void startWatcher() {
        plugin.logDebug("Starting broadcast queue");
        bt = this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
            @Override
            public void run() {
                queueAndSend();
            }
        }, 0, 5);
    }

    private void queueAndSend() {
        Message message = queue.poll();
        if (message != null) {
            plugin.getServer().broadcast(message.message, message.permission);
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
        return "Elements removed from broadcast queue: " + size;
    }

    /**
     * Add an IRCCommand to the command queue
     *
     * @param message
     */
    public void add(Message message) {
        plugin.logDebug("Adding message to broadcast queue: " + message.message);
        queue.offer(message);
    }
}
