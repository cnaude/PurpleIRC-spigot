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

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import com.cnaude.purpleirc.TemplateName;
import com.palmergames.bukkit.TownyChat.Chat;
import com.palmergames.bukkit.TownyChat.channels.Channel;
import com.palmergames.bukkit.TownyChat.channels.channelTypes;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import java.util.ArrayList;
import org.bukkit.entity.Player;

/**
 *
 * @author Chris Naude
 */
public class TownyChatHook {

    private final PurpleIRC plugin;
    private final Chat chat;
    private final ArrayList<channelTypes> townyChannelTypes;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public TownyChatHook(PurpleIRC plugin) {
        this.plugin = plugin;
        chat = (Chat) plugin.getServer().getPluginManager().getPlugin("TownyChat");
        townyChannelTypes = new ArrayList<>();
        townyChannelTypes.add(channelTypes.TOWN);
        townyChannelTypes.add(channelTypes.GLOBAL);
        townyChannelTypes.add(channelTypes.NATION);
        townyChannelTypes.add(channelTypes.DEFAULT);
    }

    public String getTown(Player player) {
        String town = "";
        Resident resident;
        try {
            resident = TownyUniverse.getDataSource().getResident(player.getName());
        } catch (NotRegisteredException ex) {
            resident = null;
        }

        if (resident != null) {
            try {
                town = resident.getTown().getName();
            } catch (NotRegisteredException ex) {
                town = "";
            }
        }
        return town;
    }

    public String getNation(Player player) {
        String nation = "";
        Resident resident;
        try {
            resident = TownyUniverse.getDataSource().getResident(player.getName());
        } catch (NotRegisteredException ex) {
            resident = null;
        }
        if (resident != null) {
            try {
                nation = resident.getTown().getNation().getName();
            } catch (NotRegisteredException ex) {
                nation = "";
            }
        }
        return nation;
    }

    public String getTitle(Player player) {
        Resident resident;
        try {
            resident = TownyUniverse.getDataSource().getResident(player.getName());
        } catch (NotRegisteredException ex) {
            resident = null;
        }
        String title = "";
        if (resident != null) {
            title = resident.getTitle();
        }
        return title;
    }

    public void sendMessage(String townyChannel, String message) {
        plugin.logDebug("TownyChatHook called...");
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            plugin.logDebug("P: " + player.getName());
            for (channelTypes ct : townyChannelTypes) {
                plugin.logDebug("CT: " + ct.name());
                String townyChannelName = chat.getChannelsHandler().getActiveChannel(player, ct).getName();
                if (townyChannel.equalsIgnoreCase(townyChannelName)) {
                    plugin.logDebug("TC [" + townyChannelName + "]: Sending message to " + player + ": " + message);
                    player.sendMessage(message.replace("\u200B", ""));
                    break;
                } else {
                    plugin.logDebug("TC " + townyChannelName + "]: invalid TC channel name for " + player);
                }

            }
        }
    }

    public void sendToIrc(PurpleBot ircBot, Player player, Channel townyChannel, String message) {
        if (!ircBot.isConnected()) {
            return;
        }
        if (plugin.tcHook != null) {
            for (String channelName : ircBot.botChannels) {
                if (!ircBot.isPlayerInValidWorld(player, channelName)) {
                    continue;
                }
                if (ircBot.isMessageEnabled(channelName, "towny-" + townyChannel.getName() + "-chat")
                        || ircBot.isMessageEnabled(channelName, "towny-" + townyChannel.getChannelTag() + "-chat")
                        || ircBot.isMessageEnabled(channelName, TemplateName.TOWNY_CHAT)
                        || ircBot.isMessageEnabled(channelName, TemplateName.TOWNY_CHANNEL_CHAT)) {
                    ircBot.asyncIRCMessage(channelName, plugin.tokenizer
                            .chatTownyChannelTokenizer(player, townyChannel, message,
                                    plugin.getMessageTemplate(ircBot.botNick, channelName, TemplateName.TOWNY_CHANNEL_CHAT)));
                }
            }
        }
    }

}
