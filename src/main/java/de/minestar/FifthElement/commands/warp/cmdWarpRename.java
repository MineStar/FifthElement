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

import java.util.Collection;

import de.minestar.minestarlibrary.protection.Guest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.minestar.fifthelement.Core;
import de.minestar.fifthelement.Settings;
import de.minestar.fifthelement.data.Warp;
import de.minestar.fifthelement.statistics.warp.WarpRenameStat;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.stats.StatisticHandler;
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
        // NEW NAME IS VALID NAME
        if (!Core.warpManager.isValidName(newName)) {
            PlayerUtils.sendError(player, pluginName, "Der Warpname '" + newName + "' ist ungültig!");
            PlayerUtils.sendError(player, pluginName, "Der Warpname muss min. " + Settings.getMinWarpnameSize() + " Zeichen und maximal " + Settings.getMaxWarpnameSize() + " Zeichen lang sein.");
            return;
        }

        // IS KEY WORD ( SUB COMMAND OF WARP)
        if (Core.warpManager.isKeyWord(newName)) {
            PlayerUtils.sendError(player, pluginName, "Der Warpname '" + newName + "' ist ein Schlüsselwort und kann nicht als Warpname benutzt werden.");
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
            Collection<Guest> guests = warp.getGuests();
            // NO GUESTS TO INFORM
            if (guests.size() == 0)
                return;
            Player guestPlayer;
            for (Guest guest : guests) {
                guestPlayer = Bukkit.getPlayerExact(guest.getName());
                if (guestPlayer != null) {
                    PlayerUtils.sendInfo(guestPlayer, "Der Spieler '" + player.getName() + "' hat den Warp '" + oldName + "' in '" + warp.getName() + "' umbenannt!");
                }
            }
        }

        // FIRE STATISTC
        StatisticHandler.handleStatistic(new WarpRenameStat(player.getName(), oldName, newName));
    }
}
