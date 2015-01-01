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

import com.cnaude.purpleirc.TemplateName;
import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.pircbotx.Channel;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.NickChangeEvent;

/**
 *
 * @author cnaude
 */
public class NickChangeListener extends ListenerAdapter {

    PurpleIRC plugin;
    PurpleBot ircBot;

    /**
     *
     * @param plugin
     * @param ircBot
     */
    public NickChangeListener(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }

    /**
     *
     * @param event
     */
    @Override
    public void onNickChange(NickChangeEvent event) {
        String newNick = event.getNewNick();
        String oldNick = event.getOldNick();
        plugin.logDebug("OLD: " + oldNick);
        plugin.logDebug("NEW: " + newNick);

        for (String channelName : ircBot.channelNicks.keySet()) {
            Channel channel = ircBot.getChannel(channelName);
            if (channel != null) {
                if (ircBot.enabledMessages.get(channelName).contains(TemplateName.IRC_NICK_CHANGE)) {
                    plugin.getServer().broadcast(plugin.colorConverter.ircColorsToGame(
                            plugin.getMsgTemplate(ircBot.botNick, TemplateName.IRC_NICK_CHANGE)                            
                            .replace("%NEWNICK%", newNick)
                            .replace("%OLDNICK%", oldNick)
                            .replace("%CHANNEL%", channelName)), "irc.message.nickchange");
                }
                if (plugin.netPackets != null) {
                    plugin.netPackets.remFromTabList(oldNick);
                    plugin.netPackets.addToTabList(newNick, ircBot, channel);
                }
                if (ircBot.channelNicks.get(channelName).contains(oldNick)) {
                    ircBot.channelNicks.get(channelName).remove(oldNick);
                    plugin.logDebug("Removing " + oldNick);
                }
                ircBot.channelNicks.get(channelName).add(newNick);
                plugin.logDebug("Adding " + newNick);
            }
        }

    }
}
