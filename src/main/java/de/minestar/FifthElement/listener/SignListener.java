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

package de.minestar.fifthelement.listener;

import de.minestar.fifthelement.Core;
import de.outinetworks.permissionshub.PermissionUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.minestar.fifthelement.data.Bank;
import de.minestar.fifthelement.data.Home;
import de.minestar.fifthelement.data.Mine;
import de.minestar.fifthelement.data.Warp;
import de.minestar.fifthelement.statistics.bank.BankSignStat;
import de.minestar.fifthelement.statistics.home.HomeSignStat;
import de.minestar.fifthelement.statistics.mine.MineSignStat;
import de.minestar.fifthelement.statistics.warp.WarpSignStat;
import de.minestar.minestarlibrary.stats.StatisticHandler;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class SignListener implements Listener {

    private final static String IGNORE_USE_MODE = "fifthelement.command.ignoreusemode";

    private final static String WARP_SIGN = "[warp]";
    private final static String HOME_SIGN = "[home]";
    private final static String BANK_SIGN = "[bank]";
    private final static String MINE_SIGN = "[mine]";

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (event.isCancelled() ||  !isSign(event.getBlock().getType()))
            return;

        String[] lines = event.getLines();
        if (lines[1] == null)
            return;

        Player player = event.getPlayer();
        // WARP SIGN
        if (lines[1].equalsIgnoreCase(WARP_SIGN) && lines[2] != null) {
            String warpName = lines[2];
            // USE LAST LINE FOR LONG WARPNAMES
            if (lines[3] != null && !lines[3].isEmpty())
                warpName += lines[3];
            Warp warp = Core.warpManager.getWarp(warpName);
            if (warp != null) {
                PlayerUtils.sendSuccess(player, Core.NAME, "Ein Rechtsklick auf das Schild teleportiert dich zum Warp '" + warp.getName() + "'.");
                event.setLine(2, warp.getName());
            } else {
                PlayerUtils.sendError(player, Core.NAME, "Der Warp '" + warpName + "' existiert nicht!");
                event.setCancelled(true);
                event.getBlock().breakNaturally();
            }
        }
        // HOME SIGN
        else if (lines[1].equals(HOME_SIGN)) {
            if (Core.homeManager.getHome(player.getUniqueId()) != null)
                PlayerUtils.sendSuccess(player, Core.NAME, "Ein Rechtsklick auf das Schild teleportiert dich zu deinem Zuhause.");
            else {
                PlayerUtils.sendError(player, Core.NAME, "Du hast im Moment kein Zuhause!");
                event.setCancelled(true);
                event.getBlock().breakNaturally();
            }
        }
        // BANK SIGN
        else if (lines[1].equals(BANK_SIGN)) {
            if (Core.homeManager.getHome(player.getUniqueId()) != null)
                PlayerUtils.sendSuccess(player, Core.NAME, "Ein Rechtsklick auf das Schild teleportiert dich zu deiner Bank.");
            else {
                PlayerUtils.sendError(player, Core.NAME, "Du hast im Moment keine Bank!");
                event.setCancelled(true);
                event.getBlock().breakNaturally();
            }
        }
        // MINE SIGN
        else if (lines[1].equals(MINE_SIGN)) {
            if (Core.mineManager.getMine(player.getUniqueId()) != null)
                PlayerUtils.sendSuccess(player, Core.NAME, "Ein Rechtsklick auf das Schild teleportiert dich zu deiner Mine.");
            else {
                PlayerUtils.sendError(player, Core.NAME, "Du hast im Moment keine Mine!");
                event.setCancelled(true);
                event.getBlock().breakNaturally();
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.useInteractedBlock().equals(Event.Result.DENY) || !event.hasBlock() || !event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;

        Block block = event.getClickedBlock();

        if (isSign(block.getType())) {
            Sign sign = (Sign) block.getState();
            String[] lines = sign.getLines();
            if (lines[1] == null)
                return;

            Player player = event.getPlayer();
            // WARP SIGN
            if (lines[1].equalsIgnoreCase(WARP_SIGN) && lines[2] != null && lines[2].length() >= 1)
                handleWarp(player, sign);
            // HOME SIGN
            else if (lines[1].equalsIgnoreCase(HOME_SIGN))
                handleHome(player, sign);
            // BANK SIGN
            else if (lines[1].equalsIgnoreCase(BANK_SIGN))
                handleBank(player, sign);
            // MINE SIGN
            else if (lines[1].equalsIgnoreCase(MINE_SIGN))
                handleMine(player, sign);
            // OTHER SIGNS
            else
                return;

            // DISALLOW PLACING BLOCKS
            event.setCancelled(true);
        }
    }

    private void handleMine(Player player, Sign sign) {
        Mine mine = Core.mineManager.getMine(player.getUniqueId());
        if (mine == null)
            PlayerUtils.sendError(player, Core.NAME, "Du hast keine Mine erstellt!");
        else {
            // STORE EVENTUALLY LAST POSITION
            Core.backManager.handleTeleport(player);

            player.teleport(mine.getLocation());
            PlayerUtils.sendSuccess(player, Core.NAME, "Willkommen in deiner Mine.");

            // FIRE STATISTIC
            StatisticHandler.handleStatistic(new MineSignStat(player.getName(), sign.getLocation()));
        }
    }

    private void handleBank(Player player, Sign sign) {
        Bank bank = Core.bankManager.getBank(player.getUniqueId());
        if (bank == null)
            PlayerUtils.sendError(player, Core.NAME, "Du hast keine Bank!");
        else {
            // STORE EVENTUALLY LAST POSITION
            Core.backManager.handleTeleport(player);

            player.teleport(bank.getLocation());
            PlayerUtils.sendSuccess(player, Core.NAME, "Willkommen in deiner Bank.");

            // FIRE STATISTIC
            StatisticHandler.handleStatistic(new BankSignStat(player.getName(), sign.getLocation()));
        }
    }

    private void handleHome(Player player, Sign sign) {
        Home home = Core.homeManager.getHome(player.getUniqueId());
        if (home == null)
            PlayerUtils.sendError(player, Core.NAME, "Du hast kein Zuhause erstellt!");
        else {
            // STORE EVENTUALLY LAST POSITION
            Core.backManager.handleTeleport(player);

            player.teleport(home.getLocation());
            PlayerUtils.sendSuccess(player, Core.NAME, "Willkommen zu Hause.");

            // FIRE STATISTIC
            StatisticHandler.handleStatistic(new HomeSignStat(player.getName(), sign.getLocation()));
        }
    }

    private void handleWarp(Player player, Sign sign) {
        String warpName = sign.getLine(2);
        // FOR LONG WARP NAMES USE LAST LINE
        if (sign.getLine(3) != null && !sign.getLine(3).isEmpty())
            warpName += sign.getLine(3);

        Warp warp = Core.warpManager.getWarp(warpName);
        if (warp == null) {
            PlayerUtils.sendError(player, Core.NAME, "Der Warp '" + warpName + "' existiert nicht mehr! Das Schild wurde abgerissen.");
            sign.getBlock().breakNaturally();
            return;
        }
        if (!canUse(warp, player)) {
            PlayerUtils.sendError(player, Core.NAME, "Du kannst den Warp '" + warp.getName() + "' nicht benutzen!");
            return;
        }
        // STORE EVENTUALLY LAST POSITION
        Core.backManager.handleTeleport(player);

        player.teleport(warp.getLocation());
        PlayerUtils.sendSuccess(player, Core.NAME, "Willkommen beim Warp '" + warp.getName() + "'.");

        // FIRE STATISTIC
        StatisticHandler.handleStatistic(new WarpSignStat(player.getName(), warp.getName(), sign.getLocation()));
    }

    private boolean canUse(Warp warp, Player player) {
        if (warp.isOwner(player) || PermissionUtils.playerCanUseCommand(player, IGNORE_USE_MODE))
            return true;
        return warp.canUse(player) && warp.canUsedBy(Warp.SIGN_USEMODE);
    }

    private boolean isSign(Material material)
    {
        if(material ==null) return false;
        if(material.equals(Material.ACACIA_SIGN)) return true;
        if(material.equals(Material.ACACIA_WALL_SIGN)) return true;
        if(material.equals(Material.BIRCH_SIGN)) return true;
        if(material.equals(Material.BIRCH_WALL_SIGN)) return true;
        if(material.equals(Material.DARK_OAK_SIGN)) return true;
        if(material.equals(Material.DARK_OAK_WALL_SIGN)) return true;
        if(material.equals(Material.JUNGLE_SIGN)) return true;
        if(material.equals(Material.JUNGLE_WALL_SIGN)) return true;
        if(material.equals(Material.OAK_SIGN)) return true;
        if(material.equals(Material.OAK_WALL_SIGN)) return true;
        if(material.equals(Material.SPRUCE_SIGN)) return true;
        return material.equals(Material.SPRUCE_WALL_SIGN);
    }
}
