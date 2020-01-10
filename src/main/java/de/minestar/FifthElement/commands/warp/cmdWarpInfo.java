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

import java.text.SimpleDateFormat;

import de.minestar.minestarlibrary.protection.Guest;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.minestar.fifthelement.Core;
import de.minestar.fifthelement.data.Warp;
import de.minestar.fifthelement.statistics.warp.WarpInfoStat;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.stats.StatisticHandler;
import de.minestar.minestarlibrary.utils.ChatUtils;
import de.minestar.minestarlibrary.utils.ConsoleUtils;
import de.minestar.moneypit.data.guests.GuestHelper;

public class cmdWarpInfo extends AbstractCommand {

    public cmdWarpInfo(String syntax, String arguments, String node) {
        super(Core.NAME, syntax, arguments, node);
    }

    @Override
    public void execute(String[] args, Player player) {
        warpInfo(args, player);
    }

    @Override
    public void execute(String[] args, ConsoleCommandSender console) {
        warpInfo(args, console);
    }

    private void warpInfo(String[] args, CommandSender sender) {
        String warpName = args[0];
        // SEARCH WARP
        Warp warp = Core.warpManager.getWarp(warpName);
        // NO WARP FOUND
        if (warp == null) {
            ChatUtils.writeError(sender, pluginName, "Der Warp '" + warpName + "' existiert nicht!");
            return;
        }
        // PLAYER IS NOT ALLOWED TO VIEW THE WARP
        if ((sender instanceof Player) && !warp.canUse((Player) sender)) {
            ChatUtils.writeError(sender, pluginName, "Du kannst keine Information über den Warp '" + warp.getName() + "' abrufen!");
            return;
        }

        displayInformation(warp, sender);

        // FIRE STATISTIC
        StatisticHandler.handleStatistic(new WarpInfoStat(warp.getName(), sender.getName()));
    }

    private final static SimpleDateFormat FORMAT = new SimpleDateFormat("H:mm 'am' d.M.Y");

    private final static String SEPERATOR = ChatColor.WHITE + "----------------------------------------";
    private final static ChatColor NAME_COLOR = ChatColor.GREEN;
    private final static ChatColor VALUE_COLOR = ChatColor.GRAY;

    private void displayInformation(Warp warp, CommandSender sender) {
        // HEAD
        ChatUtils.writeMessage(sender, SEPERATOR);
        ChatUtils.writeColoredMessage(sender, NAME_COLOR, "Informationen über Warp '" + VALUE_COLOR + warp.getName() + NAME_COLOR + "'");
        ChatUtils.writeMessage(sender, SEPERATOR);

        // OWNER
        ChatUtils.writeMessage(sender, String.format("%s %s", NAME_COLOR + "Erstellt von:", VALUE_COLOR + warp.getOwner().toString()));

        // CREATION DATE
        ChatUtils.writeMessage(sender, String.format("%s %s", NAME_COLOR + "Erstellt:", VALUE_COLOR + FORMAT.format(warp.getCreationDate())));

        // USE MODE
        ChatUtils.writeMessage(sender, String.format("%s %s", NAME_COLOR + "Benutzbar von:", VALUE_COLOR + useModeToText(warp.getUseMode())));

        // PUBLIC OR PRIVATE
        if (warp.isPublic())
            ChatUtils.writeMessage(sender, String.format("%s %s", NAME_COLOR + "Typ:", VALUE_COLOR + "Öffentlich"));
        else {
            ChatUtils.writeMessage(sender, String.format("%s %s", NAME_COLOR + "Typ:", VALUE_COLOR + "Privat"));
            // HAS GUESTS
            if (warp.getGuests().size() > 0) {
                StringBuilder sBuilder = new StringBuilder();
                for (Guest guest : warp.getGuests()) {
                    if (guest.getName().length() < 1) {
                        continue;
                    }
                    if (guest.getName().startsWith(GuestHelper.GROUP_PREFIX)) {
                        sBuilder.append(guest.getName().replaceFirst(GuestHelper.GROUP_PREFIX, "group: "));
                    } else {
                        sBuilder.append(guest.getName());
                    }
                    sBuilder.append(", ");
                }
                sBuilder.deleteCharAt(sBuilder.length() - 1);

                ChatUtils.writeMessage(sender, String.format("%s %s", NAME_COLOR + "Gäste:", VALUE_COLOR + sBuilder.toString()));
            }
        }

        // POSITION AND DISTANCE
        Location loc = warp.getLocation();
        ChatUtils.writeMessage(sender, String.format(NAME_COLOR + "X:" + VALUE_COLOR + " %d " + NAME_COLOR + "Y:" + VALUE_COLOR + " %d " + NAME_COLOR + "Z: " + VALUE_COLOR + "%d " + NAME_COLOR + "Welt:" + VALUE_COLOR + " %s", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName()));
        if (sender instanceof Player) {
            Location loc2 = ((Player) sender).getLocation();
            if (loc2.getWorld().equals(warp.getLocation().getWorld()))
                ChatUtils.writeMessage(sender, String.format("%s %s m", NAME_COLOR + "Entfernung:", VALUE_COLOR + ("" + (int) (loc.distance(loc2)))));
        }
        // END OF INFORMATION

        ChatUtils.writeMessage(sender, SEPERATOR);

    }

    private String useModeToText(byte useMode) {
        String result = "";
        if (useMode == Warp.COMMAND_USEMODE)
            result = "Befehlen";
        else if (useMode == Warp.SIGN_USEMODE)
            result = "Schildern";
        else if (useMode == (Warp.SIGN_USEMODE | Warp.COMMAND_USEMODE))
            result = "Befehlen und Schilder";
        else
            ConsoleUtils.printError(pluginName, "Unknown usemode : " + useMode);

        return result;

    }
}
