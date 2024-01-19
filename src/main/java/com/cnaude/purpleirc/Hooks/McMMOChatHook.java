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

import com.cnaude.purpleirc.PurpleIRC;
import com.gmail.nossr50.api.PartyAPI;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.locale.LocaleLoader;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Chris Naude
 */
public class McMMOChatHook {

    private final PurpleIRC plugin;
    private final Plugin mcMMOPlugin;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public McMMOChatHook(PurpleIRC plugin) {
        this.plugin = plugin;
        this.mcMMOPlugin = plugin.getServer().getPluginManager().getPlugin("mcMMO");
    }

    public void sendAdminMessage(String sender, String message) {
        if (mcMMOPlugin != null) {
            plugin.logDebug("[mcMMOChatHook:sendAdminMessage]: " + message);
            message = LocaleLoader.getTextComponent("Chat.Style.Admin", message).content();
            if (message != null && message != "") {
                plugin.broadcastToGame(message, "", "mcmmo.chat.adminchat");
            }
        }
    }

    public void sendPartyMessage(String sender, String party, String message) {
        if (mcMMOPlugin != null) {
            for (Party p : PartyAPI.getParties()) {
                if (p.getName().equalsIgnoreCase(party)) {
                    message = LocaleLoader.getTextComponent("Chat.Style.Party", message).content();
                    for (Player member : p.getOnlineMembers()) {
                        member.sendMessage(message);
                    }
                    return;
                }
            }
        }
    }

}
