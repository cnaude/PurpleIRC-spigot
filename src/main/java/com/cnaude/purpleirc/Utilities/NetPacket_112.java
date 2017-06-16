/*
 * Copyright (C) 2015 cnaude
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

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PlayerInteractManager;

/**
 *
 * @author Chris Naude
 */
public class NetPacket_112 {

    public static PacketContainer add(String displayName) {
        UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + displayName).getBytes(Charsets.UTF_8));
        EntityPlayer pl = new EntityPlayer(
                MinecraftServer.getServer(),
                MinecraftServer.getServer().getWorldServer(0),
                (GameProfile) (new WrappedGameProfile(uuid, displayName)).getHandle(),
                new PlayerInteractManager(MinecraftServer.getServer().getWorldServer(0))
        );
        PacketPlayOutPlayerInfo pi
                = new PacketPlayOutPlayerInfo(
                        PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, pl);
        return PacketContainer.fromPacket(pi);
    }

    public static PacketContainer rem(String displayName) {
        UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + displayName).getBytes(Charsets.UTF_8));
        EntityPlayer pl = new EntityPlayer(
                MinecraftServer.getServer(),
                MinecraftServer.getServer().getWorldServer(0),
                (GameProfile) (new WrappedGameProfile(uuid, displayName)).getHandle(),
                new PlayerInteractManager(MinecraftServer.getServer().getWorldServer(0))
        );
        PacketPlayOutPlayerInfo pi
                = new PacketPlayOutPlayerInfo(
                        PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, pl);
        return PacketContainer.fromPacket(pi);
    }

}
