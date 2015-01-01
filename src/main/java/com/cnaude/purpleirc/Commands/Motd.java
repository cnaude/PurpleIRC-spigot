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
import java.util.ArrayList;
import org.bukkit.command.CommandSender;

/**
 *
 * @author cnaude
 */
public class Motd implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "([bot])";
    private final String desc = "Get server motd.";
    private final String name = "motd";

    /**
     *
     * @param plugin
     */
    public Motd(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void dispatch(CommandSender sender, String[] args) {
        java.util.List<PurpleBot> myBots = new ArrayList<PurpleBot>();
        if (args.length >= 2) {
            if (plugin.ircBots.containsKey(args[1])) {
                myBots.add(plugin.ircBots.get(args[1]));
                
            } else {
                sender.sendMessage(plugin.invalidBotName.replace("%BOT%", args[1]));
            }
        } else {
            myBots.addAll(plugin.ircBots.values());
        }

        for (PurpleBot ircBot : myBots) {
            String motd = ircBot.getMotd();
            if (motd != null) {
                sender.sendMessage(motd);
            } else {
                sender.sendMessage("No MOTD found.");
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
