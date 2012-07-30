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

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.minestar.FifthElement.core.Core;
import de.minestar.FifthElement.data.Warp;
import de.minestar.FifthElement.statistics.warp.WarpRenameStat;
import de.minestar.illuminati.IlluminatiCore;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdWarpRename extends AbstractCommand {

    private final static String PUBLIC_RENAME_PERMISSION = "fifthelement.command.renamepublic";

    public cmdWarpRename(String syntax, String arguments, String node) {
        super(Core.NAME, syntax, arguments, node);
    }

    @Override
    public void execute(String[] args, Player player) {

        String oldName = args[0];
        String newName = args[1];
        // SEARCH WARP
        Warp warp = Core.warpManager.getWarp(oldName);
        // NO WARP FOUND
        if (warp == null) {
            PlayerUtils.sendError(player, pluginName, "Der Warp '" + oldName + "' existiert nicht!");
            return;
        }
        if (warp.isPublic() && !checkSpecialPermission(player, PUBLIC_RENAME_PERMISSION)) {
            PlayerUtils.sendError(player, pluginName, "Du kannst keine öffentlichen Warps umbenennen!");
            return;
        }
        // NEW WARP NAME ALREADY EXISTS
        if (Core.warpManager.isWarpExisting(newName)) {
            PlayerUtils.sendError(player, pluginName, "Der Warp '" + newName + "' existiert bereits!");
            return;
        }
        // PLAYER IS NOT ALLOWED TO RENAME
        if (!warp.canEdit(player)) {
            PlayerUtils.sendError(player, pluginName, "Du kannst den Warp '" + warp.getName() + "' nicht umbennen!");
            return;
        }
        // RENAME
        oldName = warp.getName();
        Core.warpManager.renameWarp(warp, newName);

        // INFORM PLAYER
        PlayerUtils.sendSuccess(player, pluginName, "Der Warp '" + oldName + "' heißt nun '" + warp.getName() + "'!");

        // PUBLIC WARPS - INFORM EVERYONE
        if (warp.isPublic())
            Bukkit.broadcastMessage("Der Warp '" + oldName + "' heißt nun '" + warp.getName() + "'!");
        // PRIVATE WARPS - INFORM GUESTS
        else {
            Set<String> guests = warp.getGuests();
            // NO GUESTS TO INFORM
            if (guests.size() == 0)
                return;
            Player guest = null;
            for (String guestName : guests) {
                guest = Bukkit.getPlayerExact(guestName);
                if (guest != null)
                    PlayerUtils.sendInfo(guest, "Der Spieler '" + player.getName() + "' hat den Warp '" + oldName + "' in '" + warp.getName() + "' umbenannt!");
            }
        }

        // FIRE STATISTC
        IlluminatiCore.handleStatistic(new WarpRenameStat(player.getName(), oldName, newName));
    }
}
