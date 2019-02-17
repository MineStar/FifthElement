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

package de.minestar.fifthelement.commands.teleport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.minestar.fifthelement.Core;
import de.minestar.fifthelement.threads.EntityTeleportThread;
import de.minestar.minestarlibrary.commands.AbstractExtendedCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdTeleportTo extends AbstractExtendedCommand {

    // PERMISSIONS
    private final static String PERMISSION_TELEPORT_TO = "fifthelement.command.teleportto";
    private final static String PERMISSION_TELEPORT_PLAYER_TO_PLAYER = "fifthelement.command.teleportplayertoplayer";
    private final static String PERMISSION_TELEPORT_TO_COORDS = "fifthelement.command.teleporttocoords";

    public cmdTeleportTo(String syntax, String arguments, String node) {
        super(Core.NAME, syntax, arguments, node);
    }

    @Override
    public void execute(String[] args, Player player) {
        // TELEPORT THE COMMAND CALLER TO THE TARGET
        if (args.length == 1) {
            teleportTo(args, player);
        }
        // TELEPORT A PLAYER TO A SECOND PLAYER
        if (args.length == 2) {
            teleportPlayerToPlayer(args, player);
        }
        // TELEPORT THE COMMAND CALLER TO COORDINATES
        if (args.length > 2) {
            teleportToCoords(args, player);
        }
    }

    private void teleportTo(String[] args, Player player) {

        if (!checkSpecialPermission(player, PERMISSION_TELEPORT_TO))
            return;

        // GET THE TARGET
        Player target = PlayerUtils.getOnlinePlayer(args[0]);
        if (target == null) {
            PlayerUtils.sendError(player, pluginName, "Spieler '" + args[0] + "' ist entweder offline oder kann nicht gefunden werden!");
            return;
        }

        // handle vehicles
        if (player.isInsideVehicle()) {
            if (player.getVehicle() instanceof Animals) {

                if (!target.getWorld().getName().equalsIgnoreCase(player.getWorld().getName())) {
                    PlayerUtils.sendError(player, pluginName, "Tiere können die Welt nicht wechseln!");
                    return;
                }
                // get the animal
                Entity entity = player.getVehicle();

                // leave it
                player.leaveVehicle();

                // load the chunk
                player.getLocation().getChunk().load(true);

                // teleport the animal
                entity.teleport(target.getLocation());

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

        // TELEPORT THE COMMAND CALLER TO TARGET
        player.teleport(target);
        PlayerUtils.sendSuccess(player, "Du bist nun beim Spieler '" + target.getName() + "'!");
    }

    private void teleportPlayerToPlayer(String[] args, Player player) {

        if (!checkSpecialPermission(player, PERMISSION_TELEPORT_PLAYER_TO_PLAYER))
            return;

        // GET PLAYER TO TELEPORT
        Player playerToTeleport = PlayerUtils.getOnlinePlayer(args[0]);
        if (playerToTeleport == null) {
            PlayerUtils.sendError(player, pluginName, "Spieler '" + args[0] + "' ist entweder offline oder kann nicht gefunden werden!");
            return;
        }
        // GET TARGET PLAYER
        Player target = PlayerUtils.getOnlinePlayer(args[1]);
        if (target == null) {
            PlayerUtils.sendError(player, pluginName, "Spieler '" + args[1] + "' ist entweder offline oder kann nicht gefunden werden!");
            return;
        }

        // handle vehicles
        if (player.isInsideVehicle()) {
            if (player.getVehicle() instanceof Animals) {

                if (!target.getWorld().getName().equalsIgnoreCase(player.getWorld().getName())) {
                    PlayerUtils.sendError(player, pluginName, "Tiere können die Welt nicht wechseln!");
                    return;
                }
                // get the animal
                Entity entity = player.getVehicle();

                // leave it
                player.leaveVehicle();

                // load the chunk
                target.getLocation().getChunk().load(true);

                // teleport the animal
                entity.teleport(target.getLocation());

                // create a Thread
                EntityTeleportThread thread = new EntityTeleportThread(player.getName(), entity);
                Bukkit.getScheduler().scheduleSyncDelayedTask(Core.getPlugin(), thread, 10L);
            } else {
                PlayerUtils.sendError(player, pluginName, "Du kannst dich mit Fahrzeug nicht teleportieren!");
                return;
            }
        }

        // STORE EVENTUALLY LAST POSITION
        Core.backManager.handleTeleport(playerToTeleport);

        // TELEPORT PLAYER TO TARGET
        playerToTeleport.teleport(target);

        // INFORMATION FOR PLAYERS
        PlayerUtils.sendSuccess(playerToTeleport, pluginName, "Du wurdest zum Spieler '" + target.getName() + "' teleportiert!");
        PlayerUtils.sendSuccess(target, pluginName, "Der Spieler '" + playerToTeleport.getName() + "' wurde zu dir teleportiert!");

        // INFORMATION FOR COMMAND EXECUTER
        PlayerUtils.sendSuccess(player, pluginName, "Der Spieler '" + playerToTeleport.getName() + "' wurde zu '" + target.getName() + "' teleportiert!");
    }

    private void teleportToCoords(String[] args, Player player) {

        if (!checkSpecialPermission(player, PERMISSION_TELEPORT_TO_COORDS))
            return;

        // GET THE COORDINATES
        double x;
        double y;
        double z;
        try {
            x = Double.parseDouble(args[0]);
            y = Double.parseDouble(args[1]);
            z = Double.parseDouble(args[2]);
        } catch (Exception e) {
            PlayerUtils.sendInfo(player, pluginName, "/tp X Y Z ");
            return;
        }

        // GET THE WORLD
        World targetWorld;
        if (args.length == 4) {
            targetWorld = player.getServer().getWorld(args[4]);
            if (targetWorld == null) {
                PlayerUtils.sendError(player, pluginName, "Die Welt '" + targetWorld + "' existiert nicht!");
                return;
            }
        } else
            targetWorld = player.getWorld();

        Location target = new Location(targetWorld, x, y, z);

        // handle vehicles
        if (player.isInsideVehicle()) {
            if (player.getVehicle() instanceof Animals) {
                if (!target.getWorld().getName().equalsIgnoreCase(player.getWorld().getName())) {
                    PlayerUtils.sendError(player, pluginName, "Tiere können die Welt nicht wechseln!");
                    return;
                }
                // get the animal
                Entity entity = player.getVehicle();

                // leave it
                player.leaveVehicle();

                // load the chunk
                target.getChunk().load(true);

                // teleport the animal
                entity.teleport(target);

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

        // TELEPORT TO COORDINATES
        player.teleport(target);
        PlayerUtils.sendSuccess(player, pluginName, "Du wurdest erfolgreich zur der Position X=" + x + " Y=" + y + " Z=" + z + " in der Welt " + targetWorld.getName());

    }
}
