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

package de.minestar.FifthElement.core;

import java.io.File;
import java.util.List;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import de.minestar.FifthElement.commands.back.cmdBack;
import de.minestar.FifthElement.commands.bank.cmdBank;
import de.minestar.FifthElement.commands.bank.cmdBankInfo;
import de.minestar.FifthElement.commands.bank.cmdSetBank;
import de.minestar.FifthElement.commands.home.cmdHome;
import de.minestar.FifthElement.commands.home.cmdHomeInfo;
import de.minestar.FifthElement.commands.home.cmdSetHome;
import de.minestar.FifthElement.commands.mine.cmdMine;
import de.minestar.FifthElement.commands.mine.cmdMineInfo;
import de.minestar.FifthElement.commands.mine.cmdSetMine;
import de.minestar.FifthElement.commands.teleport.cmdTeleportTo;
import de.minestar.FifthElement.commands.teleport.cmdTeleportHere;
import de.minestar.FifthElement.commands.warp.cmdWarp;
import de.minestar.FifthElement.commands.warp.cmdWarpCreate;
import de.minestar.FifthElement.commands.warp.cmdWarpDelete;
import de.minestar.FifthElement.commands.warp.cmdWarpInfo;
import de.minestar.FifthElement.commands.warp.cmdWarpInvite;
import de.minestar.FifthElement.commands.warp.cmdWarpList;
import de.minestar.FifthElement.commands.warp.cmdWarpMode;
import de.minestar.FifthElement.commands.warp.cmdWarpMove;
import de.minestar.FifthElement.commands.warp.cmdWarpPrivate;
import de.minestar.FifthElement.commands.warp.cmdWarpPublic;
import de.minestar.FifthElement.commands.warp.cmdWarpRandom;
import de.minestar.FifthElement.commands.warp.cmdWarpRename;
import de.minestar.FifthElement.commands.warp.cmdWarpSearch;
import de.minestar.FifthElement.commands.warp.cmdWarpUninvite;
import de.minestar.FifthElement.data.Warp;
import de.minestar.FifthElement.data.filter.PublicFilter;
import de.minestar.FifthElement.database.DatabaseHandler;
import de.minestar.FifthElement.listener.SignListener;
import de.minestar.FifthElement.manager.BackManager;
import de.minestar.FifthElement.manager.BankManager;
import de.minestar.FifthElement.manager.HomeManager;
import de.minestar.FifthElement.manager.MineManager;
import de.minestar.FifthElement.manager.WarpManager;
import de.minestar.FifthElement.statistics.bank.BankInfoStat;
import de.minestar.FifthElement.statistics.bank.BankSignStat;
import de.minestar.FifthElement.statistics.bank.BankStat;
import de.minestar.FifthElement.statistics.bank.SetBankStat;
import de.minestar.FifthElement.statistics.home.HomeInfoStat;
import de.minestar.FifthElement.statistics.home.HomeSignStat;
import de.minestar.FifthElement.statistics.home.HomeStat;
import de.minestar.FifthElement.statistics.home.SetHomeStat;
import de.minestar.FifthElement.statistics.mine.MineInfoStat;
import de.minestar.FifthElement.statistics.mine.MineSignStat;
import de.minestar.FifthElement.statistics.mine.MineStat;
import de.minestar.FifthElement.statistics.mine.SetMineStat;
import de.minestar.FifthElement.statistics.teleport.TeleportHereStat;
import de.minestar.FifthElement.statistics.teleport.TeleportPlayerToStat;
import de.minestar.FifthElement.statistics.teleport.TeleportToStat;
import de.minestar.FifthElement.statistics.warp.PrivateWarpStat;
import de.minestar.FifthElement.statistics.warp.PublicWarpStat;
import de.minestar.FifthElement.statistics.warp.WarpCreateStat;
import de.minestar.FifthElement.statistics.warp.WarpDeleteStat;
import de.minestar.FifthElement.statistics.warp.WarpInfoStat;
import de.minestar.FifthElement.statistics.warp.WarpInviteStat;
import de.minestar.FifthElement.statistics.warp.WarpListStat;
import de.minestar.FifthElement.statistics.warp.WarpModeStat;
import de.minestar.FifthElement.statistics.warp.WarpMoveStat;
import de.minestar.FifthElement.statistics.warp.WarpRandomStat;
import de.minestar.FifthElement.statistics.warp.WarpRenameStat;
import de.minestar.FifthElement.statistics.warp.WarpSignStat;
import de.minestar.FifthElement.statistics.warp.WarpToStat;
import de.minestar.FifthElement.statistics.warp.WarpUninviteStat;
import de.minestar.minestarlibrary.AbstractCore;
import de.minestar.minestarlibrary.annotations.UseStatistic;
import de.minestar.minestarlibrary.commands.CommandList;
import de.minestar.minestarlibrary.stats.StatisticHandler;
;

