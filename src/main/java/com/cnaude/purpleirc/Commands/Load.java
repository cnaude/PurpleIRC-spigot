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
import java.io.File;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author cnaude
 */
public class Load implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "[bot]";
    private final String desc = "Load a bot file.";
    private final String name = "load";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage;

    /**
     *
     * @param plugin
     */
    public Load(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void dispatch(CommandSender sender, String[] args) {
        if (args.length >= 2) {
            String bot = plugin.botify(args[1]);
            if (plugin.ircBots.containsKey(bot)) {
                sender.sendMessage(ChatColor.RED + "Sorry that bot is already loaded. Try to unload it first.");
                return;
            }
            File file = new File(plugin.botsFolder, bot);
            if (file.exists()) {
                sender.sendMessage(ChatColor.WHITE + "Loading " + bot + "...");
                plugin.ircBots.put(file.getName(), new PurpleBot(file, plugin));
                sender.sendMessage("Loaded bot: " + file.getName() + "[" + plugin.ircBots.get(file.getName()).botNick + "]");
            } else {
                sender.sendMessage(ChatColor.RED + "No such bot file: " + ChatColor.WHITE + bot);
            }
        } else {
            sender.sendMessage(fullUsage);
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
