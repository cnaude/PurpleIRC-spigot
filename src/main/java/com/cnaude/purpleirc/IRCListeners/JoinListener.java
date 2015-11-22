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
import org.pircbotx.hooks.events.JoinEvent;

/**
 *
 * @author Chris Naude
 */
public class JoinListener extends ListenerAdapter {

    PurpleIRC plugin;
    PurpleBot ircBot;

    /**
     *
     * @param plugin the PurpleIRC plugin
     * @param ircBot
     */
    public JoinListener(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }

    /**
     *
     * @param event
     */
    @Override
    public void onJoin(JoinEvent event) {
        Channel channel = event.getChannel();
        String channelName = channel.getName();
        User user = event.getUser();

        if (!ircBot.isValidChannel(channel.getName())) {
            plugin.logDebug("Invalid channel: " + channelName);
            plugin.logDebug("Part if invalid: " + ircBot.partInvalidChannels);
            plugin.logDebug("Nick: " + user.getNick());
            if (user.getNick().equals(ircBot.botNick) && ircBot.partInvalidChannels) {
                plugin.logInfo("Leaving invalid channel: " + channel.getName());
                channel.send().part(ircBot.partInvalidChannelsMsg);
            }
            return;
        }
        ircBot.broadcastIRCJoin(user, channel);
        ircBot.opIrcUser(channel, user);
        ircBot.voiceIrcUser(channel, user);
        ircBot.joinNotice(channel, user);
        if (user.getNick().equals(ircBot.botNick)) {
            plugin.logInfo("Joining channel: " + channelName);
            plugin.logDebug("Setting channel modes: " + channelName + " => " + ircBot.channelModes.get(channel.getName()));
            channel.send().setMode(ircBot.channelModes.get(channelName));
            ircBot.fixTopic(channel, channel.getTopic(), channel.getTopicSetter());
            ircBot.updateNickList(channel);
            if (ircBot.msgOnJoin.containsKey(channelName) && ircBot.joinMsg.containsKey(channelName)) {
                if (ircBot.msgOnJoin.get(channelName) && !ircBot.joinMsg.get(channelName).isEmpty()) {
                    plugin.logDebug("Sending on join message to IRC server: " + ircBot.joinMsg.get(channelName));
                    ircBot.asyncRawlineNow(ircBot.joinMsg.get(channelName));
                }
            }
        }
        if (plugin.netPackets != null) {
            plugin.netPackets.addToTabList(user.getNick(), ircBot, channel);
        }
    }
}