@UseStatistic
public class Core extends AbstractCore {

    public static final String NAME = "FifthElement";
    private static Plugin INSTANCE = null;

    /* MANAGER */
    public static DatabaseHandler dbHandler;
    public static WarpManager warpManager;
    public static HomeManager homeManager;
    public static BankManager bankManager;
    public static BackManager backManager;
    public static MineManager mineManager;

    /* LISTENER */
    private Listener warpSignListener;

    public Core() {
        super(NAME);
        Core.INSTANCE = this;
    }

    @Override
    protected boolean loadingConfigs(File dataFolder) {
        return Settings.init(dataFolder, NAME, getDescription().getVersion());
    }

    @Override
    protected boolean createManager() {
        dbHandler = new DatabaseHandler(new File(getDataFolder(), "sqlconfig.yml"));
        if (!dbHandler.hasConnection())
            return false;

        warpManager = new WarpManager();
        homeManager = new HomeManager();
        bankManager = new BankManager();
        backManager = new BackManager();
        mineManager = new MineManager();

        return true;
    }

    @Override
    protected boolean registerStatistics() {

        StatisticHandler.registerStatistic(TeleportHereStat.class);
        StatisticHandler.registerStatistic(TeleportToStat.class);
        StatisticHandler.registerStatistic(TeleportPlayerToStat.class);

        StatisticHandler.registerStatistic(PrivateWarpStat.class);
        StatisticHandler.registerStatistic(PublicWarpStat.class);
        StatisticHandler.registerStatistic(WarpCreateStat.class);
        StatisticHandler.registerStatistic(WarpDeleteStat.class);
        StatisticHandler.registerStatistic(WarpInfoStat.class);
        StatisticHandler.registerStatistic(WarpInviteStat.class);
        StatisticHandler.registerStatistic(WarpListStat.class);
        StatisticHandler.registerStatistic(WarpModeStat.class);
        StatisticHandler.registerStatistic(WarpMoveStat.class);
        StatisticHandler.registerStatistic(WarpRandomStat.class);
        StatisticHandler.registerStatistic(WarpRenameStat.class);
        StatisticHandler.registerStatistic(WarpToStat.class);
        StatisticHandler.registerStatistic(WarpUninviteStat.class);

        StatisticHandler.registerStatistic(BankInfoStat.class);
        StatisticHandler.registerStatistic(BankStat.class);
        StatisticHandler.registerStatistic(SetBankStat.class);

        StatisticHandler.registerStatistic(HomeInfoStat.class);
        StatisticHandler.registerStatistic(HomeStat.class);
        StatisticHandler.registerStatistic(SetHomeStat.class);

        StatisticHandler.registerStatistic(MineInfoStat.class);
        StatisticHandler.registerStatistic(MineStat.class);
        StatisticHandler.registerStatistic(SetMineStat.class);

        StatisticHandler.registerStatistic(BankSignStat.class);
        StatisticHandler.registerStatistic(HomeSignStat.class);
        StatisticHandler.registerStatistic(WarpSignStat.class);
        StatisticHandler.registerStatistic(MineSignStat.class);

        return true;
    }

