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

package de.minestar.fifthelement.commands.mine;

import com.mojang.api.profiles.HttpProfileRepository;
import com.mojang.api.profiles.Profile;
import com.mojang.api.profiles.ProfileRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.minestar.fifthelement.Core;
import de.minestar.fifthelement.data.Mine;
import de.minestar.fifthelement.statistics.mine.MineStat;
import de.minestar.fifthelement.threads.EntityTeleportThread;
import de.minestar.minestarlibrary.stats.StatisticHandler;
import de.minestar.minestarlibrary.commands.AbstractExtendedCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdMine extends AbstractExtendedCommand
{
    private static final String OTHER_MINE_PERMISSION = "fifthelement.command.othermine";

    public cmdMine(String syntax, String arguments, String node)
    {
        super(Core.NAME, syntax, arguments, node);
    }

    @Override
    public void execute(String[] args, Player player)
    {
        Mine mine;
        // OWN MINE
        if (args.length == 0)
        {
            mine = Core.mineManager.getMine(player.getUniqueId());
            if (mine == null)
            {
                PlayerUtils.sendError(player, pluginName, "Du hast keine Mine erstellt!");
                PlayerUtils.sendInfo(player, "Mit '/setMine' erstellst du dir eine Mine.");
                return;
            }

            // handle vehicles
            if (player.isInsideVehicle())
            {
                if (player.getVehicle() instanceof Animals)
                {
                    // get the animal
                    Entity entity = player.getVehicle();
                    // leave it
                    player.leaveVehicle();
                    // load the chunk
                    mine.getLocation().getChunk().load(true);
                    // teleport the animal
                    entity.teleport(mine.getLocation());
                    // create a Thread
                    EntityTeleportThread thread = new EntityTeleportThread(player.getName(), entity);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Core.getPlugin(), thread, 10L);
                }
                else {
                    PlayerUtils.sendError(player, pluginName, "Du kannst dich mit Fahrzeug nicht teleportieren!");
                    return;
                }
            }
            // STORE EVENTUALLY LAST POSITION
            Core.backManager.handleTeleport(player);
            player.teleport(mine.getLocation());
            PlayerUtils.sendSuccess(player, pluginName, "Willkommen in deiner Mine.");
        }
        // MINE OF OTHER PLAYER
        else if (args.length == 1)
        {
            // CAN PLAYER USE OTHER MINES
            if (checkSpecialPermission(player, OTHER_MINE_PERMISSION))
            {
                ProfileRepository repository = new HttpProfileRepository("minecraft");
                Profile target = repository.findProfileByName(args[0]);
                if (target == null)
                {
                    PlayerUtils.sendError(player, pluginName, "Kann den Spieler '" + args[0] + "' nicht finden!");
                    return;
                }
                mine = Core.mineManager.getMine(target.getUUID());
                if (mine == null)
                {
                    PlayerUtils.sendError(player, pluginName, "Der Spieler '" + target.getName() + "' hat keine Mine erstellt!");
                    return;
                }
                // handle vehicles
                if (player.isInsideVehicle())
                {
                    if (player.getVehicle() instanceof Animals)
                    {
                        if (!mine.getLocation().getWorld().getName().equalsIgnoreCase(player.getWorld().getName())) {
                            PlayerUtils.sendError(player, pluginName, "Tiere können die Welt nicht wechseln!");
                            return;
                        }
                        // get the animal
                        Entity entity = player.getVehicle();
                        // leave it
                        player.leaveVehicle();
                        // load the chunk
                        mine.getLocation().getChunk().load(true);
                        // teleport the animal
                        entity.teleport(mine.getLocation());
                        // create a Thread
                        EntityTeleportThread thread = new EntityTeleportThread(player.getName(), entity);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Core.getPlugin(), thread, 10L);
                    }
                    else {
                        PlayerUtils.sendError(player, pluginName, "Du kannst dich mit Fahrzeug nicht teleportieren!");
                        return;
                    }
                }
                // STORE EVENTUALLY LAST POSITION
                Core.backManager.handleTeleport(player);
                player.teleport(mine.getLocation());
                PlayerUtils.sendSuccess(player, pluginName, "Mine von '" + mine.getOwnerName() + "'.");
            } else return;
        }
        // WRONG COMMAND SYNTAX
        else {
            PlayerUtils.sendError(player, pluginName, getHelpMessage());
            return;
        }
        // FIRE STATISTIC
        StatisticHandler.handleStatistic(new MineStat(player.getName(), mine.getOwnerName()));
    }
}
