package com.wizardlybump17.wlib.config;

import com.wizardlybump17.wlib.util.ArrayUtils;
import com.wizardlybump17.wlib.util.CollectionUtil;
import com.wizardlybump17.wlib.util.bukkit.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

@EqualsAndHashCode(callSuper = false)
@Data
public class Config extends YamlConfiguration implements Configuration<YamlConfiguration> {

    private final JavaPlugin plugin;
    private final String name;
    private final File file;
    private boolean loaded;

    public void saveDefaultConfig() {
        plugin.saveResource(name, false);
        reloadConfig();
    }

    public void saveConfig() {
        try {
            save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        try {
            load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public World getWorld(String path, World def) {
        String name = getString(path);
        if (name == null)
            return def;
        World world = Bukkit.getWorld(name);
        return world == null ? def : world;
    }

    public World getWorld(String path) {
        return getWorld(path, null);
    }

    public Number getNumber(String path, Number def) {
        return getByType(path, def);
    }

    public Number getNumber(String path) {
        return getByType(path);
    }

    @SuppressWarnings("unchecked")
    public <T> T getByType(String path, T def) {
        return (T) get(path, def);
    }

    public <T> T getByType(String path) {
        return getByType(path, null);
    }

    @Override
    public Object get(String path, Class<?> type) {
        return get(path, null, type);
    }

    @Override
    public Object get(String path, Object def, Class<?> type) {
        return get(path, def);
    }

    public byte getByte(String path, byte def) {
        Object o = get(path, def);
        if (!(o instanceof Number))
            return def;
        return ((Number) o).byteValue();
    }

    public byte getByte(String path) {
        return getByte(path, (byte) 0);
    }

    public short getShort(String path, short def) {
        Object o = get(path, def);
        if (!(o instanceof Number))
            return def;
        return ((Number) o).shortValue();
    }

    public short getShort(String path) {
        return getShort(path, (short) 0);
    }

    public float getFloat(String path, float def) {
        Object o = get(path, def);
        if (!(o instanceof Number))
            return def;
        return ((Number) o).floatValue();
    }

    public float getFloat(String path) {
        return getFloat(path, 0);
    }

    public Map<String, Object> getMap(String path, Map<String, Object> def) {
        Object object = get(path);
        if (object instanceof ConfigurationSerializable)
            return ((ConfigurationSerializable) object).serialize();
        if (object instanceof ConfigurationSection)
            return convertToMap((ConfigurationSection) object);
        return def;
    }

    public Map<String, Object> getMap(String path) {
        return getMap(path, null);
    }

    public <T extends Enum<T>> Map<T, Object> getEnumMap(String path, Class<T> classType, Map<T, Object> def) {
        Object defObject = get(path, def);
        if (!(defObject instanceof ConfigurationSection))
            return def;

        EnumMap<T, Object> map = new EnumMap<>(classType);
        Map<String, Object> originalMap = convertToMap(((ConfigurationSection) defObject));

        originalMap.forEach((key, value) -> {
            try {
                map.put(Enum.valueOf(classType, key.toUpperCase()), value);
            } catch (IllegalArgumentException ignored) { //we don't want a console log for this
            }
        });

        return map;
    }

    public <T extends Enum<T>> Map<T, Object> getEnumMap(String path, Class<T> classType) {
        return getEnumMap(path, classType, null);
    }

    /**
     * Returns the backing map of this configuration.
     * The map is unmodifiable
     *
     * @return the backing map of this configuration
     */
    public Map<String, Object> asMap() {
        return Collections.unmodifiableMap(map);
    }

    public String getFancyString(String path, String[] placeholders, Object[] replacements) {
        String string = getFancyString(path);
        if (string == null)
            return null;
        for (int i = 0; i < placeholders.length; i++)
            string = string.replace('{' + placeholders[i] + '}', replacements[i] == null ? "null" : replacements[i].toString());
        return string;
    }

    public String getFancyString(String path, String def) {
        String string = getString(path, def);
        if (string == null)
            return def;
        return string.replace("\\n", "\n").replace('&', '§');
    }

    public String getFancyString(String path) {
        return getFancyString(path, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object get(String path, Object def, Class<?> type, Path requester) {
        Object o = get(path, def, type);
        if (o == null)
            o = def;

        if (ArrayUtils.contains(requester.options(), "fancy")) {
            if (o instanceof String)
                return StringUtil.colorize(o.toString().replace("\\n", "\n"));
            if (o instanceof List<?>)
                return new CollectionUtil<>(StringUtil.colorize((List<String>) o)).replace("\\n", "\n").getCollection();
        }

        if (o instanceof Number)
            return fixNumber(o, type);

        if (o instanceof ConfigurationSection)
            return convertToMap((MemorySection) o);

        return o;
    }

    private Number fixNumber(Object object, Class<?> type) {
        if (type == byte.class || type == Byte.class)
            return ((Number) object).byteValue();
        if (type == short.class || type == Short.class)
            return ((Number) object).shortValue();
        if (type == int.class || type == Integer.class)
            return ((Number) object).intValue();
        if (type == long.class || type == Long.class)
            return ((Number) object).longValue();
        if (type == float.class || type == Float.class)
            return ((Number) object).floatValue();
        if (type == double.class || type == Double.class)
            return ((Number) object).doubleValue();
        return 0;
    }

    @Override
    public Object get(String path, Class<?> type, Path requester) {
        return get(path, null, type, requester);
    }

    @Override
    public YamlConfiguration getHandle() {
        return this;
    }

    /**
     * Loads the config from the given file path.
     *
     * @param filePath    where this configuration should be loaded from
     * @param plugin      the plugin that is using this configuration
     * @param saveDefault if it must save the default config
     * @return the configuration loaded in the given file path
     */
    public static Config load(String filePath, JavaPlugin plugin, boolean saveDefault) {
        Config config = new Config(plugin, filePath, new File(plugin.getDataFolder(), filePath));

        if (saveDefault) {
            if (config.getFile().exists())
                config.reloadConfig();
            else
                config.saveDefaultConfig();
        }

        return config;
    }

    /**
     * Loads the config from the given file path.
     *
     * @param filePath where this configuration should be loaded from
     * @param plugin   the plugin that is using this configuration
     * @return the config loaded in the given file path
     */
    public static Config load(String filePath, JavaPlugin plugin) {
        return load(filePath, plugin, true);
    }

    /**
     * Loads the config from the given file.
     *
     * @param file   the file that have the config
     * @param plugin the plugin that is using this config
     * @return the config loaded in the given file
     */
    public static Config load(File file, JavaPlugin plugin) {
        return load(file, plugin, true);
    }

    /**
     * Loads the config from the given file.
     *
     * @param file   the file that have the config
     * @param plugin the plugin that is using this config
     * @param load   if it must load the config or just save it
     * @return the config loaded in the given file
     */
    public static Config load(File file, JavaPlugin plugin, boolean load) {
        final String absolutePath = file.getAbsolutePath();
        final Config config = new Config(
                plugin,
                absolutePath.substring(absolutePath.indexOf(plugin.getDataFolder().getAbsolutePath())),
                file
        );
        if (load)
            config.reloadConfig();

        return config;
    }


    @SuppressWarnings("unchecked")
    public static Map<String, Object> convertToMap(ConfigurationSection section) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (String key : section.getKeys(false)) {
            Object object = section.get(key);
            if (object instanceof ConfigurationSection)
                object = convertToMap((ConfigurationSection) object);
            if (object instanceof List) {
                try {
                    final List<ConfigurationSection> list = (List<ConfigurationSection>) object;
                    List<Map<String, Object>> fixedList = new ArrayList<>(list.size());
                    for (ConfigurationSection configurationSection : list)
                        fixedList.add(convertToMap(configurationSection));
                    object = fixedList;
                } catch (ClassCastException ignored) { //never
                }
            }
            map.put(key, object);
        }
        return map;
    }
}
