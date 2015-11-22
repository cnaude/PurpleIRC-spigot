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

import java.io.File;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.apache.commons.io.input.TailerListenerAdapter;

/**
 *
 * @author Chris Naude Poll the command queue and dispatch to Bukkit
 */
public class LogTailer {

    private final PurpleIRC plugin;
    private final PurpleBot ircBot;
    private final String target;
    private final boolean ctcp;
    private static final int SLEEP = 500;
    private final File file;
    private Tailer tailer;
    private Thread thread;
    private TailerListener listener;

    /**
     *
     * @param plugin the PurpleIRC plugin
     * @param ircBot
     * @param target
     * @param ctcp
     * @param fileName
     */
    public LogTailer(final PurpleBot ircBot, final PurpleIRC plugin, final String target, final boolean ctcp, final String fileName) {
        this.plugin = plugin;
        this.ircBot = ircBot;
        this.target = target;
        this.ctcp = ctcp;
        this.file = new File(fileName);
        if (file.exists()) {
            startWatcher();
        } else {
            plugin.logError("No such file: " + fileName);
        }
    }

    private void startWatcher() {
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                plugin.logInfo("Tailing file: " + file.getName());
                listener = new MyTailerListener();
                tailer = new Tailer(file, listener, SLEEP);
                thread = new Thread(tailer);
                thread.setDaemon(true); // optional
                thread.start();
            }
        }, 0);
    }

    public class MyTailerListener extends TailerListenerAdapter {

        @Override
        public void handle(String line) {
            if (ctcp) {
                blockingCTCPMessage(target, line);
            } else {
                blockingIRCMessage(target, line);
            }
        }

    }

    private void blockingIRCMessage(final String target, final String message) {
        if (!ircBot.isConnected()) {
            return;
        }
        plugin.logDebug("[blockingIRCMessage] About to send IRC message to " + target + ": " + message);
        ircBot.bot.sendIRC().message(target, message);
        plugin.logDebug("[blockingIRCMessage] Message sent to " + target + ": " + message);
    }

    private void blockingCTCPMessage(final String target, final String message) {
        if (!ircBot.isConnected()) {
            return;
        }
        plugin.logDebug("[blockingCTCPMessage] About to send IRC message to " + target + ": " + message);
        ircBot.bot.sendIRC().ctcpResponse(target, message);
        plugin.logDebug("[blockingCTCPMessage] Message sent to " + target + ": " + message);
    }

    protected void stopTailer() {
        if (tailer != null) {
            plugin.logInfo("Stoping tailer.");
            tailer.stop();
        }
    }

}
