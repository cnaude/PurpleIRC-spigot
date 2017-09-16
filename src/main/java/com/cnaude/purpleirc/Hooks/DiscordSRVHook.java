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
import com.cnaude.purpleirc.PurpleIRC;import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.core.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
;

/**
 *
 * @author Chris Naude
 */
public class DiscordSRVHook {

    private final PurpleIRC plugin;
    private final DiscordListener discordListener;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public DiscordSRVHook(PurpleIRC plugin) {
        this.plugin = plugin;
        discordListener = new DiscordListener(this.plugin);
        DiscordSRV.api.subscribe(discordListener);
    }
    
    public void removeListener() {
        DiscordSRV.api.unsubscribe(discordListener);
    }

    public void sendMessage(String channelName, String message) {
        TextChannel textChannel = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channelName);
        TextChannel chatChannel = DiscordSRV.getPlugin().getMainTextChannel();
        String discordMessage = DiscordUtil.convertMentionsFromNames(message, DiscordSRV.getPlugin().getMainGuild());
        plugin.logDebug("DiscordSRVHook: Message to be sent: " + message);
        if (textChannel != null) {
            plugin.logDebug("DiscordSRVHook: Sending message to channel " + channelName);
            DiscordUtil.sendMessage(textChannel, discordMessage);
        } else {
            plugin.logDebug("DiscordSRVHook: Unable to find channel: " + channelName);
            plugin.logDebug("DiscordSRVHook: Channel list: " + DiscordSRV.getPlugin().getChannels().keySet());
            plugin.logDebug("DiscordSRVHook: Sending message to ChatChannel instead: " + chatChannel.getName());
            DiscordUtil.sendMessage(chatChannel, discordMessage);
        }

    }
}
