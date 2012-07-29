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

import com.bukkit.gemo.utils.UtilPermissions;

import de.minestar.FifthElement.core.Core;
import de.minestar.FifthElement.data.Warp;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.commands.AbstractSuperCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdWarp extends AbstractSuperCommand {

    private final static String IGNORE_USE_MODE = "fifthelement.command.ignoreusemode";

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

        // SORT OUT WARPS THE PLAYER CAN'T USE OR THE WARP CAN ONLY USE BY SIGNS
        Iterator<Warp> iter = matches.iterator();
        while (iter.hasNext()) {
            Warp warp = iter.next();
            if (!canUse(warp, player))
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

    private boolean canUse(Warp warp, Player player) {
        // OWNER AND GUYS WITH SPECIAL PERMISSION CAN ACCESS ALL WARPS
        if (warp.isOwner(player) || UtilPermissions.playerCanUseCommand(player, IGNORE_USE_MODE))
            return true;

        // WARP CAN ACCESSED BY COMMAND AND PLAYER CAN USE THE WARP
        return warp.canUsedBy(Warp.COMMAND_USEMODE) && warp.canUse(player);
    }
}
