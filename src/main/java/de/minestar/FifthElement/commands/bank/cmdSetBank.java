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

package de.minestar.fifthelement.commands.bank;

import com.mojang.api.profiles.HttpProfileRepository;
import com.mojang.api.profiles.Profile;
import com.mojang.api.profiles.ProfileRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.minestar.fifthelement.Core;
import de.minestar.fifthelement.data.Bank;
import de.minestar.fifthelement.statistics.bank.SetBankStat;
import de.minestar.minestarlibrary.stats.StatisticHandler;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdSetBank extends AbstractCommand {

    public cmdSetBank(String syntax, String arguments, String node)
    {
        super(Core.NAME, syntax, arguments, node);
    }

    @Override
    public void execute(String[] args, Player player)
    {
        ProfileRepository repository = new HttpProfileRepository("minecraft");
        Profile target = repository.findProfileByName(args[0]);
        // SEARCH FOR PLAYER NAME
        if (target == null)
        {
            PlayerUtils.sendError(player, pluginName, "Der Spieler '" + args[0] + "' wurde nicht gefunden!");
            return;
        }
        // GET THE BANK
        Bank bank = Core.bankManager.getBank(target.getUUID());
        // PLAYER HAS NO BANK YET
        if (bank == null)
        {
            // CREATE BANK
            Core.bankManager.createBank(player, target.getUUID());
            PlayerUtils.sendSuccess(player, pluginName, "Es wurde eine Bank für den Spieler '" + target.getName() + "' erstellt.");
            // INFORM THE BANK OWNER IF ONLINE
            Player targetPlayer = Bukkit.getPlayer(target.getUUID());
            if (targetPlayer != null)
            {
                PlayerUtils.sendInfo(targetPlayer, pluginName, "Der Spieler '" + player.getName() + "' hat für dich eine Bank erstellt!");
                PlayerUtils.sendInfo(targetPlayer, "Verwende den Befehl '/bank' um dich dort hin zu teleportieren!");
            }
            // FIRE STATISTIC
            StatisticHandler.handleStatistic(new SetBankStat(player.getName(), target.getName(), false));
        }
        // PLAYER HAS A BANK -> UPDATE POSITION
        else {
            // CHANGE BANK POSITION
            Core.bankManager.moveBank(player, bank);
            PlayerUtils.sendSuccess(player, pluginName, "Es wurde eine Bank für den Spieler '" + target.getName() + "' erstellt.");
            // INFORM THE BANK OWNER IF ONLINE
            Player targetPlayer = Bukkit.getPlayer(target.getUUID());
            if (targetPlayer != null) PlayerUtils.sendInfo(targetPlayer, pluginName, "Der Spieler '" + player.getName() + "' hat deine Bankposition verändert!");
            // FIRE STATISTIC
            StatisticHandler.handleStatistic(new SetBankStat(player.getName(), target.getName(), true));
        }
    }
}
