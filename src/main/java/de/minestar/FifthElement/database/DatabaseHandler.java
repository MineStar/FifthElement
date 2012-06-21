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

package de.minestar.FifthElement.database;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Location;

import de.minestar.FifthElement.core.Core;
import de.minestar.FifthElement.data.Warp;
import de.minestar.minestarlibrary.config.MinestarConfig;
import de.minestar.minestarlibrary.database.AbstractDatabaseHandler;
import de.minestar.minestarlibrary.database.DatabaseConnection;
import de.minestar.minestarlibrary.database.DatabaseType;
import de.minestar.minestarlibrary.database.DatabaseUtils;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class DatabaseHandler extends AbstractDatabaseHandler {

    public DatabaseHandler(File dataFolder) {
        super(Core.NAME, dataFolder);
    }

    @Override
    protected DatabaseConnection createConnection(String pluginName, File dataFolder) throws Exception {
        File configFile = new File(dataFolder, "sqlconfig.yml");
        if (!configFile.exists())
            DatabaseUtils.createDatabaseConfig(DatabaseType.MySQL, configFile, pluginName);
        else
            return new DatabaseConnection(pluginName, DatabaseType.MySQL, new MinestarConfig(configFile));

        return null;
    }

    @Override
    protected void createStructure(String pluginName, Connection con) throws Exception {
        DatabaseUtils.createStructure(getClass().getResourceAsStream("/structure.sql"), con, pluginName);
    }

    @Override
    protected void createStatements(String pluginName, Connection con) throws Exception {

        addWarp = con.prepareStatement("INSERT INTO warp (name, owner, world, x, y, z, yaw, pitch, isPublic, guests) VALUES (? , ? , ? , ? , ? , ? , ? , ? , ? , ?)", PreparedStatement.RETURN_GENERATED_KEYS);

        deleteWarp = con.prepareStatement("DELETE FROM warp WHERE id = ?");

        updateWarpLocation = con.prepareStatement("UPDATE warp SET world = ? , x = ? , y = ? , z = ? , yaw = ? , pitch = ? WHERE id = ?");

        updateWarpName = con.prepareStatement("UPDATE warp SET name = ? WHERE id = ?");

        updateGuestList = con.prepareStatement("UPDATE warp SET guests = ? WHERE id = ?");

        updateAccess = con.prepareStatement("UPDATE warp SET isPublic = ? WHERE id = ?");
    }

    /* STATEMENTS */
    private PreparedStatement addWarp;
    private PreparedStatement deleteWarp;
    private PreparedStatement updateWarpLocation;
    private PreparedStatement updateWarpName;
    private PreparedStatement updateGuestList;
    private PreparedStatement updateAccess;

    public Map<String, Warp> loadWarps() {
        Map<String, Warp> warpMap = new TreeMap<String, Warp>();
        try {
            Statement stat = dbConnection.getConnection().createStatement();
            ResultSet rs = stat.executeQuery("SELECT id,name, owner, world, x, y, z, yaw, pitch, isPublic, guests FROM warp");
            // TEMP VARIABLEN
            int id;
            String name;
            String owner;
            String worldName;
            double x;
            double y;
            double z;
            float yaw;
            float pitch;
            boolean isPublic;
            String guests;

            // CREATE WARPS
            while (rs.next()) {
                // LOAD VARIABLEN
                id = rs.getInt(1);
                name = rs.getString(2);
                owner = rs.getString(3);
                worldName = rs.getString(4);
                x = rs.getDouble(5);
                y = rs.getDouble(6);
                z = rs.getDouble(7);
                yaw = rs.getFloat(8);
                pitch = rs.getFloat(9);
                isPublic = rs.getBoolean(10);
                guests = rs.getString(11);

                // CREATE WARP AND PUT IT TO MAP
                warpMap.put(name.toLowerCase(), new Warp(id, name, isPublic, owner, guests, worldName, x, y, z, yaw, pitch));
            }
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't load warps from table!");
            // RETURN AN EMPTY MAP WHEN THERE IS AN ERROR
            warpMap.clear();
        }
        return warpMap;
    }

    public boolean addWarp(Warp warp) {

        try {
            // INSERT WARP INTO TABLE
            addWarp.setString(1, warp.getName());
            addWarp.setString(2, warp.getOwner());
            addWarp.setString(3, warp.getLocation().getWorld().getName().toLowerCase());
            addWarp.setDouble(4, warp.getLocation().getX());
            addWarp.setDouble(5, warp.getLocation().getY());
            addWarp.setDouble(6, warp.getLocation().getZ());
            addWarp.setFloat(7, warp.getLocation().getYaw());
            addWarp.setFloat(8, warp.getLocation().getPitch());
            addWarp.setBoolean(9, false);
            addWarp.setString(10, "");

            addWarp.executeUpdate();

            // GET THE GENERATED ID
            ResultSet rs = addWarp.getGeneratedKeys();
            int id = 0;
            if (rs.next()) {
                id = rs.getInt(1);
                warp.setId(id);
                return true;
            } else {
                ConsoleUtils.printError(Core.NAME, "Can't get the id for the warp = " + warp);
                return false;
            }

        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't insert the warp = " + warp);
            return false;
        }
    }

    public boolean deleteWarp(Warp warp) {

        try {
            // DELETE WARP FROM TABLE
            deleteWarp.setInt(1, warp.getId());
            return deleteWarp.executeUpdate() == 1;
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't delete the warp = " + warp);
            return false;
        }
    }

    public boolean updateWarpLocation(Warp warp) {

        try {
            // UPDATE THE WARP LOCATION
            Location loc = warp.getLocation();
            updateWarpLocation.setString(1, loc.getWorld().getName().toLowerCase());
            updateWarpLocation.setDouble(2, loc.getX());
            updateWarpLocation.setDouble(3, loc.getY());
            updateWarpLocation.setDouble(4, loc.getZ());
            updateWarpLocation.setFloat(5, loc.getYaw());
            updateWarpLocation.setFloat(6, loc.getPitch());
            updateWarpLocation.setInt(7, warp.getId());
            return updateWarpLocation.executeUpdate() == 1;
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't update warp location of warp = " + warp);
            return false;
        }
    }

    public boolean updateWarpName(Warp warp) {

        try {
            // UPDATE THE WARP NAME
            updateWarpName.setString(1, warp.getName());
            updateWarpName.setInt(2, warp.getId());
            return updateWarpName.executeUpdate() == 1;
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't update warp name of warp = " + warp);
            return false;
        }
    }

    public boolean updateGuests(Warp warp) {

        try {
            // UPDATE THE GUEST LIST
            updateGuestList.setString(1, warp.getGuestList());
            updateGuestList.setInt(2, warp.getId());
            return updateGuestList.executeUpdate() == 1;
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't update the guest list of warp = " + warp);
            return false;
        }
    }

    public boolean updateAccess(Warp warp) {

        try {
            // UPDATE THE ACCESS MODIFIER
            updateAccess.setBoolean(1, warp.isPublic());
            updateAccess.setInt(2, warp.getId());
            return updateAccess.executeUpdate() == 1;
        } catch (Exception e) {
            ConsoleUtils.printException(e, Core.NAME, "Can't update the access modifier of warp = " + warp);
            return false;
        }
    }
}
