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
package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import com.cnaude.purpleirc.TemplateName;
import me.botsko.prism.events.PrismBlocksDrainEvent;
import me.botsko.prism.events.PrismBlocksExtinguishEvent;
import me.botsko.prism.events.PrismBlocksRollbackEvent;
import me.botsko.prism.events.PrismCustomPlayerActionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author Chris Naude
 */
public class PrismListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin the PurpleIRC plugin
     */
    public PrismListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onPrismBlocksRollbackEvent(PrismBlocksRollbackEvent event) {
        plugin.logDebug("onPrismBlocksRollbackEvent caught");
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.gamePrismRollback(event.onBehalfOf(), event.getQueryParameters(), event.getResult().getBlockStateChanges());
        }
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onPrismBlocksDrainEvent(PrismBlocksDrainEvent event) {
        plugin.logDebug("onPrismBlocksDrainEvent caught");
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.gamePrismDrainOrExtinguish(TemplateName.PRISM_DRAIN, event.onBehalfOf(), event.getRadius(), event.getBlockStateChanges());
        }
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onPrismBlocksExtinguishEvent(PrismBlocksExtinguishEvent event) {
        plugin.logDebug("onPrismBlocksExtinguishEvent caught");
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.gamePrismDrainOrExtinguish(TemplateName.PRISM_EXTINGUISH, event.onBehalfOf(), event.getRadius(), event.getBlockStateChanges());
        }
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onPrismCustomPlayerActionEvent(PrismCustomPlayerActionEvent event) {
        plugin.logDebug("onPrismCustomPlayerActionEvent caught");
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.gamePrismCustom(event.getPlayer(), event.getActionTypeName(),
                    event.getMessage(), event.getPluginName());
        }
    }
}
