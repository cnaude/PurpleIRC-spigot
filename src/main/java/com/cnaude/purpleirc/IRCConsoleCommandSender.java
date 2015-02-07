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

import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_8_R1.command.CraftConsoleCommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachmentInfo;

/**
 *
 * @author Chris Naude We have to implement our own CommandSender so that we can
 * receive output from the command dispatcher.
 */
public class IRCConsoleCommandSender extends CraftConsoleCommandSender {

    private final PurpleBot ircBot;
    private final String target;
    private final PurpleIRC plugin;
    private final boolean ctcpResponse;
    private final String name;

    /**
     *
     * @param message
     */
    @Override
    public void sendMessage(String message) {
        plugin.logDebug("sendMessage: " + message);
        addMessageToQueue(message);
    }

    /**
     *
     * @param messages
     */
    @Override
    public void sendMessage(String[] messages) {
        for (String message : messages) {
            plugin.logDebug("sendMessage[]: " + message);
            addMessageToQueue(message);
        }
    }

    private void addMessageToQueue(String message) {
        ircBot.messageQueue.add(new IRCMessage(target,
                plugin.colorConverter.gameColorsToIrc(message), ctcpResponse));
    }

    /**
     *
     * @param ircBot
     * @param target
     * @param plugin
     * @param ctcpResponse
     * @param name
     */
    public IRCConsoleCommandSender(PurpleBot ircBot, String target, PurpleIRC plugin, boolean ctcpResponse, String name) {
        super();
        this.target = target;
        this.ircBot = ircBot;
        this.plugin = plugin;
        this.ctcpResponse = ctcpResponse;
        this.name = name;
    }

    /**
     *
     * @return
     */
    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }

    /**
     *
     * @return
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return null;
    }

    /**
     *
     * @param perm
     * @return
     */
    @Override
    public boolean hasPermission(final String perm) {
        return true;
    }

    /**
     *
     * @param arg0
     * @return
     */
    @Override
    public boolean hasPermission(final Permission arg0) {
        return true;
    }

    /**
     *
     * @param arg0
     * @return
     */
    @Override
    public boolean isPermissionSet(final String arg0) {
        return true;
    }

    /**
     *
     * @param arg0
     * @return
     */
    @Override
    public boolean isPermissionSet(final Permission arg0) {
        return true;
    }
    
    @Override
    public void sendRawMessage(String string) {
        plugin.logDebug("sendRawMessage: " + string);
    }

}
