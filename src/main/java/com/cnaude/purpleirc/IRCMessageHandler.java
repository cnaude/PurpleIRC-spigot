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
package com.cnaude.purpleirc;

import com.cnaude.purpleirc.Utilities.CaseInsensitiveMap;
import com.google.common.base.Joiner;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.pircbotx.Channel;
import org.pircbotx.User;

/**
 *
 * @author Chris Naude
 */
public class IRCMessageHandler {

    PurpleIRC plugin;
    CaseInsensitiveMap<Long> coolDownMap;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public IRCMessageHandler(PurpleIRC plugin) {
        this.plugin = plugin;
        this.coolDownMap = new CaseInsensitiveMap<>();
    }

    private void sendFloodWarning(User user, PurpleBot ircBot) {
        String message = plugin.colorConverter.gameColorsToIrc(plugin.getMessageTemplate(
                ircBot.botNick, "", TemplateName.IRC_FLOOD_WARNING)
                .replace("%COOLDOWN%", ircBot.floodChecker.getCoolDown(user)));
        if (!message.isEmpty()) {
            user.send().notice(message);
        }
    }

    /**
     *
     * @param ircBot
     * @param user
     * @param channel
     * @param message
     * @param privateMessage
     */
    public void processMessage(PurpleBot ircBot, User user, Channel channel, String message, boolean privateMessage) {
        if (ircBot.floodChecker.isSpam(user)) {
            sendFloodWarning(user, ircBot);
            return;
        }
        plugin.logDebug("processMessage: " + message);
        String channelName = channel.getName();
        if (ircBot.muteList.get(channelName).contains(user.getNick())) {
            plugin.logDebug("User is muted. Ignoring message from " + user.getNick() + ": " + message);
            return;
        }
        String command = message.split(" ")[0].substring(ircBot.commandPrefix.length());
        
        if (message.startsWith(ircBot.commandPrefix) && command.matches("^\\w.*")) {
            String commandArgs = null;
            if (message.contains(" ")) {
                commandArgs = message.split(" ", 2)[1];
            }
            plugin.logDebug("IRC command detected: " + command);

            plugin.logDebug(message);
            String target = channel.getName();
            if (ircBot.commandMap.get(channelName).containsKey(command)) {
                boolean privateListen = Boolean.parseBoolean(ircBot.commandMap
                        .get(channelName).get(command).get("private_listen"));
                boolean channelListen = Boolean.parseBoolean(ircBot.commandMap
                        .get(channelName).get(command).get("channel_listen"));
                plugin.logDebug("privateListen: " + privateListen);
                plugin.logDebug("channelListen: " + channelListen);
                if (privateMessage && !privateListen) {
                    plugin.logDebug("This is a private message but privateListen is false. Ignoring...");
                    return;
                }
                if (!privateMessage && !channelListen) {
                    plugin.logDebug("This is a channel message but channelListen is false. Ignoring...");
                    return;
                }

                String gc = (String) ircBot.commandMap.get(channelName).get(command).get("game_command");
                long coolDown;
                try {
                    coolDown = Long.parseLong(ircBot.commandMap.get(channelName).get(command).get("cool_down"));
                } catch (Exception ex) {
                    coolDown = 0;
                    plugin.logError(ex.getMessage());
                }
                if (coolDown > 0) {
                    if (isWarm(command, coolDown)) {
                        String s = "";
                        if (coolDown != 1) {
                            s = "s";
                        }
                        sendMessage(ircBot, user.getNick(), "Cool down for this command triggered. Please wait at least " + coolDown + " second" + s + ".", true);
                        return;
                    }
                }
                String gcUsage = (String) ircBot.commandMap.get(channelName).get(command).get("game_command_usage");
                List<String> extraCommands = ircBot.extraCommandMap.get(channelName).get(command);
                List<String> gameCommands = new ArrayList<>();
                List<String> userMasks = ircBot.commandUsermasksMap.get(channelName).get(command);
                gameCommands.add(gc);
                gameCommands.addAll(extraCommands);
                String modes = (String) ircBot.commandMap.get(channelName).get(command).get("modes");
                String perm = (String) ircBot.commandMap.get(channelName).get(command).get("perm");
                String outputTemplate = (String) ircBot.commandMap.get(channelName).get(command).get("output");
                boolean privateCommand = Boolean.parseBoolean(ircBot.commandMap.get(channelName).get(command).get("private"));
                boolean ctcpResponse = Boolean.parseBoolean(ircBot.commandMap.get(channelName).get(command).get("ctcp"));
                String senderName = ircBot.commandMap.get(channelName).get(command).get("sender").replace("%NICK%", user.getNick());

                if (privateCommand || privateMessage) {
                    target = user.getNick();
                }

                plugin.logDebug("Target: " + target);

                if (isValidMode(modes, user, channel)
                        && checkPerm(perm, user.getNick())
                        && checkHostMask(ircBot, user, userMasks)) {
                    gc_loop:
                    for (String gameCommand : gameCommands) {
                        switch (gameCommand) {
                            case "@list":
                                sendMessage(ircBot, target, plugin.getMCPlayers(ircBot, channelName), ctcpResponse);
                                break;
                            case "@uptime":
                                sendMessage(ircBot, target, plugin.getMCUptime(), ctcpResponse);
                                break;
                            case "@help":
                                sendMessage(ircBot, target, getCommands(ircBot.commandMap, channelName), ctcpResponse);
                                break;
                            case "@chat":
                                ircBot.broadcastChat(user, channel, target, commandArgs, false, ctcpResponse);
                                break;
                            case "@ochat":
                                ircBot.broadcastChat(user, channel, target, commandArgs, true, ctcpResponse);
                                break;
                            case "@hchat":
                                ircBot.broadcastHeroChat(user, channel, target, commandArgs);
                                break;
                            case "@motd":
                                sendMessage(ircBot, target, plugin.getServerMotd(), ctcpResponse);
                                break;
                            case "@version":
                                sendMessage(ircBot, target, plugin.getServer().getVersion(), ctcpResponse);
                                break;
                            case "@versionfull":
                                String v = "This server is running "
                                        + plugin.getServer().getName()
                                        + " version "
                                        + plugin.getServer().getVersion()
                                        + " (Implementing API version "
                                        + plugin.getServer().getBukkitVersion() + ")";
                                sendMessage(ircBot, target, v, ctcpResponse);
                                break;
                            case "@bukkit":
                                sendMessage(ircBot, target, plugin.getServer().getBukkitVersion(), ctcpResponse);
                                break;
                            case "@rtsmb":
                                if (plugin.reportRTSHook != null) {
                                    plugin.reportRTSHook.modBroadcast(user.getNick(), commandArgs);
                                }
                                break;
                            case "@msg":
                                ircBot.playerChat(user, channel, target, commandArgs);
                                break;
                            case "@r":
                                ircBot.playerReplyChat(user, channel, target, commandArgs);
                                break;
                            case "@clearqueue":
                                sendMessage(ircBot, target, plugin.commandQueue.clearQueue(), ctcpResponse);
                                sendMessage(ircBot, target, ircBot.messageQueue.clearQueue(), ctcpResponse);
                                break;
                            case "@query":
                                sendMessage(ircBot, target, plugin.getRemotePlayers(commandArgs), ctcpResponse);
                                break;
                            case "@a":
                                if (plugin.adminPrivateChatHook != null) {
                                    String newMessage = ircBot.filterMessage(
                                            plugin.tokenizer.ircChatToGameTokenizer(ircBot, user, channel, plugin.getMessageTemplate(ircBot.botNick, channelName, TemplateName.IRC_ADMIN_CHAT), commandArgs), channelName);
                                    plugin.adminPrivateChatHook.sendMessage(newMessage, user.getNick());
                                    String acResponse = plugin.tokenizer.msgChatResponseTokenizer(target, commandArgs, plugin.getMessageTemplate(TemplateName.IRC_ADMIN_RESPONSE));
                                    if (!acResponse.isEmpty()) {
                                        sendMessage(ircBot, target, acResponse, ctcpResponse);
                                    }
                                }
                                break;
                            default:
                                if (commandArgs == null) {
                                    commandArgs = "";
                                }

                                if (gameCommand.contains("%ARGS%")) {
                                    gameCommand = gameCommand.replace("%ARGS%", commandArgs);
                                }

                                if (gameCommand.contains("%NAME%")) {
                                    gameCommand = gameCommand.replace("%NAME%", user.getNick());
                                }

                                if (gameCommand.matches(".*%ARG\\d+%.*")) {
                                    String commandArgsArray[] = commandArgs.split(" ");
                                    for (int i = 0; i < commandArgsArray.length; i++) {
                                        gameCommand = gameCommand.replace("%ARG" + (i + 1) + "%", commandArgsArray[i]);
                                    }
                                }

                                Pattern pattern = Pattern.compile(".*%ARG(\\d+)\\+%.*");
                                Matcher matcher = pattern.matcher(gameCommand);
                                if (matcher.matches()) {
                                    String commandArgsArray[] = commandArgs.split(" ");
                                    int startPos = Integer.valueOf(matcher.group(1));
                                    if (commandArgsArray.length >= startPos) {
                                        gameCommand = gameCommand.replace("%ARG" + startPos + "+%",
                                                Joiner.on(" ").join(Arrays.copyOfRange(commandArgsArray, startPos - 1, commandArgsArray.length)));
                                    }
                                }

                                if (gameCommand.matches(".*%ARG\\d+%.*")
                                        || gameCommand.matches(".*%ARG(\\d+)\\+%.*")
                                        || gameCommand.contains("%ARGS%")) {
                                    plugin.logDebug("GM BAIL: \"" + gameCommand.trim() + "\"");
                                    ircBot.asyncIRCMessage(target, plugin.colorConverter.gameColorsToIrc(
                                            ChatColor.translateAlternateColorCodes('&', gcUsage)));
                                    break gc_loop;
                                } else {
                                    plugin.logDebug("GM: \"" + gameCommand.trim() + "\"");
                                    try {
                                        plugin.commandQueue.add(new IRCCommand(
                                                new IRCCommandSender(ircBot, target, plugin, ctcpResponse, senderName, outputTemplate),
                                                new IRCConsoleCommandSender(ircBot, target, plugin, ctcpResponse, senderName),
                                                gameCommand.trim()
                                        ));
                                    } catch (Exception ex) {
                                        plugin.logError(ex.getMessage());
                                    }
                                }
                                break;
                        }
                    }
                } else {
                    plugin.logDebug("User '" + user.getNick() + "' mode not okay.");
                    ircBot.asyncIRCMessage(target, plugin.getMessageTemplate(
                            ircBot.botNick, channelName, TemplateName.NO_PERM_FOR_IRC_COMMAND)
                            .replace("%NICK%", user.getNick())
                            .replace("%CMDPREFIX%", ircBot.commandPrefix));
                }
            } else {
                if (privateMessage || ircBot.invalidCommandPrivate.get(channelName)) {
                    target = user.getNick();
                }
                plugin.logDebug("Invalid command: " + command);
                String invalidIrcCommand = plugin.getMessageTemplate(
                        ircBot.botNick, channelName, TemplateName.INVALID_IRC_COMMAND)
                        .replace("%NICK%", user.getNick())
                        .replace("%CMDPREFIX%", ircBot.commandPrefix);
                plugin.logDebug("invalidIrcCommand: " + invalidIrcCommand);
                if (!invalidIrcCommand.isEmpty()) {
                    if (ircBot.invalidCommandCTCP.get(channelName)) {
                        ircBot.asyncCTCPCommand(target, invalidIrcCommand);
                    } else {
                        ircBot.asyncIRCMessage(target, invalidIrcCommand);
                    }
                }
                if (ircBot.enabledMessages.get(channelName).contains(TemplateName.INVALID_IRC_COMMAND)) {
                    plugin.logDebug("Invalid IRC command dispatched for broadcast...");
                    ircBot.broadcastChat(user, channel, null, message, false, false);
                }
            }
        } else {
            if (ircBot.ignoreIRCChat.get(channelName)) {
                plugin.logDebug("Message NOT dispatched for broadcast due to \"ignore-irc-chat\" being true ...");
                return;
            }
            if (privateMessage && !ircBot.relayPrivateChat) {
                plugin.logDebug("Message NOT dispatched for broadcast due to \"relay-private-chat\" being false and this is a private message ...");
                return;
            }
            plugin.logDebug("Message dispatched for broadcast...");
            ircBot.broadcastChat(user, channel, null, message, false, false);
        }
    }

