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

package de.minestar.fifthelement.data.filter;

import de.minestar.fifthelement.data.Warp;

public class PublicFilter implements WarpFilter {

    private static final PublicFilter INSTANCE = new PublicFilter();

    private PublicFilter() {

    }

    public static WarpFilter getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean accept(Warp warp) {
        return warp.isPublic();
    }

    private static final String PUBLIC_FILTER_OPTION = "-public";

    @Override
    public String getOption() {
        return PUBLIC_FILTER_OPTION;
    }

    @Override
    public String getArgs() {
        return "";
    }

    @Override
    public String toString() {
        return "Öffentlich";
    }

}
