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
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.QuitEvent;

/**
 *
 * @author Chris Naude
 */
public class QuitListener extends ListenerAdapter {

    PurpleIRC plugin;
    PurpleBot ircBot;

    /**
     *
     * @param plugin the PurpleIRC plugin
     * @param ircBot
     */
    public QuitListener(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }

    /**
     *
     * @param event
     */
    @Override
    public void onQuit(QuitEvent event) {
        String nick = event.getUser().getNick();
        for (String channelName : ircBot.channelNicks.keySet()) {
            if (ircBot.channelNicks.get(channelName).contains(nick)) {
                ircBot.broadcastIRCQuit(event.getUser(), ircBot.getChannel(channelName), event.getReason());
                if (plugin.netPackets != null) {
                    plugin.netPackets.remFromTabList(nick);
                }
            }
            ircBot.channelNicks.get(channelName).remove(nick);
        }
    }
}
