package com.wizardlybump17.wlib.adapter;

import lombok.NonNull;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;
import java.util.Map;

public abstract class ItemAdapter {

    private static ItemAdapter instance;

    public static ItemAdapter getInstance() {
        return instance;
    }

    public static void setInstance(ItemAdapter instance) {
        if (ItemAdapter.instance == null)
            ItemAdapter.instance = instance;
    }

    public abstract void setSkull(@NonNull SkullMeta meta, @NonNull String url);

    public abstract String getSkullUrl(@NonNull SkullMeta meta);

    public abstract void setNbtTags(@NonNull ItemMeta meta, @NonNull Map<String, Object> nbtTags);

    public abstract void setNbtTag(@NonNull ItemMeta meta, @NonNull String key, @NonNull Object value);

    @NotNull
    public abstract Map<String, Object> getNbtTags(@NonNull ItemMeta meta);

    public abstract Object nbtToJava(Object original);

    public abstract Object javaToNbt(Object original);

    public static String getSkullTextureBase64(String url) {
        return new String(Base64.getEncoder().encode(("{textures:{SKIN:{url:\"" + url + "\"}}}").getBytes()));
    }
}
