/*
 * Copyright (C) 2012 MineStar.de 
 * 
 * This file is part of FifthElement.
 * 
 * FifthElement is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * FifthElement is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with FifthElement.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.minestar.FifthElement.commands.warp;

import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.Player;

import de.minestar.FifthElement.core.Core;
import de.minestar.FifthElement.data.Warp;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.commands.AbstractSuperCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdWarp extends AbstractSuperCommand {

    public cmdWarp(String syntax, String arguments, String node, AbstractCommand... subCommands) {
        super(Core.NAME, syntax, arguments, node, true, subCommands);
    }

    @Override
    public void execute(String[] args, Player player) {
        // SEARCH FOR POSSIBLE WARPS
        List<Warp> matches = Core.warpManager.searchWarp(args[0]);
        // NO MATCH FOUND
        if (matches.isEmpty()) {
            PlayerUtils.sendError(player, pluginName, "Keine Warps mit dem Suchwort '" + args[0] + "' wurden gefunden!");
            return;
        }

        // SORT OUT WARPS THE PLAYER CAN'T USE
        Iterator<Warp> iter = matches.iterator();
        while (iter.hasNext()) {
            Warp warp = iter.next();
            if (!warp.canUse(player))
                iter.remove();
        }

        // ALL FOUND WARPS ARE FORBIDDEN FOR THE PLAYER
        if (matches.isEmpty()) {
            PlayerUtils.sendError(player, pluginName, "Du kannst keinen Warp mit dem Suchwort '" + args[0] + "' nutzen!");
            return;
        }

        // FIND THE BEST MATCH
        // LOWEST LENGTH DIFFERENCE BETWEEN SEARCH WORD AND WARP NAME
        Warp bestMatch = matches.get(0);
        int delta = Math.abs(bestMatch.getName().length() - args[0].length());
        int curDelta = 0;
        for (Warp warp : matches) {
            curDelta = Math.abs(warp.getName().length() - args[0].length());
            if (curDelta < delta) {
                bestMatch = warp;
                delta = curDelta;
            }
        }

        // TELEPORT PLAYER THE TO WARP
        player.teleport(bestMatch.getLocation());
        PlayerUtils.sendSuccess(player, pluginName, "Willkommen beim Warp '" + bestMatch.getName() + "'.");
    }
}
