package com.wizardlybump17.wlib.adapter.v1_8_R3;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.wizardlybump17.wlib.util.ReflectionUtil;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ItemAdapter extends com.wizardlybump17.wlib.adapter.ItemAdapter {

    public static final Class<?> CRAFT_META_ITEM_CLASS = ReflectionUtil.getClass("org.bukkit.craftbukkit.v1_8_R3.inventory.CraftMetaItem");
    public static final Set<String> IGNORED_TAGS = ReflectionUtil.getField(CRAFT_META_ITEM_CLASS, null, "HANDLED_TAGS");

    @Override
    public void setSkull(@NonNull SkullMeta meta, @NonNull String url) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", getSkullTextureBase64(url)));
        ReflectionUtil.setField(meta.getClass(), meta, "profile", profile);
    }

    @Override
    public String getSkullUrl(@NonNull SkullMeta meta) {
        GameProfile profile = ReflectionUtil.getField(meta.getClass(), meta, "profile");
        if (profile == null)
            return null;

        return profile.getProperties().get("textures").iterator().next().getValue();
    }

    @Override
    public void setNbtTags(@NonNull ItemMeta meta, @NonNull Map<String, Object> nbtTags) {
        Map<String, Object> tags = new HashMap<>(nbtTags.size());
        for (Map.Entry<String, Object> entry : nbtTags.entrySet())
            tags.put(entry.getKey(), javaToNbt(entry.getValue()));
        ReflectionUtil.setField(getItemMetaClass(meta.getClass()), meta, "unhandledTags", tags);
    }

    @Override
    public void setNbtTag(@NonNull ItemMeta meta, @NonNull String key, @NonNull Object value) {
        Map<String, Object> nbtTags = getNbtTags(meta);
        nbtTags.put(key, javaToNbt(value));
        setNbtTags(meta, nbtTags);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull Map<String, Object> getNbtTags(@NonNull ItemMeta meta) {
        Map<String, Object> tags = (Map<String, Object>) nbtToJava(ReflectionUtil.getField(getItemMetaClass(meta.getClass()), meta, "unhandledTags"));
        for (String tag : IGNORED_TAGS)
            tags.remove(tag);
        return tags;
    }

    private Class<?> getItemMetaClass(Class<?> clazz) {
        if (clazz == CRAFT_META_ITEM_CLASS)
            return clazz;

        return getItemMetaClass(clazz.getSuperclass());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object nbtToJava(Object original) {
        if (original instanceof Map) {
            Map<Object, Object> map = (Map<Object, Object>) original;
            Map<String, Object> output = new HashMap<>(map.size());
            for (Map.Entry<Object, Object> entry : map.entrySet())
                output.put(entry.getKey().toString(), nbtToJava(entry.getValue()));
            return output;
        }
        if (original instanceof List) {
            List<Object> list = (List<Object>) original;
            List<Object> output = new ArrayList<>(list.size());
            for (Object object : list)
                output.add(nbtToJava(object));
            return output;
        }

        if (!(original instanceof NBTBase))
            return original;

        if (original instanceof NBTTagString)
            return ((NBTTagString) original).a_();
        if (original instanceof NBTTagByte)
            return ((NBTTagByte) original).f();
        if (original instanceof NBTTagByteArray)
            return ((NBTTagByteArray) original).c();
        if (original instanceof NBTTagShort)
            return ((NBTTagShort) original).e();
        if (original instanceof NBTTagInt)
            return ((NBTTagInt) original).d();
        if (original instanceof NBTTagIntArray)
            return ((NBTTagIntArray) original).c();
        if (original instanceof NBTTagLong)
            return ((NBTTagLong) original).c();
        if (original instanceof NBTTagFloat)
            return ((NBTTagFloat) original).h();
        if (original instanceof NBTTagDouble)
            return ((NBTTagDouble) original).g();
        if (original instanceof NBTTagList) {
            NBTTagList nbtList = (NBTTagList) original;
            List<Object> output = new ArrayList<>(nbtList.size());
            for (int i = 0; i < nbtList.size(); i++)
                output.add(nbtToJava(nbtList.get(i)));
            return output;
        }
        if (original instanceof NBTTagCompound) {
            NBTTagCompound nbtCompound = (NBTTagCompound) original;
            Map<String, Object> output = new HashMap<>(nbtCompound.c().size());
            for (String key : nbtCompound.c())
                output.put(key, nbtToJava(nbtCompound.get(key)));
            return output;
        }

        return null;
    }

    @Override
    public NBTBase javaToNbt(Object original) {
        if (original instanceof NBTBase)
            return (NBTBase) original;

        if (original instanceof String)
            return new NBTTagString((String) original);
        if (original instanceof Byte)
            return new NBTTagByte((Byte) original);
        if (original instanceof byte[])
            return new NBTTagByteArray((byte[]) original);
        if (original instanceof Short)
            return new NBTTagShort((Short) original);
        if (original instanceof Integer)
            return new NBTTagInt((Integer) original);
        if (original instanceof int[])
            return new NBTTagIntArray((int[]) original);
        if (original instanceof Long)
            return new NBTTagLong((Long) original);
        if (original instanceof Float)
            return new NBTTagFloat((Float) original);
        if (original instanceof Double)
            return new NBTTagDouble((Double) original);
        if (original instanceof List) {
            NBTTagList nbtList = new NBTTagList();
            for (Object object : (List<?>) original)
                nbtList.add(javaToNbt(object));
            return nbtList;
        }
        if (original instanceof Map) {
            NBTTagCompound nbtCompound = new NBTTagCompound();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) original).entrySet())
                nbtCompound.set((String) entry.getKey(), javaToNbt(entry.getValue()));
            return nbtCompound;
        }

        return null;
    }
}