    private boolean isValidMode(String modes, User user, Channel channel) {
        plugin.logDebug("[isValidMode]: " + modes);
        boolean modeOkay = false;
        if (modes.equals("*")) {
            return true;
        }
        if (modes.contains("i") && !modeOkay) {
            modeOkay = user.isIrcop();
        }
        if (modes.contains("o") && !modeOkay) {
            modeOkay = user.getChannelsOpIn().contains(channel);
        }
        if (modes.contains("v") && !modeOkay) {
            modeOkay = user.getChannelsVoiceIn().contains(channel);
        }
        if (modes.contains("h") && !modeOkay) {
            modeOkay = user.getChannelsHalfOpIn().contains(channel);
        }
        if (modes.contains("q") && !modeOkay) {
            modeOkay = user.getChannelsOwnerIn().contains(channel);
        }
        if (modes.contains("s") && !modeOkay) {
            modeOkay = user.getChannelsSuperOpIn().contains(channel);
        }
        return modeOkay;
    }

    private void sendMessage(PurpleBot ircBot, String target, String message, boolean ctcpResponse) {
        if (ctcpResponse) {
            plugin.logDebug("Sending message to target: " + target + " => " + message);
            ircBot.asyncCTCPMessage(target, message);
        } else {
            plugin.logDebug("Sending message to target: " + target + " => " + message);
            ircBot.asyncIRCMessage(target, message);
        }
    }

