package com.wizardlybump17.wlib.util;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class CollectionUtil<E> {

    private final Collection<E> collection;

    public ListUtil replace(String old, String replacement) {
        List<String> result = new ArrayList<>(collection.size());
        for (E e : collection)
            result.add(e.toString().replace(old, replacement));
        return new ListUtil(result);
    }

    public ListUtil replace(char old, char replacement) {
        List<String> result = new ArrayList<>(collection.size());
        for (E e : collection)
            result.add(e.toString().replace(old, replacement));
        return new ListUtil(result);
    }

    public E getIf(Predicate<E> predicate) {
        for (E e : collection)
            if (predicate.test(e))
                return e;
        return null;
    }

    public static List<String> breakLines(String string, int length, String prefix) {
        List<String> result = new ArrayList<>();
        int current = 0;
        while (current < string.length())
            result.add(prefix + string.substring(current, current + length > string.length() ? (current = string.length()) : (current += length)));
        return result;
    }
}