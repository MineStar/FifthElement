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

import java.util.UUID;

public class WarpCounter {

    private UUID playerUUID;
    private int privateWarps;
    private int publicWarps;

    // CONSTRUCTOR FOR A NEW WARP COUNTER
    public WarpCounter(UUID playerUUID) {
        this(playerUUID, 0, 0);
    }

    // CONSTRUCTOR WHEN WARPS ARE LOADED FROM DATABASE
    public  WarpCounter(UUID playerUUID, int privateWarps, int publicWarps) {
        this.playerUUID = playerUUID;
        this.privateWarps = privateWarps;
        this.publicWarps = publicWarps;
    }

    public void incrementPrivateWarps() {
        ++privateWarps;
    }

    public void decrementPrivateWarps() {
        if (privateWarps > 0)
            --privateWarps;
    }

    public void incrementPublicWarps() {
        ++publicWarps;
    }

    public void decrementPublicWarps() {
        if (publicWarps > 0)
            --publicWarps;
    }

    public int getPrivateWarps() {
        return privateWarps;
    }

    public int getPublicWarps() {
        return publicWarps;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

}
