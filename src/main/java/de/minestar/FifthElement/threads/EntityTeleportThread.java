/*
 * Copyright (C) 2013 MineStar.de 
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

package de.minestar.fifthelement.threads;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class EntityTeleportThread implements Runnable {

    private final String playerName;
    private final Entity entity;

    public EntityTeleportThread(String playerName, Entity entity) {
        this.playerName = playerName;
        this.entity = entity;
    }

    @Override
    public void run() {
        if (playerName != null && entity != null) {
            Player currentPlayer = Bukkit.getServer().getPlayer(this.playerName);
            if (currentPlayer != null) {
                entity.addPassenger(currentPlayer);
            }
        }
    }

}
