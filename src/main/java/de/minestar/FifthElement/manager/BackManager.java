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

package de.minestar.FifthElement.manager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.bukkit.gemo.utils.UtilPermissions;

import de.minestar.FifthElement.core.Settings;

public class BackManager {

    private Map<String, LinkedList<Location>> lastPositionMap;

    public BackManager() {
        lastPositionMap = new HashMap<String, LinkedList<Location>>();
    }

    private static final String BACK_PERMISSION = "fifthelement.command.back";

    public void handleTeleport(Player player) {
        if (UtilPermissions.playerCanUseCommand(player, BACK_PERMISSION))
            storeLastPosition(player);
    }

    private void storeLastPosition(Player player) {
        String playerName = player.getName().toLowerCase();
        LinkedList<Location> lastPositions = lastPositionMap.get(playerName);
        if (lastPositions == null) {
            lastPositions = new LinkedList<Location>();
            lastPositionMap.put(playerName, lastPositions);
        }

        // STORE ONLY LAST X POSITION
        if (lastPositions.size() > Settings.getBackPositionLimit())
            lastPositions.removeFirst();

        lastPositions.addLast(player.getLocation());
    }

    public Location getLastPosition(String player) {
        LinkedList<Location> list = lastPositionMap.get(player.toLowerCase());
        if (list == null || list.isEmpty())
            return null;
        else
            return list.removeLast();
    }

}
