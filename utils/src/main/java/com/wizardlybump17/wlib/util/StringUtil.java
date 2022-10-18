package com.wizardlybump17.wlib.util;

public class StringUtil {

    private StringUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static <E extends Enum<E>> String getName(Enum<E> e) {
        String name = e.name().replace('_', ' ');
        String[] s = name.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String s1 : s)
            sb.append(s1.substring(0, 1).toUpperCase()).append(s1.substring(1).toLowerCase()).append(" ");
        return sb.toString().trim();
    }

    /**
     * It will convert a string to a camel case string.
     * Example: HELLO_WORLD -> Hello World
     * @param name the string to convert
     * @return the camel case string
     */
    public static String fixName(String name) {
        name = name.replace('_', ' ');
        String[] s = name.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String s1 : s)
            sb.append(s1.substring(0, 1).toUpperCase()).append(s1.substring(1).toLowerCase()).append(" ");
        return sb.toString().trim();
    }

    public static String repeat(String string, int times) {
        StringBuilder builder = new StringBuilder(string.length() * times);
        for (int i = 0; i < times; i++)
            builder.append(string);
        return builder.toString();
    }
}
