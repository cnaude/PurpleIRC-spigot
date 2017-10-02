/*
 * Copyright (C) 2017 cnaude
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
package com.cnaude.purpleirc.Hooks;

import com.cnaude.purpleirc.PurpleIRC;
import com.dthielke.herochat.ChannelManager;
import com.dthielke.herochat.Herochat;

/**
 *
 * @author cnaude
 */
public class HerochatHook {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public HerochatHook(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    public void sendHeroMessage(String heroChannel, String message) {
        getChannelManager().getChannel(heroChannel).sendRawMessage(message);
    }

    public String getHeroNick(String channel) {
        return getChannelManager().getChannel(channel).getNick();
    }

    public String getHeroColor(String channel) {
        return getChannelManager().getChannel(channel).getColor().toString();
    }

    public boolean isValidChannel(String channel) {
        return getChannelManager().hasChannel(channel);
    }

    public String getChannelName(String channel) {
        return getChannelManager().getChannel(channel).getName();
    }

    private ChannelManager getChannelManager() {
        return Herochat.getChannelManager();
    }

}
