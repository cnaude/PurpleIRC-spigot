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
package com.cnaude.purpleirc.Hooks;

import com.cnaude.purpleirc.GameListeners.DiscordListener;
import com.cnaude.purpleirc.PurpleIRC;
import com.scarsz.discordsrv.DiscordSRV;
import com.scarsz.discordsrv.api.DiscordSRVAPI;
import com.scarsz.discordsrv.api.DiscordSRVListener;
import com.scarsz.discordsrv.jda.entities.TextChannel;

/**
 *
 * @author Chris Naude
 */
public class DiscordSRVHook {

    private final PurpleIRC plugin;
    private final DiscordSRVListener discordListener;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public DiscordSRVHook(PurpleIRC plugin) {
        this.plugin = plugin;
        discordListener = new DiscordListener(this.plugin);
        DiscordSRVAPI.addListener(discordListener);
    }
    
    public void removeListener() {
        DiscordSRVAPI.removeListener(discordListener);
    }

    public void sendMessage(String channelName, String message) {
        TextChannel textChannel = DiscordSRV.getTextChannelFromChannelName(channelName);
        TextChannel chatChannel = DiscordSRV.chatChannel;
        plugin.logDebug("DiscordSRVHook: Message to be sent: " + message);
        if (textChannel != null) {
            plugin.logDebug("DiscordSRVHook: Sending mssage to channel " + channelName);
            textChannel.sendMessage(message);
        } else {
            plugin.logDebug("DiscordSRVHook: Unable to find channel: " + channelName);
            plugin.logDebug("DiscordSRVHook: Channel list: " + DiscordSRV.channels.keySet());
            plugin.logDebug("DiscordSRVHook: Sending message to ChatChannel instead: " + chatChannel.getName());
            DiscordSRV.sendMessageToChatChannel(message);
        }

    }
}
