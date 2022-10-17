package com.wizardlybump17.wlib.item;

import com.wizardlybump17.wlib.adapter.ItemAdapter;
import com.wizardlybump17.wlib.item.enchantment.GlowEnchantment;
import com.wizardlybump17.wlib.util.CollectionUtil;
import com.wizardlybump17.wlib.util.MapUtils;
import com.wizardlybump17.wlib.util.bukkit.StringUtil;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@SerializableAs("item-builder")
public class ItemBuilder implements ConfigurationSerializable, Cloneable {

    private static final ItemFlag[] EMPTY_ITEM_FLAG_ARRAY = new ItemFlag[0];

    private final ItemStack item;
    private final Map<Object, Object> customData;

    public ItemBuilder(ItemStack item, Map<Object, Object> customData) {
        this.item = item == null ? new ItemStack(Material.AIR) : item;
        this.customData = customData;
    }

    public ItemBuilder() {
        this(new ItemStack(Material.AIR), new HashMap<>());
    }

    public ItemBuilder consumeMeta(Consumer<ItemMeta> consumer) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return this;

        consumer.accept(meta);
        item.setItemMeta(meta);

        return this;
    }
    
    public ItemMeta getItemMeta() {
        return item.getItemMeta();
    }

    private <T> T getFromMeta(Function<ItemMeta, T> supplier, T def) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return def;

        T t = supplier.apply(meta);
        return t == null ? def : t;
    }

    public ItemBuilder type(@NonNull Material material) {
        item.setType(material);
        return this;
    }

    public Material type() {
        return item.getType();
    }

    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public int amount() {
        return item.getAmount();
    }

    public ItemBuilder damage(int durability) {
        item.setDurability((short) durability);
        return this;
    }

    public int damage() {
        return item.getDurability();
    }

    public ItemBuilder lore(@Nullable String... lore) {
        return consumeMeta(meta -> meta.setLore(lore == null ? null : Arrays.asList(lore)));
    }

    public ItemBuilder lore(@Nullable List<String> lore) {
        return consumeMeta(meta -> meta.setLore(lore));
    }

    public ItemBuilder itemFlags(@NotNull ItemFlag... itemFlags) {
        return consumeMeta(meta -> {
            meta.removeItemFlags(meta.getItemFlags().toArray(EMPTY_ITEM_FLAG_ARRAY));
            meta.addItemFlags(itemFlags);
        });
    }

    public ItemBuilder itemFlags(@NotNull Set<ItemFlag> itemFlags) {
        return consumeMeta(meta -> {
            meta.removeItemFlags(meta.getItemFlags().toArray(EMPTY_ITEM_FLAG_ARRAY));
            meta.addItemFlags(itemFlags.toArray(EMPTY_ITEM_FLAG_ARRAY));
        });
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        return consumeMeta(meta -> meta.addEnchant(enchantment, level, true));
    }

    public ItemBuilder enchantments(Map<Enchantment, Integer> enchantments) {
        return consumeMeta(meta -> enchantments.forEach((enchantment, level) -> meta.addEnchant(enchantment, level, true)));
    }

    public Map<Enchantment, Integer> enchantments() {
        return getFromMeta(ItemMeta::getEnchants, new HashMap<>());
    }

    public ItemBuilder glow(boolean glow) {
        return consumeMeta(meta -> {
            if (glow)
                meta.addEnchant(GlowEnchantment.INSTANCE, 0, true);
            else
                meta.removeEnchant(GlowEnchantment.INSTANCE);
        });
    }

    public boolean glow() {
        return getFromMeta(meta -> meta.hasEnchant(GlowEnchantment.INSTANCE), false);
    }

    @NotNull
    public String displayName() {
        return getFromMeta(ItemMeta::getDisplayName, "");
    }

    public ItemBuilder displayName(@NotNull String displayName) {
        return consumeMeta(meta -> meta.setDisplayName(displayName));
    }

    @NotNull
    public List<String> lore() {
        return getFromMeta(ItemMeta::getLore, new ArrayList<>());
    }

    @NotNull
    public Set<ItemFlag> itemFlags() {
        return getFromMeta(ItemMeta::getItemFlags, new HashSet<>());
    }

    public ItemBuilder replaceDisplayNameLore(Map<String, Object> replacements) {
        String displayName = displayName();
        List<String> lore = lore();

        for (Map.Entry<String, Object> entry : replacements.entrySet()) {
            displayName = displayName.replace(entry.getKey(), entry.getValue().toString());
            lore = new CollectionUtil<>(lore).replace(entry.getKey(), entry.getValue().toString()).getCollection();
        }

        return displayName(displayName).lore(lore);
    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        return consumeMeta(meta -> meta.spigot().setUnbreakable(unbreakable));
    }

    public boolean unbreakable() {
        return getFromMeta(meta -> meta.spigot().isUnbreakable(), false);
    }

    public String skullUrl() {
        if (getItemMeta() instanceof SkullMeta)
            return ItemAdapter.getInstance().getSkullUrl((SkullMeta) getItemMeta());
        return null;
    }

    public ItemBuilder skull(String url) {
        return consumeMeta(meta -> {
            if (meta instanceof SkullMeta)
                ItemAdapter.getInstance().setSkull((SkullMeta) meta, url);
        });
    }

    public String skullOwner() {
        return getFromMeta(meta -> {
            if (meta instanceof SkullMeta)
                return ((SkullMeta) meta).getOwner();
            return null;
        }, null);
    }

    public ItemBuilder skull(OfflinePlayer owner) {
        return consumeMeta(meta -> {
            if (meta instanceof SkullMeta)
                ((SkullMeta) meta).setOwner(owner.getName());
        });
    }

    public ItemBuilder customData(Object key, Object value) {
        this.customData.put(key, value);
        return this;
    }

    public ItemBuilder customData(Map<Object, Object> customData) {
        this.customData.clear();
        this.customData.putAll(customData);
        return this;
    }

    public Map<Object, Object> customData() {
        return this.customData;
    }

    public ItemBuilder applyColor(boolean applyColor) {
        if (applyColor)
            customData.put("apply-color", true);
        else
            customData.remove("apply-color");
        return this;
    }

    public boolean applyColor() {
        return (boolean) customData.getOrDefault("apply-color", true);
    }

    public ItemBuilder nbtTag(@NonNull String key, @NonNull Object value) {
        return consumeMeta(meta -> ItemAdapter.getInstance().setNbtTag(meta, key, value));
    }

    public ItemBuilder nbtTags(@NonNull Map<String, Object> nbtTags) {
        return consumeMeta(meta -> ItemAdapter.getInstance().setNbtTags(meta, nbtTags));
    }

    @Nullable
    public Object nbtTag(@NonNull String key) {
        return nbtTags().get(key);
    }

    @NotNull
    public Map<String, Object> nbtTags() {
        return ItemAdapter.getInstance().getNbtTags(getItemMeta());
    }

    public ItemStack build() {
        if (applyColor()) {
            displayName(StringUtil.colorize(displayName()));
            lore(StringUtil.colorize(new ArrayList<>(lore()))); //creating a new ArrayList to avoid immutable list
        }

        return item;
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<>();

        result.put("type", type().name());
        if (amount() != 1)
            result.put("amount", amount());
        if (damage() != 0)
            result.put("damage", damage());
        if (!displayName().isEmpty())
            result.put("display-name", displayName());
        if (!lore().isEmpty())
            result.put("lore", lore());
        if (!itemFlags().isEmpty())
            result.put("item-flags", itemFlags().stream().map(Enum::name).collect(Collectors.toCollection(ArrayList::new)));
        if (!enchantments().isEmpty())
            result.put("enchantments", MapUtils.mapKeys(enchantments(), Enchantment::getName));
//        if (!nbtTags().isEmpty())
//            result.put("nbt-tags", ItemAdapter.getInstance().serializeContainer(container()));
        if (unbreakable())
            result.put("unbreakable", true);
        if (skullUrl() != null)
            result.put("skull", skullUrl());
        if (skullOwner() != null)
            result.put("skull", skullOwner());
        if (!customData().isEmpty())
            result.put("custom-data", customData());

        return MapUtils.removeEmptyValues(MapUtils.removeNullValues(result));
    }

    @Override
    public ItemBuilder clone() {
        return new ItemBuilder(item.clone(), new HashMap<>(customData));
    }

    /**
     * Copies the data from the given ItemStack into a new builder.
     * If the item is null, air or not have an ItemMeta, the builder will be empty
     *
     * @param item the item to copy from
     * @return a new builder with the data from the given item
     */
    public static ItemBuilder fromItemStack(@Nullable ItemStack item) {
        return new ItemBuilder(item, new HashMap<>());
    }

    @SuppressWarnings("unchecked")
    public static ItemBuilder deserialize(Map<String, Object> map) {
        ItemBuilder result = new ItemBuilder();

        if (map.get("nbt-tags") != null)
            result.nbtTags((Map<String, Object>) map.get("nbt-tags"));

        result
                .customData((Map<Object, Object>) map.getOrDefault("custom-data", Collections.emptyMap()))
                .type(Material.valueOf((String) map.get("type")))
                .amount((int) map.getOrDefault("amount", 1));

        if (map.get("damage") != null)
            result.damage((int) map.get("damage"));
        if (map.get("display-name") != null)
            result.displayName(StringUtil.colorize(map.get("display-name").toString()));
        if (map.get("lore") != null)
            result.lore(StringUtil.colorize((List<String>) map.get("lore")));
        if (map.get("item-flags") != null)
            result.itemFlags(((List<String>) map.get("item-flags")).stream().map(ItemFlag::valueOf).collect(Collectors.toSet()));
        if (map.get("enchantments") != null)
            ((Map<String, Number>) map.get("enchantments")).forEach((key, value) -> result.enchantment(Enchantment.getByName(key), value.intValue()));

        if (map.get("skull") != null) {
            String skull = map.get("skull").toString();
            try {
                result.skull(Bukkit.getOfflinePlayer(UUID.fromString(skull)));
            } catch (IllegalArgumentException ignored) {
                result.skull(skull);
            }
        }

        result.unbreakable((boolean) map.getOrDefault("unbreakable", false));

        if (result.applyColor()) {
            result
                    .displayName(StringUtil.colorize(result.displayName()))
                    .lore(StringUtil.colorize(result.lore()));
        }

        return result;
    }
}
