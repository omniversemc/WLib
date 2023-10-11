package com.wizardlybump17.wlib.util.bukkit;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

@UtilityClass
public class ConfigUtil {

    /**
     * <p>
     *     Gets a value from the given {@link Map} by the given key.
     *     If the value is null, it will throw a {@link NullPointerException}.
     * </p>
     * @param key the key to get the value
     * @param map the map to get the value from
     * @return the value
     * @param <T> the type of the value
     */
    @SuppressWarnings("unchecked")
    public static <T> @NonNull T get(@NonNull String key, @NonNull Map<@NonNull String, @Nullable Object> map) {
        return (T) Objects.requireNonNull(map.get(key), "The key '" + key + "' is not present in the config!");
    }

    /**
     * <p>
     *     Gets a value from the given {@link Map} by the given key.
     *     If the value is null, it will return the given default value.
     * </p>
     * @param key the key to get the value
     * @param map the map to get the value from
     * @param defaultValue the default value to return if the value is null
     * @return the value or the default value
     * @param <T> the type of the value
     */
    @Contract("_, _, !null -> !null")
    @SuppressWarnings("unchecked")
    public static <T> @Nullable T get(@NonNull String key, @NonNull Map<@NonNull String, @Nullable Object> map, @Nullable T defaultValue) {
        Object object = map.get(key);
        return object == null ? defaultValue : (T) object;
    }
}
