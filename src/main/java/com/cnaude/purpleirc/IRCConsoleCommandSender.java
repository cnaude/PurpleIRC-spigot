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

import com.cnaude.purpleirc.IRCMessage.Type;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Chris Naude We have to implement our own CommandSender so that we can
 * receive output from the command dispatcher.
 */
public class IRCConsoleCommandSender implements ConsoleCommandSender {

    private final PurpleBot ircBot;
    private final String target;
    private final PurpleIRC plugin;
    private final Type type;
    private final String name;

    /**
     *
     * @param message
     */
    @Override
    public void sendMessage(String message) {
        if (!message.isEmpty()) {
            plugin.logDebug("sendMessage: " + message);
            addMessageToQueue(message);
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
                addMessageToQueue(message);
            }
        }
    }

    private void addMessageToQueue(String message) {
        ircBot.messageQueue.add(new IRCMessage(target,
                plugin.colorConverter.gameColorsToIrc(message), type));
    }

    /**
     *
     * @param ircBot
     * @param target
     * @param plugin the PurpleIRC plugin
     * @param type
     * @param name
     */
    public IRCConsoleCommandSender(PurpleBot ircBot, String target, PurpleIRC plugin, Type type, String name) {
        super();
        this.target = target;
        this.ircBot = ircBot;
        this.plugin = plugin;
        this.type = type;
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

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String string, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String string, boolean bln, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeAttachment(PermissionAttachment pa) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void recalculatePermissions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isOp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setOp(boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isConversing() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void acceptConversationInput(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean beginConversation(Conversation c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void abandonConversation(Conversation c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void abandonConversation(Conversation c, ConversationAbandonedEvent cae) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Spigot spigot() {
        plugin.logDebug("Spigot?");
        return null;
    }

}
