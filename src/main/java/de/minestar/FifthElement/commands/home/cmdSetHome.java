/*
 * Copyright (C) 2012 MineStar.de 
 * 
 * This file is part of fifthelement.
 * 
 * fifthelement is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * fifthelement is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with fifthelement.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.minestar.fifthelement.commands.home;

import org.bukkit.entity.Player;

import de.minestar.fifthelement.Core;
import de.minestar.fifthelement.data.Home;
import de.minestar.fifthelement.statistics.home.SetHomeStat;
import de.minestar.minestarlibrary.stats.StatisticHandler;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdSetHome extends AbstractCommand {

    public cmdSetHome(String syntax, String arguments, String node) {
        super(Core.NAME, syntax, arguments, node);
    }

    @Override
    public void execute(String[] args, Player player) {
        Home home = Core.homeManager.getHome(player.getName());
        // CREATE NEW HOME
        if (home == null) {
            Core.homeManager.createHome(player);
            PlayerUtils.sendSuccess(player, pluginName, "Du hast dir nun ein Zuhause erstellt!");
            PlayerUtils.sendInfo(player, "Mit '/home' kommst du nach Hause.");

            // FIRE STATISTIC
            StatisticHandler.handleStatistic(new SetHomeStat(player.getName(), false));
        }
        // UPDATE HOME POSITION
        else {
            Core.homeManager.moveHome(player, home);
            PlayerUtils.sendSuccess(player, pluginName, "Dein Zuhause ist nun hier.");

            // FIRE STATISTIC
            StatisticHandler.handleStatistic(new SetHomeStat(player.getName(), true));

        }
    }
}
