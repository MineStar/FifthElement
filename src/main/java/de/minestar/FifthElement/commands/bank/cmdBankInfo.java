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

package de.minestar.FifthElement.commands.bank;

import java.text.DecimalFormat;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.minestar.FifthElement.core.Core;
import de.minestar.FifthElement.data.Bank;
import de.minestar.FifthElement.statistics.bank.BankInfoStat;
import de.minestar.illuminati.IlluminatiCore;
import de.minestar.minestarlibrary.commands.AbstractExtendedCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdBankInfo extends AbstractExtendedCommand {

    private static final String OTHER_BANK_INFO_PERMISSION = "fifthelement.command.otherbankinfo";

    public cmdBankInfo(String syntax, String arguments, String node) {
        super(Core.NAME, syntax, arguments, node);
    }

    @Override
    public void execute(String[] args, Player player) {
        Bank bank = null;
        // INFORMATION ABOUT OWN BANK
        if (args.length == 0) {
            bank = Core.bankManager.getBank(player.getName());
            // HAS NO BANK YET
            if (bank == null) {
                PlayerUtils.sendError(player, pluginName, "Du hast keine Bank!");
                return;
            }
        }
        // INFORMATION ABOUT OTHER BANK
        else if (args.length == 1) {
            // CAN USE THE COMMAND
            if (checkSpecialPermission(player, OTHER_BANK_INFO_PERMISSION)) {
                // GET CORRECT PLAYER NAME
                String targetName = PlayerUtils.getCorrectPlayerName(args[0]);
                // PLAYER NOT FOUND
                if (targetName == null) {
                    PlayerUtils.sendError(player, pluginName, "Der Spieler '" + args[0] + "' wurde nicht gefunden!");
                    return;
                }
                bank = Core.bankManager.getBank(targetName);
                // TARGET HAS NO BANK YET
                if (bank == null) {
                    PlayerUtils.sendError(player, pluginName, "Der Spieler '" + targetName + "' hat keine Bank!");
                    return;
                }
            }
        }
        // WRONG SYNTAX
        else {
            PlayerUtils.sendError(player, pluginName, getHelpMessage());
            return;
        }

        displayBankInformation(player, bank);

        // FIRE STATISTIC
        IlluminatiCore.handleStatistic(new BankInfoStat(player.getName(), bank.getOwner()));
    }

    // FORMATTER TO DISPLAY ONLY ONE DIGIT
    private final static DecimalFormat dF = new DecimalFormat("#0.0");

    private void displayBankInformation(Player caller, Bank bank) {

        Location bankLoc = bank.getLocation();
        double distance = bankLoc.distance(caller.getLocation());

        PlayerUtils.sendInfo(caller, pluginName, "Information über die Bank von " + bank.getOwner() + ":");
        PlayerUtils.sendInfo(caller, "Position: " + bankLoc.getBlockX() + " " + bankLoc.getBlockY() + " " + bankLoc.getBlockZ() + " in " + bankLoc.getWorld().getName());
        PlayerUtils.sendInfo(caller, "Entfernung von hier: " + dF.format(distance) + "m");
    }
}
