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
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Chris Naude We have to implement our own CommandSender so that we can
 * receive output from the command dispatcher.
 */
public class IRCCommandSender implements CommandSender {

    private final PurpleBot ircBot;
    private final String target;
    private final PurpleIRC plugin;
    private final boolean ctcpResponse;
    private final String name;
    private final String template;

    /**
     *
     * @param message
     */
    @Override
    public void sendMessage(String message) {
        if (!message.isEmpty()) {
            plugin.logDebug("sendMessage: " + message);
            addMessageToQueue(template.replace("%RESULT%", message));
        }
    }

    /**
     *
     * @param messages
     */
    @Override
    public void sendMessage(String[] messages) {
        for (String message : messages) {
            if (!message.isEmpty()) {
                plugin.logDebug("sendMessage[]: " + message);
                addMessageToQueue(template.replace("%RESULT%", message));
            }
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
     * @param plugin the PurpleIRC plugin
     * @param ctcpResponse
     * @param name
     * @param template
     */
    public IRCCommandSender(PurpleBot ircBot, String target, PurpleIRC plugin, boolean ctcpResponse, String name, String template) {
        super();
        this.target = target;
        this.ircBot = ircBot;
        this.plugin = plugin;
        this.ctcpResponse = ctcpResponse;
        this.name = name;
        this.template = template;
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

    /**
     *
     */
    @Override
    public void recalculatePermissions() {
    }

    /**
     *
     * @param arg0
     */
    @Override
    public void removeAttachment(final PermissionAttachment arg0) {
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isOp() {
        return true;
    }

    /**
     *
     * @param op
     */
    @Override
    public void setOp(final boolean op) {
    }

    /**
     *
     * @param arg0
     * @return
     */
    @Override
    public PermissionAttachment addAttachment(final Plugin arg0) {
        return null;
    }

    /**
     *
     * @param arg0
     * @param arg1
     * @return
     */
    @Override
    public PermissionAttachment addAttachment(final Plugin arg0, final int arg1) {
        return null;
    }

    /**
     *
     * @param arg0
     * @param arg1
     * @param arg2
     * @return
     */
    @Override
    public PermissionAttachment addAttachment(final Plugin arg0, final String arg1, final boolean arg2) {
        return null;
    }

    /**
     *
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     * @return
     */
    @Override
    public PermissionAttachment addAttachment(final Plugin arg0, final String arg1, final boolean arg2, final int arg3) {
        return null;
    }

}
