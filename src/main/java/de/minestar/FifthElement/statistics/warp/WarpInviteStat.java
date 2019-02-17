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

package de.minestar.fifthelement.statistics.warp;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Queue;

import de.minestar.fifthelement.Core;
import de.minestar.minestarlibrary.stats.Statistic;
import de.minestar.minestarlibrary.stats.StatisticType;

public class WarpInviteStat implements Statistic {

    private String warpName;
    private String inviter;
    private String guest;
    private Timestamp date;

    public WarpInviteStat() {
        // EMPTY CONSTRUCTOR FOR REFLECTION ACCESS
    }

    public WarpInviteStat(String warpName, String inviter, String guest) {
        this.warpName = warpName;
        this.inviter = inviter;
        this.guest = guest;
        this.date = new Timestamp(System.currentTimeMillis());
    }

    @Override
    public String getPluginName() {
        return Core.NAME;
    }

    @Override
    public String getName() {
        return "WarpInvite";
    }

    @Override
    public LinkedHashMap<String, StatisticType> getHead() {
        LinkedHashMap<String, StatisticType> head = new LinkedHashMap<>();

        head.put("warpName", StatisticType.STRING);
        head.put("inviter", StatisticType.STRING);
        head.put("guest", StatisticType.STRING);
        head.put("date", StatisticType.DATETIME);

        return head;
    }

    @Override
    public Queue<Object> getData() {
        Queue<Object> data = new LinkedList<>();

        data.add(warpName);
        data.add(inviter);
        data.add(guest);
        data.add(date);

        return data;
    }
}
