/*
 * Copyright (C) 2014 MineStar.de 
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



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;

import de.minestar.minestarlibrary.datastructure.SortedList;

public class WarpListTest {

    @Test
    public void test() {
        
        List<String> exlist = Arrays.asList("ownprivate1","ownprivate2","ownprivate3","ownpublic1","ownpublic2","ownpublic3","ownpublic4","ownpublic5","invitedprivate1","public1","public2","public3");
        
        
        ResultList list = new ResultList();
        list.addOwnPrivate("ownprivate1"); // 0
        list.addOwnPrivate("ownprivate2"); // 1
        list.addOwnPrivate("ownprivate3"); // 2
        assertEquals(3, list.getOwnPrivates().size());

        list.addOwnPublic("ownpublic1"); // 3
        list.addOwnPublic("ownpublic2"); // 4
        list.addOwnPublic("ownpublic3"); // 5
        list.addOwnPublic("ownpublic4"); // 6
        list.addOwnPublic("ownpublic5"); // 7
        assertEquals(5, list.getOwnPublics().size());

        list.addInvitedToPrivate("invitedprivate1"); // 8
        assertEquals(1, list.getInvitedToPrivates().size());

        list.addPublic("public1"); // 9
        list.addPublic("public2"); // 10
        list.addPublic("public3"); // 11
        assertEquals(3, list.getPublicWarps().size());

        List<String> controll = new ArrayList<String>();
        for (String string : list) {
            controll.add(string);
        }
        
        assertEquals(exlist, controll);

        ResultList subList = list.subList(3, 9);
        exlist = Arrays.asList("ownpublic1","ownpublic2","ownpublic3","ownpublic4","ownpublic5","invitedprivate1");
        assertTrue(subList.ownPrivates.isEmpty());
        assertTrue(subList.publics.isEmpty());
        controll = new ArrayList<String>();
        for (String string : subList) {
            controll.add(string);
        }

    }
    // Same implementation only with strings instead of warps
    private class ResultList implements Iterable<String> {

        private List<String> ownPrivates;
        private List<String> ownPublics;
        private List<String> invitedToPrivates;
        private List<String> publics;

        public ResultList() {
            this.ownPrivates = new SortedList<String>(String.class);
            this.ownPublics = new SortedList<String>(String.class);
            this.invitedToPrivates = new SortedList<String>(String.class);
            this.publics = new SortedList<String>(String.class);
        }

        public void addOwnPrivate(String warp) {
            this.ownPrivates.add(warp);
        }

        public List<String> getOwnPrivates() {
            return ownPrivates;
        }

        public void addOwnPublic(String warp) {
            this.ownPublics.add(warp);
        }

        public List<String> getOwnPublics() {
            return ownPublics;
        }

        public void addInvitedToPrivate(String warp) {
            this.invitedToPrivates.add(warp);
        }

        public List<String> getInvitedToPrivates() {
            return invitedToPrivates;
        }

        public void addPublic(String warp) {
            this.publics.add(warp);
        }

        public List<String> getPublicWarps() {
            return publics;
        }

        public ResultList subList(int fromIndex, int toIndex) {
            ResultList subList = new ResultList();

            int i = fromIndex;
            int total = toIndex - fromIndex;
            int cur = 0;

            if (i < ownPrivates.size()) {
                for (; i < ownPrivates.size() && cur < total; ++i, ++cur) {
                    subList.addOwnPrivate(ownPrivates.get(i));
                }
                i = 0;
                if (cur == total)
                    return subList;
            } else {
                i -= ownPrivates.size();
            }

            if (i < ownPublics.size()) {
                for (; i < ownPublics.size() && cur < total; ++i, ++cur) {
                    subList.addOwnPublic(ownPublics.get(i));
                }
                i = 0;
                if (cur == total)
                    return subList;
            } else {
                i -= ownPublics.size();
            }

            if (i < invitedToPrivates.size()) {
                for (; i < invitedToPrivates.size() && cur < total; ++i, ++cur) {
                    subList.addInvitedToPrivate(invitedToPrivates.get(i));
                }
                i = 0;
                if (cur == total)
                    return subList;
            } else {
                i -= invitedToPrivates.size();
            }

            if (i < publics.size()) {
                for (; i < publics.size() && cur < total; ++i, ++cur) {
                    subList.addPublic(publics.get(i));
                }
                i = 0;
                if (cur == total)
                    return subList;
            }

            return subList;
        }

        @Override
        public Iterator<String> iterator() {
            return new ResultListIterator<String>(ownPrivates.iterator(), ownPublics.iterator(), invitedToPrivates.iterator(), publics.iterator());
        }

        private class ResultListIterator<T> implements Iterator<String> {
            private final Iterator<String> iters[];

            private int index;

            @SafeVarargs
            public ResultListIterator(Iterator<String>... iterators) {
                if (iterators == null || iterators.length == 0) {
                    throw new IllegalArgumentException("Must have iterators");
                }
                this.iters = iterators;
            }

            @Override
            public boolean hasNext() {
                for (; index < iters.length; index++) {
                    if (iters[index] != null && iters[index].hasNext()) {
                        return true;
                    }
                }

                return false;
            }

            @Override
            public String next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                return iters[index].next();
            }

            @Override
            public void remove() {
                iters[index].remove();
            }
        }
    }
}
