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
import de.minestar.FifthElement.statistics.PrivateWarpStat;
import de.minestar.FifthElement.statistics.PublicWarpStat;
import de.minestar.FifthElement.statistics.TeleportHereStat;
import de.minestar.FifthElement.statistics.TeleportPlayerToStat;
import de.minestar.FifthElement.statistics.TeleportToStat;
import de.minestar.FifthElement.statistics.WarpCreateStat;
import de.minestar.FifthElement.statistics.WarpInviteStat;
import de.minestar.FifthElement.statistics.WarpMoveStat;
import de.minestar.FifthElement.statistics.WarpRenameStat;
import de.minestar.FifthElement.statistics.WarpToStat;
import de.minestar.FifthElement.statistics.WarpUninviteStat;
import de.minestar.illuminati.IlluminatiCore;
import de.minestar.minestarlibrary.AbstractCore;
import de.minestar.minestarlibrary.annotations.UseStatistic;

@UseStatistic
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
    protected boolean registerStatistics() {

        IlluminatiCore.registerStatistic(TeleportHereStat.class);
        IlluminatiCore.registerStatistic(TeleportToStat.class);
        IlluminatiCore.registerStatistic(TeleportPlayerToStat.class);

        IlluminatiCore.registerStatistic(PrivateWarpStat.class);
        IlluminatiCore.registerStatistic(PublicWarpStat.class);
        IlluminatiCore.registerStatistic(WarpCreateStat.class);
        IlluminatiCore.registerStatistic(WarpInviteStat.class);
        IlluminatiCore.registerStatistic(WarpMoveStat.class);
        IlluminatiCore.registerStatistic(WarpRenameStat.class);
        IlluminatiCore.registerStatistic(WarpToStat.class);
        IlluminatiCore.registerStatistic(WarpUninviteStat.class);

        return true;
    }

    @Override
    protected boolean commonDisable() {
        dbHandler.closeConnection();

        return !dbHandler.hasConnection();
    }
}
