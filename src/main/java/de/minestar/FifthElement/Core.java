/*
 * Copyright (C) 2018 MineStar.de
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

package de.minestar.fifthelement;

import de.minestar.fifthelement.commands.back.cmdBack;
import de.minestar.fifthelement.commands.bank.*;
import de.minestar.fifthelement.commands.home.*;
import de.minestar.fifthelement.commands.mine.*;
import de.minestar.fifthelement.commands.teleport.*;
import de.minestar.fifthelement.commands.warp.*;
import de.minestar.fifthelement.database.DatabaseHandler;
import de.minestar.fifthelement.listener.SignListener;
import de.minestar.fifthelement.manager.*;
import de.minestar.fifthelement.statistics.bank.*;
import de.minestar.fifthelement.statistics.home.*;
import de.minestar.fifthelement.statistics.mine.*;
import de.minestar.fifthelement.statistics.teleport.*;
import de.minestar.fifthelement.statistics.warp.*;
import de.minestar.minestarlibrary.AbstractCore;
import de.minestar.minestarlibrary.annotations.UseStatistic;
import de.minestar.minestarlibrary.commands.CommandList;
import de.minestar.minestarlibrary.stats.StatisticHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.File;

@UseStatistic
public class Core extends AbstractCore
{

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

    public Core()
    {
        super(NAME);
        INSTANCE = this;
    }

    @Override
    protected boolean loadingConfigs(File dataFolder)
    {
        return Settings.init(dataFolder, NAME, getDescription().getVersion());
    }

    @Override
    protected boolean createManager()
    {
        dbHandler = new DatabaseHandler(new File(getDataFolder(), "sqlconfig.yml"));
        if (!dbHandler.hasConnection()) return false;

        warpManager = new WarpManager();
        homeManager = new HomeManager();
        bankManager = new BankManager();
        backManager = new BackManager();
        mineManager = new MineManager();
        return true;
    }

    @Override
    protected boolean registerStatistics()
    {
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
    protected boolean createCommands()
    {

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
                new cmdTeleportTo(  "/tpto",          "<Target> [OtherTarget] | [x] [y] [z] [[world]]", ""),
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
    protected boolean createListener()
    {
        this.warpSignListener = new SignListener();
        return true;
    }

    @Override
    protected boolean registerEvents(PluginManager pm)
    {
        pm.registerEvents(warpSignListener, this);
        return true;
    }

    @Override
    protected boolean commonDisable()
    {
        dbHandler.closeConnection();
        return !dbHandler.hasConnection();
    }

    public static Plugin getPlugin()
    {
        return de.minestar.fifthelement.Core.INSTANCE;
    }
}
