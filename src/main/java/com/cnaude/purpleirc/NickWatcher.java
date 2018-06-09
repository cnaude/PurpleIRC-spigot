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
 *
 * @author Chris Naude Poll the command queue and dispatch to Bukkit
 */
public class NickWatcher {

    private final PurpleIRC plugin;
    private final PurpleBot ircBot;
    private BukkitTask bt = null;
    
    /**
     *
     * @param ircBot
     * @param plugin
     */
    public NickWatcher(final PurpleBot ircBot, final PurpleIRC plugin) {
        this.ircBot = ircBot;
        this.plugin = plugin;
        startWatcher();
    }

    private void startWatcher() {
        plugin.logDebug("Starting nick watcher");
        bt = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new Runnable() {
            @Override
            public void run() {
                checkNick();
            }
        }, ircBot.nickTimer, ircBot.nickTimer);
    }

    private void checkNick() {
        if (ircBot.bot == null) {
            return;
        }
        if (!ircBot.bot.isConnected()) {
            return;
        }
        if (!ircBot.bot.getNick().equals(ircBot.nick)) {
            plugin.logDebug("My nick is wrong. Attempting to change...");
            // we don't want to continusouly cycle thorugh our nicks
            ircBot.nickIndex = 0;
            ircBot.bot.sendIRC().changeNick(ircBot.nick);
        }
    }

    public void cancel() {
        this.bt.cancel();
    }

}
