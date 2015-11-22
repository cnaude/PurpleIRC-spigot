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
package com.cnaude.purpleirc.IRCListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

/**
 *
 * @author Chris Naude
 */
public class MessageListener extends ListenerAdapter {

    PurpleIRC plugin;
    PurpleBot ircBot;

    /**
     *
     * @param plugin the PurpleIRC plugin
     * @param ircBot
     */
    public MessageListener(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }

    /**
     *
     * @param event
     */
    @Override
    public void onMessage(MessageEvent event) {

        String message = event.getMessage();
        Channel channel = event.getChannel();
        User user = event.getUser();

        plugin.logDebug("Message caught <" + user.getNick() + ">: " + message);
        try {
            if (plugin.shortifyHook != null && ircBot.isShortifyEnabled(channel.getName())) {
                plugin.logDebug("Shortifying message (before): " + message);
                message = plugin.shortifyHook.shorten(message);
                plugin.logDebug("Shortifying message (after): " + message);
            } else {
                plugin.logDebug("Shortify: false");
            }

            if (ircBot.isValidChannel(channel.getName())) {
                plugin.ircMessageHandler.processMessage(ircBot, user, channel, message, false);
            } else {
                plugin.logDebug("Channel " + channel.getName() + " is not valid.");
            }
        } catch (Exception ex) {
            plugin.logError("onMessage: " + ex.getMessage());
        }
    }
}
