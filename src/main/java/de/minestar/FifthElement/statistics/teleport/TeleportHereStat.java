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

package de.minestar.FifthElement.statistics.teleport;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Queue;

import org.bukkit.Location;

import de.minestar.FifthElement.core.Core;
import de.minestar.minestarlibrary.database.DatabaseUtils;
import de.minestar.minestarlibrary.stats.Statistic;
import de.minestar.minestarlibrary.stats.StatisticType;

public class TeleportHereStat implements Statistic {

    private String playerName;
    private String targetName;
    private String playerLocation;
    private Date date;

    public TeleportHereStat() {
        // EMPTY CONSTRUCTOR FOR REFLECTION ACCESS
    }

    public TeleportHereStat(String playerName, String targetName, Location playerLocation) {
        this.playerName = playerName;
        this.targetName = targetName;
        this.playerLocation = playerLocation.toString();
        this.date = new Date();
    }

    @Override
    public String getPluginName() {
        return Core.NAME;
    }

    @Override
    public String getName() {
        return "TeleportHere";
    }

    @Override
    public LinkedHashMap<String, StatisticType> getHead() {
        LinkedHashMap<String, StatisticType> head = new LinkedHashMap<String, StatisticType>();

        head.put("playerName", StatisticType.STRING);
        head.put("targetName", StatisticType.STRING);
        head.put("playerLocation", StatisticType.STRING);
        head.put("date", StatisticType.DATETIME);

        return head;
    }

    @Override
    public Queue<Object> getData() {
        Queue<Object> data = new LinkedList<Object>();
        data.add(playerName);
        data.add(targetName);
        data.add(playerLocation);
        data.add(DatabaseUtils.getDateTimeString(date));
        return data;
    }
}
