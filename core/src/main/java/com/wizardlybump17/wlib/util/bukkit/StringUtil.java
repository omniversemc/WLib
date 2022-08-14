package com.wizardlybump17.wlib.util.bukkit;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;

import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class StringUtil {

    public static final Pattern HEX_PATTERN = Pattern.compile("<#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})>");

    public static Color getColor(String string) {
        Matcher matcher = HEX_PATTERN.matcher(string);
        if (!matcher.matches()) {
            try {
                org.bukkit.ChatColor bukkitColor = org.bukkit.ChatColor.valueOf(string.toUpperCase());
                java.awt.Color color = bukkitColor.asBungee().getColor();
                return Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
            } catch (IllegalArgumentException ignored) {
            }
            return null;
        }

        String hex = "#" + matcher.group(1);
        java.awt.Color javaColor = java.awt.Color.decode(hex);
        return Color.fromRGB(javaColor.getRed(), javaColor.getGreen(), javaColor.getBlue());
    }

    public static ChatColor toBungeeColor(Color color) {
        return ChatColor.of(new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue()));
    }

    /**
     * Colorizes a string.<br>
     * It uses a regex to apply hex colors to the string.<br>
     * @see #HEX_PATTERN
     * @param string the string to colorize
     * @return the colorized string
     */
    public static String colorize(String string) {
        string = ChatColor.translateAlternateColorCodes('&', string);

        Matcher matcher = HEX_PATTERN.matcher(string);
        while (matcher.find()) {
            String hex = "<#" + matcher.group(1) + ">";
            Color color = getColor(hex);
            string = string.replaceFirst(hex, toBungeeColor(color).toString());
        }

        return string;
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