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
import me.zford.jobs.Jobs;
import me.zford.jobs.PlayerManager;
import me.zford.jobs.container.Job;
import org.bukkit.entity.Player;

/**
 *
 * @author cnaude
 */
public class JobsHookOld {

    private final PurpleIRC plugin;
    private PlayerManager playerManager = null;

    /**
     *
     * @param plugin
     */
    public JobsHookOld(PurpleIRC plugin) {
        this.plugin = plugin;
        try {
            this.playerManager = Jobs.getPlayerManager();
        } catch (Exception ex) {
            plugin.logError("Jobs: " + ex.getMessage());
        }
    }

    public String getPlayerJob(Player player, boolean shortName) {
        if (playerManager == null) {
            return "";
        }
        if (player != null) {
            ArrayList<String> j = new ArrayList<>();
            if (plugin.isPluginEnabled("Jobs")) {
                for (Job job : Jobs.getJobs()) {
                    if (playerManager.getJobsPlayer(player).isInJob(job)) {
                        if (shortName) {
                            j.add(job.getShortName());
                        } else {
                            j.add(job.getName());
                        }
                    }
                }
                if (!j.isEmpty()) {
                    return Joiner.on(plugin.getMsgTemplate(TemplateName.JOBS_SEPARATOR)).join(j);
                }
            }
        }
        return "";
    }
}
