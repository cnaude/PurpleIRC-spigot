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
import org.pircbotx.hooks.events.KickEvent;

/**
 *
 * @author Chris Naude
 */
public class KickListener extends ListenerAdapter {

    PurpleIRC plugin;
    PurpleBot ircBot;

    /**
     *
     * @param plugin the PurpleIRC plugin
     * @param ircBot
     */
    public KickListener(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }

    /**
     *
     * @param event
     */
    @Override
    public void onKick(KickEvent event) {
        Channel channel = event.getChannel();
        String channelName = channel.getName();
        User recipient = event.getRecipient();
        User user = event.getUser();
        
        if (recipient.getNick().equalsIgnoreCase(ircBot.botNick)) {
            plugin.logDebug("onKick: " + recipient.getNick());
            if (ircBot.joinOnKick) {
                plugin.logDebug("onKick: rejoining");
                if (ircBot.channelPassword.get(channelName).isEmpty()) {
                    ircBot.asyncJoinChannel(channelName);
                } else {
                    ircBot.asyncJoinChannel(channelName, ircBot.channelPassword.get(channelName));
                }
            } else {
                plugin.logDebug("onKick: NOT rejoining");
            }
        }

        if (ircBot.isValidChannel(channel.getName())) {
            ircBot.broadcastIRCKick(recipient, user, event.getReason(), channel);
            if (plugin.netPackets != null) {
                plugin.netPackets.remFromTabList(recipient.getNick());
            }
        }
    }
}
