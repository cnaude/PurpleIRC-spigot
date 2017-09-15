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
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;

/**
 *
 * @author Chris Naude
 */
public class DiscordListener {

    private final PurpleIRC plugin;
    private final DiscordSRV discordPlugin;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public DiscordListener(PurpleIRC plugin) {
        this.plugin = plugin;
        this.discordPlugin = (DiscordSRV) plugin.getServer().getPluginManager().getPlugin("DiscordSRV");
    }

    @Subscribe(priority = ListenerPriority.MONITOR)
    public void onDiscordGuildMessageReceivedEvent(DiscordGuildMessageReceivedEvent event) {
        if (discordPlugin.getConfig().getBoolean("DiscordChatChannelListCommandEnabled")
                && event.getMessage().getContent().equalsIgnoreCase(discordPlugin.getConfig().getString("DiscordChatChannelListCommandMessage"))) {
            plugin.logDebug("[onDiscordGuildMessageReceivedEvent] Ignoring DiscordChatChannelListCommandMessage");
            return;
        }
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.discordChat(event.getMessage().getAuthor().getName(),
                    event.getMember().getNickname(),
                    event.getMember().getEffectiveName(),
                    event.getMember().getColor(),
                    event.getChannel().getName(),
                    event.getMessage().getContent());
        }

    }

}