    private String getCommands(CaseInsensitiveMap<CaseInsensitiveMap<CaseInsensitiveMap<String>>> commandMap, String myChannel) {
        if (commandMap.containsKey(myChannel)) {
            List<String> sortedCommands = new ArrayList<>();
            for (String command : commandMap.get(myChannel).keySet()) {
                sortedCommands.add(command);
            }
            Collections.sort(sortedCommands, Collator.getInstance());
            String cmds = Joiner.on(", ").join(sortedCommands);
            String msg = plugin.getMessageTemplate(TemplateName.VALID_IRC_COMMANDS).replace("%COMMANDS%", cmds);
            return msg;
        }
        return "";
    }

    private boolean checkPerm(String perm, String playerName) {
        if (perm.isEmpty()) {
            return true;
        }
        Player player = plugin.getServer().getPlayer(playerName);

        if (player != null) {
            plugin.logDebug("[checkPerm] Player " + playerName + " permission node " + perm + "=" + player.hasPermission(perm));
            return player.hasPermission(perm);
        } else {
            plugin.logDebug("[checkPerm] Player not online: " + playerName);
            return false;
        }
    }

    private boolean checkHostMask(PurpleBot ircBot, User user, List<String> userMasks) {
        if (userMasks.isEmpty()) {
            plugin.logDebug("checkHostMask [empty]: " + true);
            return true;
        }
        for (String userMask : userMasks) {
            plugin.logDebug("checkHostMask [testing]: " + userMask);
            if (userMask.equals("*")
                    || userMask.isEmpty()
                    || ircBot.checkUserMask(user, userMask)) {
                plugin.logDebug("checkHostMask [match]: ");
                return true;
            }
        }
        plugin.logDebug("checkHostMask [no match]: " + false);
        return false;
    }

    public boolean isWarm(String command, Long coolDown) {
        Long timeNow = System.currentTimeMillis();
        if (coolDownMap.containsKey(command)) {
            Long timeLast = coolDownMap.get(command);
            Long coolDiff = (timeNow - timeLast) / 1000;
            if (coolDiff < coolDown) {
                plugin.logDebug("Warm: " + command + " " + coolDiff);
                return true;
            } else {
                plugin.logDebug("Cold: " + command + " " + coolDiff);
                coolDownMap.remove(command);
            }
        } else {
            coolDownMap.put(command, timeNow);
        }
        return false;
    }

}
