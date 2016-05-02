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
import com.dthielke.api.Channel;
import com.dthielke.api.ChatResult;
import com.dthielke.api.Chatter;
import com.dthielke.api.event.ChannelChatEvent;
import com.dthielke.channel.ConversationChannel;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 *
 * @author Chris Naude
 */
public class HeroChatListener implements Listener {

    final PurpleIRC plugin;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public HeroChatListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onChannelChatEvent(ChannelChatEvent event) {
        Chatter chatter = event.getChatter();
        Channel channel = chatter.getActiveChannel();
        if ((channel instanceof ConversationChannel)) {
            plugin.logDebug("Ignoring private message: " + chatter.getPrivateMessageFocus() + ":" + event.getMessage());
            return;
        }
        ChatResult result = event.getResult();
        plugin.logDebug("HC Format: " + event.getFormat());
        plugin.logDebug("HC Result: " + event.getResult());
        if (plugin.heroPrivateChatFormat.equals(event.getFormat())) {
            plugin.logDebug("HC Private: TRUE");
            return;
        }

        ChatColor chatColor = event.getChannel().getColor();
        Player player = chatter.getPlayer();
        if (player.hasPermission("irc.message.gamechat")
                && chatter.getChannels().contains(event.getChannel())
                && result.equals(ChatResult.ALLOWED)) {
            for (PurpleBot ircBot : plugin.ircBots.values()) {
                if (plugin.heroChatEmoteFormat.equals(event.getFormat())) {
                    plugin.logDebug("HC Emote: TRUE");
                    ircBot.heroAction(chatter, chatColor, event.getMessage());
                } else {
                    plugin.logDebug("HC Emote: FALSE");
                    ircBot.heroChat(chatter, chatColor, event.getMessage());
                }
            }
        }
    }

}
