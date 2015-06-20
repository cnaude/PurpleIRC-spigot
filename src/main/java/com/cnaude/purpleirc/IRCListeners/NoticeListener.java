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
package com.cnaude.purpleirc.IRCListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import com.cnaude.purpleirc.Utilities.CaseInsensitiveMap;
import java.util.ArrayList;
import org.apache.commons.codec.binary.Base64;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.NoticeEvent;

/**
 *
 * @author cnaude
 */
public class NoticeListener extends ListenerAdapter {

    PurpleIRC plugin;
    PurpleBot ircBot;

    /**
     *
     * @param plugin
     * @param ircBot
     */
    public NoticeListener(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }

    /**
     *
     * @param event
     */
    @Override
    public void onNotice(NoticeEvent event) {
        Channel channel = event.getChannel();
        String message = event.getMessage().trim();
        String notice = event.getNotice();
        User user = event.getUser();
        String nick = user.getNick();

        if (message.startsWith(PurpleIRC.LINK_CMD) && ircBot.botLinkingEnabled) {
            String encodedText = message.replace(PurpleIRC.LINK_CMD, "");
            String decodedText = new String(Base64.decodeBase64(encodedText.getBytes()));
            String splitMsg[] = decodedText.split(":");

            plugin.logDebug("REMOTE LINK COMMAND: " + encodedText + "(" + decodedText + ")");

            if (splitMsg.length >= 2) {
                String command = splitMsg[0];
                String code = splitMsg[1];

                if (command.equals("LINK_REQUEST")) {
                    ircBot.linkRequests.put(user.getNick(), code);
                    plugin.logInfo("PurpleIRC bot link request from " + user.getNick());
                    plugin.logInfo("To accept: /irc linkaccept "
                            + ircBot.getFileName().replace(".yml", "") + " " + user.getNick());
                    return;
                }

                plugin.logDebug("Are we linked to " + user.getNick() + "?");
                if (ircBot.botLinks.containsKey(nick)) {
                    plugin.logDebug("Yes we are linked. Is the code correct?");
                    if (ircBot.botLinks.get(nick).equals(code)) {
                        plugin.logDebug("Yes the code is correct!");
                        plugin.logDebug(" [COMMAND: " + command + "]");
                        plugin.logDebug(" [CODE: " + code + "]");

                        if (command.equals("PRIVATE_MSG") && splitMsg.length >= 5) {
                            String from = splitMsg[2];
                            String target = splitMsg[3];
                            String sMessage = decodedText.split(":", 5)[4];

                            plugin.logDebug(" [FROM:" + from + "]");
                            plugin.logDebug(" [TO:" + target + "]");
                            plugin.logDebug(" [MSG: " + sMessage + "]");
                            ircBot.playerCrossChat(user, from, target, sMessage);
                        } else if (command.equals("PLAYER_INFO") && splitMsg.length >= 4) {
                            String curCount = splitMsg[2];
                            String maxCount = splitMsg[3];
                            String players = "";
                            if (splitMsg.length == 5) {
                                players = splitMsg[4];
                            }
                            plugin.logDebug(" [CUR_COUNT:" + curCount + "]");
                            plugin.logDebug(" [MAX_COUNT:" + maxCount + "]");
                            plugin.logDebug(" [PLAYERS:" + players + "]");
                            if (!ircBot.remoteServerInfo.containsKey(nick)) {
                                ircBot.remoteServerInfo.put(nick, new CaseInsensitiveMap<String>());
                            }
                            if (ircBot.remoteServerInfo.containsKey(nick)) {
                                ircBot.remoteServerInfo.get(nick).put("CUR_COUNT", curCount);
                                ircBot.remoteServerInfo.get(nick).put("MAX_COUNT", maxCount);
                            }
                            if (!ircBot.remotePlayers.containsKey(nick)) {
                                ircBot.remotePlayers.put(nick, new ArrayList<String>());
                            }
                            if (ircBot.remotePlayers.containsKey(nick)) {
                                ircBot.remotePlayers.get(nick).clear();
                                if (!players.isEmpty()) {
                                    for (String s : players.split(",")) {
                                        plugin.logDebug(" [ADDING:" + s + "]");
                                        ircBot.remotePlayers.get(nick).add(s);
                                    }
                                }
                            }
                        }
                    } else {
                        plugin.logDebug("Invalid code from " + nick + "!");
                    }
                } else {
                    plugin.logDebug("We are not linked to " + nick + "!");
                }
            }
            return;
        }

        plugin.logInfo("-" + user.getNick() + "-" + message);
        if (channel != null) {
            if (ircBot.isValidChannel(channel.getName())) {
                ircBot.broadcastIRCNotice(user, message, notice, channel);
            }
        }
    }
}
