package com.wizardlybump17.wlib.util.bukkit;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Supplier;
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
        if (string == null)
            return null;

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
        if (strings == null)
            return Collections.emptyList();

        ListIterator<String> iterator = strings.listIterator();
        while (iterator.hasNext())
            iterator.set(colorize(iterator.next()));
        return strings;
    }

    public static @NonNull List<String> colorize(@NonNull List<String> strings, @Nullable Supplier<List<String>> listSupplier) {
        if (listSupplier == null) {
            ListIterator<String> iterator = strings.listIterator();
            while (iterator.hasNext())
                iterator.set(colorize(iterator.next()));
            return strings;
        }

        List<String> list = listSupplier.get();
        for (String string : strings)
            list.add(colorize(string));
        return list;
    }

    /**
     * A beautiful method to use instead of {@link Object#toString()}.<br>
     * If the object is a {@link Vector} or a {@link Location}, it will return a string with the coordinates
     * @param object the object to convert to string
     * @return the string
     */
    public static String toString(Object object) {
        if (object instanceof Vector vector)
            return "X: " + vector.getX() + ", Y: " + vector.getY() + ", Z: " + vector.getZ();

        if (object instanceof Location location)
            return "X: " + location.getX() + ", Y: " + location.getY() + ", Z: " + location.getZ() + ", World: " + (location.getWorld() == null ? "null" : location.getWorld().getName());

        return object.toString();
    }

    /**
     * <p>Colorizes and replaces the {@code \n} literal into actual new lines.</p>
     * @param string the string to fancy
     * @return the 🌹fancy🌹 and formatted string
     */
    @Contract("null -> null; !null -> !null")
    public static @Nullable String fancy(@Nullable String string) {
        if (string == null)
            return null;
        return colorize(string).replace("\\n", "\n");
    }
}
