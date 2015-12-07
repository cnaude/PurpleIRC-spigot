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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author Chris Naude
 */
public class UpdateChecker {

    PurpleIRC plugin;

    private BukkitTask bt;
    private int newBuild = 0;
    private int currentBuild = 0;
    private String currentVersion = "";
    private String newVersion = "";

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public UpdateChecker(PurpleIRC plugin) {
        this.plugin = plugin;
        currentVersion = plugin.getDescription().getVersion();
        try {
            currentBuild = Integer.valueOf(currentVersion.split("-")[1]);
        } catch (NumberFormatException e) {
            currentBuild = 0;
        }
        startUpdateChecker();
    }

    private void startUpdateChecker() {
        bt = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                if (plugin.isUpdateCheckerEnabled()) {
                    plugin.logInfo("Checking for " + plugin.updateCheckerMode() + " updates ... ");
                    updateCheck(plugin.getServer().getConsoleSender(), plugin.updateCheckerMode());
                }
            }
        }, 0, 432000);
    }

    public void asyncUpdateCheck(final CommandSender sender, final String mode) {
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                if (plugin.isUpdateCheckerEnabled()) {
                    updateCheck(sender, mode);
                }
            }
        }, 0);
    }

    private void updateCheck(CommandSender sender, String mode) {
        if (plugin.getServer().getVersion().contains("Spigot")) {
            try {
                URL url = new URL("http://h.cnaude.org:8081/jenkins/job/PurpleIRC-spigot/lastStableBuild/api/json");
                URLConnection conn = url.openConnection();
                conn.setReadTimeout(5000);
                conn.addRequestProperty("User-Agent", "PurpleIRC-spigot Update Checker");
                conn.setDoOutput(true);
                final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                final String response = reader.readLine();
                final JSONObject obj = (JSONObject) JSONValue.parse(response);
                if (obj.isEmpty()) {
                    sender.sendMessage(plugin.LOG_HEADER_F + " No files found, or Feed URL is bad.");
                    return;
                }

                newVersion = obj.get("number").toString();
                String downloadUrl = obj.get("url").toString();
                plugin.logDebug("newVersionTitle: " + newVersion);
                newBuild = Integer.valueOf(newVersion);
                if (newBuild > currentBuild) {
                    sender.sendMessage(plugin.LOG_HEADER_F + " Latest dev build: " + newVersion + " is out!" + " You are still running build: " + currentVersion);
                    sender.sendMessage(plugin.LOG_HEADER_F + " Update at: " + downloadUrl);
                } else if (currentBuild > newBuild) {
                    sender.sendMessage(plugin.LOG_HEADER_F + " Dev build: " + newVersion + " | Current build: " + currentVersion);
                } else {
                    sender.sendMessage(plugin.LOG_HEADER_F + " No new version available");
                }
            } catch (IOException | NumberFormatException e) {
                sender.sendMessage(plugin.LOG_HEADER_F + " Error checking for latest dev build: " + e.getMessage());
            }
        } else if (mode.equalsIgnoreCase("stable")) {
            try {
                URL url = new URL("https://api.curseforge.com/servermods/files?projectids=56773");
                URLConnection conn = url.openConnection();
                conn.setReadTimeout(5000);
                conn.addRequestProperty("User-Agent", "PurpleIRC Update Checker");
                conn.setDoOutput(true);
                final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                final String response = reader.readLine();
                final JSONArray array = (JSONArray) JSONValue.parse(response);
                if (array.isEmpty()) {
                    plugin.logInfo("No files found, or Feed URL is bad.");
                    return;
                }
                newVersion = ((String) ((JSONObject) array.get(array.size() - 1)).get("name")).trim();
                plugin.logDebug("newVersionTitle: " + newVersion);
                newBuild = Integer.valueOf(newVersion.split("-")[1]);
                if (newBuild > currentBuild) {
                    sender.sendMessage(plugin.LOG_HEADER_F + " Stable version: " + newVersion + " is out!" + " You are still running version: " + currentVersion);
                    sender.sendMessage(plugin.LOG_HEADER_F + " Update at: http://dev.bukkit.org/server-mods/purpleirc");
                } else if (currentBuild > newBuild) {
                    sender.sendMessage(plugin.LOG_HEADER_F + " Stable version: " + newVersion + " | Current Version: " + currentVersion);
                } else {
                    sender.sendMessage(plugin.LOG_HEADER_F + " No new version available");
                }
            } catch (IOException | NumberFormatException e) {
                sender.sendMessage(plugin.LOG_HEADER_F + " Error checking for latest version: " + e.getMessage());
            }
        } else {
            try {
                URL url = new URL("http://h.cnaude.org:8081/jenkins/job/PurpleIRC/lastStableBuild/api/json");
                URLConnection conn = url.openConnection();
                conn.setReadTimeout(5000);
                conn.addRequestProperty("User-Agent", "PurpleIRC Update Checker");
                conn.setDoOutput(true);
                final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                final String response = reader.readLine();
                final JSONObject obj = (JSONObject) JSONValue.parse(response);
                if (obj.isEmpty()) {
                    sender.sendMessage(plugin.LOG_HEADER_F + " No files found, or Feed URL is bad.");
                    return;
                }

                newVersion = obj.get("number").toString();
                String downloadUrl = obj.get("url").toString();
                plugin.logDebug("newVersionTitle: " + newVersion);
                newBuild = Integer.valueOf(newVersion);
                if (newBuild > currentBuild) {
                    sender.sendMessage(plugin.LOG_HEADER_F + " Latest dev build: " + newVersion + " is out!" + " You are still running build: " + currentVersion);
                    sender.sendMessage(plugin.LOG_HEADER_F + " Update at: " + downloadUrl);
                } else if (currentBuild > newBuild) {
                    sender.sendMessage(plugin.LOG_HEADER_F + " Dev build: " + newVersion + " | Current build: " + currentVersion);
                } else {
                    sender.sendMessage(plugin.LOG_HEADER_F + " No new version available");
                }
            } catch (IOException | NumberFormatException e) {
                sender.sendMessage(plugin.LOG_HEADER_F + " Error checking for latest dev build: " + e.getMessage());
            }
        }

    }

    public void cancel() {
        bt.cancel();
    }
}
