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

package de.minestar.fifthelement.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import de.outinetworks.permissionshub.PermissionUtils;
import org.bukkit.World;
import org.bukkit.entity.Player;

import de.minestar.fifthelement.Core;
import de.minestar.fifthelement.Settings;
import de.minestar.fifthelement.data.Warp;
import de.minestar.fifthelement.data.WarpCounter;
import de.minestar.fifthelement.data.filter.WarpFilter;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class WarpManager {

    private TreeMap<String, Warp> warpMap;
    private Map<String, WarpCounter> warpCounterMap;

    public WarpManager() {
        loadWarps();
    }

    // **************************
    // LOADING AND INITIALIZATION
    // **************************

    private void loadWarps() {
        warpMap = Core.dbHandler.loadWarps();
        int[] counter = countWarps();
        ConsoleUtils.printInfo(Core.NAME, "Loaded " + warpMap.size() + " Warps. There were " + counter[0] + " public and " + counter[1] + " private warps");
    }

    private int[] countWarps() {
        // FIRST ELEMENT = GOBAL COUNTER FOR PUBLIC
        // SECOND ELEMENT = GOBAL COUNTER FOR PRIVATE
        int[] counter = new int[2];
        warpCounterMap = new HashMap<>();

        String owner;
        WarpCounter warpCounter;

        for (Warp warp : warpMap.values()) {
            owner = warp.getOwner().toLowerCase();
            // GET COUNTER FOR THE OWNER
            warpCounter = getWarpCounter(owner);

            // INCREMENT THE COUNTER
            if (warp.isPublic()) {
                warpCounter.incrementPublicWarps();
                counter[0]++;
            } else {
                warpCounter.incrementPrivateWarps();
                counter[1]++;
            }
        }

        return counter;
    }

    // **************************
    // ******* FIND WARPS *******
    // **************************

    // EXACT NAME NEEDED
    public boolean isWarpExisting(String warpName) {
        return warpMap.containsKey(warpName.toLowerCase());
    }

    public void transferWarps(String oldPlayer, String newPlayer) {
        Core.dbHandler.transferWarps(oldPlayer, newPlayer);
        loadWarps();
    }

    // EXACT NAME NEEDED
    public Warp getWarp(String warpName) {
        return warpMap.get(warpName.toLowerCase());
    }

    public List<Warp> findWarp(String searchWord) {

        List<Warp> results = new ArrayList<>();
        // HAVE FOUND EXACT NAME
        Warp warp = getWarp(searchWord);
        if (warp != null)
            results.add(warp);

        searchWord = searchWord.toLowerCase();
        warp = warpMap.ceilingEntry(searchWord).getValue();
        if (warp != null)
            results.add(warp);

        warp = warpMap.floorEntry(searchWord).getValue();
        if (warp != null)
            results.add(warp);

        return results;
    }

    // SEARCH FOR WARP USING CONTAINS
    public List<Warp> searchWarp(String searchWord) {
        List<Warp> results = new LinkedList<>();

        searchWord = searchWord.toLowerCase();

        for (Entry<String, Warp> entry : warpMap.entrySet()) {
            if (entry.getKey().contains(searchWord))
                results.add(entry.getValue());
        }

        return results;
    }

    public List<Warp> filterWarps(WarpFilter... warpFilter) {

        List<Warp> results = new ArrayList<>();

        out : for (Warp warp : warpMap.values()) {
            for (WarpFilter filter : warpFilter) {
                if (!filter.accept(warp))
                    continue out;
            }
            results.add(warp);
        }
        return results;
    }

    public List<Warp> filterWarps(List<WarpFilter> warpFilter) {
        List<Warp> results = new ArrayList<>();

        out : for (Warp warp : warpMap.values()) {
            for (WarpFilter filter : warpFilter) {
                if (!filter.accept(warp))
                    continue out;
            }
            results.add(warp);
        }
        return results;
    }

    // **************************
    // MANIPULATE CURRENT WARPS
    // **************************

    // NAME MUST BE UNIQUE
    public void createWarp(String warpName, Player creator) {
        Warp warp = new Warp(warpName, creator);
        incrementWarpCount(warp);
        Core.dbHandler.addWarp(warp);
        warpMap.put(warpName.toLowerCase(), warp);
    }

    private void incrementWarpCount(Warp warp) {
        WarpCounter counter = getWarpCounter(warp.getOwner());

        if (warp.isPublic())
            counter.incrementPublicWarps();
        else
            counter.incrementPrivateWarps();
    }

    public void deleteWarp(Warp warp) {
        decrementWarpCount(warp);
        Core.dbHandler.deleteWarp(warp);
        warpMap.remove(warp.getName().toLowerCase());
    }

    private void decrementWarpCount(Warp warp) {
        WarpCounter counter = getWarpCounter(warp.getOwner());

        if (warp.isPublic())
            counter.decrementPublicWarps();
        else
            counter.decrementPrivateWarps();
    }

    public void changeAccess(Warp warp, boolean toPublic) {
        WarpCounter counter = getWarpCounter(warp.getOwner());
        if (toPublic) {
            counter.decrementPrivateWarps();
            counter.incrementPublicWarps();
        } else {
            counter.decrementPublicWarps();
            counter.incrementPrivateWarps();
        }

        warp.setAccessMode(toPublic);
        Core.dbHandler.updateAccess(warp);
    }

    public void addGuest(Warp warp, String guestName) {
        warp.addGuest(guestName);
        Core.dbHandler.updateGuests(warp);
    }

    public void removeGuest(Warp warp, String guestName) {
        warp.removeGuest(guestName);
        Core.dbHandler.updateGuests(warp);
    }

    public WarpCounter getWarpCounter(String owner) {
        WarpCounter counter = warpCounterMap.get(owner.toLowerCase());
        if (counter == null) {
            counter = new WarpCounter(owner);
            warpCounterMap.put(owner.toLowerCase(), counter);
        }
        return counter;
    }

    public boolean canCreatePublic(String playerName) {
        // GET THE CURRENT COUNT
        WarpCounter counter = getWarpCounter(playerName);
        // GET THE GROUP OF THE PLAYER
        //MinestarGroup group = MinestarCore.getPlayer(playerName).getMinestarGroup();
        // CURRENT COUNTER IS LOWER THAN ALLOWED
        return counter.getPublicWarps() < Integer.parseInt(PermissionUtils.getOption(playerName,"maxpublicwarps"));
    }

    public boolean canCreatePrivate(String playerName) {
        // GET THE CURRENT COUNT
        WarpCounter counter = getWarpCounter(playerName);
        // GET THE GROUP OF THE PLAYER
        //MinestarGroup group = MinestarCore.getPlayer(playerName).getMinestarGroup();
        // CURRENT COUNTER IS LOWER THAN ALLOWED
        return counter.getPrivateWarps() < Integer.parseInt(PermissionUtils.getOption(playerName,"maxprivatewarps"));
    }

    public void moveWarp(Warp warp, Player player) {
        warp.move(player);
        Core.dbHandler.updateWarpLocation(warp);
    }

    public void renameWarp(Warp warp, String newName) {
        // DELETE OLD ENTRY
        this.warpMap.remove(warp.getName().toLowerCase());
        // RENAME
        warp.rename(newName);
        this.warpMap.put(warp.getName().toLowerCase(), warp);
        // PERSIST
        Core.dbHandler.updateWarpName(warp);
    }

    public void setUseMode(Warp warp, byte useMode) {
        warp.setUseMode(useMode);

        // PERSIST
        Core.dbHandler.updateUseMode(warp);
    }

    public boolean isWarpAllowedIn(World world) {
        return isWarpAllowedIn(world.getName());
    }

    public boolean isWarpAllowedIn(String worldName) {
        return !Settings.getForbiddenWarpWorlds().contains(worldName.toLowerCase());
    }

    /* KEY WORD HANDELING */

    private static Set<String> keyWords;

    static {
        keyWords = new HashSet<>();
        keyWords.add("create");
        keyWords.add("delete");
        keyWords.add("help");
        keyWords.add("invite");
        keyWords.add("uninvite");
        keyWords.add("move");
        keyWords.add("rename");
        keyWords.add("random");
        keyWords.add("public");
        keyWords.add("private");
        keyWords.add("info");
        keyWords.add("list");
        keyWords.add("mode");
    }

    public boolean isKeyWord(String warpName) {
        return keyWords.contains(warpName.toLowerCase());
    }

    public boolean isValidName(String warpName) {
        return warpName.length() >= Settings.getMinWarpnameSize() && warpName.length() <= Settings.getMaxWarpnameSize();
    }
}
