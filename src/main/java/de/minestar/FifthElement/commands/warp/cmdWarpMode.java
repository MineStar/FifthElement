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

package de.minestar.FifthElement.commands.warp;

import org.bukkit.entity.Player;

import de.minestar.FifthElement.core.Core;
import de.minestar.FifthElement.data.Warp;
import de.minestar.FifthElement.statistics.warp.WarpModeStat;
import de.minestar.minestarlibrary.stats.StatisticHandler;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdWarpMode extends AbstractCommand {

    private final static String USE_MODE_ALL_KEYWORD = "ALL";
    private final static String USE_MODE_SIGN_KEYWORD = "SIGN";
    private final static String USE_MODE_COMMAND_KEYWORD = "COMMAND";

    public cmdWarpMode(String syntax, String arguments, String node) {
        super(Core.NAME, syntax, arguments, node);
    }

    @Override
    public void execute(String[] args, Player player) {
        Warp warp = Core.warpManager.getWarp(args[0]);
        // WARP NOT FOUND
        if (warp == null) {
            PlayerUtils.sendError(player, pluginName, "Warp '" + args[0] + "' existiert nicht!");
            return;
        }
        // CAN'T EDIT USE MODE
        if (!warp.canEdit(player)) {
            PlayerUtils.sendError(player, pluginName, "Du kannst den Benutzungsmodus des Warp '" + warp.getName() + "' nicht ändern!");
            return;
        }
        // GET USE MODE
        byte useMode = 0;
        byte oldUseMode = warp.getUseMode();

        // WARP CAN USED BY SIGNS AND COMMANDS
        if (args[1].equalsIgnoreCase(USE_MODE_ALL_KEYWORD)) {
            useMode = (Warp.COMMAND_USEMODE | Warp.SIGN_USEMODE);
            PlayerUtils.sendSuccess(player, pluginName, "Der Warp '" + warp.getName() + "' kann per Befehl und per Schild genutzt werden.");
        }
        // WARP CAN USED BY COMMANDS
        else if (args[1].equalsIgnoreCase(USE_MODE_COMMAND_KEYWORD)) {
            useMode = Warp.COMMAND_USEMODE;
            PlayerUtils.sendSuccess(player, pluginName, "Der Warp '" + warp.getName() + "' kann per Befehl genutzt werden.");
        }
        // WARP CAN USED BY SIGNS
        else if (args[1].equalsIgnoreCase(USE_MODE_SIGN_KEYWORD)) {
            useMode = Warp.SIGN_USEMODE;
            PlayerUtils.sendSuccess(player, pluginName, "Der Warp '" + warp.getName() + "' kann per Schild genutzt werden.");
        }
        // UNKNOWN PARAMETER
        else {
            PlayerUtils.sendError(player, pluginName, "Unbekannter Benutzungsmodus! Bitte benutze folgende Angaben:");
            PlayerUtils.sendError(player, USE_MODE_ALL_KEYWORD + ", " + USE_MODE_COMMAND_KEYWORD + ", " + USE_MODE_SIGN_KEYWORD);
            return;
        }

        // PERSIST
        Core.warpManager.setUseMode(warp, useMode);

        // FIRE STATISTIC
        StatisticHandler.handleStatistic(new WarpModeStat(player.getName(), oldUseMode, warp));
    }
}
