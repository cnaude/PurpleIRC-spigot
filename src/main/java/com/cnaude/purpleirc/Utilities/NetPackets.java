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
package com.cnaude.purpleirc.Utilities;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.pircbotx.Channel;
import org.pircbotx.User;

/**
 *
 * @author cnaude
 */
public class NetPackets {

    PurpleIRC plugin;
    private final ProtocolManager protocolManager;

    /**
     *
     * @param plugin
     */
    public NetPackets(PurpleIRC plugin) {
        this.plugin = plugin;
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    /**
     *
     * @param name
     * @param ircBot
     * @param channel
     */
    public void addToTabList(String name, PurpleBot ircBot, Channel channel) {
        if (!plugin.customTabList) {
            return;
        }
        String channelName = channel.getName();
        if (ircBot.tabIgnoreNicks.containsKey(channelName)) {
            if (ircBot.tabIgnoreNicks.get(channelName).contains(name)) {
                plugin.logDebug("Not adding " + name + " to tab list.");
                return;
            }
        }
        try {
            PacketContainer packet = tabPacket(name, true);
            if (packet != null) {
                for (Player reciever : plugin.getServer().getOnlinePlayers()) {
                    if (reciever.hasPermission("irc.tablist")) {
                        protocolManager.sendServerPacket(reciever, packet);
                    }
                }
            }
        } catch (FieldAccessException | InvocationTargetException e) {
            plugin.logError(e.getMessage());
        }
    }

    /**
     *
     * @param name
     */
    public void remFromTabList(String name) {
        if (!plugin.customTabList) {
            return;
        }
        try {
            PacketContainer packet = tabPacket(name, false);
            if (packet != null) {
                for (Player reciever : plugin.getServer().getOnlinePlayers()) {
                    if (reciever.hasPermission("irc.tablist")) {
                        protocolManager.sendServerPacket(reciever, packet);
                    }
                }
            }
        } catch (FieldAccessException | InvocationTargetException e) {
            plugin.logError(e.getMessage());
        }
    }

    private PacketContainer tabPacket(String name, boolean add) {
        String displayName = truncateName(plugin.customTabPrefix + name);
        PacketContainer packet = null;
        String version = plugin.getServer().getVersion();
        if (version.contains("MC: 1.8")) {
            try {
                UUID uuid = null; // = plugin.getPlayerUuid(name);
                if (uuid == null) {
                    uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + displayName).getBytes(Charsets.UTF_8));
                }
                if (add) {
                    packet = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);

                    PlayerInfoData pid = new PlayerInfoData(
                            new WrappedGameProfile(uuid, displayName),
                            0,
                            NativeGameMode.valueOf(plugin.customTabGamemode.toUpperCase()),
                            WrappedChatComponent.fromJson("{\"text\": \"" + displayName + "\"}"));
                    packet.getPlayerInfoDataLists().write(0, Arrays.asList(pid));
                } else {
                    plugin.logDebug("T: Removing: " + name);
                    net.minecraft.server.v1_8_R2.EntityPlayer pl = new net.minecraft.server.v1_8_R2.EntityPlayer(
                            net.minecraft.server.v1_8_R2.MinecraftServer.getServer(),
                            net.minecraft.server.v1_8_R2.MinecraftServer.getServer().getWorldServer(0),
                            (GameProfile) (new WrappedGameProfile(uuid, displayName)).getHandle(),
                            new net.minecraft.server.v1_8_R2.PlayerInteractManager(net.minecraft.server.v1_8_R2.MinecraftServer.getServer().getWorldServer(0))
                    );
                    net.minecraft.server.v1_8_R2.PacketPlayOutPlayerInfo pi = 
                            new net.minecraft.server.v1_8_R2.PacketPlayOutPlayerInfo(
                                    net.minecraft.server.v1_8_R2.PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, pl);
                    return PacketContainer.fromPacket(pi);

                }
                return packet;
            } catch (Exception ex) {
                plugin.logError("tabPacket: " + ex.getMessage());
            }
        } 
        return packet;
    }

    /**
     *
     * @param player
     * @param ircBot
     * @param channel
     */
    public void updateTabList(Player player, final PurpleBot ircBot, final Channel channel) {
        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                for (User user : channel.getUsers()) {
                    String nick = user.getNick();
                    addToTabList(nick, ircBot, channel);
                }
            }
        }, 5);

    }

    /**
     *
     * @param player
     */
    public void updateTabList(Player player) {
        if (player.hasPermission("irc.tablist")) {
            for (PurpleBot ircBot : plugin.ircBots.values()) {
                if (ircBot.isConnected()) {
                    for (Channel channel : ircBot.getChannels()) {
                        if (ircBot.isValidChannel(channel.getName())) {
                            updateTabList(player, ircBot, channel);
                        }
                    }
                }
            }
        }
    }

    private String truncateName(String name) {
        if (name.length() > 16) {
            return name.substring(0, 15);
        } else {
            return name;
        }
    }
}
