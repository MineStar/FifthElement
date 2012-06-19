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

import de.minestar.FifthElement.core.Core;
import de.minestar.FifthElement.data.Warp;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class WarpManager {

    private Map<String, Warp> warpMap;

    public WarpManager() {
        loadWarps();
    }

    private void loadWarps() {
        warpMap = Core.dbHandler.loadWarps();
        ConsoleUtils.printInfo(Core.NAME, "Loaded " + warpMap.size() + " Warps.");
    }
}
