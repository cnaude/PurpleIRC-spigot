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
import org.pircbotx.Channel;

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
            boolean okayToSend = false;
            if (target.startsWith("#")) {
                try {
                    for (Channel channel : ircBot.bot.getUserBot().getChannels()) {
                        if (channel.getName().equalsIgnoreCase(target)) {
                            okayToSend = true;
                            break;
                        }
                    }
                } catch (Exception ex) {
                    plugin.logDebug(ex.getMessage());
                }
            } else {
                okayToSend = true;
            }
            if (okayToSend && !excludesMatch(line)) {
                String template = plugin.getMsgTemplate(ircBot.botNick, target, TemplateName.LOG_TAILER);
                String message = plugin.tokenizer.logTailerTokenizer(file.getName(), line, template);
                if (ctcp) {
                    blockingCTCPMessage(target, message);
                } else {
                    blockingIRCMessage(target, message);
                }
            }
        }

    }

    private boolean excludesMatch(String message) {
        if (!ircBot.tailerFilters.isEmpty()) {
            for (String filter : ircBot.tailerFilters) {
                if (filter.startsWith("/") && filter.endsWith("/")) {
                    filter = filter.substring(1, filter.length() - 1);
                    if (message.matches(filter)) {
                        return true;
                    }
                } else {
                    plugin.logDebug("Filtering " + filter + " from " + message);
                    if (message.contains(filter)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void blockingIRCMessage(final String target, final String message) {
        if (!ircBot.isConnected()) {
            return;
        }
        ircBot.bot.sendIRC().message(target, message);
    }

    private void blockingCTCPMessage(final String target, final String message) {
        if (!ircBot.isConnected()) {
            return;
        }
        ircBot.bot.sendIRC().ctcpResponse(target, message);
    }

    protected void stopTailer() {
        if (tailer != null) {
            plugin.logInfo("Stopping tailer.");
            tailer.stop();
        }
    }

}
