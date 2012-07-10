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

package de.minestar.FifthElement.commands.home;

import java.text.DecimalFormat;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.minestar.FifthElement.core.Core;
import de.minestar.FifthElement.data.Home;
import de.minestar.FifthElement.statistics.HomeInfoStat;
import de.minestar.illuminati.IlluminatiCore;
import de.minestar.minestarlibrary.commands.AbstractExtendedCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdHomeInfo extends AbstractExtendedCommand {

    private static final String OTHER_HOME_INFO_PERMISSION = "fifthelement.command.otherhomeinfo";

    public cmdHomeInfo(String syntax, String arguments, String node) {
        super(Core.NAME, syntax, arguments, node);
    }

    @Override
    public void execute(String[] args, Player player) {
        Home home = null;
        // INFORMATION ABOUT OWN HOME
        if (args.length == 0) {
            home = Core.homeManager.getHome(player.getName());
            // HAS NO HOME YET
            if (home == null) {
                PlayerUtils.sendError(player, pluginName, "Du hast kein Zuhause!");
                return;
            }
        }
        // INFORMATION ABOUT OTHER HOMES
        else if (args.length == 1) {
            // CAN USE THE COMMAND
            if (checkSpecialPermission(player, OTHER_HOME_INFO_PERMISSION)) {
                // GET CORRECT PLAYER NAME
                String targetName = PlayerUtils.getCorrectPlayerName(args[0]);
                // PLAYER NOT FOUND
                if (targetName == null) {
                    PlayerUtils.sendError(player, pluginName, "Der Spieler '" + args[0] + "' wurde nicht gefunden!");
                    return;
                }
                home = Core.homeManager.getHome(targetName);
                // TARGET HAS NO HOME YET
                if (home == null) {
                    PlayerUtils.sendError(player, pluginName, "Der Spieler '" + targetName + "' hat kein Zuhause!");
                    return;
                }
            }
        }
        // WRONG SYNTAX
        else {
            PlayerUtils.sendError(player, pluginName, getHelpMessage());
            return;
        }

        displayHomeInformation(player, home);

        // FIRE STATISTICS
        IlluminatiCore.handleStatistic(new HomeInfoStat(player.getName(), home.getOwner()));
    }
    // FORMATTER TO DISPLAY ONLY ONE DIGIT
    private final static DecimalFormat dF = new DecimalFormat("#0.0");

    private void displayHomeInformation(Player caller, Home home) {

        Location homeLoc = home.getLocation();
        double distance = homeLoc.distance(caller.getLocation());

        PlayerUtils.sendInfo(caller, pluginName, "Information über das Zuhause von " + home.getOwner() + ":");
        PlayerUtils.sendInfo(caller, "Position: " + homeLoc.getBlockX() + " " + homeLoc.getBlockY() + " " + homeLoc.getBlockZ() + " in " + homeLoc.getWorld().getName());
        PlayerUtils.sendInfo(caller, "Entfernung von hier: " + dF.format(distance) + "m");
    }
}
