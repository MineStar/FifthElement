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

package de.minestar.FifthElement.statistics.home;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Queue;

import de.minestar.FifthElement.core.Core;
import de.minestar.minestarlibrary.database.DatabaseUtils;
import de.minestar.minestarlibrary.stats.Statistic;
import de.minestar.minestarlibrary.stats.StatisticType;

public class SetHomeStat implements Statistic {

    private final String playerName;
    private final boolean updated;
    private final Date date;

    public SetHomeStat(String playerName, boolean updated) {
        this.playerName = playerName;
        this.updated = updated;
        this.date = new Date();
    }

    @Override
    public String getPluginName() {
        return Core.NAME;
    }

    @Override
    public String getName() {
        return "SetHome";
    }

    @Override
    public LinkedHashMap<String, StatisticType> getHead() {

        LinkedHashMap<String, StatisticType> head = new LinkedHashMap<String, StatisticType>();

        head.put("playerName", StatisticType.STRING);
        head.put("updated", StatisticType.INT);
        head.put("date", StatisticType.DATETIME);

        return head;
    }

    @Override
    public Queue<Object> getData() {

        Queue<Object> data = new LinkedList<Object>();

        data.add(playerName);
        // 1 = Home location updated
        // 0 = Home created
        data.add(updated ? 1 : 0);
        data.add(DatabaseUtils.getDateTimeString(date));

        return data;
    }

}
