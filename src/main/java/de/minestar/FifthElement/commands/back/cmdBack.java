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

package de.minestar.fifthelement.commands.back;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.minestar.fifthelement.Core;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdBack extends AbstractCommand {

    public cmdBack(String syntax, String arguments, String node) {
        super(Core.NAME, syntax, arguments, node);
    }

    @Override
    public void execute(String[] args, Player player) {
        Location loc = Core.backManager.getLastPosition(player.getName());
        if (loc == null) {
            PlayerUtils.sendError(player, pluginName, "Es existiert keine letzte Position von dir!");
            return;
        }

        player.teleport(loc);
        PlayerUtils.sendSuccess(player, pluginName, "Du wurdest zu deiner letzten Position teleportiert!");
    }
}
