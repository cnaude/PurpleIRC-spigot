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
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Chris Naude
 */
public class Test implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "[name|sslciphers]";
    private final String desc = "Testing various Vault methods.";
    private final String name = "test";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public Test(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void dispatch(CommandSender sender, String[] args) {
        //irc test <username>
        if (plugin.vaultHelpers == null) {
            sender.sendMessage(ChatColor.RED + "Vault is no enabled!");
            return;
        }
        if (plugin.debugMode()) {
            if (args.length >= 2) {
                String playername = args[1];
                if (playername.equalsIgnoreCase("sslciphers")) {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Available SSL ciphers");
                    SSLContext context;
                    try {
                        context = SSLContext.getDefault();
                        SSLSocketFactory sf = context.getSocketFactory();
                        String[] cipherSuites = sf.getSupportedCipherSuites();
                        for (String s : cipherSuites) {
                            sender.sendMessage(ChatColor.WHITE + "-- " + ChatColor.GOLD + s);
                        }
                    } catch (NoSuchAlgorithmException ex) {
                        Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (playername.equalsIgnoreCase("splits")) {
                    for (PurpleBot ircBot : plugin.ircBots.values()) {
                        for (String channelName : ircBot.botChannels) {
                            ircBot.asyncIRCMessage(channelName, "Test\r\nTest2\r\nTest3\nTest4\nTest5");
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Testing " + playername);
                    sender.sendMessage("displayName     : " + plugin.getDisplayName(name));
                    sender.sendMessage("getGroupPrefix  : " + plugin.getGroupPrefix(plugin.defaultPlayerWorld, playername));
                    sender.sendMessage("getGroupSuffix  : " + plugin.getGroupSuffix(plugin.defaultPlayerWorld, playername));
                    sender.sendMessage("getPlayerPrefix : " + plugin.getPlayerPrefix(plugin.defaultPlayerWorld, playername));
                    sender.sendMessage("getPlayerSuffix : " + plugin.getPlayerSuffix(plugin.defaultPlayerWorld, playername));
                    sender.sendMessage("getPlayerGroup  : " + plugin.getPlayerGroup(plugin.defaultPlayerWorld, playername));
                }
            } else {
                sender.sendMessage(fullUsage);
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Debug mode must enabled to use this feature.");
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
