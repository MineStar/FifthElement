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

import java.util.Map;

import org.bukkit.entity.Player;

import de.minestar.FifthElement.core.Core;
import de.minestar.FifthElement.data.Home;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class HomeManager {

    private Map<String, Home> homeMap;

    public HomeManager() {
        loadHomes();
    }

    // **************************
    // LOADING AND INITIALIZATION
    // **************************

    private void loadHomes() {
        homeMap = Core.dbHandler.loadHomes();
        ConsoleUtils.printInfo(Core.NAME, "Loaded " + homeMap.size() + " Homes.");
    }

    public Home getHome(String playerName) {
        return homeMap.get(playerName.toLowerCase());
    }

    public void transferHome(String oldPlayer, String newPlayer) {
        Core.dbHandler.transferHome(oldPlayer, newPlayer);
        loadHomes();
    }

    // **************************
    // MANIPULATE CURRENT HOMES
    // **************************

    public void createHome(Player player) {
        Home home = new Home(player);
        homeMap.put(player.getName().toLowerCase(), home);
        Core.dbHandler.addHome(home);
    }

    public void moveHome(Player player, Home home) {
        home.updateLocation(player.getLocation());
        Core.dbHandler.updateHomeLocation(home);
    }
}