package com.wizardlybump17.wlib.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.wizardlybump17.wlib.adapter.ItemAdapter;
import com.wizardlybump17.wlib.adapter.NMSAdapter;
import com.wizardlybump17.wlib.adapter.NMSAdapterRegister;
import com.wizardlybump17.wlib.adapter.WMaterial;
import com.wizardlybump17.wlib.util.CollectionUtil;
import com.wizardlybump17.wlib.util.MapUtils;
import lombok.Builder;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

//TODO refactor. Move the builder to a proper class
@Data
@Builder
public class Item {

    private static final Map<UUID, ItemStack> HEADS = new HashMap<>();
    private static final NMSAdapter ADAPTER = NMSAdapterRegister.getInstance().current();

    private Material type;
    private Integer amount;
    private short durability;
    private String displayName;
    private List<String> lore;
    private boolean glow, unbreakable;
    private Map<Enchantment, Integer> enchantments;
    private Set<ItemFlag> flags;
    private Map<String, Object> nbtTags;
    private Integer customModelData;

    public static ItemBuilder getHead(String base64) {
        final UUID uuid = UUID.nameUUIDFromBytes(base64.getBytes());
        if (HEADS.containsKey(uuid))
            return fromItemStack(HEADS.get(uuid));

        try {
            ItemStack itemStack = builder().type(WMaterial.PLAYER_HEAD).build();
            SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();

            GameProfile gameProfile = new GameProfile(uuid, null);
            PropertyMap properties = gameProfile.getProperties();

            properties.put("textures", new Property("textures", base64));

            Field profileField = itemMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(itemMeta, gameProfile);

            itemStack.setItemMeta(itemMeta);

            HEADS.put(uuid, itemStack);

            return fromItemStack(itemStack);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ItemBuilder getHead(UUID player) {
        if (HEADS.containsKey(player))
            return fromItemStack(HEADS.get(player));

        ItemStack item = builder().type(WMaterial.PLAYER_HEAD).build();
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(Bukkit.getOfflinePlayer(player).getName());
        item.setItemMeta(meta);

        HEADS.put(player, item);

        return fromItemStack(item);
    }

    /**
     * Attempts to create an ItemBuilder of the item.
     * If the item is null it will return an empty ItemBuilder
     * @param item the item
     * @return the ItemBuilder based on the item
     */
    public static ItemBuilder fromItemStack(ItemStack item) {
        final ItemBuilder builder = builder();
        if (item == null)
            return builder;

        ItemAdapter itemAdapter = ADAPTER.getItemAdapter(item);
        ItemMeta itemMeta = item.getItemMeta();

        builder
                .type(item.getType())
                .amount(item.getAmount() == 1 ? null : item.getAmount())
                .durability(item.getDurability())
                .enchantments(item.getEnchantments().isEmpty() ? null : item.getEnchantments())
                .nbtTags(itemAdapter.getNbtTags().isEmpty() ? null : itemAdapter.getNbtTags())
                .glow(itemAdapter.hasGlow());

        if (item.hasItemMeta())
            builder
                    .displayName(itemMeta.getDisplayName())
                    .lore(itemMeta.getLore())
                    .flags(itemMeta.getItemFlags().isEmpty() ? null : itemMeta.getItemFlags())
                    .customModelData(itemAdapter.getCustomModelData())
                    .unbreakable(itemAdapter.isUnbreakable());

        return builder;
    }

    public static ItemBuilder deserialize(Map<String, Object> args) {
        return builder().setData(args);
    }

    public static String toBase64(ItemStack item) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(item);
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ItemStack fromBase64(String base64) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();
            return item;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SerializableAs("item-builder")
    public static class ItemBuilder implements ConfigurationSerializable {

        public ItemBuilder() {
        }

        public ItemBuilder(Map<String, Object> data) {
            setData(data);
        }

        private WMaterial wmaterial;

        public String getDisplayName() {
            return displayName;
        }

        public Material getType() {
            fixMaterial();
            return type;
        }

        public int getAmount() {
            return amount == null ? 1 : amount;
        }

        public short getDurability() {
            return durability;
        }

        public Map<Enchantment, Integer> getEnchantments() {
            return enchantments == null ? new HashMap<>() : enchantments;
        }

        public Set<ItemFlag> getFlags() {
            return flags;
        }

        public ItemBuilder type(Material type) {
            this.type = type;
            wmaterial = null;
            return this;
        }

        public ItemBuilder type(WMaterial type) {
            this.type = null;
            wmaterial = type;
            return this;
        }

        public ItemBuilder lore(String... lore) {
            return lore(Arrays.asList(lore));
        }

        public ItemBuilder lore(List<String> lore) {
            this.lore = lore;
            return this;
        }

        public ItemBuilder enchantment(Enchantment enchantment, int level) {
            if (enchantments == null)
                enchantments = new HashMap<>();
            enchantments.put(enchantment, level);
            return this;
        }

        public ItemBuilder flags(ItemFlag... flags) {
            if (this.flags == null)
                this.flags = new HashSet<>();
            this.flags.addAll(Arrays.asList(flags));
            return this;
        }

        public ItemBuilder flags(Set<ItemFlag> flags) {
            this.flags = flags;
            return this;
        }

        public ItemBuilder nbtTag(String key, Object value) {
            if (nbtTags == null)
                nbtTags = new HashMap<>();
            nbtTags.put(key, value);
            return this;
        }

        public boolean hasNbtTag(String key) {
            if (nbtTags == null)
                return false;
            return nbtTags.containsKey(key);
        }

        public boolean hasFlag(ItemFlag flag) {
            if (flags == null)
                flags = new HashSet<>();
            return flags.contains(flag);
        }

        public Object getNbtTag(String key) {
            if (nbtTags == null)
                return null;
            return ADAPTER.nbtToJava(nbtTags.get(key));
        }

        @SuppressWarnings("unchecked")
        public Map<String, Object> getNbtTags() {
            if (nbtTags == null)
                return new HashMap<>();
            return (Map<String, Object>) ADAPTER.nbtToJava(nbtTags);
        }

        public ItemBuilder copy(ItemStack item) {
            ItemBuilder builder = Item.fromItemStack(item);
            return type(builder.type)
                    .amount(builder.amount)
                    .durability(builder.durability)
                    .displayName(builder.displayName)
                    .lore(builder.lore)
                    .enchantments(builder.enchantments)
                    .nbtTags(builder.nbtTags)
                    .flags(builder.flags)
                    .glow(builder.glow)
                    .unbreakable(builder.unbreakable)
                    .customModelData(builder.customModelData);
        }

        public ItemStack build() {
            ItemStack itemStack;
            if (wmaterial != null) //ill add all the items in WMaterial :D
                itemStack = fixMaterial();
            else
                itemStack = new ItemStack(type, amount == null ? 1 : amount, durability);

            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null)
                return itemStack;
            itemMeta.setDisplayName(displayName == null ? null : displayName.replace('&', '§'));
            itemMeta.setLore(lore);

            itemStack.setItemMeta(itemMeta);
            ItemAdapter itemAdapter = ADAPTER.getItemAdapter(itemStack);
            itemAdapter.setUnbreakable(unbreakable);
            itemStack = itemAdapter.getTarget();
            itemMeta = itemStack.getItemMeta();

            itemMeta.addItemFlags(flags == null ? new ItemFlag[0] : flags.toArray(new ItemFlag[]{}));
            itemStack.setItemMeta(itemMeta);
            itemStack.addUnsafeEnchantments(enchantments == null ? new HashMap<>() : enchantments);

            itemAdapter = ADAPTER.getItemAdapter(itemStack);

            if (enchantments == null || enchantments.isEmpty()) {
                itemAdapter.setGlow(glow);
                itemStack = itemAdapter.getTarget();
                itemAdapter = ADAPTER.getItemAdapter(itemStack);
            }
            if (nbtTags != null) {
                itemAdapter.setNbtTags(nbtTags, false);
                itemStack = itemAdapter.getTarget();
                itemAdapter = ADAPTER.getItemAdapter(itemStack);
            }
            if (customModelData != null) {
                itemAdapter.setCustomModelData(customModelData);
                itemStack = itemAdapter.getTarget();
            }

            return itemStack;
        }

        public List<String> getLore() {
            return lore == null ? new ArrayList<>() : new ArrayList<>(lore);
        }

        public boolean hasGlow() {
            return glow;
        }

        @Override
        public Map<String, Object> serialize() {
            Map<String, Object> map = new LinkedHashMap<>();

            fixMaterial();
            map.put("material", type.name());
            if (amount != null)
                map.put("amount", amount);
            if (durability != 0)
                map.put("durability", durability);
            if (displayName != null)
                map.put("display-name", displayName);
            if (lore != null)
                map.put("lore", lore);
            if (flags != null)
                map.put("flags", flags.stream().map(ItemFlag::name).collect(Collectors.toList()));
            if (nbtTags != null)
                map.put("nbt-tags", nbtTags);
            if (unbreakable)
                map.put("unbreakable", true);
            if (glow)
                map.put("glow", true);
            if (enchantments != null)
                map.put("enchantments", MapUtils.mapKeys(enchantments, Enchantment::getName));
            if (customModelData != null)
                map.put("custom-model-data", customModelData);

            return map;
        }

        @SuppressWarnings("unchecked")
        public ItemBuilder setData(Map<String, Object> args) {
            List<String> lore = new ArrayList<>();
            if (args.get("lore") != null)
                lore = (List<String>) new CollectionUtil<>((List<String>) args.get("lore")).replace('&', '§').getCollection();

            Set<ItemFlag> flags = new HashSet<>();
            if (args.get("flags") != null) {
                final List<String> flagList = (List<String>) args.get("flags");
                for (String s : flagList)
                    flags.add(ItemFlag.valueOf(s.toUpperCase()));
            }

            Map<Enchantment, Integer> enchantments = new HashMap<>();
            if (args.get("enchantments") != null) {
                Map<String, Number> enchantmentsMap = (Map<String, Number>) args.get("enchantments");
                for (Map.Entry<String, Number> entry : enchantmentsMap.entrySet())
                    enchantments.put(Enchantment.getByName(entry.getKey()), entry.getValue().intValue());
            }

            if (args.containsKey("material") || args.containsKey("type"))
                type(Material.valueOf(args.getOrDefault("material", args.get("type")).toString().toUpperCase()));
            if (args.containsKey("type"))
                type(Material.valueOf(args.get("type").toString().toUpperCase()));
            if (args.containsKey("amount"))
                amount((int) args.get("amount"));
            if (args.containsKey("durability"))
                durability(((Number) args.get("durability")).shortValue());
            if (args.containsKey("display-name"))
                displayName(args.get("display-name").toString().replace('&', '§'));
            if (args.containsKey("lore"))
                lore(lore);
            if (args.containsKey("flags"))
                flags(flags);
            if (args.containsKey("unbreakable"))
                unbreakable((boolean) args.get("unbreakable"));
            if (args.containsKey("glow"))
                glow((boolean) args.get("glow"));
            if (args.containsKey("enchantments"))
                enchantments(enchantments);
            if (args.containsKey("nbt-tags"))
                nbtTags((Map<String, Object>) args.get("nbt-tags"));
            if (args.containsKey("custom-model-data"))
                customModelData((int) args.get("custom-model-data"));
            return this;
        }

        public static ItemBuilder valueOf(Map<String, Object> args) {
            return deserialize(args);
        }

        public static ItemBuilder deserialize(Map<String, Object> args) {
            return new ItemBuilder(args);
        }

        private ItemStack fixMaterial() {
            if (wmaterial != null) {
                if (wmaterial.dataDependent())
                    return new ItemStack(Material.valueOf(wmaterial.name()), 1, durability);
                final ItemStack stack = wmaterial.getItemStack();
                type = stack.getType();
                durability = stack.getDurability();
                return stack;
            }
            return null;
        }
    }
}
