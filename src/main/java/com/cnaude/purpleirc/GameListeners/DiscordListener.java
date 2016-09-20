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
package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import com.scarsz.discordsrv.api.DiscordSRVListener;
import com.scarsz.discordsrv.api.events.ProcessChatEvent;
import com.scarsz.discordsrv.jda.events.message.MessageReceivedEvent;
import org.bukkit.event.EventHandler;

/**
 *
 * @author Chris Naude
 */
public class DiscordListener extends DiscordSRVListener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public DiscordListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onProcessChatEvent(ProcessChatEvent event) {

    }

    @Override
    public void onDiscordMessageReceived(MessageReceivedEvent event) {
        plugin.logDebug("onDiscordMessageReceived: " + event.getMessage().getContent());
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.discordChat(event.getMessage().getAuthor().getUsername(),
                    event.getMessage().getChannelId(),
                    event.getMessage().getContent());
        }
    }

    /*
    @Override
    public void onProcessChat(ProcessChatEvent event) {
        plugin.logDebug("onProcessChat: " + event.message);
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.discordChat(event.sender, event.channel, event.message);
        }
    }
     */
}
