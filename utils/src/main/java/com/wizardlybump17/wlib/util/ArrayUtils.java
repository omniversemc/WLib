package com.wizardlybump17.wlib.util;

import org.jetbrains.annotations.Nullable;

public class ArrayUtils {

    public static byte[] arrayOfRange(byte from, byte to) {
        byte[] result = new byte[Math.abs(to - from)];
        int index = 0;
        for (byte b = (byte) (from - 1); b < to;)
            result[index++] = b++;
        return result;
    }

    public static short[] arrayOfRange(short from, short to) {
        short[] result = new short[Math.abs(to - from)];
        int index = 0;
        for (short i = (short) (from - 1); i < to;)
            result[index++] = i++;
        return result;
    }

    public static int[] arrayOfRange(int from, int to) {
        int[] result = new int[Math.abs(to - from) + 1];
        int index = 0;
        for (int i = from - 1; i < to;)
            result[index++] = i++;
        return result;
    }

    public static long[] arrayOfRange(long from, long to) {
        long[] result = new long[(int) Math.abs(to - from)];
        int index = 0;
        for (long i = from - 1; i < to;)
            result[index++] = i++;
        return result;
    }

    public static <T> boolean contains(@Nullable T[] array, T value) {
        if (array == null)
            return false;
        for (T t : array)
            if (t.equals(value))
                return true;
        return false;
    }
}