    @Override
    protected boolean createCommands() {

        // @formatter:off
        cmdList = new CommandList(NAME,

                // HOME COMMANDS
                new cmdHome(        "/home",        "[HomeOwner]",          "fifthelement.command.home"),
                new cmdSetHome(     "/sethome",     "",                     "fifthelement.command.sethome"),
                new cmdHomeInfo(    "/homeinfo",    "[HomeOwner]",          "fifthelement.command.homeinfo"),

                // MINE COMMANDS
                new cmdMine(        "/mine",        "[mineOwner]",          "fifthelement.command.mine"),
                new cmdSetMine(     "/setmine",     "",                     "fifthelement.command.setmine"),
                new cmdMineInfo(    "/mineinfo",    "[mineOwner]",          "fifthelement.command.mineinfo"),

                // BANK COMMANDS
                new cmdBank(        "/bank",        "[BankOwner]",         "fifthelement.command.bank"),
                new cmdSetBank(     "/setbank",     "<BankOwner>",         "fifthelement.command.setbank"),
                new cmdBankInfo(    "/bankinfo",    "[BankOwner]",         "fifthelement.command.bankinfo"),

                // TELEPORT COMMANDS
                new cmdTeleportTo(    "/tpto",          "<Target> [OtherTarget] | [x] [y] [z] [[world]]", ""),
                new cmdTeleportHere("/tphere",      "<Target> ... [Target n]", "fifthelement.command.tphere"),
                
                new cmdBack(        "/back",        "",                     "fifthelement.command.back"),    

                // WARP COMMANDS AND SUB COMMANDS
                new cmdWarp(        "/warp",        "<Warp>",               "fifthelement.command.warp", 
                        
                        new cmdWarpCreate(  "create",       "<Warp>",                       "fifthelement.command.warpcreate"),
                        new cmdWarpDelete(  "delete",       "<Warp>",                       "fifthelement.command.warpdelete"),
                        new cmdWarpInvite(  "invite",       "<Warp> <Player> ...[PlayerN]", "fifthelement.command.warpinvite"),
                        new cmdWarpUninvite("uninvite",     "<Warp> <Player> ...[PlayerN]", "fifthelement.command.warpuninvite"),
                        new cmdWarpMove(    "move",         "<Warp>",                       "fifthelement.command.warpmove"),
                        new cmdWarpRename(  "rename",       "<OldName> <NewName>",          "fifthelement.command.warprename"),
                        new cmdWarpRandom(  "random",       "",                             "fifthelement.command.warprandom"),
                        new cmdWarpPublic(  "public",       "<Warp>",                       "fifthelement.command.warppublic"),
                        new cmdWarpPrivate( "private",      "<Warp>",                       "fifthelement.command.warpprivate"),
                        new cmdWarpInfo(    "info",         "<Warp>",                       "fifthelement.command.warpinfo"),
                        new cmdWarpList(    "list",         "",                             "fifthelement.command.warplist"),
                        new cmdWarpSearch(  "search",       "<Suchwort>",                   "fifthelement.command.warplist"),
                        new cmdWarpMode(    "mode",         "<Warp> <ALL | SIGN | COMMAND>","fifthelement.command.warpmode")
                )
        );
        // @formatter:on

        return true;
    }

    @Override
    protected boolean createListener() {

        this.warpSignListener = new SignListener();

        return true;
    }

    @Override
    protected boolean registerEvents(PluginManager pm) {
        pm.registerEvents(warpSignListener, this);
        return true;
    }

    public static List<Warp> getPublicWarps() {
        return warpManager.filterWarps(PublicFilter.getInstance());
    }

    @Override
    protected boolean commonDisable() {
        dbHandler.closeConnection();

        return !dbHandler.hasConnection();
    }

    public static Plugin getPlugin() {
        return Core.INSTANCE;
    }
}
