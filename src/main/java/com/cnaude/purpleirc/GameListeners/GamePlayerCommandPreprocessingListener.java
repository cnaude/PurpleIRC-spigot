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
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 *
 * @author Chris Naude
 */
public class GamePlayerCommandPreprocessingListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public GamePlayerCommandPreprocessingListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        String message = event.getMessage();
        String cmd;
        String action;
        if (message.contains(" ")) {
            cmd = message.split(" ", 2)[0];
            action = message.split(" ", 2)[1];
        } else {
            cmd = message;
            action = "";
        }
        if (event.getPlayer().hasPermission("irc.message.gamechat")) {
            for (PurpleBot ircBot : plugin.ircBots.values()) {
                for (String s : ircBot.actionCommands) {
                    if (cmd.equalsIgnoreCase(s)) {
                        ircBot.gameAction(event.getPlayer(), action);
                        break;
                    }
                }
            }
            if (cmd.equalsIgnoreCase("/broadcast")) {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    ircBot.gameBroadcast(event.getPlayer(), action);
                }
            }
        }
        if (plugin.isPluginEnabled("Essentials")) {
            if (cmd.equalsIgnoreCase("/helpop") || cmd.equalsIgnoreCase("/amsg") || cmd.equalsIgnoreCase("/ac")) {
                if (!action.isEmpty()) {
                    for (PurpleBot ircBot : plugin.ircBots.values()) {
                        ircBot.essHelpOp(event.getPlayer(), action);
                    }
                }
            }
        }
        if (plugin.overrideMsgCmd) {
            if (cmd.equalsIgnoreCase(plugin.smsgAlias)) {
                event.setCancelled(true);
                if (player.hasPermission("irc.smsg")) {
                    String args[] = message.replaceFirst(cmd, "smsg").split(" ");
                    if (args.length >= 3) {
                        plugin.commandHandlers.commands.get("smsg").dispatch(player, args);
                    } else {
                        player.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + plugin.smsgAlias + " [player] [message]");
                    }
                } else {
                    player.sendMessage(plugin.noPermission);
                }
            } else if (cmd.equalsIgnoreCase(plugin.smsgReplyAlias)) {
                event.setCancelled(true);
                if (player.hasPermission("irc.smsg")) {
                    String pName = player.getName();
                    if (plugin.privateMsgReply.containsKey(pName)) {
                        String args[] = message.replaceFirst(cmd, "smsg " + plugin.privateMsgReply.get(pName)).split(" ");
                        if (args.length >= 3) {
                            plugin.commandHandlers.commands.get("smsg").dispatch(player, args);
                        } else {
                            player.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + plugin.smsgReplyAlias + " [message]");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "No messages received.");
                    }
                } else {
                    player.sendMessage(plugin.noPermission);
                }
            }
        }
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            if (!ircBot.channelCmdNotifyEnabled) {
                continue;
            }
            if (message.toLowerCase().startsWith("/")) {
                boolean ignoreMe = false;
                for (String s : ircBot.channelCmdNotifyIgnore) {
                    if (s.equalsIgnoreCase(cmd)) {
                        ignoreMe = true;
                    }
                }
                if (!ignoreMe) {
                    ircBot.commandNotify(event.getPlayer(), cmd, action);
                }
            }
        }
    }
}
