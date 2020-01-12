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
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.minestar.fifthelement.Core;
import de.minestar.fifthelement.data.Bank;
import de.minestar.fifthelement.statistics.bank.BankInfoStat;
import de.minestar.minestarlibrary.stats.StatisticHandler;
import de.minestar.minestarlibrary.commands.AbstractExtendedCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdBankInfo extends AbstractExtendedCommand
{

    private static final String OTHER_BANK_INFO_PERMISSION = "fifthelement.command.otherbankinfo";

    public cmdBankInfo(String syntax, String arguments, String node)
    {
        super(Core.NAME, syntax, arguments, node);
    }

    @Override
    public void execute(String[] args, Player player)
    {
        Bank bank;
        // INFORMATION ABOUT OWN BANK
        if (args.length == 0)
        {
            bank = Core.bankManager.getBank(player.getUniqueId());
            // HAS NO BANK YET
            if (bank == null)
            {
                PlayerUtils.sendError(player, pluginName, "Du hast keine Bank!");
                return;
            }
        }
        // INFORMATION ABOUT OTHER BANK
        else if (args.length == 1)
        {
            // CAN USE THE COMMAND
            if (checkSpecialPermission(player, OTHER_BANK_INFO_PERMISSION))
            {
                ProfileRepository repository = new HttpProfileRepository("minecraft");
                Profile target = repository.findProfileByName(args[0]);
                // PLAYER NOT FOUND
                if (target == null)
                {
                    PlayerUtils.sendError(player, pluginName, "Der Spieler '" + args[0] + "' wurde nicht gefunden!");
                    return;
                }
                bank = Core.bankManager.getBank(target.getUUID());
                // TARGET HAS NO BANK YET
                if (bank == null)
                {
                    PlayerUtils.sendError(player, pluginName, "Der Spieler '" + target.getName() + "' hat keine Bank!");
                    return;
                }
            }
            else return;
        }
        // WRONG SYNTAX
        else {
            PlayerUtils.sendError(player, pluginName, getHelpMessage());
            return;
        }
        displayBankInformation(player, bank);
        // FIRE STATISTIC
        StatisticHandler.handleStatistic(new BankInfoStat(player.getName(), bank.getOwnerName()));
    }

    private final static String SEPERATOR = "----------------------------------------";
    private final static ChatColor NAME_COLOR = ChatColor.GREEN;
    private final static ChatColor VALUE_COLOR = ChatColor.GRAY;

    private void displayBankInformation(Player player, Bank bank)
    {

        PlayerUtils.sendInfo(player, SEPERATOR);
        PlayerUtils.sendInfo(player, String.format("%s %s", NAME_COLOR + "Bank von:", VALUE_COLOR + bank.getOwnerName()));
        PlayerUtils.sendInfo(player, SEPERATOR);

        Location loc = bank.getLocation();
        PlayerUtils.sendInfo(player, String.format(NAME_COLOR + "X:" + VALUE_COLOR + " %d " + NAME_COLOR + "Y:" + VALUE_COLOR + " %d " + NAME_COLOR + "Z: " + VALUE_COLOR + "%d " + NAME_COLOR + "Welt:" + VALUE_COLOR + " %s", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName()));
        if (player.getLocation().getWorld().equals(bank.getLocation().getWorld()))
            PlayerUtils.sendInfo(player, String.format("%s %s m", NAME_COLOR + "Entfernung:", VALUE_COLOR + Integer.toString((int) player.getLocation().distance(loc))));
    }
}
