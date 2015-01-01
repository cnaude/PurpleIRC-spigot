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
import org.pircbotx.hooks.events.PrivateMessageEvent;

/**
 *
 * @author cnaude
 */
public class PrivateMessageListener extends ListenerAdapter {

    PurpleIRC plugin;
    PurpleBot ircBot;

    /**
     *
     * @param plugin
     * @param ircBot
     */
    public PrivateMessageListener(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }

    /**
     *
     * @param event
     */
    @Override
    public void onPrivateMessage(PrivateMessageEvent event) {

        String message = event.getMessage();
        User user = event.getUser();
        Channel channel;

        plugin.logDebug("Private message caught <" + user.getNick() + ">: " + message);

        for (String myChannel : ircBot.botChannels) {
            channel = ircBot.getChannel(myChannel);
            if (channel != null) {
                if (user.getChannels().contains(channel)) {
                    plugin.ircMessageHandler.processMessage(ircBot, user, channel, message, true);
                    return;
                }
                plugin.logDebug("Private message from " + user.getNick() + " ignored because not in valid channel.");
            }
        }
    }
}
