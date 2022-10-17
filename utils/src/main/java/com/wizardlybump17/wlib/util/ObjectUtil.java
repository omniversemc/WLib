package com.wizardlybump17.wlib.util;

import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.function.Supplier;

@UtilityClass
public class ObjectUtil {

    /**
     * <p>Tries to clone the object.</p>
     * <p>
     *     If the object is a {@link Map}, {@link MapUtils#deepClone(Supplier, Map)} will be used.<br>
     *     If the object is a {@link List}, {@link CollectionUtil#deepClone(Supplier, Collection)} will be used.<br>
     *     If the object is a {@link Set}, {@link CollectionUtil#deepClone(Supplier, Collection)} will be used.<br>
     *     If the object is a {@link Cloneable}, it will try to call {@link Object#clone()} reflectively.<br>
     * </p>
     * @param original the original object
     * @return the cloned object or the original object if it can't be cloned
     * @param <T> the object type
     */
    @SuppressWarnings("unchecked")
    public static <T> T clone(T original) {
        if (original instanceof Map<?,?>)
            return (T) MapUtils.deepClone(HashMap::new, (Map<?, ?>) original);

        if (original instanceof List<?>)
            return (T) CollectionUtil.deepClone(ArrayList::new, (List<?>) original);

        if (original instanceof Set<?>)
            return (T) CollectionUtil.deepClone(HashSet::new, (Set<?>) original);

        if (!(original instanceof Cloneable))
            return original;

        try {
            return (T) original.getClass().getMethod("clone").invoke(original);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
