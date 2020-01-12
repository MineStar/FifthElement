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

package de.minestar.fifthelement.commands.warp;

import com.mojang.api.profiles.HttpProfileRepository;
import com.mojang.api.profiles.Profile;
import com.mojang.api.profiles.ProfileRepository;
import org.bukkit.entity.Player;

import de.minestar.fifthelement.Core;
import de.minestar.fifthelement.data.Warp;
import de.minestar.fifthelement.statistics.warp.WarpUninviteStat;
import de.minestar.minestarlibrary.commands.AbstractExtendedCommand;
import de.minestar.minestarlibrary.stats.StatisticHandler;
import de.minestar.minestarlibrary.utils.PlayerUtils;
import de.minestar.moneypit.data.guests.Group;
import de.minestar.moneypit.data.guests.GroupManager;
import de.minestar.moneypit.data.guests.GuestHelper;

public class cmdWarpUninvite extends AbstractExtendedCommand {

    public cmdWarpUninvite(String syntax, String arguments, String node) {
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
            PlayerUtils.sendError(player, pluginName, "Du kannst aus dem Warp '" + warp.getName() + "' niemanden ausladen!");
            return;
        }

        ProfileRepository repository = new HttpProfileRepository("minecraft");

        // UNINVITE PERSON
        for (int i = 1; i < args.length; ++i) {
            if (args[i].startsWith(GuestHelper.GROUP_PREFIX))
            {
                if (args[i].matches("(" + GuestHelper.GROUP_PREFIX + ")([a-zA-Z0-9_])*")) {
                    Group group = GroupManager.getGroup(player.getUniqueId(), args[i]);
                    if (group == null)
                    {
                        PlayerUtils.sendError(player, "Die Gruppe '" + args[i] + "' wurde nicht gefunden!");
                        continue;
                    }

                    // targetName is the groupName
                    String targetGroup = group.getName();
                    String canonicalName = targetGroup.replaceFirst(GuestHelper.GROUP_PREFIX, "");

                    // GROUP WAS GUEST
                    if (warp.isGuest(targetGroup))
                    {
                        Core.warpManager.removeGuest(warp, targetGroup);
                        PlayerUtils.sendSuccess(player, "Gruppe '" + canonicalName + "' wurde aus dem Warp '" + warp.getName() + "' ausgeladen.");
                        // FIRE STATISTIC
                        StatisticHandler.handleStatistic(new WarpUninviteStat(warp.getName(), player.getName(), targetGroup));
                    }
                    else {
                        // GROUP WAS NO GUEST
                        PlayerUtils.sendError(player, "Die Gruppe '" + canonicalName + "' konnte den Warp '" + warp.getName() + "' nicht benutzen.");
                    }

                    // INFORM PLAYERS
                    for (String playerName : group.getPlayerList())
                    {
                        Player targetPlayer = PlayerUtils.getOnlinePlayer(playerName);
                        if (targetPlayer != null)
                        {
                            PlayerUtils.sendInfo(targetPlayer, pluginName, "Du wurdest von '" + player.getName() + "' aus dem Warp '" + warp.getName() + "' ausgeladen.");
                        }
                    }
                }
            }
            else {
                Profile target = repository.findProfileByName(args[i]);
                // PLAYER NOT FOUND
                if (target == null)
                {
                    PlayerUtils.sendError(player, "Der Spieler '" + args[i] + "' wurde nicht gefunden!");
                    continue;
                }
                // PLAYER WAS GUEST
                if (warp.isGuest(target.getUUID().toString()))
                {
                    Core.warpManager.removeGuest(warp, target.getUUID().toString());
                    PlayerUtils.sendSuccess(player, "Spieler '" + target.getName() + "' wurde aus dem Warp '" + warp.getName() + "' ausgeladen.");
                    // FIRE STATISTIC
                    StatisticHandler.handleStatistic(new WarpUninviteStat(warp.getName(), player.getName(), target.getName()));
                }
                else {
                    // PLAYER WAS NO GUEST
                    PlayerUtils.sendError(player, "Der Spieler '" + target.getName() + "' konnte den Warp '" + warp.getName() + "' nicht benutzen.");
                }

                // INFORM PLAYER
                Player targetPlayer = PlayerUtils.getOnlinePlayer(target.getName());
                if (targetPlayer != null) {
                    PlayerUtils.sendInfo(targetPlayer, pluginName, "Du wurdest von '" + player.getName() + "' aus dem Warp '" + warp.getName() + "' ausgeladen.");
                }
            }
        }
    }
}
