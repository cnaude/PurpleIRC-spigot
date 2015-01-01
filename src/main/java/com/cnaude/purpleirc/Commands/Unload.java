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

import com.cnaude.purpleirc.PurpleIRC;
import java.io.File;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author cnaude
 */
public class Unload implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "[bot] (disable)";
    private final String desc = "Unload the bot and optionally disable it.";
    private final String name = "unload";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage;

    /**
     *
     * @param plugin
     */
    public Unload(PurpleIRC plugin) {
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
                sender.sendMessage(ChatColor.WHITE + "Unloading " + bot + "...");
                plugin.ircBots.get(bot).quit();
                plugin.ircBots.get(bot).saveConfig(plugin.getServer().getConsoleSender());
                plugin.ircBots.remove(bot);
                if (args.length >= 3) {
                    if (args[2].equalsIgnoreCase("disable")) {
                        sender.sendMessage(ChatColor.WHITE + "Renaming " + bot + " to " + bot + ".disabled");
                        File file = new File(plugin.botsFolder, bot);
                        file.renameTo(new File(plugin.botsFolder, bot + ".disabled"));
                    }
                }
            } else {
                sender.sendMessage(plugin.invalidBotName.replace("%BOT%", bot));
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
