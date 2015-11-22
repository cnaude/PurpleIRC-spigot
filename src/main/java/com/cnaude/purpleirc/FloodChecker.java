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
package com.cnaude.purpleirc;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.pircbotx.User;

/**
 *
 * @author Chris Naude
 */
public class FloodChecker {

    private final PurpleIRC plugin;
    private final PurpleBot ircBot;
    private final String TIME_FORMAT = "%2.2f";
    private final Map<String, MyUser> myUsers;

    private class MyUser {

        Long lastCommand = System.currentTimeMillis();
        Long coolDownTimer = 0L;
        int commandCount = 0;

        public MyUser() {
        }

    }

    public FloodChecker(final PurpleBot ircBot, final PurpleIRC plugin) {
        this.ircBot = ircBot;
        this.plugin = plugin;
        this.myUsers = new HashMap<>();
    }

    public boolean isSpam(Object object) {
        if (ircBot.floodControlEnabled) {
            String key;
            String name;
            if (object instanceof User) {
                key = ((User) object).getHostmask();
                name = ((User) object).getNick();
            } else if (object instanceof Player) {
                key = ((Player) object).getUniqueId().toString();
                name = ((Player) object).getName();
            } else {
                return false;
            }

            if (myUsers.containsKey(key)) {
                MyUser myUser = myUsers.get(key);
                Long timeNow = System.currentTimeMillis();
                Long timeDiff = timeNow - myUser.lastCommand;
                Long coolDiff = timeNow - myUser.coolDownTimer;
                myUser.commandCount = Math.max(myUser.commandCount
                        - (Math.round(timeDiff / ircBot.floodControlTimeInterval * ircBot.floodControlMaxMessages)), 0) + 1;
                myUser.lastCommand = timeNow;

                if (coolDiff < ircBot.floodControlCooldown) {
                    plugin.logDebug("Cooldown: " + name + "(" + coolDiff + " < " + ircBot.floodControlCooldown + ")");
                    return true;
                }

                if (myUser.commandCount > ircBot.floodControlMaxMessages) {
                    myUser.coolDownTimer = timeNow;
                    plugin.logDebug("Spam ignored from: " + name + "(" + key + ")");
                    return true;
                }

            } else {
                myUsers.put(key, new MyUser());
            }
        }

        return false;
    }

    public String getCoolDown(User user) {
        return getCoolDown(user.getHostmask());
    }

    public String getCoolDown(Player player) {
        return getCoolDown(player.getUniqueId().toString());
    }

    private String getCoolDown(String key) {
        Long timeNow = System.currentTimeMillis();
        if (myUsers.containsKey(key)) {
            return String.format(TIME_FORMAT,
                    ((ircBot.floodControlCooldown - (timeNow - myUsers.get(key).coolDownTimer)) / 1000f));
        }
        return "0";
    }

}
