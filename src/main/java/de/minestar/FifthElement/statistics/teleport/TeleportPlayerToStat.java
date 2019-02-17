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

package de.minestar.fifthelement.statistics.teleport;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Queue;

import org.bukkit.Location;

import de.minestar.fifthelement.Core;
import de.minestar.minestarlibrary.stats.Statistic;
import de.minestar.minestarlibrary.stats.StatisticType;

public class TeleportPlayerToStat implements Statistic {

    private String playerName;
    private String targetName;
    private String targetLocation;
    private Timestamp date;

    public TeleportPlayerToStat() {
        // EMPTY CONSTRUCTOR FOR REFLECTION ACCESS
    }

    public TeleportPlayerToStat(String playerName, String targetName, Location targetLocation) {
        this.playerName = playerName;
        this.targetName = targetName;
        this.targetLocation = targetLocation.toString();
        this.date = new Timestamp(System.currentTimeMillis());
    }

    @Override
    public String getPluginName() {
        return Core.NAME;
    }

    @Override
    public String getName() {
        return "TeleportPlayerTo";
    }

    @Override
    public LinkedHashMap<String, StatisticType> getHead() {
        LinkedHashMap<String, StatisticType> head = new LinkedHashMap<>();

        head.put("playerName", StatisticType.STRING);
        head.put("targetName", StatisticType.STRING);
        head.put("targetLocation", StatisticType.STRING);
        head.put("date", StatisticType.DATETIME);

        return head;
    }

    @Override
    public Queue<Object> getData() {
        Queue<Object> data = new LinkedList<>();

        data.add(playerName);
        data.add(targetName);
        data.add(targetLocation);
        data.add(date);

        return data;
    }
}
