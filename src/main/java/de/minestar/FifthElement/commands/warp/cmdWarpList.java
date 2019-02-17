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

package de.minestar.fifthelement.commands.warp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import de.outinetworks.permissionshub.PermissionUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.minestar.fifthelement.Core;
import de.minestar.fifthelement.Settings;
import de.minestar.fifthelement.data.Warp;
import de.minestar.fifthelement.data.WarpCounter;
import de.minestar.fifthelement.data.filter.NameFilter;
import de.minestar.fifthelement.data.filter.OwnerFilter;
import de.minestar.fifthelement.data.filter.PrivateFilter;
import de.minestar.fifthelement.data.filter.PublicFilter;
import de.minestar.fifthelement.data.filter.UseFilter;
import de.minestar.fifthelement.data.filter.WarpFilter;
import de.minestar.fifthelement.statistics.warp.WarpListStat;
import de.minestar.minestarlibrary.chat.ChatMessage;
import de.minestar.minestarlibrary.chat.ChatMessage.ChatMessageBuilder;
import de.minestar.minestarlibrary.chat.ClickEvent;
import de.minestar.minestarlibrary.chat.HoverEvent;
import de.minestar.minestarlibrary.chat.HoverEvent.HoverEventBuilder;
import de.minestar.minestarlibrary.chat.TextPart;
import de.minestar.minestarlibrary.chat.TextPart.TextPartBuilder;
import de.minestar.minestarlibrary.commands.AbstractExtendedCommand;
import de.minestar.minestarlibrary.datastructure.SortedList;
import de.minestar.minestarlibrary.stats.StatisticHandler;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class cmdWarpList extends AbstractExtendedCommand {

    public cmdWarpList(String syntax, String arguments, String node) {
        super(Core.NAME, syntax, arguments, node);
    }

    @Override
    public void execute(String[] args, Player player) {
        List<WarpFilter> filterList = new ArrayList<>();

        int pageNumber = 1;
        filterList.add(new UseFilter(player));

        // APPLY FILTER
        if (args.length > 0) {
            for (int i = 0; i < args.length; ++i) {
                String arg = args[i];
                // PAGE NUMBER
                if (arg.equalsIgnoreCase("-page")) {
                    // NEXT PARAMETER MUST EXIST
                    if (i < args.length - 1) {
                        try {
                            pageNumber = Integer.valueOf(args[++i]);
                            // NEGATIVE PAGE NUMBER
                            if (pageNumber <= 0) {
                                PlayerUtils.sendError(player, pluginName, "Die Seitenzahl muss größer 0 sein!");
                                return;
                            }
                        } catch (Exception e) {
                            // NOT A VALID NUMBER
                            PlayerUtils.sendError(player, pluginName, args[i] + " ist keine gültige Seitenzahl!");
                            return;
                        }
                    } else {
                        PlayerUtils.sendError(player, pluginName, "Es fehlt bei '-page' die Seitenzahl!");
                        return;
                    }
                }
                // DISPLAY OWN CREATED WARPS
                else if (arg.equalsIgnoreCase("-created") || arg.equalsIgnoreCase("-my")) {
                    filterList.add(new OwnerFilter(player.getName()));
                }
                // DISPLAY USEABLE PRIVATE WARPS
                else if (arg.equalsIgnoreCase("-private")) {
                    filterList.add(PrivateFilter.getInstance());
                }
                // DISPLAY PUBLIC WARPS
                else if (arg.equalsIgnoreCase("-public")) {
                    filterList.add(PublicFilter.getInstance());
                }
                // DISPLAY WARPS FROM A SPECIFIC PLAYER WHICH THE COMMAND CALLER
                // CAN USE
                else if (arg.equalsIgnoreCase("-player")) {
                    String targetName;
                    // AFTER -player THERE MUST BE A PLAYER NAME
                    if (i < args.length - 1) {
                        targetName = PlayerUtils.getCorrectPlayerName(args[++i]);
                        // PLAYER NOT FOUND
                        if (targetName == null) {
                            PlayerUtils.sendError(player, pluginName, "Der Spieler '" + args[i] + "' wurde nicht gefunden!");
                            return;
                        }

                        filterList.add(new OwnerFilter(targetName));
                    } else {
                        PlayerUtils.sendError(player, pluginName, "Es fehlt bei '-player' der Name des Spielers!");
                        return;
                    }
                }
                // DISPLAY WARPS CONTAINING A NAME
                else if (arg.equalsIgnoreCase("-name")) {
                    if (i < args.length - 1)
                        filterList.add(new NameFilter(args[++i]));
                    else {
                        PlayerUtils.sendError(player, pluginName, "Es fehlt bei '-name' der Name des Warps!");
                        return;
                    }
                }
            }
        }

        // GET WARPS
        List<Warp> results = Core.warpManager.filterWarps(filterList);
        // NO WARPS FOUND
        if (results.isEmpty()) {
            PlayerUtils.sendError(player, pluginName, "Keine Ergebnisse gefunden mit folgendem Filter:");
            PlayerUtils.sendError(player, pluginName, filterList.toString());
            return;
        }

        int resultSize = results.size();
        ResultList result = split(results, player);

        // GET THE SINGLE PAGE
        int pageSize = Settings.getPageSize();
        int fromIndex = pageSize * (pageNumber - 1);
        if (fromIndex >= resultSize) {
            PlayerUtils.sendError(player, pluginName, "Zu hohe Seitenzahl!");
            return;
        }
        int toIndex = fromIndex + pageSize;
        if (toIndex > resultSize)
            toIndex = resultSize;

        // MAXNUMBER IS ALWAYS A FULL NUMBER
        int maxNumber = (int) Math.ceil((double) resultSize / (double) Settings.getPageSize());

        result = result.subList(fromIndex, toIndex);
        displayList(result, player, pageNumber, maxNumber, filterList);

        // FIRE STATISTIC
        StatisticHandler.handleStatistic(new WarpListStat(player.getName(), resultSize, filterList));
    }

    private final static Comparator<Warp> WARP_COMPARATOR = (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName());

    // SORT THE WARP LIST THE FOLLOWNING:
    // 1. DISPLAY OWN PRIVATE WARPS
    // 2. DISPLAY OWN PUBLIC WARPS
    // 3. DISPLAY INVITED WARPS
    // 4. DISPLAY PUBLIC WARPS
    // ALL ARE ALPHABETICALLY SORTED

    private ResultList split(List<Warp> warpList, Player player) {

        ResultList result = new ResultList();

        // FIRST THE OWN PRIVATE
        Warp warp;
        for (int i = 0; i < warpList.size(); ++i) {
            warp = warpList.get(i);
            if (warp.isPrivate() && warp.isOwner(player)) {
                result.addOwnPrivate(warp);
                warpList.set(i, null);
            }

        }

        // THEN THE OWN PUBLICS
        for (int i = 0; i < warpList.size(); ++i) {
            warp = warpList.get(i);
            if (warp == null)
                continue;

            if (warp.isPublic() && warp.isOwner(player)) {
                result.addOwnPublic(warp);
                warpList.set(i, null);
            }
        }

        // THEN THE INVITED PRIVATES
        for (int i = 0; i < warpList.size(); ++i) {
            warp = warpList.get(i);
            if (warp == null)
                continue;

            if (warp.isPrivate() && !warp.isOwner(player)) {
                result.addInvitedToPrivate(warp);
                warpList.set(i, null);
            }
        }

        // THEN THE OTHER(REMAINING PUBLICS)
        for (Warp warp1 : warpList) {
            warp = warp1;
            if (warp == null)
                continue;
            result.addPublic(warp);
        }

        return result;
    }

    private final static ChatColor NAME_COLOR = ChatColor.GREEN;
    private final static ChatColor VALUE_COLOR = ChatColor.GRAY;

    private void displayList(ResultList list, Player player, int pageNumber, int maxNumber, List<WarpFilter> filter) {

        ChatMessage separatorMessage = ChatMessage.create().addTextPart(TextPart.create("----------------------------------------").setColor(ChatColor.WHITE).build()).build();

        // Send head
        separatorMessage.sendTo(player);
        // send page number, prev and next buttons
        buildPageMessage(pageNumber, maxNumber, filter).sendTo(player);

        // send information about used counts of warps
        WarpCounter counter = Core.warpManager.getWarpCounter(player.getName());
        buildUsedNumberMessage("Private", counter.getPrivateWarps(), Integer.parseInt(PermissionUtils.getOption(player,"maxprivatewarps"))).sendTo(player);
        buildUsedNumberMessage("Public", counter.getPublicWarps(), Integer.parseInt(PermissionUtils.getOption(player,"maxpublicwarps"))).sendTo(player);

        // Finish head
        separatorMessage.sendTo(player);

        // Calculate warp index
        int index = ((pageNumber - 1) * Settings.getPageSize()) + 1;
        if (index < 0)
            index = 1;

        index = displayWarpGroup(list.ownPrivates, Settings.getWarpListOwned(), "Eigene Private", index, player);
        index = displayWarpGroup(list.ownPublics, Settings.getWarpListPublic(), "Eigene Öffentliche", index, player);
        index = displayWarpGroup(list.invitedToPrivates, Settings.getWarpListPrivate(), "Eingeladene", index, player);
        displayWarpGroup(list.publics, Settings.getWarpListPublic(), "Öffentliche", index, player);
    }

    private int displayWarpGroup(List<Warp> warpGroup, ChatColor color, String name, int index, Player player) {
        if (!warpGroup.isEmpty()) {
            buildSeparatorMessage(name).sendTo(player);
            for (Warp warp : warpGroup) {
                buildWarpMessage(warp, index++, color, player).sendTo(player);
            }
        }
        return index;
    }

    private ChatMessage buildPageMessage(int pageNumber, int maxNumber, List<WarpFilter> filter) {
        ChatMessageBuilder messageBuilder = ChatMessage.create();

        // Create Seite: X/Y
        messageBuilder.addTextPart(TextPart.create("Seite:  ").setColor(NAME_COLOR).build());
        messageBuilder.addTextPart(TextPart.create(pageNumber + "/" + maxNumber + "     ").setColor(VALUE_COLOR).build());

        // Build [VOR] as a button to previous page
        TextPartBuilder prevButtonBuilder = TextPart.create("[Zurück]");
        if (pageNumber <= 1)
            prevButtonBuilder.setColor(ChatColor.GRAY);
        else {
            prevButtonBuilder.setColor(ChatColor.GREEN).setClickEvent(buildPageClickEvent(pageNumber - 1, filter));
        }
        messageBuilder.addTextPart(prevButtonBuilder.build());

        TextPartBuilder nextButtonBuilder = TextPart.create("[Weiter]");
        if (pageNumber == maxNumber)
            nextButtonBuilder.setColor(ChatColor.GRAY);
        else {
            nextButtonBuilder.setColor(ChatColor.GREEN).setClickEvent(buildPageClickEvent(pageNumber + 1, filter));
        }
        messageBuilder.addTextPart(nextButtonBuilder.build());

        return messageBuilder.build();
    }

    private ClickEvent buildPageClickEvent(int pageNumber, List<WarpFilter> filter) {

        StringBuilder command = new StringBuilder("/warp list ");
        for (WarpFilter warpFilter : filter) {
            command.append(warpFilter.getOption()).append(' ');
            command.append(warpFilter.getArgs()).append(' ');
        }

        command.append("-page ").append(pageNumber);

        return new ClickEvent.RunCommandClickEvent(command.toString());
    }

    private ChatMessage buildUsedNumberMessage(String fieldName, int current, int max) {
        ChatMessageBuilder messageBuilder = ChatMessage.create();
        messageBuilder.addTextPart(TextPart.create(fieldName + ":  ").setColor(NAME_COLOR).build());
        messageBuilder.addTextPart(TextPart.create(current + "/" + max).setColor(VALUE_COLOR).build());
        return messageBuilder.build();
    }

    private ChatMessage buildSeparatorMessage(String content) {

        ChatMessageBuilder messageBuilder = ChatMessage.create();
        messageBuilder.addTextPart(TextPart.create("---- ").setColor(NAME_COLOR).build());
        messageBuilder.addTextPart(TextPart.create(content).setColor(VALUE_COLOR).build());
        messageBuilder.addTextPart(TextPart.create(" ----").setColor(NAME_COLOR).build());
        return messageBuilder.build();
    }

    private ChatMessage buildWarpMessage(Warp warp, final int index, ChatColor warpColor, Player player) {

        ChatMessageBuilder messageBuilder = ChatMessage.create();
        // Build index
        messageBuilder.addTextPart(TextPart.create("#").setColor(NAME_COLOR).build());
        messageBuilder.addTextPart(TextPart.create(index + " ").setColor(VALUE_COLOR).build());

        TextPartBuilder warpToPartBuilder = TextPart.create("@ ").setColor(ChatColor.GOLD).setClickEvent(new ClickEvent.RunCommandClickEvent("/warp " + warp.getName()));
        warpToPartBuilder.setHoverEvent(HoverEvent.create("Warpen").build());

        messageBuilder.addTextPart(warpToPartBuilder.build());

        HoverEventBuilder hoverEventBuilder = HoverEvent.create(warp.getOwner());

        hoverEventBuilder.addLine("World = " + warp.getLocation().getWorld().getName());
        hoverEventBuilder.addLine("x = " + warp.getLocation().getBlockX());
        hoverEventBuilder.addLine("y = " + warp.getLocation().getBlockY());
        hoverEventBuilder.addLine("z = " + warp.getLocation().getBlockZ());

        if (warp.getLocation().getWorld().equals(player.getWorld())) {
            int distance = (int) player.getLocation().distance(warp.getLocation());
            hoverEventBuilder.addLine("Distanz = " + distance);
        }

        ClickEvent suggestWarp = new ClickEvent.SuggestTextClickEvent("/warp " + warp.getName());

        messageBuilder.addTextPart(TextPart.create(warp.getName()).setColor(warpColor).setClickEvent(suggestWarp).setHoverEvent(hoverEventBuilder.build()).build());

        return messageBuilder.build();
    }

    private class ResultList {

        private List<Warp> ownPrivates;
        private List<Warp> ownPublics;
        private List<Warp> invitedToPrivates;
        private List<Warp> publics;

        ResultList() {
            this.ownPrivates = new SortedList<>(WARP_COMPARATOR);
            this.ownPublics = new SortedList<>(WARP_COMPARATOR);
            this.invitedToPrivates = new SortedList<>(WARP_COMPARATOR);
            this.publics = new SortedList<>(WARP_COMPARATOR);
        }

        void addOwnPrivate(Warp warp) {
            this.ownPrivates.add(warp);
        }

        void addOwnPublic(Warp warp) {
            this.ownPublics.add(warp);
        }

        void addInvitedToPrivate(Warp warp) {
            this.invitedToPrivates.add(warp);
        }

        void addPublic(Warp warp) {
            this.publics.add(warp);
        }

        ResultList subList(int fromIndex, int toIndex) {
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
                //i = 0;
                if (cur == total)
                    return subList;
            }

            return subList;
        }
    }

}
