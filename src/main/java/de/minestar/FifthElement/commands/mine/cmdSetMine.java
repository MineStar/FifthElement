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

package de.minestar.FifthElement.commands.mine;

import org.bukkit.World;
import org.bukkit.entity.Player;

import de.minestar.FifthElement.core.Core;
import de.minestar.FifthElement.core.Settings;
import de.minestar.FifthElement.data.Mine;
import de.minestar.FifthElement.statistics.mine.SetMineStat;
import de.minestar.illuminati.IlluminatiCore;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdSetMine extends AbstractCommand {

    public cmdSetMine(String syntax, String arguments, String node) {
        super(Core.NAME, syntax, arguments, node);
    }

    @Override
    public void execute(String[] args, Player player) {

        World resourceWorld = Settings.getResourceWorld();
        // CHECK IF RESOURCE WORLD IS EXISTING
        if (resourceWorld == null) {
            PlayerUtils.sendError(player, pluginName, "Es ist keine Resourcenwelt eingestellt!");
            return;
        }
        // CHECK IF PLAYER IS IN RESOURCE WORLD
        if (!player.getWorld().equals(resourceWorld)) {
            PlayerUtils.sendError(player, pluginName, "Du kannst die Mine nur in der Resourcenwelt erstellen!");
            return;
        }

        Mine mine = Core.mineManager.getMine(player.getName());
        // CREATE NEW MINE
        if (mine == null) {
            Core.mineManager.createMine(player);
            PlayerUtils.sendSuccess(player, pluginName, "Du hast dir nun eine Mine erstellt!");
            PlayerUtils.sendInfo(player, "Mit '/mine' kommst du zu deiner Mine.");

            // FIRE STATISTIC
            IlluminatiCore.handleStatistic(new SetMineStat(player.getName(), false));
        }
        // UPDATE MINE POSITION
        else {
            Core.mineManager.moveMine(player, mine);
            PlayerUtils.sendSuccess(player, pluginName, "Deine Mine ist nun hier.");

            // FIRE STATISTIC
            IlluminatiCore.handleStatistic(new SetMineStat(player.getName(), true));
        }
    }
}
