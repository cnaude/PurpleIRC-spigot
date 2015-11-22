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
import org.pircbotx.ReplyConstants;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ServerResponseEvent;

/**
 *
 * @author Chris Naude
 */
public class ServerResponseListener extends ListenerAdapter {

    PurpleIRC plugin;
    PurpleBot ircBot;

    /**
     *
     * @param plugin the PurpleIRC plugin
     * @param ircBot
     */
    public ServerResponseListener(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }

    /**
     *
     * @param event
     */
    @Override
    public void onServerResponse(ServerResponseEvent event) {
        int serverReply = event.getCode();

        String rawMessage[] = event.getRawLine().split(" ", 5);
        String target = rawMessage[3];

        if (serverReply == ReplyConstants.ERR_NICKNAMEINUSE) {
            plugin.logInfo("[" + target + "] Nick is already in use.");
            ircBot.altNickChange();
        } else if (serverReply == ReplyConstants.ERR_BADCHANNELKEY) {
            plugin.logError("Cannot join " + target + " (Requires keyword)");
        } else if (serverReply >= 400 && serverReply <= 599) {
            plugin.logError(event.getRawLine());
        }
    }
}
