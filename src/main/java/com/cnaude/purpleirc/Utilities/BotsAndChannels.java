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
package com.cnaude.purpleirc.Utilities;

import com.cnaude.purpleirc.PurpleIRC;
import java.util.ArrayList;
import org.bukkit.command.CommandSender;

/**
 *
 * @author cnaude
 */
public class BotsAndChannels {

    public ArrayList<String> bot = new ArrayList<>();
    public ArrayList<String> channel = new ArrayList<>();

    public BotsAndChannels(PurpleIRC plugin, CommandSender sender, String botName, String channelName) {
        if (plugin.ircBots.containsKey(botName)) {
            bot.add(botName);
            if (plugin.ircBots.get(botName).isValidChannel(channelName)) {           
                channel.add(channelName);
            } else {
                sender.sendMessage(plugin.invalidChannelName.replace("%CHANNEL%", channelName));
            }
        } else {
            sender.sendMessage(plugin.invalidBotName.replace("%BOT%", botName));
        }
    }

    public BotsAndChannels(PurpleIRC plugin, CommandSender sender) {
        for (String botName : plugin.ircBots.keySet()) {
            bot.add(botName);
            for (String channelName : plugin.ircBots.get(botName).botChannels) {
                channel.add(channelName);
            }
        }
    }

}
