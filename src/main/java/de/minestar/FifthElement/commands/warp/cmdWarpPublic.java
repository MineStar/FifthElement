/*
 * Copyright (C) 2012 MineStar.de 
 * 
 * This file is part of fifthelement.
 * 
 * fifthelement is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * fifthelement is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with fifthelement.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.minestar.fifthelement.commands.warp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.minestar.fifthelement.Core;
import de.minestar.fifthelement.data.Warp;
import de.minestar.fifthelement.statistics.warp.PublicWarpStat;
import de.minestar.minestarlibrary.stats.StatisticHandler;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdWarpPublic extends AbstractCommand {

    public cmdWarpPublic(String syntax, String arguments, String node) {
        super(Core.NAME, syntax, arguments, node);
    }

    @Override
    public void execute(String[] args, Player player) {
        String warpName = args[0];
        // SEARCH WARP
        Warp warp = Core.warpManager.getWarp(warpName);
        // NO WARP FOUND
        if (warp == null) {
            PlayerUtils.sendError(player, pluginName, "Der Warp '" + warpName + "' existiert nicht!");
            return;
        }
        // WARP IS ALREADY PUBLIC
        if (warp.isPublic()) {
            PlayerUtils.sendError(player, pluginName, "Der Warp '" + warp.getName() + "' ist bereits öffentlich!");
            return;
        }
        // PLAYER IS NOT ALLOWED TO PUBLIC A WARP
        if (!warp.canEdit(player)) {
            PlayerUtils.sendError(player, pluginName, "Du kannst den Warp '" + warp.getName() + "' nicht öffentlich machen!");
            return;
        }
        if (!Core.warpManager.canCreatePublic(warp.getOwner())) {
            if (!warp.canEdit(player)) {
                PlayerUtils.sendError(player, pluginName, "Der Warp Besitzer '" + warp.getOwner() + "' kann keine öffentlichen Warps mehr besitzen!");
                return;
            }
        }

        // CONVERT TO PUBLIC WARP
        Core.warpManager.changeAccess(warp, true);
        PlayerUtils.sendSuccess(player, pluginName, "Der Warp '" + warp.getName() + "' ist nun öffentlich!");

        Bukkit.broadcastMessage(ChatColor.AQUA + "[" + Core.NAME + "]: " + ChatColor.WHITE + "Der Warp '" + warp.getName() + "' ist nun öffentlich.");

        // FIRE STATISTIC
        StatisticHandler.handleStatistic(new PublicWarpStat(player.getName(), warp.getName()));
    }

}
