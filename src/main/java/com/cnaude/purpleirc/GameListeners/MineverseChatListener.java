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

import com.cnaude.purpleirc.Events.MineverseChatEvent;
import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import com.cnaude.purpleirc.TemplateName;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 *
 * @author Chris Naude
 */
public class MineverseChatListener implements Listener {

    final PurpleIRC plugin;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public MineverseChatListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onMineverseChatEvent(MineverseChatEvent event) {
        mineverseChat(event.getPlayer(), event.getMessage(), event.getBot());
    }

    /**
     * MineverseChat from game to IRC
     *
     * @param player
     * @param message
     * @param bot the calling bot
     */
    public void mineverseChat(Player player, String message, PurpleBot bot) {
        MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer(player);
        ChatChannel eventChannel = mcp.getCurrentChannel();
        if (mcp.isQuickChat()) { //for single message chat detection
            eventChannel = mcp.getQuickChannel();
        }
        if (!bot.isConnected()) {
            return;
        }
        if (bot.floodChecker.isSpam(player)) {
            bot.sendFloodWarning(player);
            return;
        }
        String mvChannel = eventChannel.getName();
        String mvColor = eventChannel.getColor();
        for (String channelName : bot.botChannels) {
            if (!bot.isPlayerInValidWorld(player, channelName)) {
                continue;
            }
            plugin.logDebug("MV Channel: " + mvChannel);
            String channelTemplateName = "mineverse-" + mvChannel + "-chat";
            if (bot.isMessageEnabled(channelName, channelTemplateName)
                    || bot.isMessageEnabled(channelName, TemplateName.MINEVERSE_CHAT)) {
                String template = plugin.getMineverseChatTemplate(bot.botNick, mvChannel);
                plugin.logDebug("MV Template: " + template);
                bot.asyncIRCMessage(channelName, plugin.tokenizer
                        .mineverseChatTokenizer(player, mvChannel, mvColor, message, template));
            } else {
                plugin.logDebug("Player " + player.getName() + " is in Mineverse channel "
                        + mvChannel + ". Message types " + channelTemplateName + " and "
                        + TemplateName.MINEVERSE_CHAT + " are disabled. No message sent to IRC.");
            }
        }
    }

}
