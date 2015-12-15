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

/**
 *
 * @author Chris Naude
 */
public class IRCCommand {

    private final IRCCommandSender sender;
    private final IRCConsoleCommandSender consoleSender;
    private final String command;

    /**
     *
     * @param sender the sender of the command
     * @param consoleSender the console sender
     * @param command the actual command
     */
    public IRCCommand(IRCCommandSender sender, IRCConsoleCommandSender consoleSender, String command) {
        this.sender = sender;
        this.consoleSender = consoleSender;
        this.command = command;
    }

    /**
     *
     * @return
     */
    public IRCCommandSender getIRCCommandSender() {
        return sender;
    }

    /**
     *
     * @return
     */
    public IRCConsoleCommandSender getIRCConsoleCommandSender() {
        return consoleSender;
    }

    /**
     *
     * @return
     */
    public String getGameCommand() {
        return command;
    }
}
