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

import com.cnaude.purpleirc.PurpleIRC;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.pircbotx.Colors;

/**
 *
 * @author Chris Naude
 */
public class ColorConverter {

    PurpleIRC plugin;
    private final boolean stripGameColors;
    private final boolean stripIRCColors;
    private final boolean stripIRCBackgroundColors;
    private final boolean stripGameColorsFromIrc;
    private final EnumMap<ChatColor, String> ircColorMap = new EnumMap<>(ChatColor.class);
    private final HashMap<String, ChatColor> gameColorMap = new HashMap<>();
    private final Pattern bgColorPattern;
    private final Pattern singleDigitColorPattern;
    private final Pattern colorHack;

    /**
     *
     * @param plugin the PurpleIRC plugin
     * @param stripGameColors
     * @param stripIRCColors
     * @param stripIRCBackgroundColors
     * @param stripGameColorsFromIrc
     */
    public ColorConverter(PurpleIRC plugin, boolean stripGameColors, 
            boolean stripIRCColors, boolean stripIRCBackgroundColors, boolean stripGameColorsFromIrc) {
        this.stripGameColors = stripGameColors;
        this.stripIRCColors = stripIRCColors;
        this.stripIRCBackgroundColors = stripIRCBackgroundColors;
        this.stripGameColorsFromIrc = stripGameColorsFromIrc;
        this.plugin = plugin;
        buildDefaultColorMaps();
        this.bgColorPattern = Pattern.compile("((\\u0003\\d+),\\d+)");
        this.singleDigitColorPattern = Pattern.compile("((\\u0003)(\\d))\\D+");
        this.colorHack = Pattern.compile("((\\u0003\\d+)(,\\d+))\\D");
    }

    /**
     *
     * @param message
     * @return
     */
    public String gameColorsToIrc(String message) {
        if (stripGameColors) {
            return ChatColor.stripColor(message);
        } else {
            String newMessage = message;
            for (ChatColor gameColor : ircColorMap.keySet()) {
                newMessage = newMessage.replace(gameColor.toString(), ircColorMap.get(gameColor));
            }
            // We return the message with the remaining MC color codes stripped out
            return ChatColor.stripColor(newMessage);
        }
    }

    /**
     *
     * @param message
     * @return
     */
    public String ircColorsToGame(String message) {        
        Matcher matcher;
        if (stripIRCBackgroundColors) {
            matcher = bgColorPattern.matcher(message);
            while (matcher.find()) {
                plugin.logDebug("Strip bg color: " + matcher.group(1) + " => " + matcher.group(2));
                message = message.replace(matcher.group(1), matcher.group(2));
            }
        }
        matcher = singleDigitColorPattern.matcher(message);
        while (matcher.find()) {
            plugin.logDebug("Single to double: " + matcher.group(3) + " => "
                    + matcher.group(2) + "0" + matcher.group(3));
            // replace \u0003N with \u00030N
            message = message.replace(matcher.group(1), matcher.group(2) + "0" + matcher.group(3));
        }
        matcher = colorHack.matcher(message);
        while (matcher.find()) {
            plugin.logDebug("Silly IRC colors: " + matcher.group(1) + " => "
                    + matcher.group(2));
            // replace \u0003N,N with \u00030N
            message = message.replace(matcher.group(1), matcher.group(2));
        }

        String m[] = message.split("");
        message = "";

        int bold = 0;
        int underline = 0;
        int italic = 0;
        int reverse = 0;
        for (String s : m) {
            switch (s) {
                case Colors.NORMAL:
                    bold=0;
                    underline=0;
                    italic=0;
                    reverse=0;                    
                    break;
                case Colors.BOLD:
                    bold++;
                    if (bold % 2 == 0) {
                        s = Colors.NORMAL;                        
                    }
                    break;
                case Colors.UNDERLINE:
                    underline++;
                    if (underline % 2 == 0) {
                        s = Colors.NORMAL;                        
                    }
                    break;
                case Colors.ITALIC:
                    italic++;
                    if (italic % 2 == 0) {
                        s = Colors.NORMAL;                        
                    }
                    break;
                case Colors.REVERSE:
                    reverse++;
                    if (reverse % 2 == 0) {
                        s = Colors.NORMAL;                        
                    }
                    break;
                default:                    
                    break;
            }
            message = message + s;
        }

        if (stripIRCColors) {
            return Colors.removeFormattingAndColors(message);
        } else {
            String newMessage = message;
            for (String ircColor : gameColorMap.keySet()) {
                newMessage = newMessage.replace(ircColor, gameColorMap.get(ircColor).toString());
            }
            // We return the message with the remaining IRC color codes stripped out
            return Colors.removeFormattingAndColors(newMessage);
        }
    }

