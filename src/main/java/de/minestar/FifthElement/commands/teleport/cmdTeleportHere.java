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

package de.minestar.FifthElement.commands.teleport;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import de.minestar.FifthElement.core.Core;
import de.minestar.minestarlibrary.commands.AbstractExtendedCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdTeleportHere extends AbstractExtendedCommand {

    public cmdTeleportHere(String syntax, String arguments, String node) {
        super(Core.NAME, syntax, arguments, node);
    }

    @Override
    public void execute(String[] args, Player player) {
        Player target;
        List<String> sucessfullTeleports = new ArrayList<String>();
        String message = "Du wurdest zum Spieler '" + player.getName() + "' teleportiert!";

        for (String targetName : args) {
            // GET THE TARGET
            target = PlayerUtils.getOnlinePlayer(targetName);
            if (target == null)
                PlayerUtils.sendError(player, pluginName, "Spieler '" + args[0] + "' ist entweder offline oder kann nicht gefunden werden!");
            else {
                // TELEPORT TARGET TO PLAYER
                target.teleport(player);
                PlayerUtils.sendInfo(target, pluginName, message);

                sucessfullTeleports.add(target.getName());
            }
        }

        // SEND INFORMATION ABOUT TELEPORTED PLAYER
        if (!sucessfullTeleports.isEmpty()) {
            PlayerUtils.sendSuccess(player, pluginName, "Folgende Spieler wurden zu dir teleportiert:");
            PlayerUtils.sendInfo(player, sucessfullTeleports.toString());
        }
    }
}
