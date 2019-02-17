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

import java.util.Map;

import org.bukkit.entity.Player;

import de.minestar.fifthelement.Core;
import de.minestar.fifthelement.data.Mine;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class MineManager {

    private Map<String, Mine> mineMap;

    public MineManager() {
        loadMines();
    }

    // **************************
    // LOADING AND INITIALIZATION
    // **************************

    private void loadMines() {
        mineMap = Core.dbHandler.loadMines();
        ConsoleUtils.printInfo(Core.NAME, "Loaded " + mineMap.size() + " Mines.");
    }

    public Mine getMine(String playerName) {
        return mineMap.get(playerName.toLowerCase());
    }

    public void transferMine(String oldPlayer, String newPlayer) {
        Core.dbHandler.transferMine(oldPlayer, newPlayer);
        loadMines();
    }

    // **************************
    // MANIPULATE CURRENT MINES
    // **************************

    public void createMine(Player player) {
        Mine mine = new Mine(player);
        mineMap.put(player.getName().toLowerCase(), mine);
        Core.dbHandler.addMine(mine);
    }

    public void moveMine(Player player, Mine mine) {
        mine.updateLocation(player.getLocation());
        Core.dbHandler.updateMineLocation(mine);
    }
}