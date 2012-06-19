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

package de.minestar.FifthElement.data;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import de.minestar.FifthElement.core.Core;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class Warp {

    // DATABASE ID
    private int id;

    // THE NAME OF THE WARP
    private String name;

    // CAN EVERYONE USE THE WARP?
    private boolean isPublic;

    // THE CREATORS NAME
    private String owner;
    // USER WHO CAN ALSO USE THE WARP
    private Set<String> guests;

    // THE LOCATION OF THE WARP
    private Location location;

    // CONSTRUCTOR WHEN PLAYER CREATES INGAME A WARP
    public Warp(String warpName, Player player) {
        this.name = warpName;
        this.owner = player.getName();
        this.isPublic = false;
        guests = null;
    }

    // CONSTRUCTOR WHEN WARP IS LOADED FROM DATABASE
    public Warp(int id, String name, boolean isPublic, String owner, Set<String> guests, String worldName, double x, double y, double z, float yaw, float pitch) {
        this.id = id;
        this.name = name;
        this.isPublic = isPublic;
        this.owner = owner;
        this.guests = guests;

        createLocation(worldName, x, y, z, yaw, pitch);
    }

    private void createLocation(String worldName, double x, double y, double z, float yaw, float pitch) {
        World w = Bukkit.getWorld(worldName);
        if (w != null)
            this.location = new Location(w, x, y, z, yaw, pitch);
        else
            ConsoleUtils.printError(Core.NAME, "Can't load the warp '" + this.name + "' because world '" + worldName + "' doesn't exist!");
    }

    // UPDATE THE LOCATION OF THE WARP
    public void move(Player player) {
        this.location = player.getLocation();
    }

    // RENAME THE WARP
    public void rename(String name) {
        this.name = name;
    }

    // CHECK IF PLAYER IS OWNER
    public boolean isOwner(Player player) {
        return this.owner.equals(player.getName());
    }

    // CHECK IF PLAYER CAN MOVE/RENAME/DELETE OR EDIT THE WARP
    // ONLY THE OWNER AND ADMINS/MODS ARE ALLOWED TO DO IT
    public boolean canEdit(Player player) {
        // TODO: IMPLEMENT PERMISSION FOR MODS / ADMINS
        return isOwner(player);
    }

    public void setAccessMode(boolean isPublic) {
        // A PUBLIC WARP DOESN'T NEED A GUEST LIST
        if (isPublic)
            this.guests = null;
        else
            this.guests = new HashSet<String>();

        this.isPublic = isPublic;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public String getName() {
        return name;
    }

    public boolean addGuest(String guestName) {
        guestName = guestName.toLowerCase();
        // RETURN TRUE WHEN GUEST WASN'T INVITED YET
        if (guests != null)
            return guests.add(guestName);

        return false;
    }

    public boolean removeGuest(String guestName) {
        guestName = guestName.toLowerCase();
        // RETURN TRUE WHEN THE PLAYER WAS A GUEST
        if (guests != null)
            return guests.remove(guestName);

        return false;
    }

    public boolean isGuest(String playerName) {
        return guests != null && guests.contains(playerName.toLowerCase());
    }

    public boolean isGuest(Player player) {
        return isGuest(player.getName());
    }

    public boolean canUse(Player player) {
        return isOwner(player) || isPublic || isGuest(player);
    }

    public void setId(int id) {
        if (id == 0)
            this.id = id;
        else
            ConsoleUtils.printError(Core.NAME, "Warp '" + name + "' has already an database id!");
    }

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder("Warp: ");

        sBuilder.append("name= ");
        sBuilder.append(name);
        sBuilder.append(", location=");
        sBuilder.append(location);
        sBuilder.append(", id=");
        sBuilder.append(id);
        sBuilder.append(", owner=");
        sBuilder.append(owner);
        sBuilder.append(", guests=");
        sBuilder.append(guests);
        return sBuilder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof Warp))
            return false;
        if (obj == this)
            return true;

        Warp that = (Warp) obj;
        return this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        // (HASH OF NAME + ID ) * 32
        return (this.name.hashCode() + this.id) << 5;
    }

    // ID OF THE DATABASE ENTRY
    public int getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

}
