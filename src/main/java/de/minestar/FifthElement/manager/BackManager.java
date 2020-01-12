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

package de.minestar.fifthelement.manager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import de.outinetworks.permissionshub.PermissionUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;


import de.minestar.fifthelement.Settings;

public class BackManager
{
    private Map<UUID, LinkedList<Location>> lastPositionMap;

    public BackManager()
    {
        lastPositionMap = new HashMap<>();
    }

    private static final String BACK_PERMISSION = "fifthelement.command.back";

    public void handleTeleport(Player player)
    {
        if (PermissionUtils.playerCanUseCommand(player, BACK_PERMISSION)) storeLastPosition(player);
    }

    private void storeLastPosition(Player player)
    {
        LinkedList<Location> lastPositions = lastPositionMap.computeIfAbsent(player.getUniqueId(), k -> new LinkedList<>());
        // STORE ONLY LAST X POSITION
        if (lastPositions.size() > Settings.getBackPositionLimit()) lastPositions.removeFirst();
        lastPositions.addLast(player.getLocation());
    }

    public Location getLastPosition(UUID playerUUID)
    {
        LinkedList<Location> list = lastPositionMap.get(playerUUID);
        if (list == null || list.isEmpty()) return null;
        else return list.removeLast();
    }
}
