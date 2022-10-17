package com.wizardlybump17.wlib.util.bukkit;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;

import java.util.List;
import java.util.ListIterator;

@UtilityClass
public class StringUtil {

    /**
     * Colorizes a string
     * @param string the string to colorize
     * @return the colorized string
     */
    public static String colorize(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    /**
     * Uses {@link #colorize(String)} to colorize each element of the list
     * @param strings the list to colorize
     * @return the list with each element colorized
     */
    public static List<String> colorize(List<String> strings) {
        ListIterator<String> iterator = strings.listIterator();
        while (iterator.hasNext())
            iterator.set(colorize(iterator.next()));
        return strings;
    }
}
