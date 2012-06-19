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

package de.minestar.FifthElement.core;

import de.minestar.FifthElement.database.DatabaseHandler;
import de.minestar.FifthElement.manager.WarpManager;
import de.minestar.minestarlibrary.AbstractCore;

public class Core extends AbstractCore {

    public static final String NAME = "FifthElement";

    /* MANAGER */
    public static DatabaseHandler dbHandler;
    public static WarpManager warpManager;

    public Core() {
        super(NAME);
    }

    @Override
    protected boolean createManager() {
        dbHandler = new DatabaseHandler(getDataFolder());
        if (!dbHandler.hasConnection())
            return false;

        warpManager = new WarpManager();

        return true;
    }

    @Override
    protected boolean commonDisable() {
        dbHandler.closeConnection();

        return !dbHandler.hasConnection();
    }
}
