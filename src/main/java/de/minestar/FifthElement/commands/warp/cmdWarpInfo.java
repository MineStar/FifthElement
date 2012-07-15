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

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.minestar.FifthElement.core.Core;
import de.minestar.FifthElement.data.Warp;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.ChatUtils;

public class cmdWarpInfo extends AbstractCommand {

    public cmdWarpInfo(String syntax, String arguments, String node) {
        super(Core.NAME, syntax, arguments, node);
    }

    @Override
    public void execute(String[] args, Player player) {
        warpInfo(args, player);
    }

    @Override
    public void execute(String[] args, ConsoleCommandSender console) {
        warpInfo(args, console);
    }

    private void warpInfo(String[] args, CommandSender sender) {
        String warpName = args[0];
        // SEARCH WARP
        Warp warp = Core.warpManager.getWarp(warpName);
        // NO WARP FOUND
        if (warp == null) {
            ChatUtils.writeError(sender, pluginName, "Der Warp '" + warpName + "' existiert nicht!");
            return;
        }
        // PLAYER IS NOT ALLOWED TO VIEW THE WARP
        if ((sender instanceof Player) && !warp.canUse((Player) sender)) {
            ChatUtils.writeError(sender, pluginName, "Du kannst keine Information über den Warp '" + warp.getName() + "' abrufen!");
            return;
        }

        displayInformation(warp, sender);
    }

    private void displayInformation(Warp warp, CommandSender sender) {

        // HEAD
        ChatUtils.writeInfo(sender, pluginName, "Informationen über Warp '" + warp.getName() + "':");

        // OWNER
        ChatUtils.writeInfo(sender, "Erstellt von: " + warp.getOwner());

        // IS PUBLIC OR PRIVATE
        if (warp.isPublic())
            ChatUtils.writeInfo(sender, "Öffentlich");
        else {
            ChatUtils.writeInfo(sender, "Privat");
            if (warp.getGuests().size() > 0) {
                ChatUtils.writeInfo(sender, "Gästeliste:");
                ChatUtils.writeInfo(sender, warp.getGuestList());
            }
        }

        // POSITION AND DISTANCE
        Location loc = warp.getLocation();
        ChatUtils.writeInfo(sender, String.format("X: %d Y: %d Z: %d Welt: %s", loc.getBlockX(), loc.getBlockY(), loc.getBlockY(), loc.getWorld().getName()));
        if (sender instanceof Player) {
            Location loc2 = ((Player) sender).getLocation();
            ChatUtils.writeInfo(sender, "Entfernung zum Warp = " + (int) (loc.distance(loc2)) + "m");
        }
    }
}
