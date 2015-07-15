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
import de.minestar.FifthElement.statistics.warp.WarpInviteStat;
import de.minestar.minestarlibrary.commands.AbstractExtendedCommand;
import de.minestar.minestarlibrary.stats.StatisticHandler;
import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.data.guests.Group;
import de.minestar.moneypit.data.guests.GroupManager;
import de.minestar.moneypit.data.guests.GuestHelper;

public class cmdWarpInvite extends AbstractExtendedCommand {

    public cmdWarpInvite(String syntax, String arguments, String node) {
        super(Core.NAME, syntax, arguments, node);
    }

    @Override
    public void execute(String[] args, Player player) {

        // SEARCH FOR WARP
        String warpName = args[0];
        Warp warp = Core.warpManager.getWarp(warpName);
        // NO WARP FOUND
        if (warp == null) {
            PlayerUtils.sendError(player, pluginName, "Der Warp '" + warpName + "' wurde nicht gefunden!");
            return;
        }
        // PLAYER CAN'T INVITE OTHER PLAYER
        if (!warp.canEdit(player)) {
            PlayerUtils.sendError(player, pluginName, "Du kannst zu dem Warp '" + warp.getName() + "' niemanden einladen!");
            return;
        }

        // INVITE PERSON
        String targetName = null;
        Player target = null;
        for (int i = 1; i < args.length; ++i) {
            if (args[i].startsWith(GuestHelper.GROUP_PREFIX)) {
                if (args[i].matches("(" + GuestHelper.GROUP_PREFIX + ")([a-zA-Z0-9_])*")) {
                    Group group = GroupManager.getGroup(player.getName(), args[i]);
                    if (group == null) {
                        PlayerUtils.sendError(player, "Die Gruppe '" + args[i] + "' wurde nicht gefunden!");
                        continue;
                    }

                    // targetName is the groupName
                    targetName = group.getName();
                    String canonicalName = targetName.replaceFirst(GuestHelper.GROUP_PREFIX, "");

                    // GROUP IS NEW GUEST
                    if (!warp.isGuest(targetName)) {
                        Core.warpManager.addGuest(warp, targetName);
                        PlayerUtils.sendSuccess(player, "Gruppe '" + canonicalName + "' wurde zum Warp '" + warp.getName() + "' eingeladen.");
                        // FIRE STATISTIC
                        StatisticHandler.handleStatistic(new WarpInviteStat(warp.getName(), player.getName(), targetName));
                    } else {
                        // GROUP WAS ALREADY INVITED
                        PlayerUtils.sendError(player, "Die Gruppe '" + canonicalName + "' kann bereits den Warp '" + warp.getName() + "' benutzen.");
                    }

                    // INFORM PLAYERS
                    for (String playerName : group.getPlayerList()) {
                        target = PlayerUtils.getOnlinePlayer(playerName);
                        if (target != null) {
                            PlayerUtils.sendInfo(target, pluginName, "Du wurdest von '" + player.getName() + "' zum Warp '" + warp.getName() + "' eingeladen.");
                        }
                    }
                }
            } else {
                targetName = PlayerUtils.getCorrectPlayerName(args[i]);
                // PLAYER NOT FOUND
                if (targetName == null) {
                    PlayerUtils.sendError(player, "Der Spieler '" + args[i] + "' wurde nicht gefunden!");
                    continue;
                }

                // PLAYER IS NEW GUEST
                if (!warp.isGuest(targetName)) {
                    Core.warpManager.addGuest(warp, targetName);
                    PlayerUtils.sendSuccess(player, "Spieler '" + targetName + "' wurde zum Warp '" + warp.getName() + "' eingeladen.");
                    // FIRE STATISTIC
                    StatisticHandler.handleStatistic(new WarpInviteStat(warp.getName(), player.getName(), targetName));
                } else {
                    // PLAYER WAS ALREADY INVITED
                    PlayerUtils.sendError(player, "Der Spieler '" + targetName + "' kann bereits den Warp '" + warp.getName() + "' benutzen.");
                }

                // INFORM PLAYER
                target = PlayerUtils.getOnlinePlayer(targetName);
                if (target != null) {
                    PlayerUtils.sendInfo(target, pluginName, "Du wurdest von '" + player.getName() + "' zum Warp '" + warp.getName() + "' eingeladen.");
                }
            }
        }
    }
}
