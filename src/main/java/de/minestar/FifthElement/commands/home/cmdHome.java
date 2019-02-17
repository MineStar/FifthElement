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

import org.bukkit.Bukkit;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.minestar.fifthelement.Core;
import de.minestar.fifthelement.data.Home;
import de.minestar.fifthelement.statistics.home.HomeStat;
import de.minestar.fifthelement.threads.EntityTeleportThread;
import de.minestar.minestarlibrary.stats.StatisticHandler;
import de.minestar.minestarlibrary.commands.AbstractExtendedCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdHome extends AbstractExtendedCommand {

    private static final String OTHER_HOME_PERMISSION = "fifthelement.command.otherhome";

    public cmdHome(String syntax, String arguments, String node) {
        super(Core.NAME, syntax, arguments, node);
    }

    @Override
    public void execute(String[] args, Player player) {

        Home home;
        // OWN HOME
        if (args.length == 0) {
            home = Core.homeManager.getHome(player.getName());
            if (home == null) {
                PlayerUtils.sendError(player, pluginName, "Du hast kein Zuhause erstellt!");
                PlayerUtils.sendInfo(player, "Mit '/setHome' erstellst du dir ein Zuhause.");
                return;
            }
            // TELEPORT PLAYER THE TO WARP

            // handle vehicles
            if (player.isInsideVehicle()) {
                if (player.getVehicle() instanceof Animals) {
                    // get the animal
                    Entity entity = player.getVehicle();

                    // leave it
                    player.leaveVehicle();

                    // load the chunk
                    home.getLocation().getChunk().load(true);

                    // teleport the animal
                    entity.teleport(home.getLocation());

                    // create a Thread
                    EntityTeleportThread thread = new EntityTeleportThread(player.getName(), entity);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Core.getPlugin(), thread, 10L);
                } else {
                    PlayerUtils.sendError(player, pluginName, "Du kannst dich mit Fahrzeug nicht teleportieren!");
                    return;
                }
            }

            // STORE EVENTUALLY LAST POSITION
            Core.backManager.handleTeleport(player);

            player.teleport(home.getLocation());
            PlayerUtils.sendSuccess(player, pluginName, "Willkommen zu Hause.");
        }
        // HOME OF OTHER PLAYER
        else if (args.length == 1) {
            // CAN PLAYER USE OTHER HOMES
            if (checkSpecialPermission(player, OTHER_HOME_PERMISSION)) {
                // FIND THE CORRECT PLAYER NAME
                String targetName = PlayerUtils.getCorrectPlayerName(args[0]);
                if (targetName == null) {
                    PlayerUtils.sendError(player, targetName, "Kann den Spieler '" + args[0] + "' nicht finden!");
                    return;
                }
                home = Core.homeManager.getHome(targetName);
                if (home == null) {
                    PlayerUtils.sendError(player, pluginName, "Der Spieler '" + targetName + "' hat kein Zuhause erstellt!");
                    return;
                }

                // handle vehicles
                if (player.isInsideVehicle()) {
                    if (player.getVehicle() instanceof Animals) {
                        if (!home.getLocation().getWorld().getName().equalsIgnoreCase(player.getWorld().getName())) {
                            PlayerUtils.sendError(player, pluginName, "Tiere können die Welt nicht wechseln!");
                            return;
                        }
                        // get the animal
                        Entity entity = player.getVehicle();

                        // leave it
                        player.leaveVehicle();

                        // load the chunk
                        home.getLocation().getChunk().load(true);

                        // teleport the animal
                        entity.teleport(home.getLocation());

                        // create a Thread
                        EntityTeleportThread thread = new EntityTeleportThread(player.getName(), entity);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Core.getPlugin(), thread, 10L);
                    } else {
                        PlayerUtils.sendError(player, pluginName, "Du kannst dich mit Fahrzeug nicht teleportieren!");
                        return;
                    }
                }

                // STORE EVENTUALLY LAST POSITION
                Core.backManager.handleTeleport(player);

                player.teleport(home.getLocation());
                PlayerUtils.sendSuccess(player, pluginName, "Haus von '" + home.getOwner() + "'.");
            } else
                return;
        }
        // WRONG COMMAND SYNTAX
        else {
            PlayerUtils.sendError(player, pluginName, getHelpMessage());
            return;
        }

        // FIRE STATISTIC
        StatisticHandler.handleStatistic(new HomeStat(player.getName(), home.getOwner()));
    }
}
