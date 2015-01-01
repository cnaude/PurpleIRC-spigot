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

import com.cnaude.purpleirc.Events.IRCCommandEvent;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.bukkit.command.CommandException;

/**
 *
 * @author Chris Naude Poll the command queue and dispatch to Bukkit
 */
public class CommandQueueWatcher {

    private final PurpleIRC plugin;
    private int bt;
    private final Queue<IRCCommand> queue = new ConcurrentLinkedQueue<>();

    /**
     *
     * @param plugin
     */
    public CommandQueueWatcher(final PurpleIRC plugin) {
        this.plugin = plugin;
        startWatcher();
    }

    private void startWatcher() {
        plugin.logDebug("Starting command queue");
        bt = this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
            @Override
            public void run() {
                queueAndSend();
            }
        }, 0, 5);
    }

    private void queueAndSend() {
        IRCCommand ircCommand = queue.poll();
        if (ircCommand != null) {
            try {
                if (plugin.getServer().getVersion().contains("MC: 1.8")
                        && plugin.getServer().getPluginCommand(ircCommand.getGameCommand()) == null) {
                    plugin.logDebug("Dispatching command as ConsoleSender: " + ircCommand.getGameCommand());
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), ircCommand.getGameCommand());
                } else {
                    plugin.logDebug("Dispatching command as IRCCommandSender: " + ircCommand.getGameCommand());
                    plugin.getServer().dispatchCommand(ircCommand.getIRCCommandSender(), ircCommand.getGameCommand());
                }
            } catch (CommandException ce) {
                plugin.logError("Error running command: " + ce.getMessage());
            }
            plugin.getServer().getPluginManager().callEvent(new IRCCommandEvent(ircCommand));
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
        return "Elements removed from command queue: " + size;
    }

    /**
     *
     * @param command
     */
    public void add(IRCCommand command) {
        plugin.logDebug("Adding command to queue: " + command.getGameCommand());
        queue.offer(command);
    }
}
