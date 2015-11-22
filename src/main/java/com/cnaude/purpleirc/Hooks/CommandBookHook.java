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
package com.cnaude.purpleirc.Hooks;

import com.cnaude.purpleirc.PurpleIRC;
import com.sk89q.commandbook.CommandBook;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;

/**
 *
 * @author Chris Naude
 */
public class CommandBookHook {

    private final PurpleIRC plugin;
    private final CommandBook commandBook;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public CommandBookHook(PurpleIRC plugin) {
        this.plugin = plugin;
        this.commandBook = (CommandBook) plugin.getServer().getPluginManager().getPlugin("CommandBook");
    }

    public boolean isCommandBookCommand(String command) {
        for (BukkitComponent bc : commandBook.getComponentManager().getComponents()) {
            for (String s : bc.getCommands().keySet()) {
                if (s.equals(command)) {
                    return true;
                }
            }
        }
        return false;
    }

}
