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

import com.cnaude.purpleirc.TemplateName;
import com.cnaude.purpleirc.PurpleIRC;
import com.google.common.base.Joiner;
import java.util.ArrayList;
import org.bukkit.entity.Player;

/**
 *
 * @author cnaude
 */
public class JobsHook {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public JobsHook(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    public String getPlayerJob(Player player, boolean shortName) {
        if (player != null) {
            try {
                if (plugin.isPluginEnabled("Jobs")) {
                    ArrayList<String> j = new ArrayList<>();
                    String m = plugin.getServer().getPluginManager().getPlugin("Jobs").getDescription().getMain();
                    if (m.contains("me.zford")) {
                        me.zford.jobs.PlayerManager playerManager = me.zford.jobs.Jobs.getPlayerManager();
                        if (playerManager == null) {
                            return "";
                        }
                        for (me.zford.jobs.container.Job job : me.zford.jobs.Jobs.getJobs()) {
                            if (playerManager.getJobsPlayer(player).isInJob(job)) {
                                if (shortName) {
                                    j.add(job.getShortName());
                                } else {
                                    j.add(job.getName());
                                }
                            }
                        }
                    } else if (m.contains("com.gamingmesh")) {
                        com.gamingmesh.jobs.PlayerManager playerManager = com.gamingmesh.jobs.Jobs.getPlayerManager();
                        if (playerManager == null) {
                            return "";
                        }
                        for (com.gamingmesh.jobs.container.Job job : com.gamingmesh.jobs.Jobs.getJobs()) {
                            if (playerManager.getJobsPlayer(player).isInJob(job)) {
                                if (shortName) {
                                    j.add(job.getShortName());
                                } else {
                                    j.add(job.getName());
                                }
                            }
                        }
                    }
                    if (!j.isEmpty()) {
                        return Joiner.on(plugin.getMsgTemplate(TemplateName.JOBS_SEPARATOR)).join(j);
                    }
                }
            } catch (Exception ex) {
                plugin.logError("getPlayerJob: " + ex.getMessage());
            }
        }
        return "";
    }
}
