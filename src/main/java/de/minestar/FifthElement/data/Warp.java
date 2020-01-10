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

package de.minestar.fifthelement.data;

import java.util.*;
import java.util.regex.Pattern;

import de.minestar.minestarlibrary.protection.Guest;
import de.outinetworks.permissionshub.PermissionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import de.minestar.fifthelement.Core;
import de.minestar.minestarlibrary.utils.ConsoleUtils;
import de.minestar.moneypit.data.guests.GuestHelper;

public class Warp {

    // DATABASE ID
    private int id;

    // THE NAME OF THE WARP
    private String name;

    // CAN EVERYONE USE THE WARP?
    private boolean isPublic;

    // THE CREATORS NAME
    private UUID owner;
    // USER WHO CAN ALSO USE THE WARP
    private Map<String, Guest> guests;

    // THE LOCATION OF THE WARP
    private Location location;

    private final Date creationDate;

    // BIT MASK HOW THE WARP CAN BE USED
    // IF 1 IS SET -> USED BY COMMANDS
    // IF 2 IS SET -> USED BY SIGNS
    private byte useMode;

    // COONSTANTS FOR USE MODE
    public final static byte COMMAND_USEMODE = 1;
    public final static byte SIGN_USEMODE = 2;

    private final static String PERMISSION_USE_ALL_WARPS = "fifthelement.useallwarps";
    private final static String PERMISSION_EDIT_ALL_WARPS = "fifthelement.editallwarps";

    // CONSTRUCTOR WHEN PLAYER CREATES INGAME A WARP
    public Warp(String warpName, Player player) {
        this.name = warpName;
        this.owner = player.getUniqueId();
        this.isPublic = false;
        this.guests = Collections.synchronizedMap(new HashMap<>());
        this.location = player.getLocation();
        this.creationDate = new Date();

        // WARP CAN BE USED BY SIGNS AND COMMANDS
        this.useMode |= COMMAND_USEMODE | SIGN_USEMODE;
    }

    // CONSTRUCTOR WHEN WARP IS LOADED FROM DATABASE
    public Warp(int id, String name, boolean isPublic, UUID owner, String guestList, String worldName, double x, double y, double z, float yaw, float pitch, byte useMode, Date creationDate) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.isPublic = isPublic;
        if (!isPublic) {
            parseGuestList(guestList);
        }

        createLocation(worldName, x, y, z, yaw, pitch);

        this.useMode = useMode;
        this.creationDate = creationDate;
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
        return isOwner(player.getUniqueId());
    }

    public boolean isOwner(UUID playerUUID) {
        return this.owner.equals(playerUUID);
    }

    // CHECK IF PLAYER CAN MOVE/RENAME/DELETE OR EDIT THE WARP
    // ONLY THE OWNER AND ADMINS/MODS ARE ALLOWED TO DO IT
    public boolean canEdit(Player player) {
        return isOwner(player) || PermissionUtils.playerCanUseCommand(player, PERMISSION_EDIT_ALL_WARPS);
    }

    public void setAccessMode(boolean isPublic) {
        // A PUBLIC WARP DOESN'T NEED A GUEST LIST
        if (isPublic)
            this.guests = null;
        else
            this.guests = Collections.synchronizedMap(new HashMap<>());

        this.isPublic = isPublic;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public boolean isPrivate() {
        return !isPublic;
    }

    public String getName() {
        return name;
    }

    public boolean addGuest(String guestName) {
        // RETURN TRUE WHEN GUEST WASN'T INVITED YET
        if (guests != null) {
            return (this.guests.put(guestName.toLowerCase(), GuestHelper.create(owner, guestName)) == null);
        }
        return false;
    }

    public boolean removeGuest(String guestName) {
        // RETURN TRUE WHEN THE PLAYER WAS A GUEST
        if (guests != null) {
            return (guests.remove(guestName.toLowerCase()) != null);
        }

        return false;
    }

    public boolean isGuest(String playerName) {
        if (guests != null) {
            for (Guest guest : this.guests.values()) {
                if (guest.isValid()) {
                    if (guest.hasAccess(playerName)) {
                        return true;
                    }
                } else {
                    this.removeGuest(guest.getName());
                }
            }
        }
        return false;
    }

    public boolean isGuest(Player player) {
        return isGuest(player.getName());
    }

    public boolean canUse(UUID playerUUID) {
        return isPublic || isOwner(playerUUID) || isGuest(playerUUID.toString());
    }

    public boolean canUse(Player player) {
        return canUse(player.getUniqueId()) || PermissionUtils.playerCanUseCommand(player, PERMISSION_USE_ALL_WARPS);
    }

    public void setId(int id) {
        if (this.id == 0)
            this.id = id;
        else
            ConsoleUtils.printError(Core.NAME, "Warp '" + name + "' has already an database id!");
    }

    @Override
    public String toString() {
        return String.format("Warp: name= %s, location=%s, id=%d, owner=%s, guests=%s ,date=%s", name, location, id, owner, guests, creationDate);
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

    public UUID getOwner() {
        return owner;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Collection<Guest> getGuests() {
        if (guests == null)
            return new HashSet<>();
        else
            return new HashSet<>(guests.values());
    }

    private final static Pattern P = Pattern.compile(";");

    private void parseGuestList(String guestList) {
        this.guests = Collections.synchronizedMap(new HashMap<>());

        if (guestList.isEmpty()) {
            return;
        }
        String[] split = P.split(guestList);
        for (String guest : split) {
            this.addGuest(guest);
        }
    }
    // *************
    // ** USEMODE **
    // *************

    public void setUseMode(byte useMode) {
        this.useMode = useMode;
    }

    public boolean canUsedBy(byte useMode) {
        return (this.useMode & useMode) == useMode;
    }

    public byte getUseMode() {
        return useMode;
    }
}
