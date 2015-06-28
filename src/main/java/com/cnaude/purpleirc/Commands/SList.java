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
package com.cnaude.purpleirc.Commands;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author cnaude
 */
public class SList implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "";
    private final String desc = "List remote players";
    private final String name = "slist";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage;

    /**
     *
     * @param plugin
     */
    public SList(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void dispatch(CommandSender sender, String[] args) {

        for (PurpleBot ircBot : plugin.ircBots.values()) {
            if (ircBot.botLinkingEnabled) {
                for (String remoteBot : ircBot.remotePlayers.keySet()) {
                    String countInfo = "";
                    if (ircBot.remoteServerInfo.containsKey(remoteBot)) {
                        if (ircBot.remoteServerInfo.get(remoteBot).containsKey("CUR_COUNT")
                                && ircBot.remoteServerInfo.get(remoteBot).containsKey("MAX_COUNT")) {
                            countInfo = ircBot.remoteServerInfo.get(remoteBot).get("CUR_COUNT")
                                    + "/"
                                    + ircBot.remoteServerInfo.get(remoteBot).get("MAX_COUNT");
                        }
                    }
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "-----[  " + ChatColor.WHITE + "Remote Players"
                            + ChatColor.LIGHT_PURPLE + " - " + ChatColor.WHITE + remoteBot 
                            + ChatColor.LIGHT_PURPLE + " - " + ChatColor.WHITE + countInfo 
                            + ChatColor.LIGHT_PURPLE + " ]-----");
                    java.util.List<String> remotePlayersSorted = new ArrayList<>(ircBot.remotePlayers.get(remoteBot));
                    Collections.sort(remotePlayersSorted, Collator.getInstance());
                    for (String playerName : remotePlayersSorted) {
                        sender.sendMessage("  " + ChatColor.WHITE + playerName);
                    }
                }
            }
        }

    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String desc() {
        return desc;
    }

    @Override
    public String usage() {
        return usage;
    }
}
