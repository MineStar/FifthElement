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

import org.bukkit.entity.Player;

import de.minestar.FifthElement.core.Core;
import de.minestar.FifthElement.data.Mine;
import de.minestar.FifthElement.statistics.mine.MineStat;
import de.minestar.minestarlibrary.stats.StatisticHandler;
import de.minestar.minestarlibrary.commands.AbstractExtendedCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdMine extends AbstractExtendedCommand {

    private static final String OTHER_MINE_PERMISSION = "fifthelement.command.othermine";

    public cmdMine(String syntax, String arguments, String node) {
        super(Core.NAME, syntax, arguments, node);
    }

    @Override
    public void execute(String[] args, Player player) {

        Mine mine = null;
        // OWN MINE
        if (args.length == 0) {
            mine = Core.mineManager.getMine(player.getName());
            if (mine == null) {
                PlayerUtils.sendError(player, pluginName, "Du hast keine Mine erstellt!");
                PlayerUtils.sendInfo(player, "Mit '/setMine' erstellst du dir eine Mine.");
                return;
            }
            // STORE EVENTUALLY LAST POSITION
            Core.backManager.handleTeleport(player);

            player.teleport(mine.getLocation());
            PlayerUtils.sendSuccess(player, pluginName, "Willkommen in deiner Mine.");
        }
        // MINE OF OTHER PLAYER
        else if (args.length == 1) {
            // CAN PLAYER USE OTHER MINES
            if (checkSpecialPermission(player, OTHER_MINE_PERMISSION)) {
                // FIND THE CORRECT PLAYER NAME
                String targetName = PlayerUtils.getCorrectPlayerName(args[0]);
                if (targetName == null) {
                    PlayerUtils.sendError(player, targetName, "Kann den Spieler '" + args[0] + "' nicht finden!");
                    return;
                }
                mine = Core.mineManager.getMine(targetName);
                if (mine == null) {
                    PlayerUtils.sendError(player, pluginName, "Der Spieler '" + targetName + "' hat keine Mine erstellt!");
                    return;
                }

                // STORE EVENTUALLY LAST POSITION
                Core.backManager.handleTeleport(player);

                player.teleport(mine.getLocation());
                PlayerUtils.sendSuccess(player, pluginName, "Mine von '" + mine.getOwner() + "'.");
            }
        }
        // WRONG COMMAND SYNTAX
        else {
            PlayerUtils.sendError(player, pluginName, getHelpMessage());
            return;
        }

        // FIRE STATISTIC
        StatisticHandler.handleStatistic(new MineStat(player.getName(), mine.getOwner()));
    }
}