    public void addIrcColorMap(String gameColor, String ircColor) {
        ChatColor chatColor;
        try {
            chatColor = ChatColor.valueOf(gameColor.toUpperCase());
            if (ircColor.equalsIgnoreCase("strip") && ircColorMap.containsKey(chatColor)) {
                plugin.logDebug("addIrcColorMap: " + ircColor + " => " + gameColor);
                ircColorMap.remove(chatColor);
                return;
            }
        } catch (Exception ex) {
            plugin.logError("Invalid game color: " + gameColor);
            return;
        }
        if (chatColor != null) {
            plugin.logDebug("addIrcColorMap: " + gameColor + " => " + ircColor);
            ircColorMap.put(chatColor, getIrcColor(ircColor));
        }
    }

    public void addGameColorMap(String ircColor, String gameColor) {
        if (gameColor.equalsIgnoreCase("strip") && gameColorMap.containsKey(getIrcColor(ircColor))) {
            plugin.logDebug("addGameColorMap: " + ircColor + " => " + gameColor);
            gameColorMap.remove(getIrcColor(ircColor));
            return;
        }
        ChatColor chatColor;
        try {
            chatColor = ChatColor.valueOf(gameColor.toUpperCase());
        } catch (Exception ex) {
            plugin.logError("Invalid game color: " + gameColor);
            return;
        }
        plugin.logDebug("addGameColorMap: " + ircColor + " => " + gameColor);
        gameColorMap.put(getIrcColor(ircColor), chatColor);
    }

    private String getIrcColor(String ircColor) {
        String s = "";
        try {
            s = (String) Colors.class.getField(ircColor.toUpperCase()).get(null);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            plugin.logError(ex.getMessage());
        }
        if (s.isEmpty()) {
            plugin.logError("Invalid IRC color: " + ircColor);
        }
        return s;
    }

    private void buildDefaultColorMaps() {
        ircColorMap.put(ChatColor.AQUA, Colors.CYAN);
        ircColorMap.put(ChatColor.BLACK, Colors.BLACK);
        ircColorMap.put(ChatColor.BLUE, Colors.BLUE);
        ircColorMap.put(ChatColor.BOLD, Colors.BOLD);
        ircColorMap.put(ChatColor.DARK_AQUA, Colors.TEAL);
        ircColorMap.put(ChatColor.DARK_BLUE, Colors.DARK_BLUE);
        ircColorMap.put(ChatColor.DARK_GRAY, Colors.DARK_GRAY);
        ircColorMap.put(ChatColor.DARK_GREEN, Colors.DARK_GREEN);
        ircColorMap.put(ChatColor.DARK_PURPLE, Colors.PURPLE);
        ircColorMap.put(ChatColor.DARK_RED, Colors.RED);
        ircColorMap.put(ChatColor.GOLD, Colors.OLIVE);
        ircColorMap.put(ChatColor.GRAY, Colors.LIGHT_GRAY);
        ircColorMap.put(ChatColor.GREEN, Colors.GREEN);
        ircColorMap.put(ChatColor.LIGHT_PURPLE, Colors.MAGENTA);
        ircColorMap.put(ChatColor.RED, Colors.RED);
        ircColorMap.put(ChatColor.UNDERLINE, Colors.UNDERLINE);
        ircColorMap.put(ChatColor.YELLOW, Colors.YELLOW);
        ircColorMap.put(ChatColor.WHITE, Colors.WHITE);
        ircColorMap.put(ChatColor.RESET, Colors.NORMAL);
        //ircColorMap.put(ChatColor.ITALIC, Colors.REVERSE);
        ircColorMap.put(ChatColor.ITALIC, Colors.ITALIC);

        gameColorMap.put(Colors.BLACK, ChatColor.BLACK);
        gameColorMap.put(Colors.BLUE, ChatColor.BLUE);
        gameColorMap.put(Colors.BOLD, ChatColor.BOLD);
        gameColorMap.put(Colors.BROWN, ChatColor.GRAY);
        gameColorMap.put(Colors.CYAN, ChatColor.AQUA);
        gameColorMap.put(Colors.DARK_BLUE, ChatColor.DARK_BLUE);
        gameColorMap.put(Colors.DARK_GRAY, ChatColor.DARK_GRAY);
        gameColorMap.put(Colors.DARK_GREEN, ChatColor.DARK_GREEN);
        gameColorMap.put(Colors.GREEN, ChatColor.GREEN);
        gameColorMap.put(Colors.LIGHT_GRAY, ChatColor.GRAY);
        gameColorMap.put(Colors.MAGENTA, ChatColor.LIGHT_PURPLE);
        gameColorMap.put(Colors.NORMAL, ChatColor.RESET);
        gameColorMap.put(Colors.OLIVE, ChatColor.GOLD);
        gameColorMap.put(Colors.PURPLE, ChatColor.DARK_PURPLE);
        gameColorMap.put(Colors.RED, ChatColor.RED);
        gameColorMap.put(Colors.TEAL, ChatColor.DARK_AQUA);
        gameColorMap.put(Colors.UNDERLINE, ChatColor.UNDERLINE);
        gameColorMap.put(Colors.WHITE, ChatColor.WHITE);
        gameColorMap.put(Colors.YELLOW, ChatColor.YELLOW);
        //gameColorMap.put(Colors.REVERSE, ChatColor.ITALIC);
        gameColorMap.put(Colors.ITALIC, ChatColor.ITALIC);
    }
}
